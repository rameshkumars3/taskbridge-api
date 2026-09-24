package com.taskbridge.projects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AuditRequest(@NotNull @Positive Long projectId, @NotNull ProjectEventType eventType,
        String previousStatus, String newStatus, @Size(max = 500) String message,
        @NotBlank @Size(max = 255) String deduplicationKey) {
}
