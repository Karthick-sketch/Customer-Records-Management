package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
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

    @Transactional
    public CustomerRecordDto createNewCustomerRecord(CustomerRecordDto customerRecordDto) {
        CustomerRecord customerRecord = customerRecordRepository.save(customerRecordDto.getDefaultFields());
        customFieldService.createCustomFields(customerRecord, customerRecordDto.getCustomFields());
        return customerRecordDto;
    }

    public List<CustomerRecordDto> fetchCustomerRecords(int pageNumber, int pageSize) {
        Page<CustomerRecord> customerRecords = customerRecordRepository.findAll(PageRequest.of(pageNumber, pageSize).withSort(Sort.by("email")));
        return customerRecords.stream()
                .map(cr -> new CustomerRecordDto(cr, customFieldService.mapCustomFields(cr.getAccountId(), cr.getId())))
                .toList();
    }

    public CustomerRecordDto fetchCustomerRecordById(long id) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findById(id);
        if (customerRecord.isEmpty()) {
            throw new NoSuchElementException("There is no record with the Id of " + id);
        }
        return new CustomerRecordDto(customerRecord.get(), customFieldService.mapCustomFields(customerRecord.get().getAccountId(), id));
    }

    public List<CustomerRecordDto> fetchCustomerRecordByAccountId(long accountId) {
        List<CustomerRecord> customerRecords = customerRecordRepository.findByAccountId(accountId);
        return customerRecords.stream()
                .map(cr -> new CustomerRecordDto(cr, customFieldService.mapCustomFields(accountId, cr.getId())))
                .toList();
    }
}
