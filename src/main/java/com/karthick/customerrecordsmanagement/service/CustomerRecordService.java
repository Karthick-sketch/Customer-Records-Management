package com.karthick.customerrecordsmanagement.service;

import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.repository.CustomerRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
