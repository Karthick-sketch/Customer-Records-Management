package com.customerrecordsmanagement.customfields.customfieldmapping;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "custom_fields_mapping")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "customFieldName"})})
public class CustomFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long accountId;
    private String fieldName;
    private String customFieldName;
    private String dataType;
}
