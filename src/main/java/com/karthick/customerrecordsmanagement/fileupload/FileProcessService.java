package com.karthick.customerrecordsmanagement.fileupload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecordRepository;
import com.karthick.customerrecordsmanagement.customerrecords.customfields.CustomFieldService;
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
    private CustomerRecordRepository customerRecordRepository;
    private CsvFileDetailRepository csvFileDetailRepository;
    private FileUploadStatusRepository fileUploadStatusRepository;
    private CustomFieldService customFieldService;

    private final Logger logger = Logger.getLogger(FileProcessService.class.getName());

    public List<FileUploadStatus> findAllFileUploadStatus() {
        return fileUploadStatusRepository.findAll();
    }

    public FileUploadStatus findFileUploadStatusById(long id) {
        Optional<FileUploadStatus> fileUploadStatus = fileUploadStatusRepository.findById(id);
        if (fileUploadStatus.isPresent()) {
            return fileUploadStatus.get();
        } else {
            throw new NoSuchElementException("The uploaded file status with the Id of " + id + " is not found");
        }
    }

    public void createNewFileUploadStatus(FileUploadStatus fileUploadStatus) {
        fileUploadStatusRepository.save(fileUploadStatus);
    }

    public void pushCustomerRecordsFromFileToDatabase(long fileId, String fileName) {
        Optional<CsvFileDetail> file = csvFileDetailRepository.findById(fileId);
        if (file.isPresent() && fileName.equals(file.get().getFileName())) {
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
        } else {
            throw new NoSuchElementException("file not found");
        }
    }

    private void createCustomerRecordsAndFileUploadStatus(String fileName, List<String[]> csvRecords) {
        FileUploadStatus fileUploadStatus = new FileUploadStatus(fileName);
        String[] headers = csvRecords.remove(0);
        fileUploadStatus.setTotalRecords(csvRecords.size());
        int uploadedRecords = 0, duplicateRecords = 0, invalidRecords = 0;
        for (String[] record : csvRecords) {
            try {
                List<Map<String, String>> fields = mapDefaultAndCustomFields(headers, record);
                // CustomerRecord customerRecord = customerRecordRepository.saveIgnore(mapToCustomerRecord(fields.get(0)));
                CustomerRecord customerRecord = customerRecordRepository.save(mapToCustomerRecord(fields.get(0)));
                customFieldService.createCustomFields(customerRecord.getId(), fields.get(1));
                uploadedRecords++;
            } catch (DataIntegrityViolationException e) {
                if (e.getCause().getClass().equals(ConstraintViolationException.class)) {
                    duplicateRecords++;
                } else if (e.getCause().getClass().equals(PropertyValueException.class)) {
                    invalidRecords++;
                }
                logger.warning(e.getMessage());
            }
        }
        fileUploadStatus.setUploadedRecords(uploadedRecords);
        fileUploadStatus.setInvalidRecords(invalidRecords);
        fileUploadStatus.setDuplicateRecords(duplicateRecords);
        createNewFileUploadStatus(fileUploadStatus);
    }

    private List<Map<String, String>> mapDefaultAndCustomFields(String[] headers, String[] records) {
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
        return List.of(defaultFields, customFields);
    }

    private CustomerRecord mapToCustomerRecord(Map<String, String> defaultFields) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(defaultFields, CustomerRecord.class);
    }

    private List<String[]> readCsvFile(String file) {
        List<String[]> splitData = null;
        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(0).build();
            splitData = csvReader.readAll();
            csvReader.close();
        } catch (IOException | CsvException e) {
            logger.severe(e.getMessage());
        }
        return splitData;
    }
}
