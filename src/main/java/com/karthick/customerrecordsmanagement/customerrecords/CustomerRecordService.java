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

    public List<CustomerRecordDto> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        List<CustomerRecord> defaultFields = customerRecordRepository.findAll(PageRequest.of(pageNumber, pageSize)).stream().toList();
        List<Map<String, String>> customFields = customFieldService.mapCustomFieldsByPagination(accountId, pageNumber, pageSize);
        return IntStream.range(0, defaultFields.size())
                .mapToObj(i -> new CustomerRecordDto(defaultFields.get(i), customFields.get(i)))
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
