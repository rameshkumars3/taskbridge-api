package com.taskbridge.projects;

import java.time.Instant;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Central HTTP mapping for project domain failures. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    ResponseEntity<ApiError> notFound(ProjectNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ApiError> validation(ValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> requestValidation(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Request validation failed");
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    ResponseEntity<ApiError> unauthorized(UnauthorizedAccessException exception) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    ResponseEntity<ApiError> forbidden(ForbiddenOperationException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({ProjectConflictException.class, InvalidProjectStatusTransitionException.class})
    ResponseEntity<ApiError> conflict(RuntimeException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message));
    }
}