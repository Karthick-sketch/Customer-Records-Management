package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CustomFieldsDto {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingRepository customFieldMappingRepository;

    public Map<String, String> map() throws IllegalAccessException {
        Map<String, String> customFieldsMap = new HashMap<>();
        List<CustomField> customFields = customFieldRepository.findAll();
        List<CustomFieldsMapping> customFieldsMapping = customFieldMappingRepository.findAll();
        for (CustomFieldsMapping cfm : customFieldsMapping) {
            customFieldsMap.put(cfm.getColumnName(), getValueByColumnName(customFields, cfm.getFieldName()));
        }
        return customFieldsMap;
    }

    private String getValueByColumnName(List<CustomField> customFieldList, String columnName) throws IllegalAccessException {
        CustomField customField = customFieldList.get(0);
        Field field = ReflectionUtils.findField(CustomField.class, columnName);
        if (field != null) {
            field.setAccessible(true);
            if (field.get(customField) instanceof String value) {
                return value;
            }
        }
        return null;
    }
}
