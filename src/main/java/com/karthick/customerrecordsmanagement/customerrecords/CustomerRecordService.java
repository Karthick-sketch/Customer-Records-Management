package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomField;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.customfields.CustomerCustomFieldValue;
import com.karthick.customerrecordsmanagement.utils.Constants;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;

    public List<CustomerRecordDTO> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        Page<CustomerRecord> customerRecords = customerRecordRepository.findByAccountId(accountId, PageRequest.of(pageNumber, pageSize).withSort(Sort.by(Constants.EMAIL_FIELD)));
        return customerRecords.stream()
                .map(cr -> new CustomerRecordDTO(cr, customFieldService.mapCustomFields(cr.getAccountId(), cr.getId())))
                .toList();
    }

    public CustomerRecordDTO fetchCustomerRecordByIdAndAccountId(long id, long accountId) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findByIdAndAccountId(id, accountId);
        if (customerRecord.isEmpty()) {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
        return new CustomerRecordDTO(customerRecord.get(), customFieldService.mapCustomFields(accountId, id));
    }

    public CustomerRecordDTO createCustomerRecord(CustomerRecordDTO customerRecordDTO) {
        List<CustomField> customFields = customFieldService.fetchCustomFieldsByAccountId(customerRecordDTO.getCustomerRecord().getAccountId());
        customerRecordDTO.setCustomerRecord(customerRecordRepository.save(mapCustomerRecord(customerRecordDTO, customFields)));
        return customerRecordDTO;
    }

    public void createAllCustomerRecord(long accountId, List<CustomerRecordDTO> customerRecordDTOs) {
        List<CustomField> customFields = customFieldService.fetchCustomFieldsByAccountId(accountId);
        List<CustomerRecord> customerRecords = customerRecordDTOs.stream()
                .map(customerRecordDTO -> mapCustomerRecord(customerRecordDTO, customFields))
                .toList();
        customerRecordRepository.saveAll(customerRecords);
    }

    private CustomerRecord mapCustomerRecord(CustomerRecordDTO customerRecordDTO, List<CustomField> customFields) {
        CustomerRecord customerRecord = customerRecordDTO.getCustomerRecord();
        customerRecord.setCustomerCustomFieldValues(customerRecordDTO.getCustomFields().entrySet().stream()
                .map(entry -> {
                    CustomField customField = findCustomFieldByName(entry.getKey(), customFields);
                    return new CustomerCustomFieldValue(customerRecord.getAccountId(), entry.getValue(), customField, customerRecord);
                }).toList());
        return customerRecord;
    }

    private CustomField findCustomFieldByName(String fieldName, List<CustomField> customFields) {
        for (CustomField customField : customFields) {
            if (fieldName.equals(customField.getFieldName())) {
                return customField;
            }
        }
        throw new NoSuchElementException("There is no custom field called " + fieldName);
    }
}
