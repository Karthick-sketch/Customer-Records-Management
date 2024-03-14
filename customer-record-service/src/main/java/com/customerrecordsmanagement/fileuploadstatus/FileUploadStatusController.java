package com.customerrecordsmanagement.fileuploadstatus;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload-status/account")
@AllArgsConstructor
public class FileUploadStatusController {
    private FileUploadStatusService fileUploadStatusService;
    private JobLauncher jobLauncher;
    private Job job;

    @GetMapping("/{accountId}/all")
    public ResponseEntity<List<FileUploadStatus>> getAllFileUploadStatus(@PathVariable long accountId) {
        return new ResponseEntity<>(fileUploadStatusService.fetchFileUploadStatusByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("/{accountId}/id/{id}")
    public ResponseEntity<FileUploadStatus> getFileUploadStatusById(@PathVariable long accountId, long id) {
        return new ResponseEntity<>(fileUploadStatusService.fetchFileUploadStatusByIdAndAccountId(id, accountId), HttpStatus.OK);
    }

    @GetMapping("{accountId}/export-csv")
    public ResponseEntity<Resource> exportCsvFile(@PathVariable long accountId) {
        Resource resource = fileUploadStatusService.exportCsvFile(accountId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/{accountId}/upload-csv")
    public ResponseEntity<FileUploadStatusDTO> uploadCsvFile(@PathVariable long accountId, @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(fileUploadStatusService.uploadCsvFile(accountId, file), HttpStatus.OK);
    }

    @GetMapping("/startJob")
    public ResponseEntity<Resource> startBatch() throws Exception {
        System.out.println("batch started ...............");

        JobParameters jobParameter = new JobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameter);
        while (jobExecution.isRunning()) {
            System.out.println("......");
        }
        System.out.println(jobExecution.getStatus());

        Resource resource = new PathResource("/home/karthick/Documents/Development/Spring Boot/Customer-Records-Management/data/output.csv");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
