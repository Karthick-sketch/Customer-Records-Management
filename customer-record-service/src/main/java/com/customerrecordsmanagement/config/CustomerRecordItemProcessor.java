package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

public class CustomerRecordItemProcessor implements ItemProcessor<Map<String, String>, CustomerRecord> {
    @Override
    public CustomerRecord process(@NonNull Map<String, String> item) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(item, CustomerRecord.class);
    }
}
