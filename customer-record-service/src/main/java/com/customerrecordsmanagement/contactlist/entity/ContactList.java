package com.customerrecordsmanagement.contactlist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"contactListMappings"})
@Entity(name = "contact_lists")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountId", "listName"})})
public class ContactList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String listName;
    @JsonIgnore
    @OneToMany(mappedBy = "contactList")
    private List<ContactListMapping> contactListMappings;
}
