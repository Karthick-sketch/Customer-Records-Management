package com.customerrecordsmanagement.contactlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactListAddDTO {
    private long accountId;
    private long listId;
    private List<Long> customerRecordIds;
}
