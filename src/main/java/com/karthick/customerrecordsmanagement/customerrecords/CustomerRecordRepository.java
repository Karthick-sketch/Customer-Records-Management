package com.karthick.customerrecordsmanagement.customerrecords;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
    Page<CustomerRecord> findByAccountId(long accountId, PageRequest pageRequest);

    Optional<CustomerRecord> findByIdAndAccountId(long id, long accountId);
}
