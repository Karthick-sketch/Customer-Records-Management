package com.customerrecordsmanagement.contactlist.service;

import com.customerrecordsmanagement.DuplicateEntryException;
import com.customerrecordsmanagement.EntityNotFoundException;
import com.customerrecordsmanagement.contactlist.dto.ContactListAddDTO;
import com.customerrecordsmanagement.contactlist.dto.ContactListDTO;
import com.customerrecordsmanagement.contactlist.entity.ContactList;
import com.customerrecordsmanagement.contactlist.entity.ContactListMapping;
import com.customerrecordsmanagement.contactlist.repository.ContactListMappingRepository;
import com.customerrecordsmanagement.contactlist.repository.ContactListRepository;
import com.customerrecordsmanagement.customerrecords.dto.ContactEmailDTO;
import com.customerrecordsmanagement.customerrecords.entity.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.service.CustomerRecordService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContactListService {
    private ContactListRepository contactListRepository;
    private ContactListMappingRepository contactListMappingRepository;
    private CustomerRecordService customerRecordService;

    public List<ContactList> fetchContactListByAccountId(long accountId) {
        return contactListRepository.findByAccountId(accountId);
    }

    // added unit test
    public ContactList fetchContactListByIdAndAccountId(long id, long accountId) {
        Optional<ContactList> contactList = contactListRepository.findByIdAndAccountId(id, accountId);
        if (contactList.isEmpty()) {
            throw new EntityNotFoundException("There is no list with ID of " + id);
        }
        return contactList.get();
    }

    // added unit test
    public ContactListDTO fetchCustomerRecordsFromList(long listId, long accountId) {
        ContactList contactList = fetchContactListByIdAndAccountId(listId, accountId);
        List<ContactListMapping> contactListMappings = contactListMappingRepository.findByAccountIdAndContactListId(accountId, listId);
        List<CustomerRecord> customerRecords = contactListMappings.stream()
                .map(ContactListMapping::getCustomerRecord)
                .toList();
        return new ContactListDTO(listId, contactList.getListName(), customerRecords);
    }

    public List<ContactEmailDTO> fetchContactEmailsByAccountIdAndListId(long accountId, long listId) {
        List<Long> customerRecordIDs = contactListMappingRepository.findCustomerRecordIDsByAccountIdAndListId(accountId, listId);
        return customerRecordService.fetchContactEmailsByAccountId(accountId).stream()
                .filter(contactEmailDTO -> !customerRecordIDs.contains(contactEmailDTO.getId()))
                .toList();
    }

    // added unit test
    public ContactList createContactList(@NonNull ContactList contactList) {
        try {
            return contactListRepository.save(contactList);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("The contact list " + contactList.getListName() + " is already present");
        }
    }

    // added unit test
    public ContactListMapping createContactListMapping(@NonNull ContactListMapping contactListMapping) {
        try {
            return contactListMappingRepository.save(contactListMapping);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("The customer record " + contactListMapping.getCustomerRecord().getEmail() + " is already present");
        }
    }

    // added unit test
    public void addCustomerRecordsToList(ContactListAddDTO contactListAddDTO) {
        long accountId = contactListAddDTO.getAccountId();
        ContactList contactList = fetchContactListByIdAndAccountId(contactListAddDTO.getListId(), accountId);
        for (long id : contactListAddDTO.getCustomerRecordIds()) {
            CustomerRecord customerRecord = customerRecordService.fetchCustomerRecordByIdAndAccountId(id, accountId);
            createContactListMapping(new ContactListMapping(accountId, contactList, customerRecord));
        }
    }
}
