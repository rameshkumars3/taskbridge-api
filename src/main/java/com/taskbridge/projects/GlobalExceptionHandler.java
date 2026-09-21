package com.taskbridge.projects;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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