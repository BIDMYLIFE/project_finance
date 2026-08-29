package com.example.erp.exception;

import com.example.erp.dto.ErrorResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), "Invalid value"));
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fields);
    }
    @ExceptionHandler(AuthenticationFailureException.class)
    ResponseEntity<ErrorResponse> authentication() { return response(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", "Authentication failed", Map.of()); }
    @ExceptionHandler(InvalidSessionException.class)
    ResponseEntity<ErrorResponse> session() { return response(HttpStatus.UNAUTHORIZED, "INVALID_SESSION", "Session is invalid", Map.of()); }
    @ExceptionHandler(BootstrapConflictException.class)
    ResponseEntity<ErrorResponse> bootstrap() { return response(HttpStatus.CONFLICT, "BOOTSTRAP_UNAVAILABLE", "Bootstrap is unavailable", Map.of()); }
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound() { return response(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found", Map.of()); }
    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ErrorResponse> business() { return response(HttpStatus.CONFLICT, "BUSINESS_RULE", "The requested operation is not allowed", Map.of()); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> invalidRequest() { return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "The request parameters are invalid", Map.of()); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> internal() { return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "The request could not be completed", Map.of()); }
    private ResponseEntity<ErrorResponse> response(HttpStatus status, String code, String message, Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, fields, Instant.now()));
    }
}
