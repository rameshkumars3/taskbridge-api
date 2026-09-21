package com.taskbridge.projects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Validated API input for project creation and update. */
public record ProjectRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 80) String teamId,
        @NotNull ProjectStatus status) {
}