package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "custom_fields")
@Data
@NoArgsConstructor
public class CustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    private Long accountId;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
}
