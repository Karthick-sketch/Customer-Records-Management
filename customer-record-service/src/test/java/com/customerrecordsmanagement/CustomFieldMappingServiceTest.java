package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingRepository;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
public class CustomFieldMappingServiceTest {
    @Mock
    private CustomFieldMappingRepository customFieldMappingRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomFieldMappingService customFieldMappingService;

    @Test
    public void testCreateCustomFieldMapping() {
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(1);
        Mockito.when(customFieldMappingRepository.save(mockCustomFieldMapping)).thenReturn(mockCustomFieldMapping);
        CustomFieldMapping validCustomFieldMapping = customFieldMappingService.createCustomFieldMapping(mockCustomFieldMapping);

        Mockito.when(customFieldMappingRepository.save(mockCustomFieldMapping)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateCustomFieldMapping = () -> customFieldMappingService.createCustomFieldMapping(mockCustomFieldMapping);

        Assertions.assertEquals(mockCustomFieldMapping, validCustomFieldMapping);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateCustomFieldMapping);
        Mockito.verify(customFieldMappingRepository, Mockito.times(2)).save(mockCustomFieldMapping);
    }
}
