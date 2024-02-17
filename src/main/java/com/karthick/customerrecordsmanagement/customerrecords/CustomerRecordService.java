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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;

    public List<CustomerRecordDto> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        Page<CustomerRecord> customerRecords = customerRecordRepository.findByAccountId(accountId, PageRequest.of(pageNumber, pageSize).withSort(Sort.by(Constants.ORDER_BY_EMAIL)));
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

    public CustomerRecordDto createCustomerRecord(CustomerRecordDto customerRecordDto) {
        customerRecordDto.setCustomerRecord(customerRecordRepository.save(mapCustomerRecord(customerRecordDto)));
        return customerRecordDto;
    }

    @Transactional
    public void createAllCustomerRecord(List<CustomerRecordDto> customerRecordDtos) {
        List<CustomerRecord> customerRecords = customerRecordDtos.stream()
                .map(this::mapCustomerRecord)
                .toList();
        customerRecordRepository.saveAll(customerRecords);
    }

    private CustomerRecord mapCustomerRecord(CustomerRecordDto customerRecordDto) {
        CustomerRecord customerRecord = customerRecordDto.getCustomerRecord();
        customerRecord.setCustomerCustomFieldValues(customerRecordDto.getCustomFields().entrySet().stream()
                .map(entry -> {
                    CustomField customField = customFieldService.fetchCustomFieldByAccountIdAndFieldName(customerRecord.getAccountId(), entry.getKey());
                    return new CustomerCustomFieldValue(customerRecord.getAccountId(), entry.getValue(), customField, customerRecord);
                }).toList());
        return customerRecord;
    }
}
