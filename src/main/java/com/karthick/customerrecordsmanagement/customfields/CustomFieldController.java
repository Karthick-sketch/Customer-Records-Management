package com.karthick.customerrecordsmanagement.customfields;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/custom-fields")
public class CustomFieldController {
    private CustomFieldService customFieldService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomField>> getCustomFields(@PathVariable long accountId) {
        return new ResponseEntity<>(customFieldService.fetchCustomFieldsByAccountId(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomField> createCustomField(@RequestBody CustomField customField) {
        return new ResponseEntity<>(customFieldService.createCustomField(customField), HttpStatus.CREATED);
    }

    // Testing
    @PostMapping("/cf")
    public ResponseEntity<List<CustomField>> createCustomFields() {
        List<CustomField> customFields = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customFields.add(customFieldService.createCustomField(new CustomField(1L, "cf"+i, "text")));
        }
        return new ResponseEntity<>(customFields, HttpStatus.CREATED);
    }
}
