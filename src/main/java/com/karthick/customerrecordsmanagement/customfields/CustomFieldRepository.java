package com.karthick.customerrecordsmanagement.customfields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
    List<CustomField> findByAccountId(long accountId);

    Optional<CustomField> findByAccountIdAndFieldName(long accountId, String fieldName);
}
