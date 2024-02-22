package com.karthick.customerrecordsmanagement.fileupload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordDTO;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.karthick.customerrecordsmanagement.csvfiledetail.CsvFileDetail;
import com.karthick.customerrecordsmanagement.csvfiledetail.CsvFileDetailService;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatusService;
import com.karthick.customerrecordsmanagement.utils.Constants;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
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
            createAllCustomerRecordsAndFileUploadStatus(accountId, csvRecords, fileUploadStatusId);
        } else {
            logger.warning(csvFileDetail.getFileName() + " file is empty");
        }
        if (!new File(csvFileDetail.getFilePath()).delete()) {
            logger.severe("Failed to delete the file");
        }
    }

    private synchronized void createAllCustomerRecordsAndFileUploadStatus(long accountId, List<String[]> csvRecords, long fileUploadStatusId) {
        String[] headers = csvRecords.remove(0);
        List<CustomerRecordDTO> customerRecordDTOs = csvRecords.stream()
                .map(record -> mapCustomerRecordAndCustomFields(accountId, headers, record, fileUploadStatusId))
                .flatMap(Optional::stream)
                .toList();
        customerRecordService.createAllCustomerRecord(customerRecordDTOs);
        fileUploadStatusService.updateFileUploadStatus(accountId, fileUploadStatusId, csvRecords.size(), 0, 0);
    }

    private Optional<CustomerRecordDTO> mapCustomerRecordAndCustomFields(long accountId, String[] headers, String[] records, long fileUploadStatusId) {
        List<String> fieldNames = CustomerRecord.getFields();
        Map<String, String> customerRecords = new LinkedHashMap<>();
        Map<String, String> customFields = new LinkedHashMap<>();
        for (int i = 0; i < headers.length && i < records.length; i++) {
            String key = headers[i], value = records[i];
            if (key.equals(Constants.EMAIL_FIELD) && (value == null || value.isBlank())) {
                fileUploadStatusService.updateFileUploadStatusInvalidRecord(accountId, fileUploadStatusId, 1);
                return Optional.empty();
            } else if (fieldNames.contains(key)) {
                customerRecords.put(key, value);
            } else {
                customFields.put(key, value);
            }
        }
        CustomerRecord customerRecord = mapToCustomerRecord(customerRecords);
        customerRecord.setAccountId(accountId);
        return Optional.of(new CustomerRecordDTO(customerRecord, customFields));
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
