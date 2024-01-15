package com.karthick.customerrecordsmanagement.controller;

import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import com.karthick.customerrecordsmanagement.service.CustomerRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/record")
@AllArgsConstructor
public class CustomerRecordsController {
    private CustomerRecordService customerRecordService;

    @GetMapping("/all")
    public ResponseEntity<List<CustomerRecord>> getCustomerRecords() {
        return new ResponseEntity<>(customerRecordService.getAllCustomerRecords(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerRecord> createCustomerRecord(@RequestBody CustomerRecord customerRecord) {
        return new ResponseEntity<>(customerRecordService.createNewCustomerRecord(customerRecord), HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestPart("file") MultipartFile file) {
        customerRecordService.uploadCsvFile(file);
        customerRecordService.uploadCsvFileDataToDb(file.getOriginalFilename());
        return new ResponseEntity<>("File uploaded successfully!", HttpStatus.OK);
    }
}
