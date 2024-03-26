package com.customerrecordsmanagement.customerrecords;

import com.customerrecordsmanagement.DuplicateEntryException;
import com.customerrecordsmanagement.EntityNotFoundException;
import com.customerrecordsmanagement.customfields.CustomField;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import com.customerrecordsmanagement.customfields.CustomFieldService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;
    private CustomFieldMappingService customFieldMappingService;
    private ObjectMapper objectMapper;

    public String generateBase64Code(long accountId) {
        Base64.Encoder encoder = Base64.getEncoder();
        String accountIdStr = "accountId=" + accountId;
        return encoder.encodeToString(accountIdStr.getBytes(StandardCharsets.UTF_8));
    }

    public long decodeBase64Code(String base64Code) {
        Base64.Decoder decoder = Base64.getDecoder();
        String decodedAccountId = new String(decoder.decode(base64Code));
        return Long.parseLong(decodedAccountId.substring(decodedAccountId.indexOf("=")));
    }

    public CustomerRecordDTO createCustomerRecordFromMap(String base64, Map<String, String> customerRecordMap) {
        CustomerRecord customerRecord = convertMapToCustomerRecord(decodeBase64Code(base64), customerRecordMap);
        return convertCustomerRecordToCustomerRecordDTO(saveCustomerRecord(customerRecord));
    }

    public CustomerRecord convertMapToCustomerRecord(long accountId, Map<String, String> stringMap) {
        List<String> customerRecordFields = CustomerRecord.getFields();
        List<CustomFieldMapping> customFieldMappingList = customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId);
        Map<String, String> customerRecordMap = customerRecordFields.stream()
                .filter(field -> stringMap.get(field) != null)
                .collect(Collectors.toMap(field -> field, stringMap::get));
        Map<String, String> customFieldMap = stringMap.entrySet().stream()
                .filter(entry -> !(customerRecordFields.contains(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        CustomerRecord customerRecord = mapToCustomerRecord(customerRecordMap);
        customerRecord.setAccountId(accountId);
        customerRecord.setCustomField(customFieldService.mapCustomFields(customerRecord, customFieldMap, customFieldMappingList));
        return customerRecord;
    }

    public List<CustomerRecordDTO> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        return customerRecordRepository.findByAccountId(accountId, PageRequest.of(pageNumber, pageSize)).stream()
                .map(customerRecord -> new CustomerRecordDTO(customerRecord, customFieldService.reverseMapCustomFields(customerRecord.getCustomField())))
                .toList();
    }

    // added unit test
    public CustomerRecord fetchCustomerRecordByIdAndAccountId(long id, long accountId) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findByIdAndAccountId(id, accountId);
        if (customerRecord.isEmpty()) {
            throw new EntityNotFoundException("There is no customer record with the Id of " + id);
        }
        return customerRecord.get();
    }

    public CustomerRecordDTO fetchCustomerRecordAndCustomFieldsByIdAndAccountId(long id, long accountId) {
        return convertCustomerRecordToCustomerRecordDTO(fetchCustomerRecordByIdAndAccountId(id, accountId));
    }

    public CustomerRecordDTO convertCustomerRecordToCustomerRecordDTO(CustomerRecord customerRecord) {
        return new CustomerRecordDTO(customerRecord, customFieldService.reverseMapCustomFields(customerRecord.getCustomField()));
    }

    // added unit test
    public CustomerRecord saveCustomerRecord(@NonNull CustomerRecord customerRecord) {
        try {
            return customerRecordRepository.save(customerRecord);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("A contact with the this " + customerRecord.getEmail() + " email is already present");
        }
    }

    // added unit test
    @Transactional("transactionManager")
    public CustomerRecordDTO createCustomerRecordByDTO(CustomerRecordDTO customerRecordDTO) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        customerRecord.setCustomField(customFieldService.createCustomFieldByCustomRecordDTO(customerRecordDTO));
        CustomerRecord createdCustomerRecord = saveCustomerRecord(customerRecord);
        return new CustomerRecordDTO(createdCustomerRecord, customFieldService.reverseMapCustomFields(createdCustomerRecord.getCustomField()));
    }

    // added unit test
    public CustomerRecordDTO updateCustomerRecord(long id, long accountId, Map<String, String> customerRecordUpdate) {
        CustomerRecord customerRecord = fetchCustomerRecordByIdAndAccountId(id, accountId);
        BeanWrapperImpl customerRecordBeanWrapper = new BeanWrapperImpl(customerRecord);
        BeanWrapperImpl customFieldBeanWrapper = new BeanWrapperImpl(customerRecord.getCustomField());

        List<String> customerRecordFieldNames = CustomerRecord.getFields();
        List<CustomFieldMapping> customFieldMappingList = customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId);
        customerRecordUpdate.forEach((key, value) -> {
            if (!key.equals("accountId") && customerRecordFieldNames.contains(key)) {
                customerRecordBeanWrapper.setPropertyValue(key, value);
            } else {
                String fieldName = CustomFieldMappingService.findColumnNameByCustomFieldName(key, customFieldMappingList);
                customFieldBeanWrapper.setPropertyValue(fieldName, value);
            }
        });

        CustomerRecord updatedCustomerRecord = (CustomerRecord) customerRecordBeanWrapper.getWrappedInstance();
        CustomField updatedCustomField = (CustomField) customFieldBeanWrapper.getWrappedInstance();
        updatedCustomerRecord.setCustomField(updatedCustomField);

        return convertCustomerRecordToCustomerRecordDTO(saveCustomerRecord(updatedCustomerRecord));
    }

    // added unit test
    public void deleteCustomerRecordById(long id, long accountId) {
        CustomerRecord customerRecord = fetchCustomerRecordByIdAndAccountId(id, accountId);
        customerRecordRepository.delete(customerRecord);
    }

    public int createAllCustomerRecords(long accountId, List<CustomerRecordDTO> customerRecordDTOs) {
        List<CustomFieldMapping> customFieldMappings = customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId);
        List<CustomerRecord> customerRecords = customerRecordDTOs.stream()
                .map(customerRecordDTO -> mapCustomerRecord(customerRecordDTO, customFieldMappings))
                .toList();
        int uploadedRecords = customerRecords.size();
        try {
            customerRecordRepository.saveAll(customerRecords);
        } catch (DataIntegrityViolationException e) {
            uploadedRecords -= createCustomerRecords(customerRecords);
        }
        return uploadedRecords;
    }

    private int createCustomerRecords(List<CustomerRecord> customerRecords) {
        int duplicateRecords = 0;
        for (CustomerRecord customerRecord : customerRecords) {
            try {
                saveCustomerRecord(customerRecord);
            } catch (DuplicateEntryException e) {
                duplicateRecords++;
            }
        }
        return duplicateRecords;
    }

    private CustomerRecord mapCustomerRecord(CustomerRecordDTO customerRecordDTO, List<CustomFieldMapping> customFieldMappings) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        customerRecord.setCustomField(customFieldService.mapCustomFields(customerRecord, customerRecordDTO.getCustomFields(), customFieldMappings));
        return customerRecord;
    }

    private CustomerRecord mapToCustomerRecord(Map<String, String> customerRecordMap) {
        return objectMapper.convertValue(customerRecordMap, CustomerRecord.class);
    }
}
