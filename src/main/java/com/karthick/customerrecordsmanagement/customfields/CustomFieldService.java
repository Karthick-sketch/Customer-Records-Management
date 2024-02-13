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
    private CustomerCustomFieldValueRepository customerCustomFieldValueRepository;

    public List<CustomField> fetchCustomFields(long customerRecordId) {
        return customFieldRepository.findByCustomerRecordId(customerRecordId);
    }

    public void createCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldsMap) {
        customFieldsMap.forEach((key, value) -> {
            CustomField customField = customFieldRepository.save(new CustomField(key, customerRecord));
            customerCustomFieldValueRepository.save(new CustomerCustomFieldValue(value, customField, customerRecord));
        });
    }

    public Map<String, String> mapCustomFields(long customerRecordId) {
        return fetchCustomFields(customerRecordId).stream()
                .collect(Collectors.toMap(CustomField::getFieldName,
                        cf -> customerCustomFieldValueRepository.findByCustomerRecordIdAndCustomFieldId(customerRecordId, cf.getId()).getCustomFieldValue()
                ));
    }
}
