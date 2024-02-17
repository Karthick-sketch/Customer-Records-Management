package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "customer_custom_field_values")
public class CustomerCustomFieldValue {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String customFieldValue;
    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "custom_field_id", referencedColumnName = "id")
    private CustomField customField;
    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_record_id", referencedColumnName = "id")
    private CustomerRecord customerRecord;
}
