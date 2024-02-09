package com.karthick.customerrecordsmanagement.fileupload.fileuploadstatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "file_upload_status")
@Data
@NoArgsConstructor
public class FileUploadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    private int totalRecords;
    private int uploadedRecords;
    private int duplicateRecords;
    private int invalidRecords;

    public FileUploadStatus(String fileName, int total, int uploaded, int duplicate, int invalid) {
        this.fileName = fileName;
        this.totalRecords = total;
        this.uploadedRecords = uploaded;
        this.duplicateRecords = duplicate;
        this.invalidRecords = invalid;
    }
}
