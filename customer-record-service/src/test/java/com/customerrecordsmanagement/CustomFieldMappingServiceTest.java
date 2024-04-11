package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customfields.entity.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.dto.CustomFieldMappingDTO;
import com.customerrecordsmanagement.customfields.repository.CustomFieldMappingRepository;
import com.customerrecordsmanagement.customfields.service.CustomFieldMappingService;
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
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomFieldMappingServiceTest {
    @Mock
    private CustomFieldMappingRepository customFieldMappingRepository;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomFieldMappingService customFieldMappingService;

    @Test
    public void testFetchCustomFieldMappingDTOByAccountId() {
        long accountId = 1;
        List<CustomFieldMapping> mockCustomFieldMappingList = MockObjects.getCustomFieldMappingList();
        List<CustomFieldMappingDTO> mockCustomFieldMappingDtoList = MockObjects.getCustomFieldMappingDtoList();
        Mockito.when(customFieldMappingRepository.findByAccountId(accountId)).thenReturn(mockCustomFieldMappingList);
        Assertions.assertEquals(mockCustomFieldMappingDtoList, customFieldMappingService.fetchCustomFieldMappingDTOByAccountId(accountId));
    }

    @Test
    public void testFetchCustomFieldMappingByIdAndAccountId() {
        long id = 1, accountId = 1;
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(id);
        Mockito.when(customFieldMappingRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCustomFieldMapping));

        CustomFieldMapping validCustomFieldMapping = customFieldMappingService.fetchCustomFieldMappingByIdAndAccountId(id, accountId);
        Executable invalidId = () -> customFieldMappingService.fetchCustomFieldMappingByIdAndAccountId(2, accountId);
        Executable invalidAccountId = () -> customFieldMappingService.fetchCustomFieldMappingByIdAndAccountId(id, 2);

        Assertions.assertEquals(mockCustomFieldMapping, validCustomFieldMapping);
        Assertions.assertThrows(EntityNotFoundException.class, invalidId);
        Assertions.assertThrows(EntityNotFoundException.class, invalidAccountId);
    }

    @Test
    public void testSaveCustomFieldMapping() {
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(1);
        Mockito.when(customFieldMappingRepository.save(mockCustomFieldMapping)).thenReturn(mockCustomFieldMapping);
        CustomFieldMapping validCustomFieldMapping = customFieldMappingService.saveCustomFieldMapping(mockCustomFieldMapping);

        Mockito.when(customFieldMappingRepository.save(mockCustomFieldMapping)).thenThrow(DataIntegrityViolationException.class);
        Executable duplicateCustomFieldMapping = () -> customFieldMappingService.saveCustomFieldMapping(mockCustomFieldMapping);

        Assertions.assertEquals(mockCustomFieldMapping, validCustomFieldMapping);
        Assertions.assertThrows(DuplicateEntryException.class, duplicateCustomFieldMapping);
        Mockito.verify(customFieldMappingRepository, Mockito.times(2)).save(mockCustomFieldMapping);
    }

    @Test
    public void testCreateCustomFieldMappingByMap() {
        long id = 1, accountId = 1;
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(id);
        CustomFieldMappingDTO mockCustomFieldMappingDTO = MockObjects.getCustomFieldMappingDTO(id);
        Map<String, String> mockCustomFieldMappingMap = MockObjects.getCustomFieldMappingMap();

        Mockito.when(customFieldMappingRepository.save(Mockito.any(CustomFieldMapping.class))).thenReturn(mockCustomFieldMapping);
        Mockito.when(customFieldMappingRepository.findByAccountId(accountId)).thenReturn(List.of());
        CustomFieldMappingDTO validCustomFieldMappingDTO = customFieldMappingService.createCustomFieldMappingByMap(accountId, mockCustomFieldMappingMap);

        Mockito.when(customFieldMappingRepository.findByAccountId(accountId)).thenReturn(MockObjects.getCustomFieldMappingList());
        Executable limitExceed = () -> customFieldMappingService.createCustomFieldMappingByMap(accountId, mockCustomFieldMappingMap);

        Assertions.assertEquals(mockCustomFieldMappingDTO, validCustomFieldMappingDTO);
        Assertions.assertThrows(BadRequestException.class, limitExceed);
        Mockito.verify(customFieldMappingRepository, Mockito.times(1)).save(Mockito.any(CustomFieldMapping.class));
    }

    @Test
    public void testUpdateCustomFieldMapping() {
        long id = 1, accountId = 1;
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(id);
        CustomFieldMapping updatedCustomFieldMapping = MockObjects.getUpdatedCustomFieldMapping(id);
        CustomFieldMappingDTO updatedCustomFieldMappingDTO = MockObjects.getUpdatedCustomFieldMappingDTO(id);

        Mockito.when(customFieldMappingRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCustomFieldMapping));
        Mockito.when(customFieldMappingRepository.save(updatedCustomFieldMapping)).thenReturn(updatedCustomFieldMapping);
        CustomFieldMappingDTO actualCustomFieldMapping = customFieldMappingService.updateCustomField(id, accountId, MockObjects.getValidCustomFieldMappingForUpdate());
        Executable invalidId = () -> customFieldMappingService.updateCustomField(2, accountId, MockObjects.getValidCustomFieldMappingForUpdate());
        Executable invalidCustomField = () -> customFieldMappingService.updateCustomField(id, accountId, MockObjects.getInvalidCustomFieldMappingForUpdate());

        Assertions.assertEquals(updatedCustomFieldMappingDTO, actualCustomFieldMapping);
        Assertions.assertThrows(EntityNotFoundException.class, invalidId);
        Assertions.assertThrows(BadRequestException.class, invalidCustomField);
        Mockito.verify(customFieldMappingRepository, Mockito.times(1)).save(updatedCustomFieldMapping);
    }

    @Test
    public void testDeleteCustomFieldMappingById() {
        long id = 1, accountId = 1;
        CustomFieldMapping mockCustomFieldMapping = MockObjects.getCustomFieldMapping(id);
        Mockito.when(customFieldMappingRepository.findByIdAndAccountId(id, accountId)).thenReturn(Optional.of(mockCustomFieldMapping));
        customFieldMappingService.deleteCustomField(id, accountId);
        Mockito.verify(customFieldMappingRepository, Mockito.times(1)).delete(mockCustomFieldMapping);
    }
}
