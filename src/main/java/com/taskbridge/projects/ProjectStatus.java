package com.taskbridge.projects;

/**
 * Project lifecycle. Projects move forward from draft to active, completed,
 * and finally archived; no backward transitions are supported.
 */
public enum ProjectStatus {
    DRAFT,
    ACTIVE,
    COMPLETED,
    ARCHIVED;

    boolean canTransitionTo(ProjectStatus nextStatus) {
        return switch (this) {
            case DRAFT -> nextStatus == ACTIVE;
            case ACTIVE -> nextStatus == COMPLETED;
            case COMPLETED -> nextStatus == ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}