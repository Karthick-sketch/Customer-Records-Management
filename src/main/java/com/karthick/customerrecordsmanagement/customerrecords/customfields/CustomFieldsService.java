package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomFieldsService {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingRepository customFieldMappingRepository;

    public void map(Map<String, String> customFieldsMap) {
        CustomField customField = new CustomField();
        for (String field : customFieldsMap.keySet()) {
            String fieldName = customField.setField(customFieldsMap.get(field));
            CustomFieldsMapping customFieldsMapping = new CustomFieldsMapping(field, fieldName);
            customFieldMappingRepository.save(customFieldsMapping);
        }
        customFieldRepository.save(customField);
    }

    public Map<String, String> map() {
        Map<String, String> customFieldsMap = new HashMap<>();
        List<CustomField> customFields = customFieldRepository.findAll();
        List<CustomFieldsMapping> customFieldsMapping = customFieldMappingRepository.findAll();
        System.out.println(customFields);
        for (CustomField customField : customFields) {
            for (CustomFieldsMapping cfm : customFieldsMapping) {
                customFieldsMap.put(cfm.getColumnName(), getValueByColumnName(customField, cfm.getFieldName()));
            }
        }
        return customFieldsMap;
    }

    private String getValueByColumnName(CustomField customField, String columnName) {
        Field field = ReflectionUtils.findField(CustomField.class, columnName);
        if (field != null) {
            try {
                field.setAccessible(true);
                if (field.get(customField) instanceof String value) {
                    return value;
                }
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
}
