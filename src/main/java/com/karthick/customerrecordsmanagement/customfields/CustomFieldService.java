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

    public void createCustomFields(CustomerRecord customerRecord, Map<String, String> customFieldsMap) {
        customFieldsMap.forEach((key, value) -> {
            Optional<CustomField> customField = customFieldRepository.findByAccountIdAndFieldName(customerRecord.getAccountId(), key);
            if (customField.isEmpty()) {
                throw new NoSuchElementException("There is no custom field called " + key);
            }
            CustomerCustomFieldValue customFieldValue = new CustomerCustomFieldValue(customerRecord.getAccountId(), value, customField.get(), customerRecord);
            customerCustomFieldValueRepository.save(customFieldValue);
        });
    }

    public Map<String, String> mapCustomFields(long accountId, long customerRecordId) {
        return fetchCustomFieldsByAccountId(accountId).stream()
                .collect(Collectors.toMap(CustomField::getFieldName,
                        cf -> customerCustomFieldValueRepository.findByCustomerRecordIdAndCustomFieldId(customerRecordId, cf.getId()).getCustomFieldValue()
                ));
    }
}
