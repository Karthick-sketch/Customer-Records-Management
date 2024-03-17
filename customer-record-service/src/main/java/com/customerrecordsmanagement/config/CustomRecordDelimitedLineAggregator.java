package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.CustomField;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;

import java.util.List;
import java.util.Map;

@Setter
public class CustomRecordDelimitedLineAggregator<T> extends DelimitedLineAggregator<T> {
    private String delimiter = ",";
    private String quoteCharacter = "'";

    @Override
    public @NonNull String doAggregate(Object @NonNull [] fields) {
        if (fields[0] instanceof CustomerRecord customerRecords) {
            return convertCustomerRecordToString(customerRecords);
        } else {
            return super.doAggregate(fields);
        }
    }

    private String convertCustomerRecordToString(CustomerRecord customerRecord) {
        List<String> nonFields = List.of("id", "customField");
        Map<String, String> customerRecordsMap = convertObjectToMap(customerRecord);
        List<String> customerRecords = customerRecordsMap.entrySet().stream()
                .filter(entry -> !nonFields.contains(entry.getKey()))
                .map(entry -> wrapWithQuote(entry.getValue()))
                .toList();
        String customerRecordString = String.join(delimiter, customerRecords);
        return customerRecordString + convertCustomFieldToString(customerRecord.getCustomField());
    }

    private String convertCustomFieldToString(CustomField customField) {
        List<String> nonFields = List.of("id", "accountId", "customerRecord");
        Map<String, String> customFieldMap = convertObjectToMap(customField);
        List<String> customFields = customFieldMap.entrySet().stream()
                .filter(entry -> !nonFields.contains(entry.getKey()))
                .map(entry -> wrapWithQuote(entry.getValue()))
                .toList();
        return String.join(delimiter, customFields);
    }

    private String wrapWithQuote(Object object) {
        return quoteCharacter + object.toString() + quoteCharacter;
    }

    public Map<String, String> convertObjectToMap(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(object, new TypeReference<>() {});
    }
}
