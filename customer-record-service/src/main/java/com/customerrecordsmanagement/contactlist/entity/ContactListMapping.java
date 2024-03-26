package com.customerrecordsmanagement.contactlist.entity;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "contact_list_mapping")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "contact_list_id", "customer_record_id"})})
public class ContactListMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
