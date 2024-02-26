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
    public ResponseEntity<List<CustomFieldMappingDTO>> getCustomFields(@PathVariable long accountId) {
        return new ResponseEntity<>(customFieldMappingService.fetchCustomFieldMappingDTOByAccountId(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomFieldMapping> createCustomField(@RequestBody CustomFieldMappingDTO customFieldMappingDTO) {
        return new ResponseEntity<>(customFieldMappingService.createCustomFieldMapping(customFieldMappingDTO), HttpStatus.CREATED);
    }

    // Testing
    @PostMapping("/cf")
    public ResponseEntity<List<CustomFieldMapping>> createCustomFields() {
        List<CustomFieldMapping> customFields = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customFields.add(customFieldMappingService.createCustomFieldMapping(new CustomFieldMappingDTO(0, 1, "cf"+i, "text")));
        }
        return new ResponseEntity<>(customFields, HttpStatus.CREATED);
    }
}
