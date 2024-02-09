package com.karthick.customerrecordsmanagement.kafka.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
public class FileUploadEvent {
    private long csvFileDetailId;
    private long fileUploadStatusId;
    private LocalDateTime timestamp;

    public FileUploadEvent(long csvFileDetailId, long fileUploadStatusId) {
        this.csvFileDetailId = csvFileDetailId;
        this.fileUploadStatusId = fileUploadStatusId;
        this.timestamp = LocalDateTime.now();
    }
}
