package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomField;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.customfields.CustomerCustomFieldValue;
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

    public CustomerRecordDto createCustomerRecord(CustomerRecordDto customerRecordDto) {
        CustomerRecord customerRecord = customerRecordDto.getCustomerRecord();
        long accountId = customerRecord.getAccountId();
        List<CustomerCustomFieldValue> customFieldValues = customerRecordDto.getCustomFields().entrySet().stream()
                .map(entry -> {
                    CustomField customField = customFieldService.fetchCustomFieldByAccountIdAndFieldName(accountId, entry.getKey());
                    return new CustomerCustomFieldValue(accountId, entry.getValue(), customField, customerRecord);
                }).toList();
        customerRecord.setCustomerCustomFieldValues(customFieldValues);
        customerRecordRepository.save(customerRecord);
        return customerRecordDto;
    }

    public List<CustomerRecordDto> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        Page<CustomerRecord> customerRecords = customerRecordRepository.findByAccountId(accountId, PageRequest.of(pageNumber, pageSize).withSort(Sort.by("email")));
        return customerRecords.stream()
                .map(cr -> new CustomerRecordDto(cr, customFieldService.mapCustomFields(cr.getAccountId(), cr.getId())))
                .toList();
    }

    public CustomerRecordDto fetchCustomerRecordByIdAndAccountId(long id, long accountId) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findByIdAndAccountId(id, accountId);
        if (customerRecord.isEmpty()) {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
        return new CustomerRecordDto(customerRecord.get(), customFieldService.mapCustomFields(accountId, id));
    }
}
