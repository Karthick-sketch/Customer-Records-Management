package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.CustomField;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Setter
public class CustomRecordDelimitedLineAggregator<T> extends DelimitedLineAggregator<T> {
    private FieldExtractor<T> fieldExtractor;
    private String delimiter = ",";
    private String quoteCharacter = "'";

    @Override
    public @NonNull String doAggregate(Object @NonNull [] fields) {
        if (fields[0] instanceof CustomerRecord customerRecords) {
            return castCustomerRecordToString(customerRecords);
        } else {
            return super.doAggregate(fields);
        }
    }

    private String castCustomerRecordToString(CustomerRecord customerRecord) {
        List<String> customerRecordValues = Arrays.stream(CustomerRecord.class.getDeclaredFields())
                .filter(field -> !field.getName().equals("id"))
                .peek(field -> field.setAccessible(true))
                .map(field -> field.getName().equals("customField")
                        ? castCustomFieldToString((CustomField) getValueFromField(customerRecord, field))
                        : wrapWithQuote(getValueFromField(customerRecord, field)))
                .toList();
        return String.join(delimiter, customerRecordValues);
    }

    private String castCustomFieldToString(CustomField customField) {
        List<String> nonFields = List.of("id", "accountId", "customerRecord");
        List<String> customFields = Arrays.stream(CustomField.class.getDeclaredFields())
                .filter(field -> !nonFields.contains(field.getName()))
                .peek(field -> field.setAccessible(true))
                .map(field -> wrapWithQuote(getValueFromField(customField, field)))
                .toList();

        return String.join(delimiter, customFields);
    }

    private Object getValueFromField(Object object, Field field) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String wrapWithQuote(Object object) {
        return quoteCharacter + object.toString() + quoteCharacter;
    }
}
