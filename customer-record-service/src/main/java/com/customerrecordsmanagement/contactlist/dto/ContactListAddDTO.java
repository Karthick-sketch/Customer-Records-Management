package com.customerrecordsmanagement.contactlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContactListAddDTO {
    private long accountId;
    private long listId;
    private List<Long> customerRecordIds;
}
