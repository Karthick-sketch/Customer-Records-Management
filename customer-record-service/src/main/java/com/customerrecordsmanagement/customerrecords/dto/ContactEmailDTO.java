package com.customerrecordsmanagement.customerrecords.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContactEmailDTO {
    private long id;
    private long accountId;
    private String email;
}
