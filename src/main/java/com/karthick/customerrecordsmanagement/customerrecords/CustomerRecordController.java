package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.fileupload.fileuploadstatus.FileUploadStatus;
import com.karthick.customerrecordsmanagement.fileupload.FileProcessService;
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
    private FileProcessService fileProcessService;

    @GetMapping
    public ResponseEntity<List<CustomerRecordDto>> getCustomerRecordsWithPagination(@RequestParam int offset, int limit) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecords(offset, limit), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRecordDto> getCustomerRecordById(@PathVariable long id) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerRecordDto> createCustomerRecord(@RequestBody CustomerRecordDto customerRecordDto) {
        return new ResponseEntity<>(customerRecordService.createNewCustomerRecord(customerRecordDto), HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestPart("file") MultipartFile file) {
        customerRecordService.uploadCsvFile(file);
        return new ResponseEntity<>("File uploaded successfully, your data will be processed in a moment", HttpStatus.OK);
    }

    @GetMapping("/upload-status/all")
    public ResponseEntity<List<FileUploadStatus>> getAllFileUploadStatus() {
        return new ResponseEntity<>(fileProcessService.findAllFileUploadStatus(), HttpStatus.OK);
    }

    @GetMapping("/upload-status/{id}")
    public ResponseEntity<FileUploadStatus> getFileUploadStatusById(@PathVariable long id) {
        return new ResponseEntity<>(fileProcessService.findFileUploadStatusById(id), HttpStatus.OK);
    }
}
