package com.karthick.customerrecordsmanagement.fileupload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordDto;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.karthick.customerrecordsmanagement.csvfiledetail.CsvFileDetail;
import com.karthick.customerrecordsmanagement.csvfiledetail.CsvFileDetailService;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatusService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FileUploadProcess {
    private CustomerRecordService customerRecordService;
    private FileUploadStatusService fileUploadStatusService;
    private CsvFileDetailService csvFileDetailService;

    private final Logger logger = Logger.getLogger(FileUploadProcess.class.getName());

    public void pushCustomerRecordsFromFileToDatabase(long accountId, long fileId, long fileUploadStatusId) {
        CsvFileDetail csvFileDetail = csvFileDetailService.fetchCsvFileDetailById(fileId);
        List<String[]> csvRecords = readCsvFile(csvFileDetail.getFilePath());
        if (csvRecords != null) {
            // createCustomerRecordsAndFileUploadStatus(accountId, csvRecords, fileUploadStatusId);
            createAllCustomerRecordsAndFileUploadStatus(accountId, csvRecords, fileUploadStatusId);
        } else {
            logger.warning(csvFileDetail.getFileName() + " file is empty");
        }
        if (!new File(csvFileDetail.getFilePath()).delete()) {
            logger.severe("Failed to delete the file");
        }
    }

    private synchronized void createCustomerRecordsAndFileUploadStatus(long accountId, List<String[]> csvRecords, long fileUploadStatusId) {
        String[] headers = csvRecords.remove(0);
        int uploadedRecords = 0, duplicateRecords = 0, invalidRecords = 0;
        for (String[] record : csvRecords) {
            try {
                customerRecordService.createCustomerRecord(mapDefaultAndCustomFields(accountId, headers, record));
                uploadedRecords++;
            } catch (DataIntegrityViolationException e) {
                if (e.getCause().getClass().equals(ConstraintViolationException.class)) {
                    duplicateRecords++;
                } else if (e.getCause().getClass().equals(PropertyValueException.class)) {
                    invalidRecords++;
                }
                logger.warning("Exception in Customer record creation. Error : " + e.getMessage());
            }
        }
        fileUploadStatusService.updateFileUploadStatus(accountId, fileUploadStatusId, csvRecords.size(), uploadedRecords, duplicateRecords, invalidRecords);
    }

    private synchronized void createAllCustomerRecordsAndFileUploadStatus(long accountId, List<String[]> csvRecords, long fileUploadStatusId) {
        int capacity = 10;
        String[] headers = csvRecords.remove(0);
        List<CustomerRecordDto> customerRecordDto = new ArrayList<>(capacity);
        for (int i = 1; i <= csvRecords.size(); i++) {
            customerRecordDto.add(mapDefaultAndCustomFields(accountId, headers, csvRecords.get(i-1)));
            if (i == csvRecords.size() || i % capacity == 0) {
                try {
                    customerRecordService.createAllCustomerRecord(customerRecordDto);
                } catch (Exception e) {
                    logger.warning("Exception in Customer record creation. Error : " + e.getMessage());
                }
                customerRecordDto.clear();
            }
        }
        fileUploadStatusService.updateFileUploadStatus(accountId, fileUploadStatusId, csvRecords.size(), 0, 0, 0);
    }

    private CustomerRecordDto mapDefaultAndCustomFields(long accountId, String[] headers, String[] records) {
        List<String> fieldNames = CustomerRecord.getFields();
        Map<String, String> defaultFields = new LinkedHashMap<>();
        Map<String, String> customFields = new LinkedHashMap<>();
        for (int i = 0; i < headers.length && i < records.length; i++) {
            String key = headers[i], value = records[i];
            if (fieldNames.contains(key)) {
                defaultFields.put(key, value);
            } else {
                customFields.put(key, value);
            }
        }
        CustomerRecord customerRecord = mapToCustomerRecord(defaultFields);
        customerRecord.setAccountId(accountId);
        return new CustomerRecordDto(customerRecord, customFields);
    }

    private CustomerRecord mapToCustomerRecord(Map<String, String> defaultFields) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(defaultFields, CustomerRecord.class);
    }

    private List<String[]> readCsvFile(String file) {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(0).build()) {
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }
}
