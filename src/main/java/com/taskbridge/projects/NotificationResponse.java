package com.taskbridge.projects;

import java.time.Instant;

public record NotificationResponse(Long id, String organisationId, Long userId, Long projectId,
        ProjectEventType eventType, String title, String message, Boolean read, Instant createdAt,
        Instant readAt, String deduplicationKey) {
    static NotificationResponse from(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getOrganisationId(), notification.getUserId(),
                notification.getProjectId(), notification.getEventType(), notification.getTitle(), notification.getMessage(),
                notification.getRead(), notification.getCreatedAt(), notification.getReadAt(), notification.getDeduplicationKey());
    }
}
