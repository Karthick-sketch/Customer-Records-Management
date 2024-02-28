package com.customerrecordsmanagement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private List<String> error;

    public ErrorResponse(String errorMessage) {
        this.error = List.of(errorMessage);
    }
}
