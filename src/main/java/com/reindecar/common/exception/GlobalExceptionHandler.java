package com.reindecar.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception [{}]: {}", ex.getCode(), ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
            ex.getCode(),
            ex.getFormattedMessage(),
            request.getRequestURI(),
            getTraceId(request)
        );
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation failed: {}", errors);
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.VALIDATION_ERROR,
            request.getRequestURI(),
            getTraceId(request),
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage
                ));
        log.warn("Constraint violation: {}", errors);
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.VALIDATION_ERROR,
            request.getRequestURI(),
            getTraceId(request),
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("JSON parse error: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.JSON_PARSE_ERROR,
            request.getRequestURI(),
            getTraceId(request),
            "Geçersiz JSON formatı"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing parameter: {}", ex.getParameterName());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.MISSING_PARAMETER,
            request.getRequestURI(),
            getTraceId(request),
            ex.getParameterName()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not allowed: {} for {}", ex.getMethod(), request.getRequestURI());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.METHOD_NOT_ALLOWED,
            request.getRequestURI(),
            getTraceId(request),
            ex.getMethod()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("Endpoint not found: {}", request.getRequestURI());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.ENTITY_NOT_FOUND,
            request.getRequestURI(),
            getTraceId(request),
            "Endpoint bulunamadı"
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        
        String message = extractConstraintMessage(ex);
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.CONSTRAINT_VIOLATION,
            request.getRequestURI(),
            getTraceId(request),
            message
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            errorCode = ErrorCode.ENTITY_NOT_FOUND;
        }
        
        ErrorResponse response = ErrorResponse.of(
            errorCode,
            request.getRequestURI(),
            getTraceId(request),
            ex.getMessage()
        );
        
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        log.warn("Illegal state: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.INVALID_OPERATION,
            request.getRequestURI(),
            getTraceId(request),
            ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.UNAUTHORIZED,
            request.getRequestURI(),
            getTraceId(request)
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.ACCESS_DENIED,
            request.getRequestURI(),
            getTraceId(request)
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(org.springframework.dao.InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(
            org.springframework.dao.InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
        log.warn("Invalid data access: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.INVALID_PARAMETER,
            request.getRequestURI(),
            getTraceId(request),
            "Geçersiz sıralama parametresi"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse response = ErrorResponse.of(
            ErrorCode.INTERNAL_ERROR,
            request.getRequestURI(),
            getTraceId(request)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute("traceId");
        if (traceId != null) {
            return traceId.toString();
        }
        String headerTraceId = request.getHeader("X-Trace-Id");
        if (headerTraceId != null && !headerTraceId.isEmpty()) {
            return headerTraceId;
        }
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    private String extractConstraintMessage(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        if (message != null) {
            if (message.contains("unique constraint") || message.contains("UNIQUE")) {
                return "Bu kayıt zaten mevcut";
            }
            if (message.contains("foreign key") || message.contains("FOREIGN KEY")) {
                return "İlişkili kayıt bulunamadı";
            }
            if (message.contains("not-null constraint") || message.contains("NOT NULL")) {
                return "Zorunlu alan boş bırakılamaz";
            }
        }
        return "Veri bütünlük hatası";
    }
}
