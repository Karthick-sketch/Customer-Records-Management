package com.karthick.customerrecordsmanagement.customerrecords;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRecordDto {
    private CustomerRecord customerRecord;
    private Map<String, String> customFields;
}
