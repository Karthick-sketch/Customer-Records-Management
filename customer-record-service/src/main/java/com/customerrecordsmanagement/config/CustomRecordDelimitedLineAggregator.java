package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.CustomField;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
public class CustomRecordDelimitedLineAggregator<T> extends DelimitedLineAggregator<T> {
    private FieldExtractor<T> fieldExtractor;
    private List<String> customFieldNames;

    @Override
    public @NonNull String doAggregate(Object @NonNull [] fields) {
        if (fields[0] instanceof CustomerRecord customerRecords) {
            return castCustomerRecordToString(customerRecords);
        } else {
            return super.doAggregate(fields);
        }
    }

    private String castCustomerRecordToString(CustomerRecord customerRecord) {
        List<String> customerRecordValues = new ArrayList<>();
        Field[] fields = CustomerRecord.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getName().equals("customField")) {
                    if (field.get(customerRecord) instanceof CustomField customField) {
                        String customFieldValue = castCustomFieldToString(customField);
                        customerRecordValues.add(customFieldValue);
                    }
                } else if (!field.getName().equals("id")) {
                    customerRecordValues.add("'" + field.get(customerRecord).toString() + "'");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return String.join(",", customerRecordValues);
    }

    private String castCustomFieldToString(CustomField customField) {
        List<String> nonFields = List.of("id", "accountId", "customerRecord");
        Map<String, String> customFields = Arrays.stream(CustomField.class.getDeclaredFields())
                .filter(field -> !nonFields.contains(field.getName()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, field -> {
                    try {
                        return "'" + field.get(customField).toString() + "'";
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
        List<String> customFieldValues = customFieldNames.stream()
                .map(customFields::get)
                .toList();

        return String.join(",", customFieldValues);
    }
}
