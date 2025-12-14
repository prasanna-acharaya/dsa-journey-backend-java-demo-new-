package com.bom.dsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception classes for the application.
 * Each exception maps to a specific HTTP status code.
 */
public class CustomExceptions {

    /**
     * Exception thrown when a requested resource is not found.
     * Maps to HTTP 404 NOT_FOUND.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        private final String resourceName;
        private final String fieldName;
        private final Object fieldValue;

        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
            this.resourceName = resourceName;
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }

        public String getResourceName() {
            return resourceName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getFieldValue() {
            return fieldValue;
        }
    }

    /**
     * Exception thrown for general business logic errors.
     * Maps to HTTP 400 BAD_REQUEST.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }

        public BusinessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when an operation is not allowed in current state.
     * Maps to HTTP 409 CONFLICT.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class InvalidOperationException extends RuntimeException {
        public InvalidOperationException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when user authentication fails.
     * Maps to HTTP 401 UNAUTHORIZED.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when user does not have permission.
     * Maps to HTTP 403 FORBIDDEN.
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when trying to create a duplicate resource.
     * Maps to HTTP 409 CONFLICT.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateResourceException extends RuntimeException {
        private final String resourceName;
        private final String fieldName;
        private final Object fieldValue;

        public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
            this.resourceName = resourceName;
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }

        public String getResourceName() {
            return resourceName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getFieldValue() {
            return fieldValue;
        }
    }

    /**
     * Exception thrown for validation errors.
     * Maps to HTTP 400 BAD_REQUEST.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a file operation fails.
     * Maps to HTTP 500 INTERNAL_SERVER_ERROR.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class FileOperationException extends RuntimeException {
        public FileOperationException(String message) {
            super(message);
        }

        public FileOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
