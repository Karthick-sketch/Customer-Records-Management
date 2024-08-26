package com.customerrecordsmanagement.customfields.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "custom_fields_mapping")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "custom_field_name"})})
public class CustomFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long accountId;
    private String fieldName;
    private String customFieldName;
    @Column(columnDefinition = "varchar(15) default 'text'")
    private String dataType;
    @Column(columnDefinition = "bool default false")
    private boolean required;

    public CustomFieldMapping() {
        this.dataType = "text";
        this.required = false;
    }
}
