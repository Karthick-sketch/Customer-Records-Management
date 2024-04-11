package com.customerrecordsmanagement.customfields.controller;

import com.customerrecordsmanagement.customfields.dto.CustomFieldMappingDTO;
import com.customerrecordsmanagement.customfields.service.CustomFieldMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(value = "http://localhost:5173/")
@RestController
@AllArgsConstructor
@RequestMapping("/custom-fields")
public class CustomFieldController {
    private CustomFieldMappingService customFieldMappingService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomFieldMappingDTO>> getCustomFields(@PathVariable long accountId) {
        return new ResponseEntity<>(customFieldMappingService.fetchCustomFieldMappingDTOByAccountId(accountId), HttpStatus.OK);
    }

    @PostMapping("/account/{accountId}")
    public ResponseEntity<CustomFieldMappingDTO> createCustomField(@PathVariable long accountId, @RequestBody Map<String, String> customFieldMappingMap) {
        return new ResponseEntity<>(customFieldMappingService.createCustomFieldMappingByMap(accountId, customFieldMappingMap), HttpStatus.CREATED);
    }

    @PatchMapping("/account/{accountId}/id/{id}")
    public ResponseEntity<CustomFieldMappingDTO> updateCustomField(@PathVariable long accountId, @PathVariable long id, @RequestBody Map<String, String> customFieldMappingMap) {
        return new ResponseEntity<>(customFieldMappingService.updateCustomField(id, accountId, customFieldMappingMap), HttpStatus.CREATED);
    }

    @DeleteMapping("/account/{accountId}/id/{id}")
    public ResponseEntity<HttpStatus> deleteCustomField(@PathVariable long accountId, @PathVariable long id) {
        customFieldMappingService.deleteCustomField(id, accountId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Testing
    @PostMapping("/cf")
    public ResponseEntity<List<CustomFieldMappingDTO>> createCustomFields() {
        List<CustomFieldMappingDTO> customFields = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customFields.add(customFieldMappingService.createCustomFieldMappingByMap(1, Map.of("customFieldName", "cf"+i, "dataType", "text", "required", "false")));
        }
        return new ResponseEntity<>(customFields, HttpStatus.CREATED);
    }
}
