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
 * User-visible notification derived from an organisation-scoped project event.
 */
@Entity
@Table(
    name = "notification",
    indexes = {
        @Index(name = "idx_notification_org_user_created_at", columnList = "organisation_id, user_id, created_at"),
        @Index(name = "idx_notification_org_user_read", columnList = "organisation_id, user_id, read_flag"),
        @Index(name = "idx_notification_org_user_event", columnList = "organisation_id, user_id, event_type"),
        @Index(name = "idx_notification_org_user_deduplication", columnList = "organisation_id, user_id, deduplication_key", unique = true)
    }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organisation_id", nullable = false, length = 64)
    private String organisationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private ProjectEventType eventType;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "read_flag", nullable = false)
    private Boolean read = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "deduplication_key", nullable = false, length = 255)
    private String deduplicationKey;

    protected Notification() {
    }

    public Notification(String organisationId, Long userId, Long projectId,
            ProjectEventType eventType, String title, String message,
            String deduplicationKey) {
        this.organisationId = organisationId;
        this.userId = userId;
        this.projectId = projectId;
        this.eventType = eventType;
        this.title = title;
        this.message = message;
        this.deduplicationKey = deduplicationKey;
        this.read = false;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (read == null) {
            read = false;
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectEventType getEventType() {
        return eventType;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getRead() {
        return read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public String getDeduplicationKey() {
        return deduplicationKey;
    }

    public void markRead() {
        this.read = true;
        this.readAt = Instant.now();
    }
}
