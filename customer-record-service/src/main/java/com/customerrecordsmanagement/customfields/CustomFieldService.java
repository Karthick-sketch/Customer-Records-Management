package com.customerrecordsmanagement.customfields;

import com.customerrecordsmanagement.EntityNotException;
import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordDTO;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomFieldService {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingService customFieldMappingService;

    public CustomField createCustomField(@NonNull CustomField customField) {
        return customFieldRepository.save(customField);
    }

    public CustomField createCustomField(CustomerRecordDTO customerRecordDTO) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        List<CustomFieldMapping> customFieldMappings = customFieldMappingService.fetchCustomFieldMappingByAccountId(customerRecord.getAccountId());
        return createCustomField(mapCustomFields(customerRecord, customerRecordDTO.getCustomFields(), customFieldMappings));
    }

    public CustomField mapCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldMap, List<CustomFieldMapping> customFieldMappings) {
        CustomField customField = new CustomField(customerRecord);
        customFieldMap.forEach((key, value) -> customField.setField(findColumnNameByCustomFieldName(key, customFieldMappings), value));
        return customField;
    }

    public Map<String, String> reverseMapCustomFields(long accountId, long customerRecordId) {
        List<CustomFieldMapping> customFieldMappings = customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId);
        Optional<CustomField> customField = customFieldRepository.findByCustomerRecordId(customerRecordId);
        if (customFieldMappings.isEmpty() || customField.isEmpty()) {
            return null;
        }
        return convertCustomFieldsToMap(customField.get(), customFieldMappings);
    }

    private String findColumnNameByCustomFieldName(String columnName, List<CustomFieldMapping> customFieldMappings) {
        for (CustomFieldMapping customFieldMapping : customFieldMappings) {
            if (columnName.equals(customFieldMapping.getCustomFieldName())) {
                return customFieldMapping.getFieldName();
            }
        }
        throw new EntityNotException("There is no custom field called " + columnName);
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getCustomFieldName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
