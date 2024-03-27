package com.customerrecordsmanagement.csvfiledetail.service;

import com.customerrecordsmanagement.EntityNotFoundException;
import com.customerrecordsmanagement.csvfiledetail.entity.CsvFileDetail;
import com.customerrecordsmanagement.csvfiledetail.repository.CsvFileDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CsvFileDetailService {
    private CsvFileDetailRepository csvFileDetailRepository;

    // added unit test
    public CsvFileDetail fetchCsvFileDetailByIdAndAccountId(long id, long accountId) {
        Optional<CsvFileDetail> csvFileDetail = csvFileDetailRepository.findByIdAndAccountId(id, accountId);
        if (csvFileDetail.isEmpty()) {
            throw new EntityNotFoundException("file not found");
        }
        return csvFileDetail.get();
    }

    public CsvFileDetail saveCsvFileDetail(CsvFileDetail csvFileDetail) {
        return csvFileDetailRepository.save(csvFileDetail);
    }

    // added unit test
    public CsvFileDetail createCsvFileDetail(long accountId, String fileName, String filePath) {
        return saveCsvFileDetail(new CsvFileDetail(accountId, fileName, filePath));
    }
}
