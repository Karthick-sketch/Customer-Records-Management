package com.karthick.customerrecordsmanagement.controller;

import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.service.CustomerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/record")
public class CustomerRecordsController {
    @Autowired
    private CustomerRecordService customerRecordService;

    @GetMapping("/all")
    public ResponseEntity<List<CustomerRecord>> getCustomerRecords() {
        return new ResponseEntity<>(customerRecordService.getAllCustomerRecords(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerRecord> createCustomerRecord(@RequestBody CustomerRecord customerRecord) {
        return new ResponseEntity<>(customerRecordService.createNewCustomerRecord(customerRecord), HttpStatus.CREATED);
    }
}
