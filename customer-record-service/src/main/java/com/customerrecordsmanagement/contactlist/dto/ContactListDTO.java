package com.customerrecordsmanagement.contactlist.dto;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContactListDTO {
    private long id;
    private String listName;
    private List<CustomerRecord> customerRecords;
}
