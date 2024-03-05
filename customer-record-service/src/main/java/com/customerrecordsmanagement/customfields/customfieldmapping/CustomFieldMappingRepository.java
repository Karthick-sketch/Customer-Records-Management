package com.customerrecordsmanagement.customfields.customfieldmapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldMappingRepository extends JpaRepository<CustomFieldMapping, Long> {
    List<CustomFieldMapping> findByAccountId(long accountId);

    @Query(value = "SELECT custom_field_name FROM custom_fields_mapping WHERE account_id = ?;", nativeQuery = true)
    List<String> findCustomFieldNameByAccountId(long accountId);
}
