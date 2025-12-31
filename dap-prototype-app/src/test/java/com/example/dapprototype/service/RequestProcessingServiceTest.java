package com.example.dapprototype.service;

import com.example.dapprototype.config.OpenApiValidatorConfig;
import com.example.dapprototype.model.ErrorResponse;
import com.example.dapprototype.model.RequestInfo;
import com.example.dapprototype.model.RequestPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({OpenApiRequestValidator.class, OpenApiValidatorConfig.class})
class RequestProcessingServiceTest {

    @Autowired
    private RequestProcessingService requestProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("validateAndProcessRequest returns success for valid payload")
    void validateAndProcessRequest_withValidPayload_returnsSuccess() throws Exception {
        RequestPayload payload = new RequestPayload(new RequestInfo("abcd", "2025-12-30T13:36:00Z"));
        String rawBody = objectMapper.writeValueAsString(payload);

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isInstanceOf(RequestPayload.class);
        RequestPayload resultPayload = (RequestPayload) result.getBody();
        assertThat(resultPayload.requestInfo().activityId()).isEqualTo("abcd");
        assertThat(resultPayload.requestInfo().activityTimeStamp()).isEqualTo("2025-12-30T13:36:00Z");
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for missing requestInfo")
    void validateAndProcessRequest_withMissingRequestInfo_returnsError() {
        String rawBody = "{}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
        assertThat(errorResponse.code()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for missing requestId")
    void validateAndProcessRequest_withMissingRequestId_returnsError() {
        String rawBody = "{\"requestInfo\": {\"timestamp\": \"2025-12-30T13:36:00Z\"}}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
        assertThat(errorResponse.code()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for missing timestamp")
    void validateAndProcessRequest_withMissingTimestamp_returnsError() {
        String rawBody = "{\"requestInfo\": {\"requestId\": \"abcd\"}}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
        assertThat(errorResponse.code()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for invalid JSON")
    void validateAndProcessRequest_withInvalidJson_returnsError() {
        String rawBody = "{invalid json}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for invalid timestamp format")
    void validateAndProcessRequest_withInvalidTimestampFormat_returnsError() {
        String rawBody = "{\"requestInfo\": {\"requestId\": \"abcd\", \"timestamp\": \"not-a-date\"}}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
        assertThat(errorResponse.code()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for null requestId")
    void validateAndProcessRequest_withNullRequestId_returnsError() {
        String rawBody = "{\"requestInfo\": {\"requestId\": null, \"timestamp\": \"2025-12-30T13:36:00Z\"}}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
        assertThat(errorResponse.code()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("validateAndProcessRequest returns error for empty requestId")
    void validateAndProcessRequest_withEmptyRequestId_returnsError() {
        String rawBody = "{\"requestInfo\": {\"requestId\": \"\", \"timestamp\": \"2025-12-30T13:36:00Z\"}}";

        ResponseEntity<?> result = requestProcessingService.validateAndProcessRequest(rawBody);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) result.getBody();
        assertThat(errorResponse.success()).isFalse();
        assertThat(errorResponse.code()).isEqualTo("VALIDATION_ERROR");
    }
}
