package com.customerrecordsmanagement.csvfiledetail.repository;

import com.customerrecordsmanagement.csvfiledetail.entity.CsvFileDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CsvFileDetailRepository extends JpaRepository<CsvFileDetail, Long> {
    Optional<CsvFileDetail> findByIdAndAccountId(long id, long accountId);
}
