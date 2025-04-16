package com.leon.controller.advice;

import com.leon.common.exception.*; // Import all custom exceptions
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

// Basic error response structure
record ErrorResponse(String message, String details) {}

@ControllerAdvice
@Slf4j // Use Slf4j for logging
public class GlobalExceptionHandler {

    // Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse errorDetails = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Handle Invalid Input and related (subclass of IllegalArgumentException)
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        log.warn("Invalid input: {}", ex.getMessage());
        ErrorResponse errorDetails = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle Duplicate Resource
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        log.warn("Duplicate resource attempt: {}", ex.getMessage());
        ErrorResponse errorDetails = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        // Often, creating a duplicate is a client error (Bad Request)
        // Could also be Conflict (409) depending on API semantics
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle Insufficient Funds
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex, WebRequest request) {
        log.warn("Insufficient funds: {}", ex.getMessage());
        ErrorResponse errorDetails = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        // This is usually a client error (Bad Request), or potentially Conflict (409)
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle general IllegalArgumentException (if not caught by specific handlers above)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ErrorResponse errorDetails = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle general IllegalStateException (if not caught by specific handlers above)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        log.warn("Illegal state: {}", ex.getMessage());
        ErrorResponse errorDetails = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        // Could be BAD_REQUEST or CONFLICT (409) depending on the context
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }


    // Generic handler for other RuntimeExceptions (potential internal server errors)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Unhandled RuntimeException: {}", ex.getMessage(), ex); // Log stack trace for unexpected errors
        ErrorResponse errorDetails = new ErrorResponse("An unexpected error occurred", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Generic handler for any other exception (ensure we always return a response)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex); // Log stack trace
        ErrorResponse errorDetails = new ErrorResponse("An internal server error occurred", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

} 