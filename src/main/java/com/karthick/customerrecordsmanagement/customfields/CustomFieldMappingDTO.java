package com.karthick.customerrecordsmanagement.customfields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldMappingDTO {
    private long id;
    private long accountId;
    private String customFieldName;
    private String dataType;
}
