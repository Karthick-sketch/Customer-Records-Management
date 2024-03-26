package com.customerrecordsmanagement.customerrecords;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.customerrecordsmanagement.customfields.CustomField;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.IncrementGenerator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(exclude = {"customField"})
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
    private String zipcode;
    @JsonIgnore
    @OneToOne(mappedBy = "customerRecord", cascade = CascadeType.ALL)
    private CustomField customField;

    public static List<String> getFields() {
        return Stream.of(CustomerRecord.class.getDeclaredFields())
                .map(Field::getName)
                .filter(fieldName -> !(fieldName.equals("id") || fieldName.equals("customField")))
                .toList();
    }
}
