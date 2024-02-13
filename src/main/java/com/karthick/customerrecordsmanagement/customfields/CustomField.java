package com.karthick.customerrecordsmanagement.customfields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "custom_fields")
@Data
@NoArgsConstructor
public class CustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fieldName;
    private String dataType;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "default_field_id")
    private CustomerRecord customerRecord;

    public CustomField(String fieldName, CustomerRecord customerRecord) {
        this.fieldName = fieldName;
        this.customerRecord = customerRecord;
        this.dataType = "text";
    }

    public CustomField(String fieldName, String dataType, CustomerRecord customerRecord) {
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.customerRecord = customerRecord;
    }
}
