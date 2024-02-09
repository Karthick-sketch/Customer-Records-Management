package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customerrecords.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.kafka.KafkaProducer;
import com.karthick.customerrecordsmanagement.fileupload.csvfiledetail.CsvFileDetail;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import com.karthick.customerrecordsmanagement.fileupload.csvfiledetail.CsvFileDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;
    private CsvFileDetailRepository csvFileDetailRepository;
    private KafkaProducer kafkaProducer;

    private final Logger logger = Logger.getLogger(CustomerRecordService.class.getName());

    @Transactional
    public CustomerRecordDto createNewCustomerRecord(CustomerRecordDto customerRecordDto) {
        CustomerRecord customerRecord = customerRecordRepository.save(customerRecordDto.getDefaultFields());
        customFieldService.createCustomFields(customerRecord, customerRecordDto.getCustomFields());
        return customerRecordDto;
    }

    public List<CustomerRecordDto> fetchCustomerRecords(int offset, int limit) {
        List<CustomerRecord> defaultFields = customerRecordRepository.findAll(PageRequest.of(offset, limit)).stream().toList();
        List<Map<String, String>> customFields = customFieldService.mapCustomFields(offset, limit);

        List<CustomerRecordDto> customerRecords = new ArrayList<>();
        for (int i = 0; i < defaultFields.size() && i < customFields.size(); i++) {
            customerRecords.add(new CustomerRecordDto(defaultFields.get(i), customFields.get(i)));
        }
        return customerRecords;
    }

    public CustomerRecordDto fetchCustomerRecordById(long id) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findById(id);
        if (customerRecord.isPresent()) {
            return new CustomerRecordDto(customerRecord.get(), customFieldService.mapCustomFields(id));
        } else {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
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
}
