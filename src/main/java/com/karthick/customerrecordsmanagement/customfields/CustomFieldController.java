package com.karthick.customerrecordsmanagement.customfields;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/custom-fields")
public class CustomFieldController {
    private CustomFieldService customFieldService;

    @GetMapping("/{accountId}")
    public ResponseEntity<List<CustomField>> getCustomFields(@PathVariable long accountId) {
        return new ResponseEntity<>(customFieldService.fetchCustomFieldsByAccountId(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomField> createCustomField(@RequestBody CustomField customField) {
        return new ResponseEntity<>(customFieldService.createCustomField(customField), HttpStatus.CREATED);
    }
}
