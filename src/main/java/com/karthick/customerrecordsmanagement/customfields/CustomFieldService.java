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

    public CustomField createCustomField(CustomField customField) {
        return customFieldRepository.save(customField);
    }

    public List<CustomField> fetchCustomFieldsByAccountId(long accountId) {
        return customFieldRepository.findByAccountId(accountId);
    }

    public CustomField fetchCustomFieldByAccountIdAndFieldName(long accountId, String fieldName) {
        Optional<CustomField> customField = customFieldRepository.findByAccountIdAndFieldName(accountId, fieldName);
        if (customField.isEmpty()) {
            throw new NoSuchElementException("There is no custom field called " + fieldName);
        }
        return customField.get();
    }

    public void createCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldsMap) {
        customFieldsMap.forEach((key, value) -> {
            CustomField customField = fetchCustomFieldByAccountIdAndFieldName(customerRecord.getAccountId(), key);
            CustomerCustomFieldValue customFieldValue = new CustomerCustomFieldValue(customerRecord.getAccountId(), value, customField, customerRecord);
            customerCustomFieldValueRepository.save(customFieldValue);
        });
    }

    public Map<String, String> mapCustomFields(long accountId, long customerRecordId) {
        return fetchCustomFieldsByAccountId(accountId).stream()
                .collect(Collectors.toMap(CustomField::getFieldName,
                        cf -> customerCustomFieldValueRepository.findByAccountIdAndCustomerRecordIdAndCustomFieldId(accountId, customerRecordId, cf.getId()).getCustomFieldValue()
                ));
    }
}
