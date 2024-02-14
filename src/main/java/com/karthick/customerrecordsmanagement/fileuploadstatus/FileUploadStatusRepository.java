package com.karthick.customerrecordsmanagement.fileuploadstatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadStatusRepository extends JpaRepository<FileUploadStatus, Long> {
    List<FileUploadStatus> findFileUploadStatusByAccountId(long accountId);

    Optional<FileUploadStatus> findFileUploadStatusByIdAndAccountId(long id, long accountId);
}
