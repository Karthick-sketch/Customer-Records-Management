package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordDTO;
import com.customerrecordsmanagement.customerrecords.CustomerRecordRepository;
import com.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.customerrecordsmanagement.customfields.CustomField;
import com.customerrecordsmanagement.customfields.CustomFieldService;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerRecordServiceTest {
    @Mock
    private CustomerRecordRepository customerRecordRepository;
    @Mock
    private CustomFieldService customFieldService;
    @Mock
    private CustomFieldMappingService customFieldMappingService;
    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomerRecordService customerRecordService;

    @Test
    public void testFetchCustomerRecordByIdAndAccountId() {
        long id = 1, accountId = 1;
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        Mockito.when(customerRecordRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCustomerRecord));

        CustomerRecord validCustomerRecord = customerRecordService.fetchCustomerRecordByIdAndAccountId(id, accountId);
        Executable invalidId = () -> customerRecordService.fetchCustomerRecordByIdAndAccountId(2, accountId);
        Executable invalidAccountId = () -> customerRecordService.fetchCustomerRecordByIdAndAccountId(id, 2);

        Assertions.assertEquals(mockCustomerRecord, validCustomerRecord);
        Assertions.assertThrows(EntityNotFoundException.class, invalidId);
        Assertions.assertThrows(EntityNotFoundException.class, invalidAccountId);
    }

    @Test
    public void testCreateCustomerRecord() {
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        Mockito.when(customerRecordRepository.save(mockCustomerRecord)).thenReturn(mockCustomerRecord);
        CustomerRecord validCustomerRecord = customerRecordService.saveCustomerRecord(mockCustomerRecord);
        Mockito.when(customerRecordRepository.save(mockCustomerRecord)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateCustomerRecord = () -> customerRecordService.saveCustomerRecord(mockCustomerRecord);
        Assertions.assertEquals(mockCustomerRecord, validCustomerRecord);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateCustomerRecord);
        Mockito.verify(customerRecordRepository, Mockito.times(2)).save(mockCustomerRecord);
    }

    @Test
    public void testCreateCustomerRecordByDTO() {
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        CustomField mockCustomField = mockCustomerRecord.getCustomField();
        Map<String, String> mockCustomFieldMap = MockObjects.getCustomFieldMap(mockCustomField);
        CustomerRecordDTO mockCustomerRecordDTO = new CustomerRecordDTO(mockCustomerRecord, mockCustomFieldMap);
        Mockito.when(customerRecordRepository.save(mockCustomerRecord)).thenReturn(mockCustomerRecord);
        Mockito.when(customFieldService.createCustomFieldByCustomRecordDTO(mockCustomerRecordDTO)).thenReturn(mockCustomField);
        Mockito.when(customFieldService.reverseMapCustomFields(mockCustomField)).thenReturn(mockCustomFieldMap);
        CustomerRecordDTO validCustomerRecordDTO = customerRecordService.createCustomerRecordByDTO(mockCustomerRecordDTO);
        Assertions.assertEquals(mockCustomerRecordDTO, validCustomerRecordDTO);
    }

    @Test
    public void testUpdateCustomerRecord() {
        long id = 1, accountId = 1;
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        List<CustomFieldMapping> mockCustomFieldMappingList = MockObjects.getCustomFieldMappingList();
        CustomerRecord updatedCustomerRecord = MockObjects.getUpdatedCustomerRecord();
        CustomField updatedCustomField = updatedCustomerRecord.getCustomField();
        Map<String, String> updatedCustomFieldMap = MockObjects.getCustomFieldMap(updatedCustomField);
        CustomerRecordDTO expectedCustomerRecordDTO = new CustomerRecordDTO(updatedCustomerRecord, updatedCustomFieldMap);

        Mockito.when(customerRecordRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCustomerRecord));
        Mockito.when(customerRecordRepository.save(updatedCustomerRecord)).thenReturn(updatedCustomerRecord);
        Mockito.when(customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId)).thenReturn(mockCustomFieldMappingList);
        Mockito.when(customFieldService.reverseMapCustomFields(updatedCustomField)).thenReturn(updatedCustomFieldMap);

        CustomerRecordDTO actualCustomerRecord = customerRecordService.updateCustomerRecord(id, accountId, MockObjects.getValidCustomerRecordFieldsForUpdate());
        Assertions.assertEquals(expectedCustomerRecordDTO, actualCustomerRecord);
        Mockito.verify(customerRecordRepository, Mockito.times(1)).save(updatedCustomerRecord);
    }

    @Test
    public void testDeleteCustomerRecordById() {
        long id = 1, accountId = 1;
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        Mockito.when(customerRecordRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCustomerRecord));
        customerRecordService.deleteCustomerRecordById(id, accountId);
        Mockito.verify(customerRecordRepository, Mockito.times(1)).delete(mockCustomerRecord);
    }
}
