package com.customerrecordsmanagement.customerrecords.repository;

import com.customerrecordsmanagement.customerrecords.entity.CustomerRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
    Optional<CustomerRecord> findByIdAndAccountId(long id, long accountId);

    Page<CustomerRecord> findByAccountId(long accountId, Pageable pageable);

    @Query(value = "SELECT * FROM customer_records cr WHERE cr.account_id = :accountId AND (cr.email LIKE %:search% OR cr.first_name LIKE %:search% OR cr.last_name LIKE %:search%)", nativeQuery = true)
    List<CustomerRecord> findByAccountIdAndSearchQuery(long accountId, String search);
}
