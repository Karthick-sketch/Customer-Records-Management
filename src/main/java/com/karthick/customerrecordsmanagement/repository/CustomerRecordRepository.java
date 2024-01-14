package com.karthick.customerrecordsmanagement.repository;

import com.karthick.customerrecordsmanagement.entity.CustomerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
}
