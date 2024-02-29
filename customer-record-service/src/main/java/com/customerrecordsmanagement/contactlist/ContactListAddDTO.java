package com.customerrecordsmanagement.contactlist;

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
