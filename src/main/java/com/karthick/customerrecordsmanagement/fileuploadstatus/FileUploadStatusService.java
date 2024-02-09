package com.karthick.customerrecordsmanagement.fileuploadstatus;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FileUploadStatusService {
    private FileUploadStatusRepository fileUploadStatusRepository;

    public List<FileUploadStatus> findAllFileUploadStatus() {
        return fileUploadStatusRepository.findAll();
    }

    public FileUploadStatus findFileUploadStatusById(long id) {
        Optional<FileUploadStatus> fileUploadStatus = fileUploadStatusRepository.findById(id);
        if (fileUploadStatus.isEmpty()) {
            throw new NoSuchElementException("The uploaded file status with the Id of " + id + " is not found");
        }
        return fileUploadStatus.get();
    }

    public FileUploadStatus createNewFileUploadStatus(String fileName) {
        return fileUploadStatusRepository.save(new FileUploadStatus(fileName));
    }

    public void updateFileUploadStatus(long fileUploadStatusId, int total, int uploaded, int duplicate, int invalid) {
        FileUploadStatus fileUploadStatus = findFileUploadStatusById(fileUploadStatusId);
        fileUploadStatus.setTotalRecords(total);
        fileUploadStatus.setUploadedRecords(uploaded);
        fileUploadStatus.setDuplicateRecords(duplicate);
        fileUploadStatus.setInvalidRecords(invalid);
        fileUploadStatusRepository.save(fileUploadStatus);
    }
}
