package com.customerrecordsmanagement.customfields.repository;

import com.customerrecordsmanagement.customfields.entity.CustomField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
    Optional<CustomField> findByCustomerRecordId(long customerRecordId);
}
