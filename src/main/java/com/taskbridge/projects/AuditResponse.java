package com.taskbridge.projects;

import java.time.Instant;

public record AuditResponse(Long id, String organisationId, Long projectId, Long actorUserId,
        ProjectEventType eventType, String previousStatus, String newStatus, String message,
        Instant createdAt, String deduplicationKey) {
    static AuditResponse from(AuditLog audit) {
        return new AuditResponse(audit.getId(), audit.getOrganisationId(), audit.getProjectId(),
                audit.getActorUserId(), audit.getEventType(), audit.getPreviousStatus(), audit.getNewStatus(),
                audit.getMessage(), audit.getCreatedAt(), audit.getDeduplicationKey());
    }
}
