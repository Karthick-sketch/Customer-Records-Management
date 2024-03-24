package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordRepository;
import com.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.customerrecordsmanagement.customfields.CustomField;
import com.customerrecordsmanagement.customfields.CustomFieldService;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test // not feasible
    public void testConvertCustomerRecordToCustomerRecordDTO() {
        CustomerRecord mockCustomerRecord = MockObjects.getCustomerRecord();
        CustomField mockCustomField = mockCustomerRecord.getCustomField();
        Map<String, String> mockCustomFieldMap = MockObjects.getCustomFieldMap(mockCustomField);
        Mockito.when(customFieldService.reverseMapCustomFields(mockCustomField)).thenReturn(mockCustomFieldMap);
    }
}
