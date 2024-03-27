package com.customerrecordsmanagement.fileuploadstatus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "file_upload_status")
public class FileUploadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String fileName;
    private int totalRecords;
    private int uploadedRecords;
    private int duplicateRecords;
    private int invalidRecords;
    private LocalDateTime uploadStart;
    private LocalDateTime uploadEnd;
}
