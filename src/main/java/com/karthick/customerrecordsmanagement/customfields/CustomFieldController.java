package com.karthick.customerrecordsmanagement.customfields;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CustomFieldController {
    private CustomFieldService customFieldService;

    @GetMapping("/custom-fields/{id}")
    public ResponseEntity<List<CustomField>> getCustomFields(@PathVariable long id) {
        return new ResponseEntity<>(customFieldService.fetchCustomFields(id), HttpStatus.OK);
    }
}
