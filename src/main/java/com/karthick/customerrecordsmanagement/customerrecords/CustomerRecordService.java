package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatusDto;
import com.karthick.customerrecordsmanagement.csvfiledetail.CsvFileDetailService;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatus;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatusService;
import com.karthick.customerrecordsmanagement.kafka.KafkaProducer;
import com.karthick.customerrecordsmanagement.csvfiledetail.CsvFileDetail;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
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
    private FileUploadStatusService fileUploadStatusService;
    private CsvFileDetailService csvFileDetailService;
    private KafkaProducer kafkaProducer;

    private final Logger logger = Logger.getLogger(CustomerRecordService.class.getName());

    @Transactional
    public CustomerRecordDto createNewCustomerRecord(CustomerRecordDto customerRecordDto) {
        CustomerRecord customerRecord = customerRecordRepository.save(customerRecordDto.getDefaultFields());
        customFieldService.createCustomFields(customerRecord, customerRecordDto.getCustomFields());
        return customerRecordDto;
    }

    public List<CustomerRecordDto> fetchCustomerRecords(int offset, int limit) {
        List<CustomerRecord> customerRecords = customerRecordRepository.findAll(PageRequest.of(offset, limit)).stream().toList();
        return customerRecords.stream()
                .map(cr -> new CustomerRecordDto(cr, customFieldService.mapCustomFields(cr.getId())))
                .toList();
    }

    public CustomerRecordDto fetchCustomerRecordById(long id) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findById(id);
        if (customerRecord.isEmpty()) {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
        return new CustomerRecordDto(customerRecord.get(), customFieldService.mapCustomFields(id));
    }

    public List<CustomerRecordDto> fetchCustomerRecordByAccountId(long accountId) {
        List<CustomerRecord> customerRecords = customerRecordRepository.findByAccountId(accountId);
        return customerRecords.stream()
                .map(cr -> new CustomerRecordDto(cr, customFieldService.mapCustomFields(cr.getId())))
                .toList();
    }

    public FileUploadStatusDto uploadCsvFile(MultipartFile file) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new BadRequestException("Only CSV files are allowed");
        }
        String filePath = getFilePath(file.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(file.getBytes());
            CsvFileDetail csvFileDetail = csvFileDetailService.saveCsvFileDetail(file.getOriginalFilename(), file.getContentType(), filePath);
            FileUploadStatus fileUploadStatus = fileUploadStatusService.createNewFileUploadStatus(csvFileDetail.getFileName());
            kafkaProducer.publishKafkaMessage(csvFileDetail.getId(), fileUploadStatus.getId());
            logger.info(file.getOriginalFilename() + " file upload");
            return new FileUploadStatusDto(fileUploadStatus.getId());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    private String getFilePath(String fileName) {
        return System.getProperty("user.dir") + "/src/main/resources/uploads/" + fileName;
    }
}
