package com.customerrecordsmanagement.csvfiledetail;

import com.customerrecordsmanagement.EntityNotException;
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
            throw new EntityNotException("file not found");
        }
        return csvFileDetail.get();
    }

    public CsvFileDetail saveCsvFileDetail(long accountId, String fileName, String filePath) {
        return csvFileDetailRepository.save(new CsvFileDetail(accountId, fileName, filePath));
    }
}
