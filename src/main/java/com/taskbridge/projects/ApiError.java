package com.taskbridge.projects;

import java.time.Instant;

/** Safe, consistent API error payload. */
public record ApiError(Instant timestamp, int status, String error, String message) {
}