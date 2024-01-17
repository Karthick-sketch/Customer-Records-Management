package com.karthick.customerrecordsmanagement.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.config.Constants;
import com.karthick.customerrecordsmanagement.entity.CsvFileDetail;
import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.repository.CsvFileDetailRepository;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
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
public class KafkaMessageService {
    private CustomerRecordRepository customerRecordRepository;
    private CsvFileDetailRepository csvFileDetailRepository;
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Logger logger = Logger.getLogger(KafkaMessageService.class.getName());

    public void publishKafkaMessage(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(Constants.TOPIC, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                logger.severe("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
    }

    @KafkaListener(topics = Constants.TOPIC, groupId = Constants.GROUP_ID)
    public void uploadCsvFileDataToDb(String kafkaMessage) {
        String fileName = kafkaMessage.replace(Constants.KAFKA_MESSAGE_PREFIX, "");
        Optional<CsvFileDetail> file = csvFileDetailRepository.findByFileName(fileName);
        if (file.isPresent()) {
            String filePath = file.get().getFilePath();
            List<String[]> csvRecords = readCsvFile(filePath);
            if (csvRecords != null) {
                String[] headers = csvRecords.remove(0);
                for (String[] record : csvRecords) {
                    customerRecordRepository.save(mapTheArraysToCustomerRecord(headers, record));
                }
                if (!new File(filePath).delete()) {
                    logger.severe("Failed to delete the file");
                }
            } else {
                logger.warning(fileName + " file is empty");
            }
        } else {
            throw new NoSuchElementException("file path not found");
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
