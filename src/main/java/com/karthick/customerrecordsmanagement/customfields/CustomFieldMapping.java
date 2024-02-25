package com.karthick.customerrecordsmanagement.customfields;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "custom_fields_mapping")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CustomFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String columnName;
    @NonNull
    private String fieldName;
    @NonNull
    private String dataType;
}
