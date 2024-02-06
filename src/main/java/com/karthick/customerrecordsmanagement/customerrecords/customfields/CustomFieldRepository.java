package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
    List<CustomField> findByDefaultFieldId(long defaultFieldId);
}
