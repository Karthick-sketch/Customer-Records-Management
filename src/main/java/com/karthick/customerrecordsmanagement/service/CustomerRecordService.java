package com.karthick.customerrecordsmanagement.service;

import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

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
        List<String[]> splitData = readCsvFile(getFilePath(fileName));
        String[] titles = splitData.remove(0);
        for (String[] record : splitData) {
            pushRecordsToDb(titles, record);
        }
    }

    private void pushRecordsToDb(String[] titles, String[] record) {
        CustomerRecord customerRecord = new CustomerRecord();
        for (int i = 0; i < titles.length; i++) {
            switch (titles[i]) {
                case "first_name":
                    customerRecord.setFirstName(record[i]);
                    break;
                case "last_name":
                    customerRecord.setLastName(record[i]);
                    break;
                case "email":
                    customerRecord.setEmail(record[i]);
                    break;
                case "phone_number":
                    customerRecord.setPhoneNumber(record[i]);
                    break;
                case "company_name":
                    customerRecord.setCompanyName(record[i]);
                    break;
                case "address":
                    customerRecord.setAddress(record[i]);
                    break;
                case "city":
                    customerRecord.setCity(record[i]);
                    break;
                case "state":
                    customerRecord.setState(record[i]);
                    break;
                case "country":
                    customerRecord.setCountry(record[i]);
                    break;
                case "zipcode":
                    customerRecord.setZipcode(Integer.parseInt(record[i]));
                    break;
            }
        }
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
