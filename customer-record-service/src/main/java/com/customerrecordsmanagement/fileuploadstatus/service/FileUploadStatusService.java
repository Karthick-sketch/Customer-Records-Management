package com.customerrecordsmanagement.fileuploadstatus.service;

import com.customerrecordsmanagement.BadRequestException;
import com.customerrecordsmanagement.EntityNotFoundException;
import com.customerrecordsmanagement.csvfiledetail.entity.CsvFileDetail;
import com.customerrecordsmanagement.csvfiledetail.service.CsvFileDetailService;
import com.customerrecordsmanagement.fileuploadstatus.fileprocess.FileUploadEventKafkaProducer;
import com.customerrecordsmanagement.fileuploadstatus.entity.FileUploadStatus;
import com.customerrecordsmanagement.fileuploadstatus.dto.FileUploadStatusDTO;
import com.customerrecordsmanagement.fileuploadstatus.repository.FileUploadStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FileUploadStatusService {
    private FileUploadStatusRepository fileUploadStatusRepository;
    private CsvFileDetailService csvFileDetailService;
    private FileUploadEventKafkaProducer fileUploadEventKafkaProducer;

    private final Logger logger = Logger.getLogger(FileUploadStatusService.class.getName());

    public List<FileUploadStatus> fetchFileUploadStatusByAccountId(long accountId) {
        return fileUploadStatusRepository.findByAccountId(accountId);
    }

    public FileUploadStatus fetchFileUploadStatusByIdAndAccountId(long id, long accountId) {
        Optional<FileUploadStatus> fileUploadStatus = fileUploadStatusRepository.findByIdAndAccountId(id, accountId);
        if (fileUploadStatus.isEmpty()) {
            throw new EntityNotFoundException("The uploaded file status with the Id of " + id + " is not found");
        }
        return fileUploadStatus.get();
    }

    public FileUploadStatus createNewFileUploadStatus(long accountId, String fileName) {
        FileUploadStatus fileUploadStatus = new FileUploadStatus(accountId, fileName);
        fileUploadStatus.setUploadStart(LocalDateTime.now());
        return fileUploadStatusRepository.save(fileUploadStatus);
    }

    public void updateFileUploadStatus(long accountId, long fileUploadStatusId, int total, int uploaded, int duplicate, int invalid) {
        FileUploadStatus fileUploadStatus = fetchFileUploadStatusByIdAndAccountId(fileUploadStatusId, accountId);
        fileUploadStatus.setTotalRecords(total);
        fileUploadStatus.setUploadedRecords(uploaded);
        fileUploadStatus.setDuplicateRecords(duplicate);
        fileUploadStatus.setInvalidRecords(invalid);
        fileUploadStatus.setUploadEnd(LocalDateTime.now());
        fileUploadStatusRepository.save(fileUploadStatus);
    }

    public FileUploadStatusDTO uploadCsvFile(long accountId, MultipartFile file) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new BadRequestException("Only CSV files are allowed");
        }
        String filePath = getFilePath(file.getOriginalFilename(), true);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(file.getBytes());
            CsvFileDetail csvFileDetail = csvFileDetailService.saveCsvFileDetail(accountId, file.getOriginalFilename(), filePath);
            FileUploadStatus fileUploadStatus = createNewFileUploadStatus(accountId, csvFileDetail.getFileName());
            fileUploadEventKafkaProducer.publishKafkaMessage(accountId, csvFileDetail.getId(), fileUploadStatus.getId());
            logger.info(file.getOriginalFilename() + " file uploaded");
            return new FileUploadStatusDTO(fileUploadStatus.getId());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    public String getFilePath(String fileName, boolean isUpload) {
        return System.getProperty("user.dir") + "/src/main/resources/" + (isUpload ? "uploads/" : "exports/") + fileName;
    }
}
