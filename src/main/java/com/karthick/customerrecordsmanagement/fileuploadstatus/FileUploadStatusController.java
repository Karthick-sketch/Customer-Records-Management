package com.karthick.customerrecordsmanagement.fileuploadstatus;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload-status/account")
@AllArgsConstructor
public class FileUploadStatusController {
    private FileUploadStatusService fileUploadStatusService;

    @GetMapping("/{accountId}/all")
    public ResponseEntity<List<FileUploadStatus>> getAllFileUploadStatus(@PathVariable long accountId) {
        return new ResponseEntity<>(fileUploadStatusService.fetchFileUploadStatusByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("/{accountId}/id/{id}")
    public ResponseEntity<FileUploadStatus> getFileUploadStatusById(@PathVariable long accountId, long id) {
        return new ResponseEntity<>(fileUploadStatusService.fetchFileUploadStatusByIdAndAccountId(id, accountId), HttpStatus.OK);
    }

    @PostMapping("/{accountId}/upload-csv")
    public ResponseEntity<FileUploadStatusDto> uploadCsvFile(@PathVariable long accountId, @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(fileUploadStatusService.uploadCsvFile(accountId, file), HttpStatus.OK);
    }
}
