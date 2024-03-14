package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

public class CustomerRecordItemProcessor implements ItemProcessor<CustomerRecord, CustomerRecord> {
    @Override
    public CustomerRecord process(@NonNull CustomerRecord customerRecord) throws Exception {
        return customerRecord;
    }
}
