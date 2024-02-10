package com.karthick.customerrecordsmanagement.customerrecords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
    List<CustomerRecord> findByAccountId(long accountId);
}
