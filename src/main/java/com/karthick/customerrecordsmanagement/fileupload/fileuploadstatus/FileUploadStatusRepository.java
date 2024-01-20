package com.karthick.customerrecordsmanagement.fileupload.fileuploadstatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadStatusRepository extends JpaRepository<FileUploadStatus, Long> {
}
