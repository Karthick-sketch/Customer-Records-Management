package com.customerrecordsmanagement.contactlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactListRepository extends JpaRepository<ContactList, Long> {
    List<ContactList> findByAccountId(long accountId);

    Optional<ContactList> findByIdAndAccountId(long id, long accountId);
}
