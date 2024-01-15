package com.karthick.customerrecordsmanagement.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerRecordService {
    @Autowired
    private CustomerRecordRepository customerRecordRepository;

    public List<CustomerRecord> getAllCustomerRecords() {
        return customerRecordRepository.findAll();
    }

    public CustomerRecord createNewCustomerRecord(CustomerRecord customerRecord) {
        return customerRecordRepository.save(customerRecord);
    }

    public void upload(String fileName) {
        List<String[]> csvRecords = readCsvFile(getFilePath(fileName));
        String[] headers = csvRecords.remove(0);
        for (String[] record : csvRecords) {
            pushRecordsToDb(headers, record);
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
        return System.getProperty("user.dir") + "/src/main/resources/sample/" + fileName;
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
