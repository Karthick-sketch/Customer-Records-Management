package com.customerrecordsmanagement;

import com.customerrecordsmanagement.csvfiledetail.entity.CsvFileDetail;
import com.customerrecordsmanagement.csvfiledetail.repository.CsvFileDetailRepository;
import com.customerrecordsmanagement.csvfiledetail.service.CsvFileDetailService;
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
public class CsvFileDetailServiceTest {
    @Mock
    private CsvFileDetailRepository csvFileDetailRepository;
    @InjectMocks
    private CsvFileDetailService csvFileDetailService;

    @Test
    public void testFetchCustomerRecordByIdAndAccountId() {
        long id = 1, accountId = 1;
        CsvFileDetail mockCsvFileDetail = MockObjects.getCsvFileDetail();
        Mockito.when(csvFileDetailRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCsvFileDetail));

        CsvFileDetail validCsvFileDetail = csvFileDetailService.fetchCsvFileDetailByIdAndAccountId(id, accountId);
        Executable invalidId = () -> csvFileDetailService.fetchCsvFileDetailByIdAndAccountId(2, accountId);
        Executable invalidAccountId = () -> csvFileDetailService.fetchCsvFileDetailByIdAndAccountId(id, 2);

        Assertions.assertEquals(mockCsvFileDetail, validCsvFileDetail);
        Assertions.assertThrows(EntityNotFoundException.class, invalidId);
        Assertions.assertThrows(EntityNotFoundException.class, invalidAccountId);
    }

    @Test
    public void testCreateCsvFileDetail() {
        CsvFileDetail mockCsvFileDetail = MockObjects.getCsvFileDetail();
        Mockito.when(csvFileDetailRepository.save(mockCsvFileDetail)).thenReturn(mockCsvFileDetail);
        CsvFileDetail validCsvFileDetail = csvFileDetailService.createCsvFileDetail(mockCsvFileDetail.getAccountId(), mockCsvFileDetail.getFileName(), mockCsvFileDetail.getFilePath());
        Assertions.assertEquals(mockCsvFileDetail, validCsvFileDetail);
        Mockito.verify(csvFileDetailRepository, Mockito.times(1)).save(mockCsvFileDetail);
    }
}
