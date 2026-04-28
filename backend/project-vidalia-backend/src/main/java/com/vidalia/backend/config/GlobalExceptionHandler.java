package com.vidalia.backend.config;

import com.vidalia.backend.dto.error.ErrorResponse;
import com.vidalia.backend.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.util.HtmlUtils;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final int MAX_DETAIL_LENGTH = 300;

    /**
     * Sanitize a string intended for inclusion in a JSON response so that
     * if a browser renders it inside HTML it won't execute untrusted markup.
     * We HTML-escape the value, strip newlines and truncate long values.
     */
    private String sanitize(String input) {
        if (input == null) return null;
        String escaped = HtmlUtils.htmlEscape(input);
        // Collapse newlines to spaces to avoid header/body injection vectors in some renderers
        escaped = escaped.replaceAll("[\\r\\n]+", " ");
        if (escaped.length() > MAX_DETAIL_LENGTH) {
            return escaped.substring(0, MAX_DETAIL_LENGTH) + "...";
        }
        return escaped;
    }

    /**
     * Handle ResourceNotFoundException - 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage("Resource not found");
        errorResponse.setDetails(sanitize(ex.getMessage()));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle ResourceAlreadyExistsException - 409
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {

        log.warn("Resource already exists: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setMessage("Resource already exists");
        errorResponse.setDetails(sanitize(ex.getMessage()));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle IncorrectOldPasswordException - 401
     */
    @ExceptionHandler(IncorrectOldPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectOldPasswordException(
            IncorrectOldPasswordException ex,
            HttpServletRequest request) {

        log.warn("Incorrect password attempt: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage("Incorrect password");
        errorResponse.setDetails(sanitize(ex.getMessage()));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle FileUploadValidationException - 400
     */
    @ExceptionHandler(FileUploadValidationException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadValidationException(
            FileUploadValidationException ex,
            HttpServletRequest request) {

        log.warn("File upload validation failed: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("File upload validation failed");
        errorResponse.setDetails(sanitize(ex.getMessage()));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle FileStorageException - 500
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorageException(
            FileStorageException ex,
            HttpServletRequest request) {

        log.error("File storage error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage("Failed to store file");
        errorResponse.setDetails(sanitize("An error occurred while processing your file. Please try again later."));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle ResourceCreationException - 400
     */
    @ExceptionHandler(ResourceCreationException.class)
    public ResponseEntity<ErrorResponse> handleResourceCreationException(
            ResourceCreationException ex,
            HttpServletRequest request) {

        log.warn("Resource creation failed: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Failed to create resource");
        errorResponse.setDetails(sanitize(ex.getMessage()));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle IllegalArgumentException - 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Invalid argument: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Invalid argument");
        errorResponse.setDetails(sanitize(ex.getMessage()));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle BadCredentialsException - 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Bad credentials: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage("Authentication failed");
        errorResponse.setDetails(ex.getMessage());
        errorResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle AccessDeniedException - 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage("Access denied");
        errorResponse.setDetails(sanitize("You do not have permission to access this resource"));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle validation errors from @Valid annotation - 400
     * This extracts field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Validation failed for request: {}", request.getRequestURI());

        // Extract field errors into a map
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(sanitize(fieldName), sanitize(errorMessage));
        });

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Validation failed");
        errorResponse.setDetails("One or more fields have validation errors");
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setFieldErrors(fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle malformed JSON requests - 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed request body: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Malformed request body");
        errorResponse.setDetails(sanitize("Please check your JSON format and required fields"));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Generic catch-all handler for any unhandled exceptions - 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage("An unexpected error occurred");
        errorResponse.setDetails(sanitize("Please contact support if the problem persists"));
        errorResponse.setPath(sanitize(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

