package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomFieldService {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingRepository customFieldMappingRepository;

    public void createCustomFields(long defaultFieldId, Map<String, String> customFieldsMap) {
        CustomField customField = new CustomField(defaultFieldId);
        customFieldsMap.forEach((key, value) -> {
            CustomFieldMapping customFieldMapping = new CustomFieldMapping(key, customField.setField(value));
            customFieldMapping.setDefaultFieldId(defaultFieldId);
            customFieldMappingRepository.save(customFieldMapping);
        });
        customFieldRepository.save(customField);
    }

    public List<Map<String, String>> mapCustomFields() {
        List<CustomField> customFields = customFieldRepository.findAll();
        List<Map<String, String>> customFieldsMapList = new ArrayList<>();
        for (CustomField customField : customFields) {
            List<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByDefaultFieldId(customField.getDefaultFieldId());
            customFieldsMapList.add(convertCustomFieldsToMap(customField, customFieldMapping));
        }
        return customFieldsMapList;
    }

    public List<Map<String, String>> mapCustomFields(int offset, int limit) {
        Page<CustomField> customFields = customFieldRepository.findAll(PageRequest.of(offset, limit));
        List<Map<String, String>> customFieldsMapList = new ArrayList<>();
        for (CustomField customField : customFields) {
            List<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByDefaultFieldId(customField.getDefaultFieldId());
            customFieldsMapList.add(convertCustomFieldsToMap(customField, customFieldMapping));
        }
        return customFieldsMapList;
    }

    public Map<String, String> mapCustomFields(long defaultFieldId) {
        Optional<CustomField> customField = customFieldRepository.findByDefaultFieldId(defaultFieldId);
        if (customField.isPresent()) {
            List<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByDefaultFieldId(defaultFieldId);
            return convertCustomFieldsToMap(customField.get(), customFieldMapping);
        } else {
            throw new NoSuchElementException("There are no custom fields present in the database with Id of " + defaultFieldId);
        }
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getColumnName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
