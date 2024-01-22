package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
//    List<CustomField> findByAccountId(long accountId);

    @Query(value = "SELECT :columnName FROM custom_fields;", nativeQuery = true)
    List<CustomField> findByColumnName(String columnName);
}
