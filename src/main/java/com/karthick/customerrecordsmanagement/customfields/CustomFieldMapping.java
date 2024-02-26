package com.karthick.customerrecordsmanagement.customfields;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "custom_fields_mapping")
public class CustomFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long accountId;
    private String fieldName;
    private String customFieldName;
    private String dataType;
}
