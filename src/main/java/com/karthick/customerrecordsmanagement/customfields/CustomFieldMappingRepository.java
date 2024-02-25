package com.karthick.customerrecordsmanagement.customfields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldMappingRepository extends JpaRepository<CustomFieldMapping, Long> {
    List<CustomFieldMapping> findByAccountId(long accountId);

    Optional<CustomFieldMapping> findByAccountIdAndColumnName(long accountId, String columnName);
}
