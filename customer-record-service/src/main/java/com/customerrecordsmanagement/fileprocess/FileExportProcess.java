package com.customerrecordsmanagement.fileprocess;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordDTO;
import com.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class FileExportProcess {
    private CustomerRecordService customerRecordService;
    private CustomFieldMappingService customFieldMappingService;

    private final Logger logger = Logger.getLogger(FileExportProcess.class.getName());

    public void writeCustomerRecordDataToCsvFile(long accountId, String filePath) {
        List<String> customerRecordFieldNames = getCustomerRecordFieldNames(accountId);
        List<Map<String, String>> customerRecordMaps = convertCustomerRecordToMap(accountId);
        String[] headers = customerRecordFieldNames.toArray(String[]::new);
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            csvWriter.writeNext(headers);
            for (Map<String, String> customerRecordMap : customerRecordMaps) {
                csvWriter.writeNext(customerRecordFieldNames.stream()
                        .map(customerRecordMap::get)
                        .toArray(String[]::new));
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private List<String> getCustomerRecordFieldNames(long accountId) {
        List<String> defaultFields = CustomerRecord.getFields();
        List<String> customFieldNames = customFieldMappingService.fetchCustomFieldNamesByAccountId(accountId);
        return Stream.concat(defaultFields.stream(), customFieldNames.stream())
                .toList();
    }

    private List<Map<String, String>> convertCustomerRecordToMap(long accountId) {
        return customerRecordService.fetchCustomerRecordsByAccountId(accountId).stream()
                .map(this::mapCustomerRecordDtoToMap)
                .toList();
    }

    private Map<String, String> mapCustomerRecordDtoToMap(CustomerRecordDTO customerRecordDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, String> customerRecordMap = objectMapper.convertValue(customerRecordDTO.getCustomerRecord(), new TypeReference<>() {});
        customerRecordMap.putAll(customerRecordDTO.getCustomFields());
        return customerRecordMap;
    }
}
