package com.karthick.customerrecordsmanagement.fileupload.csvfiledetail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "csv_file_details")
@Data
@NoArgsConstructor
public class CsvFileDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    private String contentType;
    private String filePath;

    public CsvFileDetail(String fileName, String contentType, String filePath) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.filePath = filePath;
    }
}
