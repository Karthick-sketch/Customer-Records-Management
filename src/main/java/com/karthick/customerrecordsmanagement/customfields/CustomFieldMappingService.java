package com.karthick.customerrecordsmanagement.customfields;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;

    public CustomFieldMapping createCustomFieldMapping(CustomFieldMapping customFieldMapping) {
        return customFieldMappingRepository.save(customFieldMapping);
    }

    public CustomFieldMapping fetchCustomFieldMappingByAccountIdAndColumnName(long accountId, String columnName) {
        Optional<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByAccountIdAndColumnName(accountId, columnName);
        if (customFieldMapping.isEmpty()) {
            throw new NoSuchElementException("There is no custom field called " + columnName);
        }
        return customFieldMapping.get();
    }

    public List<CustomFieldMapping> fetchCustomFieldMappingAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }
}
