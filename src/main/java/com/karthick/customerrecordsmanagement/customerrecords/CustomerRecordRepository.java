package com.karthick.customerrecordsmanagement.customerrecords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
    List<CustomerRecord> findByAccountId(long accountId);

    Optional<CustomerRecord> findByIdAndAccountId(long id, long accountId);
}
