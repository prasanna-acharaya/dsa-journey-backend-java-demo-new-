package com.bom.dsa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Handles all exceptions and returns appropriate error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        /**
         * Handle resource not found exceptions.
         */
        @ExceptionHandler(CustomExceptions.ResourceNotFoundException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFoundException(
                        CustomExceptions.ResourceNotFoundException ex) {
                log.warn("Resource not found: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Resource Not Found")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
        }

        /**
         * Handle business exceptions.
         */
        @ExceptionHandler(CustomExceptions.BusinessException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(CustomExceptions.BusinessException ex) {
                log.error("Business exception: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Business Error")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        /**
         * Handle invalid operation exceptions.
         */
        @ExceptionHandler(CustomExceptions.InvalidOperationException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleInvalidOperationException(
                        CustomExceptions.InvalidOperationException ex) {
                log.warn("Invalid operation: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .error("Invalid Operation")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
        }

        /**
         * Handle unauthorized exceptions.
         */
        @ExceptionHandler(CustomExceptions.UnauthorizedException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedException(
                        CustomExceptions.UnauthorizedException ex) {
                log.warn("Unauthorized access: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
        }

        /**
         * Handle forbidden exceptions.
         */
        @ExceptionHandler(CustomExceptions.ForbiddenException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleForbiddenException(CustomExceptions.ForbiddenException ex) {
                log.warn("Forbidden access: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("Forbidden")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
        }

        /**
         * Handle duplicate resource exceptions.
         */
        @ExceptionHandler(CustomExceptions.DuplicateResourceException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleDuplicateResourceException(
                        CustomExceptions.DuplicateResourceException ex) {
                log.warn("Duplicate resource: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .error("Duplicate Resource")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
        }

        /**
         * Handle validation exceptions from WebFlux.
         */
        @ExceptionHandler(WebExchangeBindException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBindException(WebExchangeBindException ex) {
                log.warn("Validation failed: {}", ex.getMessage());

                Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                error -> error.getDefaultMessage() != null ? error.getDefaultMessage()
                                                                : "Invalid value",
                                                (existing, replacement) -> existing));

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("One or more fields have validation errors")
                                .timestamp(Instant.now())
                                .fieldErrors(fieldErrors)
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        /**
         * Handle validation exceptions from method arguments.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
                log.warn("Method argument validation failed: {}", ex.getMessage());

                Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                error -> error.getDefaultMessage() != null ? error.getDefaultMessage()
                                                                : "Invalid value",
                                                (existing, replacement) -> existing));

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("One or more fields have validation errors")
                                .timestamp(Instant.now())
                                .fieldErrors(fieldErrors)
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        /**
         * Handle bad credentials exceptions.
         */
        @ExceptionHandler(BadCredentialsException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleBadCredentials(BadCredentialsException ex) {
                log.warn("Bad credentials: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Failed")
                                .message("Invalid username or password")
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
        }

        /**
         * Handle authentication exceptions.
         */
        @ExceptionHandler(AuthenticationException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleAuthenticationException(AuthenticationException ex) {
                log.warn("Authentication failed: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Failed")
                                .message(ex.getMessage())
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
        }

        /**
         * Handle all other exceptions.
         */
        @ExceptionHandler(Exception.class)
        public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
                log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred. Please try again later.")
                                .timestamp(Instant.now())
                                .build();

                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
        }

        /**
         * Error response DTO.
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        public static class ErrorResponse {
                private int status;
                private String error;
                private String message;
                private Instant timestamp;
                private Map<String, String> fieldErrors;
        }
}
