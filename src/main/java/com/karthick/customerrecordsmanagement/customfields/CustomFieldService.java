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

    public List<Map<String, String>> mapCustomFields(int pageNumber, int pageSize) {
        Page<CustomField> customFields = customFieldRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return customFields.stream()
                .map(cf -> convertCustomFieldsToMap(cf, customFieldMappingRepository.findByCustomFieldId(cf.getId())))
                .toList();
    }

    public Map<String, String> mapCustomFields(long customerRecordId) {
        Optional<CustomField> customFieldOptional = customFieldRepository.findByCustomerRecordId(customerRecordId);
        if (customFieldOptional.isPresent()) {
            CustomField customField = customFieldOptional.get();
            return convertCustomFieldsToMap(customField, customFieldMappingRepository.findByCustomFieldId(customField.getId()));
        } else {
            throw new NoSuchElementException("There are no custom fields present in the database with Id of " + customerRecordId);
        }
    }

    public List<Map<String, String>> mapCustomFieldsByAccountId(long accountId) {
        return customFieldRepository.findByAccountId(accountId).stream()
                .map(customField -> {
                    List<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByCustomFieldId(customField.getId());
                    return convertCustomFieldsToMap(customField, customFieldMapping);
                }).toList();
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getColumnName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
