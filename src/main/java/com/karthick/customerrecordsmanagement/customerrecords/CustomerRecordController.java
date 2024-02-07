package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.fileupload.fileuploadstatus.FileUploadStatus;
import com.karthick.customerrecordsmanagement.fileupload.FileProcessService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/record")
@AllArgsConstructor
public class CustomerRecordController {
    private CustomerRecordService customerRecordService;
    private FileProcessService fileProcessService;

    @GetMapping
    public ResponseEntity<Page<CustomerRecord>> getCustomerRecordsWithPagination(@RequestParam int offset, int limit) {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordsWithPagination(offset, limit), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getCustomerRecordById(@PathVariable long id) throws IllegalAccessException {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecordById(id), HttpStatus.OK);
    }

    @GetMapping("/fields")
    public ResponseEntity<List<Map<String, String>>> getAllCustomerRecordFields() throws IllegalAccessException {
        return new ResponseEntity<>(customerRecordService.fetchCustomerRecords(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerRecord> createCustomerRecord(@RequestBody CustomerRecord customerRecord) {
        return new ResponseEntity<>(customerRecordService.createNewCustomerRecord(customerRecord), HttpStatus.CREATED);
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
