package com.karthick.customerrecordsmanagement.repository;

import com.karthick.customerrecordsmanagement.entity.FileUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadStatusRepository extends JpaRepository<FileUploadStatus, Long> {
}
