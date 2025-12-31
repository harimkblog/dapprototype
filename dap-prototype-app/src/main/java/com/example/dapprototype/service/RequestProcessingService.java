package com.example.dapprototype.service;

import com.atlassian.oai.validator.report.ValidationReport;
import com.example.dapprototype.model.ErrorResponse;
import com.example.dapprototype.model.RequestPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RequestProcessingService {

    private final OpenApiRequestValidator openApiRequestValidator;
    private final ObjectMapper objectMapper;

    public RequestProcessingService(OpenApiRequestValidator openApiRequestValidator, ObjectMapper objectMapper) {
        this.openApiRequestValidator = openApiRequestValidator;
        this.objectMapper = objectMapper;
    }

    /**
     * Validates and processes a raw JSON request body.
     * 
     * @param rawBody the raw JSON request body
     * @return ResponseEntity with either the validated RequestPayload or an ErrorResponse
     */
    public ResponseEntity<?> validateAndProcessRequest(String rawBody) {
        // Validate request against OpenAPI spec
        ValidationReport report = openApiRequestValidator.validatePostJson("/request", rawBody, MediaType.APPLICATION_JSON_VALUE);
        if (report.hasErrors()) {
            ErrorResponse error = ErrorResponse.fromMessages(report.getMessages().stream()
                    .map(ValidationReport.Message::toString)
                    .toList());
            return ResponseEntity.badRequest().body(error);
        }

        // Deserialize after validation passes
        RequestPayload payload;
        try {
            payload = objectMapper.readValue(rawBody, RequestPayload.class);
        } catch (JsonProcessingException ex) {
            ErrorResponse error = ErrorResponse.fromMessages(java.util.List.of("Invalid JSON payload"));
            return ResponseEntity.badRequest().body(error);
        }

        return ResponseEntity.ok(payload);
    }
}
