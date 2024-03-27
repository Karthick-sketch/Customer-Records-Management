package com.customerrecordsmanagement.fileuploadstatus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "file_upload_status")
public class FileUploadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long accountId;
    private String fileName;
    private int totalRecords;
    private int uploadedRecords;
    private int duplicateRecords;
    private int invalidRecords;
    private LocalDateTime uploadStartTime;
    private LocalDateTime uploadEndTime;

    public FileUploadStatus(long accountId, String fileName) {
        this.accountId = accountId;
        this.fileName = fileName;
        this.uploadStartTime = LocalDateTime.now();
    }
}
