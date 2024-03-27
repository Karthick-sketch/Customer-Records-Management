package com.customerrecordsmanagement.fileuploadstatus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileUploadStatusDTO {
    private String response;
    private String uploadStatusUrl;

    public FileUploadStatusDTO(long fileUploadStatusId) {
        this.response = "File uploaded successfully, your data will be processed in a moment";
        this.uploadStatusUrl = "/record/upload-status/" + fileUploadStatusId;
    }
}
