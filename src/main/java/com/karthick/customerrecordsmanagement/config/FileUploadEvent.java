package com.karthick.customerrecordsmanagement.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
public class FileUploadEvent {
    private long fileId;
    private String fileName;
    private LocalDateTime timestamp;

    public FileUploadEvent(long fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.timestamp = LocalDateTime.now();
    }
}
