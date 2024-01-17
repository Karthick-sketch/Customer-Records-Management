package com.karthick.customerrecordsmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "file_upload_status")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class FileUploadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String fileName;
    private boolean status = true;
    private int totalRecords = 0;
    private int uploadedRecords = 0;
    private int duplicateRecords = 0;
    private int invalidRecords = 0;
}
