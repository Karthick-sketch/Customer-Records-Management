package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomFieldService {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingService customFieldMappingService;

    public void createCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldsMap) {
        CustomField customField = customFieldRepository.save(new CustomField(customerRecord));
        customFieldsMap.forEach((key, value) -> {
            CustomFieldMapping customFieldMapping = new CustomFieldMapping(customerRecord.getAccountId(), key, customField.setField(value), "text");
            customFieldMappingService.createCustomFieldMapping(customFieldMapping);
        });
    }

    public Map<String, String> mapCustomFields(long accountId, long customerRecordId) {
        List<CustomFieldMapping> customFieldMappingList = customFieldMappingService.fetchCustomFieldMappingAccountId(accountId);
        Optional<CustomField> customFieldOptional = customFieldRepository.findByCustomerRecordId(customerRecordId);
        if (customFieldMappingList.isEmpty() || customFieldOptional.isEmpty()) {
            return null;
        }
        return convertCustomFieldsToMap(customFieldOptional.get(), customFieldMappingList);
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getColumnName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
