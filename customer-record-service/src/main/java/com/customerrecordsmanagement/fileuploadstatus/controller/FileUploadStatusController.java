package com.customerrecordsmanagement.fileuploadstatus.controller;

import com.customerrecordsmanagement.fileuploadstatus.entity.FileUploadStatus;
import com.customerrecordsmanagement.fileuploadstatus.dto.FileUploadStatusDTO;
import com.customerrecordsmanagement.fileuploadstatus.service.FileUploadStatusService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/upload-status/account/{accountId}")
public class FileUploadStatusController {
    private final JobLauncher exportJobLauncher;
    private final JobLauncher importJobLauncher;
    private final Job exportJob;
    private final Job importJob;
    private final FileUploadStatusService fileUploadStatusService;

    public FileUploadStatusController(@Qualifier("batchExportJobLauncher") JobLauncher exportJobLauncher,
            @Qualifier("customerRecordExportJob") Job exportJob,
            @Qualifier("batchImportJobLauncher") JobLauncher importJobLauncher,
            @Qualifier("customerRecordImportJob") Job importJob, FileUploadStatusService fileUploadStatusService) {
        this.fileUploadStatusService = fileUploadStatusService;
        this.exportJobLauncher = exportJobLauncher;
        this.exportJob = exportJob;
        this.importJobLauncher = importJobLauncher;
        this.importJob = importJob;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FileUploadStatus>> getAllFileUploadStatus(@PathVariable long accountId) {
        return new ResponseEntity<>(fileUploadStatusService.fetchFileUploadStatusByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<FileUploadStatus> getFileUploadStatusById(@PathVariable long accountId, long id) {
        return new ResponseEntity<>(fileUploadStatusService.fetchFileUploadStatusByIdAndAccountId(id, accountId), HttpStatus.OK);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<FileUploadStatusDTO> uploadCsvFile(@PathVariable long accountId, @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(fileUploadStatusService.uploadCsvFile(accountId, file), HttpStatus.OK);
    }

    @GetMapping("/export-csv")
    public ResponseEntity<Resource> startExportJob(@PathVariable long accountId) throws Exception {
        File file = new File("src/main/resources/exports/" + accountId + "-customer-records-" + LocalDateTime.now() + ".csv");

        System.out.println("Export batch started ............");
        JobParameters jobParameter = new JobParametersBuilder()
                .addLong("accountId", accountId)
                .addString("filePath", file.getAbsolutePath())
                .toJobParameters();

        JobExecution jobExecution = exportJobLauncher.run(exportJob, jobParameter);
        while (jobExecution.isRunning()) {
            System.out.println("......");
        }
        System.out.println("Export batch status : " + jobExecution.getStatus());

        Resource resource = new PathResource(file.getAbsolutePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/startImportJob")
    public ResponseEntity<HttpStatus> startImportJob(@PathVariable long accountId) throws Exception {
        String filePath = fileUploadStatusService.getFilePath("1-contact.csv", true);

        System.out.println("Import batch started ............");
        JobParameters jobParameter = new JobParametersBuilder()
                .addLong("accountId", accountId)
                .addString("filePath", filePath)
                .toJobParameters();

        JobExecution jobExecution = importJobLauncher.run(importJob, jobParameter);
        while (jobExecution.isRunning()) {
            System.out.println("......");
        }
        System.out.println("Import batch status : " + jobExecution.getStatus());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
