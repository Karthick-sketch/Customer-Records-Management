package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Entity(name = "custom_fields")
@Data
@NoArgsConstructor
public class CustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long defaultFieldId;
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

    public CustomField(long defaultFieldId) {
        this.defaultFieldId = defaultFieldId;
    }

    public String setField(String value) {
        List<String> nonFields = List.of("id", "defaultFieldId", "customerRecord");
        String fieldName = null;
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!nonFields.contains(field.getName())) {
                    field.setAccessible(true);
                    if (field.get(this) == null) {
                        field.set(this, value);
                        fieldName = field.getName();
                        break;
                    }
                }
            } catch (IllegalAccessException e) {
                e.fillInStackTrace();
            }
        }
        return fieldName;
    }

    public String getValueByFieldName(String fieldName) {
        Field field = ReflectionUtils.findField(CustomField.class, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                return (String) field.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
        } else {
            throw new RuntimeException("There is no field called " + fieldName + " in the CustomField class.");
        }
    }
}
