package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;

    @Transactional
    public CustomerRecordDto createNewCustomerRecord(CustomerRecordDto customerRecordDto) {
        CustomerRecord customerRecord = customerRecordRepository.save(customerRecordDto.getDefaultFields());
        customFieldService.createCustomFields(customerRecord, customerRecordDto.getCustomFields());
        return customerRecordDto;
    }

    public List<CustomerRecordDto> fetchCustomerRecords(int pageNumber, int pageSize) {
        List<CustomerRecord> defaultFields = customerRecordRepository.findAll(PageRequest.of(pageNumber, pageSize)).stream().toList();
        List<Map<String, String>> customFields = customFieldService.mapCustomFields(pageNumber, pageSize);
        return IntStream.range(0, defaultFields.size())
                .mapToObj(i -> new CustomerRecordDto(defaultFields.get(i), customFields.get(i)))
                .toList();
    }

    public CustomerRecordDto fetchCustomerRecordById(long id) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findById(id);
        if (customerRecord.isPresent()) {
            return new CustomerRecordDto(customerRecord.get(), customFieldService.mapCustomFields(id));
        } else {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
    }

    public List<CustomerRecordDto> fetchCustomerRecordByAccountId(long accountId) {
        List<CustomerRecord> customerRecords = customerRecordRepository.findByAccountId(accountId);
        List<Map<String, String>> customFields = customFieldService.mapCustomFieldsByAccountId(accountId);
        return IntStream.range(0, customerRecords.size())
                .mapToObj(i -> new CustomerRecordDto(customerRecords.get(i), customFields.get(i)))
                .toList();
    }
}
