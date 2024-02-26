package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;
    private ModelMapper modelMapper;

    public CustomFieldMapping createCustomFieldMapping(CustomFieldMappingDTO customFieldMappingDTO) {
        List<String> fields = CustomField.getFieldNames();
        fetchCustomFieldMappingAccountId(customFieldMappingDTO.getAccountId()).forEach(customFieldMapping ->
            fields.remove(customFieldMapping.getCustomFieldName())
        );
        if (fields.isEmpty()) {
            throw new BadRequestException("Custom field limit exceed");
        }
        CustomFieldMapping customFieldMapping = convertToCustomFieldMapping(customFieldMappingDTO);
        customFieldMapping.setColumnName(fields.get(0));
        return customFieldMappingRepository.save(customFieldMapping);
    }

    public List<CustomFieldMapping> fetchCustomFieldMappingAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }

    private CustomFieldMapping convertToCustomFieldMapping(CustomFieldMappingDTO customFieldMappingDTO) {
        return modelMapper.map(customFieldMappingDTO, CustomFieldMapping.class);
    }
}
