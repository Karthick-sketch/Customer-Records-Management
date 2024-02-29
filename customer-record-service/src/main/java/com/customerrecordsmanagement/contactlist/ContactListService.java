package com.customerrecordsmanagement.contactlist;

import com.customerrecordsmanagement.EntityNotException;
import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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

    public ContactList fetchContactListByIdAndAccountId(long id, long accountId) {
        Optional<ContactList> contactList = contactListRepository.findByIdAndAccountId(id, accountId);
        if (contactList.isEmpty()) {
            throw new EntityNotException("There is no list with ID of " + id);
        }
        return contactList.get();
    }

    public ContactListDTO fetchCustomerRecordsFromList(long id, long accountId) {
        ContactList contactList = fetchContactListByIdAndAccountId(id, accountId);
        List<ContactListMapping> contactListMappings = contactListMappingRepository.findByAccountIdAndContactListId(accountId, id);
        List<CustomerRecord> customerRecords = contactListMappings.stream()
                .map(ContactListMapping::getCustomerRecord)
                .toList();
        return new ContactListDTO(id, contactList.getListName(), customerRecords);
    }

    public ContactList createContactList(@NonNull ContactList contactList) {
        return contactListRepository.save(contactList);
    }

    public void addCustomerRecordsToList(ContactListAddDTO contactListAddDTO) {
        long accountId = contactListAddDTO.getAccountId();
        ContactList contactList = fetchContactListByIdAndAccountId(contactListAddDTO.getListId(), accountId);
        contactListAddDTO.getCustomerRecordIds().forEach(id -> {
            CustomerRecord customerRecord = customerRecordService.fetchCustomerRecordByIdAndAccountId(id, accountId);
            contactListMappingRepository.save(new ContactListMapping(accountId, contactList, customerRecord));
        });
    }
}
