package com.karthick.customerrecordsmanagement.customerrecords.customfields;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Entity(name = "custom_fields")
@Data
@NoArgsConstructor
public class CustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String field1;
    private String field2;
    private String field3;

    public String setField(String value) {
        String fieldName = "";
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.getName().equals("id")) {
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
}
