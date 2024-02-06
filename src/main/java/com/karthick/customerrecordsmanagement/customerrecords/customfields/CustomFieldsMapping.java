package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Entity(name = "custom_fields_mapping")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CustomFieldsMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long defaultFieldId;
    @NonNull
    private String columnName;
    @NonNull
    private String fieldName;
}
