package com.customerrecordsmanagement.customfields.service;

import com.customerrecordsmanagement.BadRequestException;
import com.customerrecordsmanagement.DuplicateEntryException;
import com.customerrecordsmanagement.EntityNotFoundException;
import com.customerrecordsmanagement.customfields.dto.CustomFieldMappingDTO;
import com.customerrecordsmanagement.customfields.entity.CustomField;
import com.customerrecordsmanagement.customfields.entity.CustomFieldMapping;
import com.customerrecordsmanagement.customfields.repository.CustomFieldMappingRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomFieldMappingService {
    private CustomFieldMappingRepository customFieldMappingRepository;
    private ModelMapper modelMapper;

    public List<CustomFieldMapping> fetchCustomFieldMappingByAccountId(long accountId) {
        return customFieldMappingRepository.findByAccountId(accountId);
    }

    // added unit test
    public List<CustomFieldMappingDTO> fetchCustomFieldMappingDTOByAccountId(long accountId) {
        return fetchCustomFieldMappingByAccountId(accountId).stream()
                .map(this::convertCustomFieldMappingToDTO)
                .toList();
    }

    public List<String> fetchCustomFieldNamesByAccountId(long accountId) {
        return customFieldMappingRepository.findCustomFieldNamesByAccountId(accountId);
    }

    // added unit test
    public CustomFieldMapping fetchCustomFieldMappingByIdAndAccountId(long id, long accountId) {
        Optional<CustomFieldMapping> customFieldMapping = customFieldMappingRepository.findByIdAndAccountId(id, accountId);
        if (customFieldMapping.isEmpty()) {
            throw new EntityNotFoundException("The custom field with the Id of '" + id + "' custom field is not exists.");
        }
        return customFieldMapping.get();
    }

    // added unit test
    public CustomFieldMapping saveCustomFieldMapping(@NonNull CustomFieldMapping customFieldMapping) {
        try {
            return customFieldMappingRepository.save(customFieldMapping);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("The custom field " + customFieldMapping.getCustomFieldName() + " is already present");
        }
    }

    // added unit test
    public CustomFieldMappingDTO createCustomFieldMappingByMap(long accountId, Map<String, String> customFieldMappingMap) {
        List<String> fields = CustomField.getFieldNames();
        fetchCustomFieldMappingByAccountId(accountId)
                .forEach(customFieldMapping -> fields.remove(customFieldMapping.getFieldName()));
        if (fields.isEmpty()) {
            throw new BadRequestException("Custom field limit exceed");
        }
        CustomFieldMapping customFieldMapping = convertMapToCustomFieldMapping(new CustomFieldMapping(), customFieldMappingMap);
        customFieldMapping.setFieldName(fields.get(0));
        return convertCustomFieldMappingToDTO(saveCustomFieldMapping(customFieldMapping));
    }

    // added unit test
    public CustomFieldMappingDTO updateCustomField(long id, long accountId, Map<String, String> customFieldMappingMap) {
        CustomFieldMapping originalCustomFieldMapping = fetchCustomFieldMappingByIdAndAccountId(id, accountId);
        CustomFieldMapping updatedCustomFieldMapping = convertMapToCustomFieldMapping(originalCustomFieldMapping, customFieldMappingMap);
        updatedCustomFieldMapping.setFieldName(originalCustomFieldMapping.getFieldName());
        return convertCustomFieldMappingToDTO(saveCustomFieldMapping(updatedCustomFieldMapping));
    }

    // added unit test
    public void deleteCustomField(long id, long accountId) {
        CustomFieldMapping customFieldMapping = fetchCustomFieldMappingByIdAndAccountId(id, accountId);
        customFieldMappingRepository.delete(customFieldMapping);
    }

    public static String findColumnNameByCustomFieldName(String columnName, List<CustomFieldMapping> customFieldMappings) {
        for (CustomFieldMapping customFieldMapping : customFieldMappings) {
            if (columnName.equals(customFieldMapping.getCustomFieldName())) {
                return customFieldMapping.getFieldName();
            }
        }
        throw new EntityNotFoundException("There is no custom field called '" + columnName + "'");
    }

    private CustomFieldMapping convertMapToCustomFieldMapping(CustomFieldMapping customFieldMapping, Map<String, String> customFieldMappingMap) {
        BeanWrapperImpl customFieldMappingBeanWrapper = new BeanWrapperImpl(customFieldMapping);
        customFieldMappingMap.forEach((key, value) -> {
            if (key.equals("customFieldName") || key.equals("dataType") || key.equals("required")) {
                customFieldMappingBeanWrapper.setPropertyValue(key, value);
            } else {
                throw new BadRequestException("Unknown property called '" + key + "'");
            }
        });
        return (CustomFieldMapping) customFieldMappingBeanWrapper.getWrappedInstance();
    }

    private CustomFieldMappingDTO convertCustomFieldMappingToDTO(CustomFieldMapping customFieldMapping) {
        return modelMapper.map(customFieldMapping, CustomFieldMappingDTO.class);
    }
}
