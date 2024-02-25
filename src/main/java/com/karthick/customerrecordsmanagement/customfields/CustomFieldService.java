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

    public CustomField createCustomField(CustomField customField) {
        return customFieldRepository.save(customField);
    }

    public CustomField createCustomField(CustomerRecord customerRecord, Map<String, String> customFieldsMap) {
        List<CustomFieldMapping> customFieldMappingList = customFieldMappingService.fetchCustomFieldMappingAccountId(customerRecord.getAccountId());
        CustomField customField = new CustomField(customerRecord);
        customFieldsMap.forEach((key, value) -> {
            assert customField.setField(findFieldNameByColumnName(key, customFieldMappingList), value);
        });
        return createCustomField(customField);
    }

    public Map<String, String> mapCustomFields(long accountId, long customerRecordId) {
        List<CustomFieldMapping> customFieldMappingList = customFieldMappingService.fetchCustomFieldMappingAccountId(accountId);
        Optional<CustomField> customFieldOptional = customFieldRepository.findByCustomerRecordId(customerRecordId);
        if (customFieldMappingList.isEmpty() || customFieldOptional.isEmpty()) {
            return null;
        }
        return convertCustomFieldsToMap(customFieldOptional.get(), customFieldMappingList);
    }

    private String findFieldNameByColumnName(String columnName, List<CustomFieldMapping> customFieldMappingList) {
        for (CustomFieldMapping customFieldMapping : customFieldMappingList) {
            if (columnName.equals(customFieldMapping.getColumnName())) {
                return customFieldMapping.getFieldName();
            }
        }
        throw new NoSuchElementException("There is no custom field called " + columnName);
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getColumnName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
