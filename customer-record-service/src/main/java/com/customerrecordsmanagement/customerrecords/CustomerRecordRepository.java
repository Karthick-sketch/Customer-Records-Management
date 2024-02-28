package com.customerrecordsmanagement.customerrecords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
    Optional<CustomerRecord> findByIdAndAccountId(long id, long accountId);
}
