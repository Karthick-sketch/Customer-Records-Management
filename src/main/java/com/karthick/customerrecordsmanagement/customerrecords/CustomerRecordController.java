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

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomerRecordDTO>> getCustomerRecordsWithPagination(@PathVariable long accountId, @RequestParam int pageNumber, int pageSize) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecords(accountId, pageNumber-1, pageSize), HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}/id/{id}")
    public ResponseEntity<CustomerRecordDTO> getCustomerRecordById(@PathVariable long accountId, @PathVariable long id) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordByIdAndAccountId(id, accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerRecordDTO> createCustomerRecord(@RequestBody CustomerRecordDTO customerRecordDTO) {
        return new ResponseEntity<>(customerRecordService.createNewCustomerRecord(customerRecordDTO), HttpStatus.CREATED);
    }
}
