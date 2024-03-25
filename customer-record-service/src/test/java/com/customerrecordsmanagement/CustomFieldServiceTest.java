package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordDTO;
import com.customerrecordsmanagement.customfields.CustomField;
import com.customerrecordsmanagement.customfields.CustomFieldRepository;
import com.customerrecordsmanagement.customfields.CustomFieldService;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class CustomFieldServiceTest {
    @Mock
    private CustomFieldRepository customFieldRepository;
    @Mock
    private CustomFieldMappingService customFieldMappingService;

    @InjectMocks
    private CustomFieldService customFieldService;

    @Test
    public void testCreateCustomFieldByCustomField() {
        CustomField mockCustomField = MockObjects.getCustomerRecord().getCustomField();
        Mockito.when(customFieldRepository.save(mockCustomField)).thenReturn(mockCustomField);
        Assertions.assertEquals(mockCustomField, customFieldService.createCustomFieldByCustomField(mockCustomField));
        Mockito.verify(customFieldRepository, Mockito.times(1)).save(mockCustomField);
    }

    @Test
    public void testCreateCustomFieldByCustomRecordDTO() {
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        CustomField mockCustomField = mockCustomerRecord.getCustomField();
        CustomerRecordDTO mockCustomerRecordDTO = new CustomerRecordDTO(mockCustomerRecord, MockObjects.getCustomFieldMap(mockCustomField));
        List<CustomFieldMapping> mockCustomFieldMappingList = MockObjects.getCustomFieldMappingList();

        Mockito.when(customFieldRepository.save(mockCustomField)).thenReturn(mockCustomField);
        Mockito.when(customFieldMappingService.fetchCustomFieldMappingByAccountId(mockCustomerRecord.getAccountId()))
                .thenReturn(mockCustomFieldMappingList);

        CustomField validCustomField = customFieldService.createCustomFieldByCustomRecordDTO(mockCustomerRecordDTO);
        Assertions.assertEquals(mockCustomField, validCustomField);
        Mockito.verify(customFieldRepository, Mockito.times(1)).save(mockCustomField);
    }

    @Test
    public void testMapCustomFields() {
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        CustomField mockCustomField = mockCustomerRecord.getCustomField();
        List<CustomFieldMapping> mockCustomFieldMappingList = MockObjects.getCustomFieldMappingList();
        CustomField validCustomField = customFieldService.mapCustomFields(mockCustomerRecord, MockObjects.getCustomFieldMap(mockCustomField), mockCustomFieldMappingList);
        Assertions.assertEquals(mockCustomField, validCustomField);
    }

    @Test
    public void testReverseMapCustomFields() {
        long accountId = 1;
        CustomField mockCustomField = MockObjects.getCustomerRecord().getCustomField();
        List<CustomFieldMapping> mockCustomFieldMappingList = MockObjects.getCustomFieldMappingList();
        Map<String, String> mockCustomFieldMap = MockObjects.getCustomFieldMap(mockCustomField);

        Mockito.when(customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId))
                .thenReturn(mockCustomFieldMappingList);
        Map<String, String> validCustomFieldMap = customFieldService.reverseMapCustomFields(mockCustomField);

        Mockito.when(customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId))
                .thenReturn(List.of());
        Map<String, String> emptyCustomFieldMap = customFieldService.reverseMapCustomFields(mockCustomField);

        Assertions.assertEquals(mockCustomFieldMap, validCustomFieldMap);
        Assertions.assertNull(emptyCustomFieldMap);
    }
}
