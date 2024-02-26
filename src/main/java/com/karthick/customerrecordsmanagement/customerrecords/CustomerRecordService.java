package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customfields.CustomFieldMapping;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldMappingService;
import com.karthick.customerrecordsmanagement.customfields.CustomFieldService;
import com.karthick.customerrecordsmanagement.exception.BadRequestException;
import com.karthick.customerrecordsmanagement.exception.EntityNotException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerRecordService {
    private CustomerRecordRepository customerRecordRepository;
    private CustomFieldService customFieldService;
    private CustomFieldMappingService customFieldMappingService;

    public List<CustomerRecordDTO> fetchCustomerRecords(long accountId, int pageNumber, int pageSize) {
        return customerRecordRepository.findAll(PageRequest.of(pageNumber, pageSize)).stream()
                .map(customerRecord -> new CustomerRecordDTO(customerRecord, customFieldService.reverseMapCustomFields(accountId, customerRecord.getId())))
                .toList();
    }

    public CustomerRecordDTO fetchCustomerRecordByIdAndAccountId(long id, long accountId) {
        Optional<CustomerRecord> customerRecord = customerRecordRepository.findByIdAndAccountId(id, accountId);
        if (customerRecord.isEmpty()) {
            throw new EntityNotException("There is no customer record with the Id of " + id);
        }
        return new CustomerRecordDTO(customerRecord.get(), customFieldService.reverseMapCustomFields(accountId, id));
    }

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
        customerRecord.setCustomField(customFieldService.createCustomField(customerRecordDTO));
        customerRecordDTO.setCustomerRecord(createCustomerRecord(customerRecord));
        return customerRecordDTO;
    }

    public int createAllCustomerRecords(long accountId, List<CustomerRecordDTO> customerRecordDTOs) {
        List<CustomFieldMapping> customFieldMappings = customFieldMappingService.fetchCustomFieldMappingAccountId(accountId);
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
                createCustomerRecord(customerRecord);
            } catch (BadRequestException e) {
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
}
