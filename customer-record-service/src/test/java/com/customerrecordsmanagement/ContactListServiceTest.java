package com.customerrecordsmanagement;

import com.customerrecordsmanagement.contactlist.ContactListService;
import com.customerrecordsmanagement.contactlist.dto.ContactListAddDTO;
import com.customerrecordsmanagement.contactlist.dto.ContactListDTO;
import com.customerrecordsmanagement.contactlist.entity.ContactList;
import com.customerrecordsmanagement.contactlist.entity.ContactListMapping;
import com.customerrecordsmanagement.contactlist.repository.ContactListMappingRepository;
import com.customerrecordsmanagement.contactlist.repository.ContactListRepository;
import com.customerrecordsmanagement.customerrecords.CustomerRecord;
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

import java.util.List;
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

    @Test
    public void testFetchCustomerRecordsFromList() {
        long id = 1, accountId = 1;
        ContactList mockContactList = MockObjects.getContactList();
        List<ContactListMapping> mockContactListMappings = mockContactList.getContactListMappings();
        ContactListDTO mockContactListDTO = MockObjects.getContactListDTO();
        Mockito.when(contactListRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockContactList));
        Mockito.when(contactListMappingRepository.findByAccountIdAndContactListId(accountId, id)).thenReturn(mockContactListMappings);
        ContactListDTO validContactListDTO = contactListService.fetchCustomerRecordsFromList(1, accountId);
        Assertions.assertEquals(mockContactListDTO, validContactListDTO);
    }

    @Test
    public void testCreateContactList() {
        ContactList mockContactList = MockObjects.getContactList();
        Mockito.when(contactListRepository.save(mockContactList)).thenReturn(mockContactList);
        ContactList validContactList = contactListService.createContactList(mockContactList);

        Mockito.when(contactListRepository.save(mockContactList)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateContactList = () -> contactListService.createContactList(mockContactList);

        Assertions.assertEquals(mockContactList, validContactList);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateContactList);
        Mockito.verify(contactListRepository, Mockito.times(2)).save(mockContactList);
    }

    @Test
    public void testCreateContactListMapping() {
        ContactListMapping mockContactListMapping = MockObjects.getContactListMapping(MockObjects.getContactList());
        Mockito.when(contactListMappingRepository.save(mockContactListMapping)).thenReturn(mockContactListMapping);
        ContactListMapping validContactListMapping = contactListService.createContactListMapping(mockContactListMapping);

        Mockito.when(contactListMappingRepository.save(mockContactListMapping)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateContactListMapping = () -> contactListService.createContactListMapping(mockContactListMapping);

        Assertions.assertEquals(mockContactListMapping, validContactListMapping);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateContactListMapping);
        Mockito.verify(contactListMappingRepository, Mockito.times(2)).save(mockContactListMapping);
    }

    @Test
    public void testAddCustomerRecordsToList() {
        long id = 1, accountId = 1;
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        ContactList mockContactList = MockObjects.getContactList();
        ContactListMapping mockContactListMapping = MockObjects.getContactListMapping(mockContactList);
        ContactListAddDTO mockContactListAddDTO = MockObjects.getContactListAddDTO();

        Mockito.when(contactListRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockContactList));
        Mockito.when(customerRecordService.fetchCustomerRecordByIdAndAccountId(id, accountId)).thenReturn(mockCustomerRecord);
        Mockito.when(contactListMappingRepository.save(Mockito.any(ContactListMapping.class))).thenReturn(mockContactListMapping);
        contactListService.addCustomerRecordsToList(mockContactListAddDTO);
        Mockito.verify(contactListMappingRepository, Mockito.times(1)).save(Mockito.any(ContactListMapping.class));
    }
}
