package com.taskbridge.projects;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) { super(message); }
}