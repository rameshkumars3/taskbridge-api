package com.taskbridge.projects;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) { super(message); }
}