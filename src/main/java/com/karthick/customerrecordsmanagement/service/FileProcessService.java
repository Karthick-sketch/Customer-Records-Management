package com.karthick.customerrecordsmanagement.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.config.Constants;
import com.karthick.customerrecordsmanagement.config.FileUploadEvent;
import com.karthick.customerrecordsmanagement.entity.CsvFileDetail;
import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.entity.FileUploadStatus;
import com.karthick.customerrecordsmanagement.repository.CsvFileDetailRepository;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import com.karthick.customerrecordsmanagement.repository.FileUploadStatusRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FileProcessService {
    private CustomerRecordRepository customerRecordRepository;
    private CsvFileDetailRepository csvFileDetailRepository;
    private FileUploadStatusRepository fileUploadStatusRepository;
    private KafkaTemplate<String, FileUploadEvent> kafkaTemplate;

    private final Logger logger = Logger.getLogger(FileProcessService.class.getName());

    public void publishKafkaMessage(long fileId, String fileName) {
        FileUploadEvent fileUploadEvent = new FileUploadEvent(fileId, fileName);
        CompletableFuture<SendResult<String, FileUploadEvent>> future = kafkaTemplate.send(Constants.TOPIC, fileUploadEvent);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[" + fileUploadEvent + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                logger.severe("Unable to send message=[" + fileUploadEvent + "] due to : " + ex.getMessage());
            }
        });
    }

    @KafkaListener(topics = Constants.TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveKafkaMessage(FileUploadEvent fileUploadEvent) {
        uploadCsvFileDataToDb(fileUploadEvent.getFileId());
    }

    public void createNewFileUploadStatus(FileUploadStatus fileUploadStatus) {
        fileUploadStatusRepository.save(fileUploadStatus);
    }

    public void uploadCsvFileDataToDb(long fileId) {
        Optional<CsvFileDetail> file = csvFileDetailRepository.findById(fileId);
        if (file.isPresent()) {
            String fileName = file.get().getFileName();
            String filePath = file.get().getFilePath();
            FileUploadStatus fileUploadStatus = new FileUploadStatus(fileName);
            List<String[]> csvRecords = readCsvFile(filePath);
            if (csvRecords != null) {
                String[] headers = csvRecords.remove(0);
                fileUploadStatus.setTotalRecords(csvRecords.size());
                int uploadedRecords = 0, duplicateRecords = 0, invalidRecords = 0;
                for (String[] record : csvRecords) {
                    try {
                        customerRecordRepository.save(mapTheArraysToCustomerRecord(headers, record));
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
                if (!new File(filePath).delete()) {
                    logger.severe("Failed to delete the file");
                }
            } else {
                logger.warning(fileName + " file is empty");
            }
            createNewFileUploadStatus(fileUploadStatus);
        } else {
            throw new NoSuchElementException("file not found");
        }
    }

    private CustomerRecord mapTheArraysToCustomerRecord(String[] headers, String[] record) {
        Map<String, String> recordMap = new HashMap<>();
        for (int i = 0; i < headers.length && i < record.length; i++) {
            recordMap.put(headers[i], record[i]);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(recordMap, CustomerRecord.class);
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
