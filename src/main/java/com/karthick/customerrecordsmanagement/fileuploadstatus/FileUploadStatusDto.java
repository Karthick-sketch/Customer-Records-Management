package com.karthick.customerrecordsmanagement.fileuploadstatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileUploadStatusDto {
    private String response;
    private String uploadStatusUrl;

    public FileUploadStatusDto(long fileUploadStatusId) {
        this.response = "File uploaded successfully, your data will be processed in a moment";
        this.uploadStatusUrl = "/record/upload-status/" + fileUploadStatusId;
    }
}
