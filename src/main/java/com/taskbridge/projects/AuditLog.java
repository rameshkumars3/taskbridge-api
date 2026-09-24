package com.taskbridge.projects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Immutable append-only record of project lifecycle events for a tenant.
 */
@Entity
@Table(
    name = "audit_log",
    indexes = {
        @Index(name = "idx_audit_org_project_created_at", columnList = "organisation_id, project_id, created_at"),
        @Index(name = "idx_audit_org_event_created_at", columnList = "organisation_id, event_type, created_at"),
        @Index(name = "idx_audit_org_deduplication", columnList = "organisation_id, deduplication_key", unique = true)
    }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organisation_id", nullable = false, length = 64)
    private String organisationId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "actor_user_id", nullable = false)
    private Long actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private ProjectEventType eventType;

    @Column(name = "previous_status", length = 32)
    private String previousStatus;

    @Column(name = "new_status", length = 32)
    private String newStatus;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deduplication_key", nullable = false, length = 255)
    private String deduplicationKey;

    protected AuditLog() {
    }

    public AuditLog(String organisationId, Long projectId, Long actorUserId,
            ProjectEventType eventType, String previousStatus, String newStatus,
            String message, String deduplicationKey) {
        this.organisationId = organisationId;
        this.projectId = projectId;
        this.actorUserId = actorUserId;
        this.eventType = eventType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.deduplicationKey = deduplicationKey;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public ProjectEventType getEventType() {
        return eventType;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getDeduplicationKey() {
        return deduplicationKey;
    }
}
