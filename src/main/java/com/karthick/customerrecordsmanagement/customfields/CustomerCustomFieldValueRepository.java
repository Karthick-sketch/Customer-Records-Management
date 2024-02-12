package com.karthick.customerrecordsmanagement.customfields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCustomFieldValueRepository extends JpaRepository<CustomerCustomFieldValue, Long> {
    CustomerCustomFieldValue findByCustomerRecordIdAndCustomFieldId(long customerRecordId, long customFieldId);
}
