package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "custom_fields")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String fieldName;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "default_field_id")
    private CustomerRecord customerRecord;
}
