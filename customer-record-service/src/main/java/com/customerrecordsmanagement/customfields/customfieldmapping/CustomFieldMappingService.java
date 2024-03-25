package com.customerrecordsmanagement.customfields.customfieldmapping;

import com.customerrecordsmanagement.BadRequestException;
import com.customerrecordsmanagement.DuplicateEntryException;
import com.customerrecordsmanagement.EntityNotFoundException;
import com.customerrecordsmanagement.customfields.CustomField;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;
    private ModelMapper modelMapper;

    // added unit test
    public CustomFieldMapping createCustomFieldMapping(@NonNull CustomFieldMapping customFieldMapping) {
        try {
            return customFieldMappingRepository.save(customFieldMapping);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("The custom field " + customFieldMapping.getCustomFieldName() + " is already present");
        }
    }

    // added unit test
    public CustomFieldMappingDTO createCustomFieldMappingByDTO(CustomFieldMappingDTO customFieldMappingDTO) {
        List<String> fields = CustomField.getFieldNames();
        fetchCustomFieldMappingByAccountId(customFieldMappingDTO.getAccountId())
                .forEach(customFieldMapping -> fields.remove(customFieldMapping.getFieldName()));
        if (fields.isEmpty()) {
            throw new BadRequestException("Custom field limit exceed");
        }
        CustomFieldMapping customFieldMapping = convertDtoToCustomFieldMapping(customFieldMappingDTO);
        customFieldMapping.setFieldName(fields.get(0));
        return convertCustomFieldMappingToDTO(createCustomFieldMapping(customFieldMapping));
    }

    public List<CustomFieldMapping> fetchCustomFieldMappingByAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }

    // added unit test
    public List<CustomFieldMappingDTO> fetchCustomFieldMappingDTOByAccountId(long accountId) {
        return fetchCustomFieldMappingByAccountId(accountId).stream()
                .map(this::convertCustomFieldMappingToDTO)
                .toList();
    }

    public List<String> fetchCustomFieldNamesByAccountId(long accountId) {
        return customFieldMappingRepository.findCustomFieldNamesByAccountId(accountId);
    }

    public static String findColumnNameByCustomFieldName(String columnName, List<CustomFieldMapping> customFieldMappings) {
        for (CustomFieldMapping customFieldMapping : customFieldMappings) {
            if (columnName.equals(customFieldMapping.getCustomFieldName())) {
                return customFieldMapping.getFieldName();
            }
        }
        throw new EntityNotFoundException("There is no custom field called '" + columnName + "'");
    }

    private CustomFieldMapping convertDtoToCustomFieldMapping(CustomFieldMappingDTO customFieldMappingDTO) {
        return modelMapper.map(customFieldMappingDTO, CustomFieldMapping.class);
    }

    private CustomFieldMappingDTO convertCustomFieldMappingToDTO(CustomFieldMapping customFieldMapping) {
        return modelMapper.map(customFieldMapping, CustomFieldMappingDTO.class);
    }
}
