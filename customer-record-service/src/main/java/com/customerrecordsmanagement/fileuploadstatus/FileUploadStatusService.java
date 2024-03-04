package com.customerrecordsmanagement.fileuploadstatus;

import com.customerrecordsmanagement.BadRequestException;
import com.customerrecordsmanagement.EntityNotException;
import com.customerrecordsmanagement.csvfiledetail.CsvFileDetail;
import com.customerrecordsmanagement.csvfiledetail.CsvFileDetailService;
import com.customerrecordsmanagement.fileuploadprocess.FileUploadEventKafkaProducer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FileUploadStatusService {
    private FileUploadStatusRepository fileUploadStatusRepository;
    private CsvFileDetailService csvFileDetailService;
    private FileUploadEventKafkaProducer kafkaProducer;

    private final Logger logger = Logger.getLogger(FileUploadStatusService.class.getName());

    public List<FileUploadStatus> fetchFileUploadStatusByAccountId(long accountId) {
        return fileUploadStatusRepository.findByAccountId(accountId);
    }

    public FileUploadStatus fetchFileUploadStatusByIdAndAccountId(long id, long accountId) {
        Optional<FileUploadStatus> fileUploadStatus = fileUploadStatusRepository.findByIdAndAccountId(id, accountId);
        if (fileUploadStatus.isEmpty()) {
            throw new EntityNotException("The uploaded file status with the Id of " + id + " is not found");
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
        String filePath = getFilePath(file.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(file.getBytes());
            CsvFileDetail csvFileDetail = csvFileDetailService.saveCsvFileDetail(accountId, file.getOriginalFilename(), filePath);
            FileUploadStatus fileUploadStatus = createNewFileUploadStatus(accountId, csvFileDetail.getFileName());
            kafkaProducer.publishKafkaMessage(accountId, csvFileDetail.getId(), fileUploadStatus.getId());
            logger.info(file.getOriginalFilename() + " file upload");
            return new FileUploadStatusDTO(fileUploadStatus.getId());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    private String getFilePath(String fileName) {
        return System.getProperty("user.dir") + "/src/main/resources/uploads/" + fileName;
    }
}
