package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.IncrementGenerator;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "customer_custom_field_values")
public class CustomerCustomFieldValue {
    @Id
    @GeneratedValue(generator = "sequence")
    @GenericGenerator(name = "sequence", type = IncrementGenerator.class)
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
