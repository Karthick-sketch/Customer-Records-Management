package com.karthick.customerrecordsmanagement.fileupload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordDto;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.karthick.customerrecordsmanagement.fileupload.csvfiledetail.CsvFileDetail;
import com.karthick.customerrecordsmanagement.fileupload.csvfiledetail.CsvFileDetailRepository;
import com.karthick.customerrecordsmanagement.fileupload.fileuploadstatus.FileUploadStatus;
import com.karthick.customerrecordsmanagement.fileupload.fileuploadstatus.FileUploadStatusRepository;
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
public class FileProcessService {
    private CustomerRecordService customerRecordService;
    private CsvFileDetailRepository csvFileDetailRepository;
    private FileUploadStatusRepository fileUploadStatusRepository;

    private final Logger logger = Logger.getLogger(FileProcessService.class.getName());

    public List<FileUploadStatus> findAllFileUploadStatus() {
        return fileUploadStatusRepository.findAll();
    }

    public FileUploadStatus findFileUploadStatusById(long id) {
        Optional<FileUploadStatus> fileUploadStatus = fileUploadStatusRepository.findById(id);
        if (fileUploadStatus.isEmpty()) {
            throw new NoSuchElementException("The uploaded file status with the Id of " + id + " is not found");
        }
        return fileUploadStatus.get();
    }

    public void createNewFileUploadStatus(String fileName, int total, int uploaded, int duplicate, int invalid) {
        fileUploadStatusRepository.save(new FileUploadStatus(fileName, total, uploaded, duplicate, invalid));
    }

    public void pushCustomerRecordsFromFileToDatabase(long fileId, String fileName) {
        Optional<CsvFileDetail> file = csvFileDetailRepository.findById(fileId);
        if (file.isEmpty() || !fileName.equals(file.get().getFileName())) {
            throw new NoSuchElementException("file not found");
        }
        String filePath = file.get().getFilePath();
        List<String[]> csvRecords = readCsvFile(filePath);
        if (csvRecords != null) {
            createCustomerRecordsAndFileUploadStatus(fileName, csvRecords);
        } else {
            logger.warning(fileName + " file is empty");
        }
        if (!new File(filePath).delete()) {
            logger.severe("Failed to delete the file");
        }
    }

    private void createCustomerRecordsAndFileUploadStatus(String fileName, List<String[]> csvRecords) {
        String[] headers = csvRecords.remove(0);
        int uploadedRecords = 0, duplicateRecords = 0, invalidRecords = 0;
        for (String[] record : csvRecords) {
            try {
                customerRecordService.createNewCustomerRecord(mapDefaultAndCustomFields(headers, record));
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
        createNewFileUploadStatus(fileName, csvRecords.size(), uploadedRecords, duplicateRecords, invalidRecords);
    }

    private CustomerRecordDto mapDefaultAndCustomFields(String[] headers, String[] records) {
        List<String> fieldNames = CustomerRecord.getFields();
        Map<String, String> defaultFields = new HashMap<>();
        Map<String, String> customFields = new HashMap<>();
        for (int i = 0; i < headers.length && i < records.length; i++) {
            String key = headers[i], value = records[i];
            if (fieldNames.contains(key)) {
                defaultFields.put(key, value);
            } else {
                customFields.put(key, value);
            }
        }
        return new CustomerRecordDto(mapToCustomerRecord(defaultFields), customFields);
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
