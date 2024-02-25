package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;

    @Transactional
    public CustomerRecordDTO createNewCustomerRecord(CustomerRecordDTO customerRecordDTO) {
        CustomerRecord customerRecord = customerRecordRepository.save(customerRecordDTO.getDefaultFields());
        customFieldService.createCustomFields(customerRecord, customerRecordDTO.getCustomFields());
        return customerRecordDTO;
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
}
