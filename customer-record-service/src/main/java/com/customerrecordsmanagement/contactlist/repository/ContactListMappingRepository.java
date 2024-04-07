package com.customerrecordsmanagement.contactlist.repository;

import com.customerrecordsmanagement.contactlist.entity.ContactListMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactListMappingRepository extends JpaRepository<ContactListMapping, Long> {
    List<ContactListMapping> findByAccountIdAndContactListId(long accountId, long listId);

    @Query(value = "SELECT customer_record_id FROM contact_list_mapping WHERE account_id = :accountId AND contact_list_id = :listId", nativeQuery = true)
    List<Long> findCustomerRecordIDsByAccountIdAndListId(long accountId, long listId);
}
