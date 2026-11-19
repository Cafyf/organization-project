package com.kafka.producer.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {


    // 1. Handle custom application exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request) {

        log.warn(
                "Application exception | Error Code: {} | Message: {}",
                ex.getErrorCode(),
                ex.getMessage()
        );

        return buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getStatus(),
                request,
                null
        );
    }

    // 2. Handle @Valid request body validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                                error -> error.getDefaultMessage(),
                                (existing, replacement) -> existing
                        ));

        return buildErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed",
                HttpStatus.BAD_REQUEST,
                request,
                errors
        );
    }

    // 3. Handle controller method validation
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {

        log.warn("Controller method validation failed: {}", ex.getMessage());

        return buildErrorResponse(
                "METHOD_VALIDATION_ERROR",
                "Invalid controller method parameters",
                HttpStatus.BAD_REQUEST,
                request,
                null
        );
    }

    // 4. Handle parameter and bean validation violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errors =
                ex.getConstraintViolations()
                        .stream()
                        .collect(Collectors.toMap(
                                violation -> violation.getPropertyPath().toString(),
                                violation -> violation.getMessage(),
                                (existing, replacement) -> existing
                        ));

        return buildErrorResponse(
                "CONSTRAINT_VIOLATION",
                "One or more constraints were violated",
                HttpStatus.BAD_REQUEST,
                request,
                errors
        );
    }

    // 5. Handle invalid JSON request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Invalid request body: {}", ex.getMessage());

        return buildErrorResponse(
                "INVALID_REQUEST_BODY",
                "Invalid JSON request or incorrect field format",
                HttpStatus.BAD_REQUEST,
                request,
                null
        );
    }

    // 6. Handle incorrect parameter types
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                "INVALID_PARAMETER",
                "Invalid value for parameter: " + ex.getName(),
                HttpStatus.BAD_REQUEST,
                request,
                null
        );
    }

    // 7. Handle missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                "MISSING_PARAMETER",
                "Required parameter missing: " + ex.getParameterName(),
                HttpStatus.BAD_REQUEST,
                request,
                null
        );
    }

    // 8. Handle unsupported HTTP methods
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                "METHOD_NOT_SUPPORTED",
                "HTTP method not supported",
                HttpStatus.METHOD_NOT_ALLOWED,
                request,
                null
        );
    }

    // 9. Handle unsupported content types
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                "UNSUPPORTED_MEDIA_TYPE",
                "Unsupported request content type",
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                request,
                null
        );
    }

    // 10. Handle missing resources
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                "ENDPOINT_NOT_FOUND",
                "Requested endpoint was not found",
                HttpStatus.NOT_FOUND,
                request,
                null
        );
    }

    // 11. Handle database integrity violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Database constraint violation", ex);

        return buildErrorResponse(
                "DATABASE_CONSTRAINT_VIOLATION",
                "Database constraint violation. Check duplicate or invalid data",
                HttpStatus.CONFLICT,
                request,
                null
        );
    }

    // 12. Handle optimistic locking
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(
            OptimisticLockingFailureException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                "CONCURRENT_MODIFICATION",
                "The resource was modified by another request. Please retry",
                HttpStatus.CONFLICT,
                request,
                null
        );
    }

    // 13. Handle general database errors
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDatabaseException(
            DataAccessException ex,
            HttpServletRequest request) {

        log.error("Database operation failed", ex);

        return buildErrorResponse(
                "DATABASE_ERROR",
                "Unable to complete the database operation",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                null
        );
    }

    // 14. Handle unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected error | Path: {} | Method: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex
        );

        return buildErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                null
        );
    }

    // Common response builder
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            String errorCode,
            String message,
            HttpStatus status,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .errorCode(errorCode)
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<String> handleUnauthenticated(
            AuthenticationCredentialsNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }
}
