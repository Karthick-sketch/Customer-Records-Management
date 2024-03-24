package com.customerrecordsmanagement;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.CustomField;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockObjects {
    public static CustomerRecord getCustomerRecord() {
        CustomerRecord customerRecord = createCustomerRecord();
        customerRecord.setCustomField(createCustomField(customerRecord));
        return customerRecord;
    }

    public static Map<String, String> getCustomFieldMap(CustomField customField) {
        return getCustomFieldMappingList().stream()
                .collect(Collectors.toMap(CustomFieldMapping::getCustomFieldName,
                        cfm -> customField.getValueByFieldName(cfm.getFieldName())));
    }

    public static CustomerRecord createCustomerRecord() {
        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setId(1);
        customerRecord.setAccountId(1);
        customerRecord.setFirstName("James");
        customerRecord.setLastName("Butt");
        customerRecord.setEmail("jbutt@email.com");
        customerRecord.setPhoneNumber("504-845-1427");
        customerRecord.setCompanyName("Benton, John B Jr");
        customerRecord.setAddress("6649 N Blue Gum St");
        customerRecord.setCity("New Orleans");
        customerRecord.setState("LA");
        customerRecord.setCountry("Orleans");
        customerRecord.setZipcode("70116");
        return customerRecord;
    }

    public static CustomField createCustomField(CustomerRecord customerRecord) {
        CustomField customField = new CustomField(customerRecord);
        customField.setField1("cf-value1");
        customField.setField2("cf-value2");
        customField.setField3("cf-value3");
        customField.setField4("cf-value4");
        customField.setField5("cf-value5");
        customField.setField6("cf-value6");
        customField.setField7("cf-value7");
        customField.setField8("cf-value8");
        customField.setField9("cf-value9");
        customField.setField10("cf-value10");
        return customField;
    }

    public static CustomFieldMapping getCustomFieldMapping(long id) {
        CustomFieldMapping customFieldMapping = new CustomFieldMapping();
        customFieldMapping.setId(id);
        customFieldMapping.setAccountId(1L);
        customFieldMapping.setFieldName("field" + id);
        customFieldMapping.setCustomFieldName("cf" + id);
        customFieldMapping.setDataType("text");
        return customFieldMapping;
    }

    public static List<CustomFieldMapping> getCustomFieldMappingList() {
        return IntStream.range(1, 10).mapToObj(MockObjects::getCustomFieldMapping).toList();
    }
}