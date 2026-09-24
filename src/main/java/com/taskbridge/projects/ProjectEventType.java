package com.taskbridge.projects;

/**
 * Supported project lifecycle events for audit and notification records.
 */
public enum ProjectEventType {
    PROJECT_CREATED,
    PROJECT_STATUS_CHANGED,
    PROJECT_DELETED,
    PROJECT_UPDATED,
    MILESTONE_REOPENED
}
