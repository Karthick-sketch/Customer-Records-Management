package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatusDto;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatus;
import com.karthick.customerrecordsmanagement.fileuploadstatus.FileUploadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/record")
@AllArgsConstructor
public class CustomerRecordController {
    private CustomerRecordService customerRecordService;
    private FileUploadStatusService fileUploadStatusService;

    @GetMapping
    public ResponseEntity<List<CustomerRecordDto>> getCustomerRecordsWithPagination(@RequestParam int offset, int limit) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecords(offset, limit), HttpStatus.OK);
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

    @PostMapping("/upload-csv")
    public ResponseEntity<FileUploadStatusDto> uploadCsvFile(@RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(customerRecordService.uploadCsvFile(file), HttpStatus.OK);
    }

    @GetMapping("/upload-status/all")
    public ResponseEntity<List<FileUploadStatus>> getAllFileUploadStatus() {
        return new ResponseEntity<>(fileUploadStatusService.findAllFileUploadStatus(), HttpStatus.OK);
    }

    @GetMapping("/upload-status/{id}")
    public ResponseEntity<FileUploadStatus> getFileUploadStatusById(@PathVariable long id) {
        return new ResponseEntity<>(fileUploadStatusService.findFileUploadStatusById(id), HttpStatus.OK);
    }
}
