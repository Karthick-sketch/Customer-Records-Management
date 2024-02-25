package com.karthick.customerrecordsmanagement.customfields;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;

    public CustomFieldMapping createCustomFieldMapping(CustomFieldMapping customFieldMapping) {
        return customFieldMappingRepository.save(customFieldMapping);
    }

    public List<CustomFieldMapping> fetchCustomFieldMappingAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }
}
