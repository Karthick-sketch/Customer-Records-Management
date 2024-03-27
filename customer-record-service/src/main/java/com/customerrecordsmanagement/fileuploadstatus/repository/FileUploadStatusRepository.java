package com.customerrecordsmanagement.fileuploadstatus.repository;

import com.customerrecordsmanagement.fileuploadstatus.entity.FileUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadStatusRepository extends JpaRepository<FileUploadStatus, Long> {
    List<FileUploadStatus> findByAccountId(long accountId);

    Optional<FileUploadStatus> findByIdAndAccountId(long id, long accountId);
}
