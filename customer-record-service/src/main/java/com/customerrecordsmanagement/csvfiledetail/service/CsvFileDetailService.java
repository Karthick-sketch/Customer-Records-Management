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

    public CsvFileDetail fetchCsvFileDetailById(long id) {
        Optional<CsvFileDetail> csvFileDetail = csvFileDetailRepository.findById(id);
        if (csvFileDetail.isEmpty()) {
            throw new EntityNotFoundException("file not found");
        }
        return csvFileDetail.get();
    }

    public CsvFileDetail saveCsvFileDetail(long accountId, String fileName, String filePath) {
        return csvFileDetailRepository.save(new CsvFileDetail(accountId, fileName, filePath));
    }
}
