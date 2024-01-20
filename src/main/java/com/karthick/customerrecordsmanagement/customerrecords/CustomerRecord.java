package com.karthick.customerrecordsmanagement.customerrecords;

import jakarta.persistence.*;
import lombok.Data;

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
}
