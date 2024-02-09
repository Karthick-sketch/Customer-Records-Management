package com.karthick.customerrecordsmanagement.csvfiledetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvFileDetailRepository extends JpaRepository<CsvFileDetail, Long> {
}
