package com.customerrecordsmanagement.customerrecords;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer-records")
@AllArgsConstructor
public class CustomerRecordController {
    private CustomerRecordService customerRecordService;

    @PostMapping("/authorization/generate/account/{accountId}")
    public ResponseEntity<String> generateAuthCode(@PathVariable long accountId) {
        return new ResponseEntity<>(customerRecordService.generateBase64Code(accountId), HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomerRecordDTO>> getCustomerRecordsWithPagination(@PathVariable long accountId, @RequestParam int pageNumber, int pageSize) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecords(accountId, pageNumber-1, pageSize), HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}/id/{id}")
    public ResponseEntity<CustomerRecordDTO> getCustomerRecordById(@PathVariable long accountId, @PathVariable long id) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordAndCustomFieldsByIdAndAccountId(id, accountId), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<CustomerRecordDTO> createCustomerRecord(@RequestBody CustomerRecordDTO customerRecordDTO) {
        return new ResponseEntity<>(customerRecordService.createCustomerRecordByDTO(customerRecordDTO), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<CustomerRecordDTO> createCustomerRecord(@RequestHeader("Authorization") String base64Code, @RequestBody Map<String, String> customerRecordMap) {
        return new ResponseEntity<>(customerRecordService.createCustomerRecordFromMap(base64Code, customerRecordMap), HttpStatus.CREATED);
    }

    @PatchMapping("/account/{accountId}/id/{id}")
    public ResponseEntity<CustomerRecordDTO> updateCustomerRecordById(@PathVariable long accountId, @PathVariable long id, @RequestBody Map<String, String> customerRecordUpdate) {
        return new ResponseEntity<>(customerRecordService.updateCustomerRecord(id, accountId, customerRecordUpdate), HttpStatus.OK);
    }

    @DeleteMapping("/account/{accountId}/id/{id}")
    public ResponseEntity<HttpStatus> deleteCustomerRecordById(@PathVariable long accountId, @PathVariable long id) {
        customerRecordService.deleteCustomerRecordById(id, accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
