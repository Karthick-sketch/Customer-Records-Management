package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

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
        String fieldName = "";
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.getName().equals("id") && !field.getName().equals("defaultFieldId")) {
                    field.setAccessible(true);
                    if (field.get(this) == null) {
                        field.set(this, value);
                        fieldName = field.getName();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return fieldName;
    }

    public String getValueByColumnName(String columnName) throws IllegalAccessException {
        Field field = ReflectionUtils.findField(CustomField.class, columnName);
        if (field != null) {
            field.setAccessible(true);
            return (String) field.get(this);
        } else {
            throw new RuntimeException("There is no field called " + columnName + " in the CustomField class.");
        }
    }
}
