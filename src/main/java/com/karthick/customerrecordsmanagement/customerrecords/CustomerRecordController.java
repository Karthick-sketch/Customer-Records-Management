package com.karthick.customerrecordsmanagement.customerrecords;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-records")
@AllArgsConstructor
public class CustomerRecordController {
    private CustomerRecordService customerRecordService;

    @GetMapping
    public ResponseEntity<List<CustomerRecordDto>> getCustomerRecordsWithPagination(@RequestParam int pageNumber, int pageSize) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecords(pageNumber-1, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRecordDto> getCustomerRecordById(@PathVariable long id) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordById(id), HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomerRecordDto>> getCustomerRecordByAccountId(@PathVariable long accountId) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordByAccountId(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerRecordDto> createCustomerRecord(@RequestBody CustomerRecordDto customerRecordDto) {
        return new ResponseEntity<>(customerRecordService.createNewCustomerRecord(customerRecordDto), HttpStatus.CREATED);
    }
}
