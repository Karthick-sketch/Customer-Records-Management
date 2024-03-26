package com.customerrecordsmanagement;

import com.customerrecordsmanagement.contactlist.ContactListService;
import com.customerrecordsmanagement.contactlist.entity.ContactList;
import com.customerrecordsmanagement.contactlist.entity.ContactListMapping;
import com.customerrecordsmanagement.contactlist.repository.ContactListMappingRepository;
import com.customerrecordsmanagement.contactlist.repository.ContactListRepository;
import com.customerrecordsmanagement.customerrecords.CustomerRecordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ContactListServiceTest {
    @Mock
    private ContactListRepository contactListRepository;
    @Mock
    private ContactListMappingRepository contactListMappingRepository;
    @Mock
    private CustomerRecordService customerRecordService;

    @InjectMocks
    private ContactListService contactListService;

    @Test
    public void testFetchContactListByIdAndAccountId() {
        long id = 1, accountId = 1;
        ContactList mockContactList = MockObjects.getContactList();
        Mockito.when(contactListRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockContactList));

        ContactList validContactList = contactListService.fetchContactListByIdAndAccountId(id, accountId);
        Executable invalidId = () -> contactListService.fetchContactListByIdAndAccountId(2, accountId);
        Executable invalidAccountId = () -> contactListService.fetchContactListByIdAndAccountId(id, 2);

        Assertions.assertEquals(mockContactList, validContactList);
        Assertions.assertThrows(EntityNotFoundException.class, invalidId);
        Assertions.assertThrows(EntityNotFoundException.class, invalidAccountId);
    }

    @Test // need to fix
    public void testCreateContactList() {
        ContactList mockContactList = MockObjects.getContactList();
        Mockito.when(contactListRepository.save(mockContactList)).thenReturn(mockContactList);
        ContactList validContactList = contactListService.createContactList(mockContactList);

        Mockito.when(contactListRepository.save(mockContactList)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateContactList = () -> contactListService.createContactList(mockContactList);

        Assertions.assertEquals(mockContactList, validContactList);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateContactList);
        Mockito.verify(contactListRepository, Mockito.times(1)).save(mockContactList);
    }

    @Test // need to fix
    public void testCreateContactListMapping() {
        ContactListMapping mockContactListMapping = MockObjects.getContactListMapping(MockObjects.getContactList());
        Mockito.when(contactListMappingRepository.save(mockContactListMapping)).thenReturn(mockContactListMapping);
        ContactListMapping validContactListMapping = contactListService.createContactListMapping(mockContactListMapping);

        Mockito.when(contactListMappingRepository.save(mockContactListMapping)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateContactListMapping = () -> contactListService.createContactListMapping(mockContactListMapping);

        Assertions.assertEquals(mockContactListMapping, validContactListMapping);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateContactListMapping);
        Mockito.verify(contactListMappingRepository, Mockito.times(1)).save(mockContactListMapping);
    }
}
