package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Entity(name = "custom_fields_mapping")
@Data
@NoArgsConstructor
public class CustomFieldsMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    private Long accountId;
    @NonNull
    private String columnName;
    @NonNull
    private String fieldName;
}
