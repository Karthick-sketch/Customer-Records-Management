package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingDTO;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingRepository;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CustomFieldMappingServiceTest {
    @Mock
    private CustomFieldMappingRepository customFieldMappingRepository;
    @Spy
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

    @Test
    public void testCreateCustomFieldMappingByDTO() {
        long id = 1, accountId = 1;
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(id);
        CustomFieldMappingDTO mockCustomFieldMappingDTO = MockObjects.getCustomFieldMappingDTO(id);

        Mockito.when(customFieldMappingRepository.save(mockCustomFieldMapping)).thenReturn(mockCustomFieldMapping);
        Mockito.when(customFieldMappingRepository.findByAccountId(accountId)).thenReturn(List.of());
        CustomFieldMappingDTO validCustomFieldMappingDTO = customFieldMappingService.createCustomFieldMappingByDTO(mockCustomFieldMappingDTO);

        Mockito.when(customFieldMappingRepository.findByAccountId(accountId)).thenReturn(MockObjects.getCustomFieldMappingList());
        Executable limitExceed = () -> customFieldMappingService.createCustomFieldMappingByDTO(mockCustomFieldMappingDTO);

        Assertions.assertEquals(mockCustomFieldMappingDTO, validCustomFieldMappingDTO);
        Assertions.assertThrows(BadRequestException.class, limitExceed);
        Mockito.verify(customFieldMappingRepository, Mockito.times(1)).save(mockCustomFieldMapping);
    }

    @Test
    public void testFetchCustomFieldMappingDTOByAccountId() {
        long accountId = 1;
        List<CustomFieldMapping> mockCustomFieldMappingList = MockObjects.getCustomFieldMappingList();
        List<CustomFieldMappingDTO> mockCustomFieldMappingDtoList = MockObjects.getCustomFieldMappingDtoList();
        Mockito.when(customFieldMappingRepository.findByAccountId(accountId)).thenReturn(mockCustomFieldMappingList);
        Assertions.assertEquals(mockCustomFieldMappingDtoList, customFieldMappingService.fetchCustomFieldMappingDTOByAccountId(accountId));
    }
}
