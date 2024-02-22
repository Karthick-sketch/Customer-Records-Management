package com.karthick.customerrecordsmanagement.customfields;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "custom_fields")
public class CustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String fieldName;
    @NonNull
    private String dataType;
}
