package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<Map<String, String>> mapCustomFields() throws IllegalAccessException {
        List<CustomField> customFields = customFieldRepository.findAll();
        List<Map<String, String>> customFieldsMapList = new ArrayList<>();
        for (CustomField customField : customFields) {
            List<CustomFieldsMapping> customFieldsMapping = customFieldMappingRepository.findByDefaultFieldId(customField.getDefaultFieldId());
            customFieldsMapList.add(convertCustomFieldsToMap(customField, customFieldsMapping));
        }
        return customFieldsMapList;
    }

    public Map<String, String> mapCustomFields(long defaultFieldId) throws IllegalAccessException {
        Optional<CustomField> customField = customFieldRepository.findByDefaultFieldId(defaultFieldId);
        if (customField.isPresent()) {
            List<CustomFieldsMapping> customFieldsMapping = customFieldMappingRepository.findByDefaultFieldId(defaultFieldId);
            return convertCustomFieldsToMap(customField.get(), customFieldsMapping);
        } else {
            throw new NoSuchElementException("There are no custom fields present in the database with Id of " + defaultFieldId);
        }
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldsMapping> customFieldsMapping) throws IllegalAccessException {
        Map<String, String> customFieldsMap = new HashMap<>();
        for (CustomFieldsMapping cfm : customFieldsMapping) {
            customFieldsMap.put(cfm.getColumnName(), customField.getValueByColumnName(cfm.getFieldName()));
        }
        return customFieldsMap;
    }
}
