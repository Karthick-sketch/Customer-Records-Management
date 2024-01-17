package com.karthick.customerrecordsmanagement.service;

import com.karthick.customerrecordsmanagement.config.Constants;
import com.karthick.customerrecordsmanagement.entity.CsvFileDetail;
import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import com.karthick.customerrecordsmanagement.repository.CsvFileDetailRepository;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CsvFileDetailRepository csvFileDetailRepository;
    private KafkaMessageService kafkaMessageService;

    private final Logger logger = Logger.getLogger(CustomerRecordService.class.getName());

    public Page<CustomerRecord> fetchCustomerRecordsWithPagination(int offset, int limit) {
        return customerRecordRepository.findAll(PageRequest.of(offset, limit).withSort(Sort.by(Constants.ORDER_BY_EMAIL)));
    }

    public CustomerRecord createNewCustomerRecord(CustomerRecord customerRecord) {
        return customerRecordRepository.save(customerRecord);
    }

    public void uploadCsvFile(MultipartFile file) {
        if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            String filePath = getFilePath(file.getOriginalFilename());
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                fileOutputStream.write(file.getBytes());
                CsvFileDetail csvFileDetail = new CsvFileDetail(file.getOriginalFilename(), file.getContentType(), filePath);
                csvFileDetailRepository.save(csvFileDetail);
                kafkaMessageService.publishKafkaMessage(Constants.KAFKA_MESSAGE_PREFIX + file.getOriginalFilename());
                logger.info(file.getOriginalFilename() + " file upload");
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        } else {
            throw new BadRequestException("Only CSV files are allowed");
        }
    }

    private String getFilePath(String fileName) {
        return System.getProperty("user.dir") + "/src/main/resources/uploads/" + fileName;
    }
}
