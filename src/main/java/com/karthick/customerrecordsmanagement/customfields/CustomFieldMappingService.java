package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;

    public CustomFieldMapping createCustomFieldMapping(CustomFieldMappingDTO customFieldMappingDTO) {
        List<String> fields = CustomField.getFieldNames();
        List<CustomFieldMapping> customFieldMappingList = fetchCustomFieldMappingAccountId(customFieldMappingDTO.getAccountId());
        for (CustomFieldMapping customFieldMapping : customFieldMappingList) {
            fields.remove(customFieldMapping.getFieldName());
        }
        if (fields.isEmpty()) {
            throw new BadRequestException("Custom field limit exceed");
        }
        CustomFieldMapping customFieldMapping = new CustomFieldMapping(customFieldMappingDTO.getAccountId(), customFieldMappingDTO.getFieldName(), fields.get(0), customFieldMappingDTO.getDataType());
        return customFieldMappingRepository.save(customFieldMapping);
    }

    public List<CustomFieldMapping> fetchCustomFieldMappingAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }
}
