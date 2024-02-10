package com.karthick.customerrecordsmanagement.customfields;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "custom_fields_mapping")
@Data
@NoArgsConstructor
public class CustomFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long accountId;
    private String columnName;
    private String fieldName;

    public CustomFieldMapping(String columnName, String fieldName, CustomField customField) {
        this.accountId = customField.getAccountId();
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.customField = customField;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "custom_field_id", referencedColumnName = "id")
    private CustomField customField;
}
