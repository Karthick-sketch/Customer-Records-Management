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
    private CustomFieldMappingService customFieldMappingService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomFieldMapping>> getCustomFields(@PathVariable long accountId) {
        return new ResponseEntity<>(customFieldMappingService.fetchCustomFieldMappingAccountId(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomFieldMapping> createCustomField(@RequestBody CustomFieldMapping customFieldMapping) {
        return new ResponseEntity<>(customFieldMappingService.createCustomFieldMapping(customFieldMapping), HttpStatus.CREATED);
    }

    // Testing
    @PostMapping("/cf")
    public ResponseEntity<List<CustomFieldMapping>> createCustomFields() {
        List<CustomFieldMapping> customFields = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customFields.add(customFieldMappingService.createCustomFieldMapping(new CustomFieldMapping(1L, "cf"+i, "field"+i, "text")));
        }
        return new ResponseEntity<>(customFields, HttpStatus.CREATED);
    }
}
