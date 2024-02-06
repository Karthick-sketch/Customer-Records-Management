package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomFieldService {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingRepository customFieldMappingRepository;

    public void createCustomFields(long defaultFieldId, Map<String, String> customFieldsMap) {
        CustomField customField = new CustomField(defaultFieldId);
        customFieldsMap.forEach((key, value) -> {
            CustomFieldsMapping customFieldsMapping = new CustomFieldsMapping(key, customField.setField(value));
            customFieldsMapping.setDefaultFieldId(defaultFieldId);
            customFieldMappingRepository.save(customFieldsMapping);
        });
        customFieldRepository.save(customField);
    }

    public Map<String, String> mapCustomFields(long defaultFieldId) throws IllegalAccessException {
        List<CustomField> customFields = customFieldRepository.findByDefaultFieldId(defaultFieldId);
        List<CustomFieldsMapping> customFieldsMapping = customFieldMappingRepository.findByDefaultFieldId(defaultFieldId);
        Map<String, String> customFieldsMap = new HashMap<>();
        for (CustomField customField : customFields) {
            for (CustomFieldsMapping cfm : customFieldsMapping) {
                customFieldsMap.put(cfm.getColumnName(), customField.getValueByColumnName(cfm.getFieldName()));
            }
        }
        return customFieldsMap;
    }
}
