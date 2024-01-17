package com.karthick.customerrecordsmanagement.repository;

import com.karthick.customerrecordsmanagement.entity.CsvFileDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvFileDetailRepository extends JpaRepository<CsvFileDetail, Long> {
}
