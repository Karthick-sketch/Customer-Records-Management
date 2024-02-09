package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
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

    public void createCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldsMap) {
        CustomField customField = customFieldRepository.save(new CustomField(customerRecord));
        customFieldsMap.forEach((key, value) -> {
            CustomFieldMapping customFieldMapping = new CustomFieldMapping(key, customField.setField(value), customField);
            customFieldMappingRepository.save(customFieldMapping);
        });
    }

    public List<Map<String, String>> mapCustomFields(int offset, int limit) {
        Page<CustomField> customFields = customFieldRepository.findAll(PageRequest.of(offset, limit));
        List<Map<String, String>> customFieldsMapList = new ArrayList<>();
        for (CustomField customField : customFields) {
            List<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByCustomFieldId(customField.getId());
            customFieldsMapList.add(convertCustomFieldsToMap(customField, customFieldMapping));
        }
        return customFieldsMapList;
    }

    public Map<String, String> mapCustomFields(long customerRecordId) {
        Optional<CustomField> customFieldOptional = customFieldRepository.findByCustomerRecordId(customerRecordId);
        if (customFieldOptional.isPresent()) {
            CustomField customField = customFieldOptional.get();
            List<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByCustomFieldId(customField.getId());
            return convertCustomFieldsToMap(customField, customFieldMapping);
        } else {
            throw new NoSuchElementException("There are no custom fields present in the database with Id of " + customerRecordId);
        }
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getColumnName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
