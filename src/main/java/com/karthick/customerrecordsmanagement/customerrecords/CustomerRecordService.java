package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomField;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldMapping;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldMappingService;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;
    private CustomFieldMappingService customFieldMappingService;

    public CustomerRecord createCustomerRecord(CustomerRecord customerRecord) {
        try {
            return customerRecordRepository.save(customerRecord);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("A contact with the this " + customerRecord.getEmail() + " email is already present");
        }
    }

    @Transactional
    public CustomerRecordDTO createNewCustomerRecord(CustomerRecordDTO customerRecordDTO) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        customerRecord.setCustomField(customFieldService.createCustomField(customerRecord, customerRecordDTO.getCustomFields()));
        customerRecordDTO.setCustomerRecord(createCustomerRecord(customerRecord));
        return customerRecordDTO;
    }

    public int createAllCustomerRecords(long accountId, List<CustomerRecordDTO> customerRecordDTOs) {
        List<CustomFieldMapping> customFields = customFieldMappingService.fetchCustomFieldMappingAccountId(accountId);
        List<CustomerRecord> customerRecords = customerRecordDTOs.stream()
                .map(customerRecordDTO -> mapCustomerRecord(customerRecordDTO, customFields))
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
                createCustomerRecord(customerRecord);
            } catch (BadRequestException e) {
                duplicateRecords++;
            }
        }
        return duplicateRecords;
    }

    public List<CustomerRecordDTO> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        return customerRecordRepository.findAll(PageRequest.of(pageNumber, pageSize)).stream()
                .map(customerRecord -> new CustomerRecordDTO(customerRecord, customFieldService.mapCustomFields(accountId, customerRecord.getId())))
                .toList();
    }

    public CustomerRecordDTO fetchCustomerRecordByIdAndAccountId(long id, long accountId) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findByIdAndAccountId(id, accountId);
        if (customerRecord.isEmpty()) {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
        return new CustomerRecordDTO(customerRecord.get(), customFieldService.mapCustomFields(accountId, id));
    }

    private CustomerRecord mapCustomerRecord(CustomerRecordDTO customerRecordDTO, List<CustomFieldMapping> customFieldMappingList) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        CustomField customField = new CustomField(customerRecord);
        customerRecordDTO.getCustomFields().forEach((key, value) -> {
            boolean isFieldSet = customField.setField(findFieldNameByColumnName(key, customFieldMappingList), value);
            if (!isFieldSet) {
                throw new NoSuchElementException("There is no custom field called " + key);
            }
        });
        customerRecord.setCustomField(customField);
        return customerRecord;
    }

    private String findFieldNameByColumnName(String columnName, List<CustomFieldMapping> customFieldMappingList) {
        for (CustomFieldMapping customFieldMapping : customFieldMappingList) {
            if (columnName.equals(customFieldMapping.getColumnName())) {
                return customFieldMapping.getFieldName();
            }
        }
        throw new NoSuchElementException("There is no custom field called " + columnName);
    }
}
