package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "customer_custom_field_values")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CustomerCustomFieldValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String customFieldValue;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "custom_field_id", referencedColumnName = "id")
    private CustomField customField;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "customer_record_id", referencedColumnName = "id")
    private CustomerRecord customerRecord;
}
