package com.karthick.customerrecordsmanagement.customerrecords;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.customerrecords.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.kafka.KafkaProducer;
import com.karthick.customerrecordsmanagement.kafka.config.Constants;
import com.karthick.customerrecordsmanagement.fileupload.csvfiledetail.CsvFileDetail;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import com.karthick.customerrecordsmanagement.fileupload.csvfiledetail.CsvFileDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;
    private CsvFileDetailRepository csvFileDetailRepository;
    private KafkaProducer kafkaProducer;

    private final Logger logger = Logger.getLogger(CustomerRecordService.class.getName());

    public Map<String, String> fetchCustomerRecordById(long id) throws IllegalAccessException {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findById(id);
        if (customerRecord.isPresent()) {
            return Stream.of(convertCustomerRecordToMap(customerRecord.get()), customFieldService.mapCustomFields(id))
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
    }

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
                kafkaProducer.publishKafkaMessage(csvFileDetail.getId(), csvFileDetail.getFileName());
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

    private Map<String, String> convertCustomerRecordToMap(CustomerRecord customerRecord) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(customerRecord, new TypeReference<>() {});
    }
}
