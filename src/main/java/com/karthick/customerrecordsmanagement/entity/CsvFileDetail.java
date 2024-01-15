package com.karthick.customerrecordsmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "csv_file_detail")
public class CsvFileDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    private String contentType;
    private String filePath;

    public CsvFileDetail() {}

    public CsvFileDetail(String fileName, String contentType, String filePath) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.filePath = filePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
