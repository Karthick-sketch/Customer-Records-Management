package com.karthick.customerrecordsmanagement.customerrecords;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karthick.customerrecordsmanagement.customfields.CustomerCustomFieldValue;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.IncrementGenerator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

@Data
@Entity(name = "customer_records")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "email"})})
public class CustomerRecord {
    @Id
    @GeneratedValue(generator = "sequence")
    @GenericGenerator(name = "sequence", type = IncrementGenerator.class)
    private long id;
    private long accountId;
    @Column(nullable = false)
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String companyName;
    private String address;
    private String city;
    private String state;
    private String country;
    private Integer zipcode;
    @JsonIgnore
    @OneToMany(mappedBy = "customerRecord", cascade = CascadeType.ALL)
    private List<CustomerCustomFieldValue> customerCustomFieldValues;

    public static List<String> getFields() {
        return Stream.of(CustomerRecord.class.getDeclaredFields())
                .map(Field::getName)
                .toList();
    }
}
