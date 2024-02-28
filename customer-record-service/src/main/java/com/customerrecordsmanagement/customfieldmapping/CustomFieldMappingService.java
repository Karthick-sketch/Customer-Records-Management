package com.customerrecordsmanagement.customfieldmapping;

import com.customerrecordsmanagement.BadRequestException;
import com.customerrecordsmanagement.customfields.CustomField;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;
    private ModelMapper modelMapper;

    public CustomFieldMappingDTO createCustomFieldMapping(CustomFieldMappingDTO customFieldMappingDTO) {
        List<String> fields = CustomField.getFieldNames();
        fetchCustomFieldMappingByAccountId(customFieldMappingDTO.getAccountId()).forEach(customFieldMapping ->
            fields.remove(customFieldMapping.getFieldName())
        );
        if (fields.isEmpty()) {
            throw new BadRequestException("Custom field limit exceed");
        }
        CustomFieldMapping customFieldMapping = convertToCustomFieldMapping(customFieldMappingDTO);
        customFieldMapping.setFieldName(fields.get(0));
        try {
            return convertToCustomFieldMappingDTO(customFieldMappingRepository.save(customFieldMapping));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("The custom field " + customFieldMapping.getCustomFieldName() + " is already present");
        }
    }

    public List<CustomFieldMapping> fetchCustomFieldMappingByAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }

    public List<CustomFieldMappingDTO> fetchCustomFieldMappingDTOByAccountId(long accountId) {
        return fetchCustomFieldMappingByAccountId(accountId).stream()
                .map(this::convertToCustomFieldMappingDTO)
                .toList();
    }

    private CustomFieldMapping convertToCustomFieldMapping(CustomFieldMappingDTO customFieldMappingDTO) {
        return modelMapper.map(customFieldMappingDTO, CustomFieldMapping.class);
    }

    private CustomFieldMappingDTO convertToCustomFieldMappingDTO(CustomFieldMapping customFieldMapping) {
        return modelMapper.map(customFieldMapping, CustomFieldMappingDTO.class);
    }
}
