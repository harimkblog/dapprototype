package com.example.dapprototype.model;

import java.util.List;

public record ErrorResponse(
        boolean success,
        String message,
        String code,
        List<String> details
) {
    public static ErrorResponse fromMessages(List<String> messages) {
        return new ErrorResponse(false, "Validation failed", "VALIDATION_ERROR", messages);
    }
}
