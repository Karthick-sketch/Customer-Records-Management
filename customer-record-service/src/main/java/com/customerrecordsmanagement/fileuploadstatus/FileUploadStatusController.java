package com.customerrecordsmanagement.fileuploadstatus;

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
@RequestMapping("/upload-status/account")
public class FileUploadStatusController {
    private final FileUploadStatusService fileUploadStatusService;
    private final JobLauncher jobLauncher;
    private final Job job;

    public FileUploadStatusController(FileUploadStatusService fileUploadStatusService, @Qualifier("jobLauncher") JobLauncher jobLauncher, @Qualifier("customerRecordJob") Job job) {
        this.fileUploadStatusService = fileUploadStatusService;
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

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

    @GetMapping("/{accountId}/startJob")
    public ResponseEntity<Resource> startBatch(@PathVariable long accountId) throws Exception {
        File file = new File("src/main/resources/exports/" + accountId + "-customer-records-" + LocalDateTime.now() + ".csv");

        System.out.println("Batch started ............");
        JobParameters jobParameter = new JobParametersBuilder()
                .addLong("accountId", accountId)
                .addString("filePath", file.getAbsolutePath())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameter);
        while (jobExecution.isRunning()) {
            System.out.println("......");
        }
        System.out.println("Batch status : " + jobExecution.getStatus());

        Resource resource = new PathResource(file.getAbsolutePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
