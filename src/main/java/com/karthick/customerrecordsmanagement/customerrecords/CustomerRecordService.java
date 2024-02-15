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
