package com.customerrecordsmanagement.contactlist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "contact_lists")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "listName"})})
public class ContactList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String listName;
    @JsonIgnore
    @OneToMany(mappedBy = "contactList")
    private List<ContactListMapping> contactListMappings;
}
