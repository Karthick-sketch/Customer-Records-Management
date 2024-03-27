package com.customerrecordsmanagement.contactlist.controller;

import com.customerrecordsmanagement.contactlist.service.ContactListService;
import com.customerrecordsmanagement.contactlist.dto.ContactListAddDTO;
import com.customerrecordsmanagement.contactlist.dto.ContactListDTO;
import com.customerrecordsmanagement.contactlist.entity.ContactList;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lists")
@AllArgsConstructor
public class ContactListController {
    private ContactListService contactListService;

    @GetMapping("/accountId/{accountId}")
    public ResponseEntity<List<ContactList>> getContactLists(@PathVariable long accountId) {
        return new ResponseEntity<>(contactListService.fetchContactListByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("/accountId/{accountId}/id/{id}")
    public ResponseEntity<ContactListDTO> getCustomerRecordsFromList(@PathVariable long accountId, @PathVariable long id) {
        return new ResponseEntity<>(contactListService.fetchCustomerRecordsFromList(id, accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ContactList> getContactLists(@RequestBody ContactList contactList) {
        return new ResponseEntity<>(contactListService.createContactList(contactList), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addCustomerRecordsToContactList(@RequestBody ContactListAddDTO contactListAddDTO) {
        contactListService.addCustomerRecordsToList(contactListAddDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
