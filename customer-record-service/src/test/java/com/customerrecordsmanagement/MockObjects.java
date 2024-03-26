package com.customerrecordsmanagement;

import com.customerrecordsmanagement.contactlist.dto.ContactListAddDTO;
import com.customerrecordsmanagement.contactlist.dto.ContactListDTO;
import com.customerrecordsmanagement.contactlist.entity.ContactList;
import com.customerrecordsmanagement.contactlist.entity.ContactListMapping;
import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.CustomField;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingDTO;

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

    public static List<CustomFieldMapping> getCustomFieldMappingList() {
        return IntStream.range(1, 11).mapToObj(MockObjects::getCustomFieldMapping).toList();
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

    public static List<CustomFieldMappingDTO> getCustomFieldMappingDtoList() {
        return IntStream.range(1, 11).mapToObj(MockObjects::getCustomFieldMappingDTO).toList();
    }

    public static CustomFieldMappingDTO getCustomFieldMappingDTO(long id) {
        CustomFieldMappingDTO customFieldMappingDTO = new CustomFieldMappingDTO();
        customFieldMappingDTO.setId(id);
        customFieldMappingDTO.setAccountId(1L);
        customFieldMappingDTO.setCustomFieldName("cf" + id);
        customFieldMappingDTO.setDataType("text");
        return customFieldMappingDTO;
    }

    public static CustomerRecord getUpdatedCustomerRecord() {
        CustomerRecord customerRecord = getCustomerRecord();
        customerRecord.setEmail("jbutt@hotmail.com");
        customerRecord.getCustomField().setField1("custom-field-value");
        return customerRecord;
    }

    public static Map<String, String> getValidCustomerRecordFieldsForUpdate() {
        return Map.of("email", "jbutt@hotmail.com", "cf1", "custom-field-value");
    }

    public static Map<String, String> getInvalidCustomerRecordFieldsForUpdate() {
        return Map.of("amount", "100.0 INR");
    }

    public static ContactList getContactList() {
        ContactList contactList = new ContactList(1L, "contact-list");
        contactList.setId(1L);
        contactList.setContactListMappings(List.of(getContactListMapping(contactList)));
        return contactList;
    }

    public static ContactListMapping getContactListMapping(ContactList contactList) {
        ContactListMapping contactListMapping = new ContactListMapping(1L, contactList, getCustomerRecord());
        contactListMapping.setId(1);
        return contactListMapping;
    }

    public static ContactListAddDTO getContactListAddDTO() {
        return new ContactListAddDTO(1L, 1L, List.of(1L));
    }

    public static ContactListDTO getContactListDTO() {
        return new ContactListDTO(1L, "contact-list", List.of(getCustomerRecord()));
    }
}
