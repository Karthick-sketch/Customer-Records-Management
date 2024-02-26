package com.karthick.customerrecordsmanagement.customfields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.karthick.customerrecordsmanagement.exception.EntityNotException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.IncrementGenerator;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@Entity(name = "custom_fields")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "customer_record_id"})})
public class CustomField {
    @Id
    @GeneratedValue(generator = "sequence")
    @GenericGenerator(name = "sequence", type = IncrementGenerator.class)
    private long id;
    private long accountId;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;
    private String field9;
    private String field10;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "customer_record_id", referencedColumnName = "id")
    private CustomerRecord customerRecord;

    public CustomField(CustomerRecord customerRecord) {
        this.accountId = customerRecord.getAccountId();
        this.customerRecord = customerRecord;
    }

    public static List<String> getFieldNames() {
        List<String> nonFields = List.of("id", "accountId", "customerRecord");
        return Stream.of(CustomField.class.getDeclaredFields())
                .map(Field::getName)
                .filter(name -> !(nonFields.contains(name)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setField(String fieldName, String value) {
        Field field = ReflectionUtils.findField(CustomField.class, fieldName);
        throwIfFieldIsNull(field, fieldName);
        try {
            field.setAccessible(true);
            if (field.get(this) == null) {
                field.set(this, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getValueByFieldName(String fieldName) {
        Field field = ReflectionUtils.findField(CustomField.class, fieldName);
        throwIfFieldIsNull(field, fieldName);
        try {
            field.setAccessible(true);
            return (String) field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void throwIfFieldIsNull(Field field, String fieldName) {
        if (field == null) {
            throw new EntityNotException("There is no field called " + fieldName + " in the CustomField class.");
        }
    }
}
