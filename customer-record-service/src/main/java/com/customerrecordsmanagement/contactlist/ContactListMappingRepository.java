package com.customerrecordsmanagement.contactlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactListMappingRepository extends JpaRepository<ContactListMapping, Long> {
    List<ContactListMapping> findByAccountIdAndContactListId(long accountId, long listId);
}
