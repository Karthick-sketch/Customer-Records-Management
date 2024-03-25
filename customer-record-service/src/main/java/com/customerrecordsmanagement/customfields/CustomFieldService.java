package com.customerrecordsmanagement.customfields;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordDTO;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomFieldService {
    private CustomFieldRepository customFieldRepository;
    private CustomFieldMappingService customFieldMappingService;

    // added unit test
    public CustomField createCustomFieldByCustomField(@NonNull CustomField customField) {
        return customFieldRepository.save(customField);
    }

    // added unit test
    public CustomField createCustomFieldByCustomRecordDTO(CustomerRecordDTO customerRecordDTO) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        List<CustomFieldMapping> customFieldMappings = customFieldMappingService.fetchCustomFieldMappingByAccountId(customerRecord.getAccountId());
        return createCustomFieldByCustomField(mapCustomFields(customerRecord, customerRecordDTO.getCustomFields(), customFieldMappings));
    }

    // added unit test
    public CustomField mapCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldMap, List<CustomFieldMapping> customFieldMappings) {
        CustomField customField = new CustomField(customerRecord);
        customFieldMap.forEach((key, value) -> {
            String field = CustomFieldMappingService.findColumnNameByCustomFieldName(key, customFieldMappings);
            customField.setField(field, value);
        });
        return customField;
    }

    // added unit test
    public Map<String, String> reverseMapCustomFields(@NonNull CustomField customField) {
        List<CustomFieldMapping> customFieldMappings = customFieldMappingService.fetchCustomFieldMappingByAccountId(customField.getAccountId());
        if (customFieldMappings.isEmpty()) {
            return null;
        }
        return convertCustomFieldsToMap(customField, customFieldMappings);
    }

    private Map<String, String> convertCustomFieldsToMap(CustomField customField, List<CustomFieldMapping> customFieldMapping) {
        return customFieldMapping.stream()
                .collect(Collectors.toMap(CustomFieldMapping::getCustomFieldName, cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }
}
