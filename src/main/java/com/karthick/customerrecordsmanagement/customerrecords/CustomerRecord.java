package com.karthick.customerrecordsmanagement.customerrecords;

import com.karthick.customerrecordsmanagement.customerrecords.customfields.CustomField;
import jakarta.persistence.*;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

@Entity(name = "customer_records")
@Data
public class CustomerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String companyName;
    private String address;
    private String city;
    private String state;
    private String country;
    private int zipcode;

    @OneToOne(optional = false)
    @JoinColumn(name = "custom_field_id", referencedColumnName = "id")
    private CustomField customField;

    public static List<String> getFields() {
        return Stream.of(CustomerRecord.class.getDeclaredFields())
                .map(Field::getName)
                .toList();
    }
}
