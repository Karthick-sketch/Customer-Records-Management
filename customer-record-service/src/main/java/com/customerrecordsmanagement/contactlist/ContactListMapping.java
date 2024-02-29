package com.customerrecordsmanagement.contactlist;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "contact_list_mapping")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "contact_list_id", "customer_record_id"})})
public class ContactListMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "contact_list_id", referencedColumnName = "id")
    private ContactList contactList;
    @NonNull
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "customer_record_id", referencedColumnName = "id")
    private CustomerRecord customerRecord;
}
