package com.karthick.customerrecordsmanagement.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.entity.CsvFileDetail;
import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import com.karthick.customerrecordsmanagement.repository.CsvFileDetailRepository;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
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

    private final Logger logger = Logger.getLogger(CustomerRecord.class.getName());

    public List<CustomerRecord> getAllCustomerRecords() {
        return customerRecordRepository.findAll();
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
                logger.info(file.getOriginalFilename() + " file upload");
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        } else {
            throw new BadRequestException("Only CSV files are allowed");
        }
    }

    public void uploadCsvFileDataToDb(String fileName) {
        Optional<CsvFileDetail> file = csvFileDetailRepository.findByFileName(fileName);
        if (file.isPresent()) {
            String filePath = file.get().getFilePath();
            List<String[]> csvRecords = readCsvFile(filePath);
            String[] headers = csvRecords.remove(0);
            for (String[] record : csvRecords) {
                pushRecordsToDb(headers, record);
            }
            if (!new File(filePath).delete()) {
                System.out.println("Failed to delete the file");
            }
        } else {
            throw new NoSuchElementException("file path not found");
        }
    }

    private void pushRecordsToDb(String[] headers, String[] record) {
        Map<String, String> recordMap = new HashMap<>();
        for (int i = 0; i < headers.length && i < record.length; i++) {
            recordMap.put(headers[i], record[i]);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CustomerRecord customerRecord = objectMapper.convertValue(recordMap, CustomerRecord.class);
        customerRecordRepository.save(customerRecord);
    }

    private String getFilePath(String fileName) {
        return System.getProperty("user.dir") + "/src/main/resources/uploads/" + fileName;
    }

    private List<String[]> readCsvFile(String file) {
        List<String[]> splitData = null;
        try (FileReader filereader = new FileReader(file)) {
            try (CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(0).build()) {
                splitData = csvReader.readAll();
            }
        } catch (IOException | CsvException e) {
            System.out.println(e.getMessage());
        }
        return splitData;
    }
}
