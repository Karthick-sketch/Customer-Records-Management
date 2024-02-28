package com.customerrecordsmanagement.customfieldmapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldMappingRepository extends JpaRepository<CustomFieldMapping, Long> {
    List<CustomFieldMapping> findByAccountId(long accountId);
}
