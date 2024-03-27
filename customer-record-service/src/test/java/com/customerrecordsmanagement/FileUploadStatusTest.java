package com.customerrecordsmanagement;

import com.customerrecordsmanagement.csvfiledetail.service.CsvFileDetailService;
import com.customerrecordsmanagement.fileuploadstatus.entity.FileUploadStatus;
import com.customerrecordsmanagement.fileuploadstatus.fileprocess.FileUploadEventKafkaProducer;
import com.customerrecordsmanagement.fileuploadstatus.repository.FileUploadStatusRepository;
import com.customerrecordsmanagement.fileuploadstatus.service.FileUploadStatusService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FileUploadStatusTest {
    @Mock
    private FileUploadStatusRepository fileUploadStatusRepository;
    @Mock
    private CsvFileDetailService csvFileDetailService;
    @Mock
    private FileUploadEventKafkaProducer fileUploadEventKafkaProducer;

    @InjectMocks
    private FileUploadStatusService fileUploadStatusService;

    @Test
    public void testFetchFileUploadStatusByIdAndAccountId() {
        long id = 1, accountId = 1;
        FileUploadStatus mockFileUploadStatus = MockObjects.getFileUploadStatus();
        Mockito.when(fileUploadStatusRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockFileUploadStatus));
        FileUploadStatus validFileUploadStatus = fileUploadStatusService.fetchFileUploadStatusByIdAndAccountId(id, accountId);
        Executable invalidId = () -> fileUploadStatusService.fetchFileUploadStatusByIdAndAccountId(2, accountId);
        Executable invalidAccountId = () -> fileUploadStatusService.fetchFileUploadStatusByIdAndAccountId(id, 2);
        Assertions.assertEquals(mockFileUploadStatus, validFileUploadStatus);
        Assertions.assertThrows(EntityNotFoundException.class, invalidId);
        Assertions.assertThrows(EntityNotFoundException.class, invalidAccountId);
    }
}
