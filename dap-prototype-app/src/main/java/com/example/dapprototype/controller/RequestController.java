package com.example.dapprototype.controller;

import com.atlassian.oai.validator.report.ValidationReport;
import com.example.dapprototype.model.ErrorResponse;
import com.example.dapprototype.model.RequestPayload;
import com.example.dapprototype.model.RequestResponse;
import com.example.dapprototype.service.OpenApiRequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RequestController {

    private final OpenApiRequestValidator openApiRequestValidator;
    private final ObjectMapper objectMapper;

    public RequestController(OpenApiRequestValidator openApiRequestValidator, ObjectMapper objectMapper) {
        this.openApiRequestValidator = openApiRequestValidator;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/request")
    public ResponseEntity<?> submitRequest(@RequestBody String rawBody) {
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

        RequestResponse response = new RequestResponse(true, "Request processed successfully");
        return ResponseEntity.ok(response);
    }
}
