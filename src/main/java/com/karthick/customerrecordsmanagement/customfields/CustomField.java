package com.karthick.customerrecordsmanagement.customfields;

import com.karthick.customerrecordsmanagement.customerrecords.CustomerRecord;
import jakarta.persistence.*;
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
    private long accountId;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;

    @OneToOne
    @JoinColumn(name = "customer_record_id", referencedColumnName = "id")
    private CustomerRecord customerRecord;

    public CustomField(CustomerRecord customerRecord) {
        this.accountId = customerRecord.getAccountId();
        this.customerRecord = customerRecord;
    }

    public String setField(String value) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!(field.getName().equals("id") || field.getName().equals("customerRecord"))) {
                try {
                    field.setAccessible(true);
                    if (field.get(this) == null) {
                        field.set(this, value);
                        return field.getName();
                    }
                } catch (IllegalAccessException e) {
                    e.fillInStackTrace();
                }
            }
        }
        return null;
    }

    public String getValueByFieldName(String fieldName) {
        Field field = ReflectionUtils.findField(CustomField.class, fieldName);
        if (field == null) {
            throw new RuntimeException("There is no field called " + fieldName + " in the CustomField class.");
        }
        try {
            field.setAccessible(true);
            return (String) field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
