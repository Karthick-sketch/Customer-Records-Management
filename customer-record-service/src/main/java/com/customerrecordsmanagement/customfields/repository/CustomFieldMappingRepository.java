package com.customerrecordsmanagement.customfields.repository;

import com.customerrecordsmanagement.customfields.entity.CustomFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldMappingRepository extends JpaRepository<CustomFieldMapping, Long> {
    List<CustomFieldMapping> findByAccountId(long accountId);

    Optional<CustomFieldMapping> findByIdAndAccountId(long id, long accountId);

    @Query(value = "SELECT custom_field_name FROM custom_fields_mapping WHERE account_id = ? ORDER BY id;", nativeQuery = true)
    List<String> findCustomFieldNamesByAccountId(long accountId);
}
