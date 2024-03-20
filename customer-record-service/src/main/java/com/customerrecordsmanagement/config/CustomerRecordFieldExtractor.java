package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Setter
public class CustomerRecordFieldExtractor implements FieldExtractor<CustomerRecord> {
    private String[] fieldNames;
    private List<CustomFieldMapping> customFieldMappingList;

    public void afterPropertiesSet() {
        Assert.notNull(fieldNames, "The 'fieldNames' property must be set.");
        Assert.notNull(customFieldMappingList, "The 'customFieldMappingList' property must be set.");
    }

    @Override
    public Object @NonNull [] extract(@NonNull CustomerRecord customerRecord) {
        afterPropertiesSet();
        BeanWrapper customerRecordBeanWrapper = new BeanWrapperImpl(customerRecord);
        BeanWrapper customFieldBeanWrapper = new BeanWrapperImpl(customerRecord.getCustomField());

        List<String> customerRecordFields = CustomerRecord.getFields();
        return Arrays.stream(fieldNames)
                .map(fieldName -> customerRecordFields.contains(fieldName)
                        ? customerRecordBeanWrapper.getPropertyValue(fieldName)
                        : customFieldBeanWrapper.getPropertyValue(getCustomFieldName(fieldName)))
                .toArray();
    }

    private String getCustomFieldName(String fieldName) {
        for (CustomFieldMapping customFieldMapping : customFieldMappingList) {
            if (fieldName.equals(customFieldMapping.getCustomFieldName())) {
                return customFieldMapping.getFieldName();
            }
        }
        throw new RuntimeException("Unknown field '" + fieldName + "'");
    }
}
