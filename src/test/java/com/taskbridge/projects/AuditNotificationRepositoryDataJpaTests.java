package com.taskbridge.projects;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AuditNotificationRepositoryDataJpaTests {
    @Autowired private AuditLogRepository auditRepository;
    @Autowired private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    void auditQueriesAreTenantScopedAndOrdered() {
        AuditLog first = auditRepository.save(new AuditLog("org-1", 1L, 42L,
                ProjectEventType.PROJECT_CREATED, null, "DRAFT", "created", "event-1"));
        auditRepository.save(new AuditLog("org-2", 1L, 99L,
                ProjectEventType.PROJECT_CREATED, null, "DRAFT", "other", "event-1"));
        auditRepository.save(new AuditLog("org-1", 2L, 42L,
                ProjectEventType.PROJECT_CREATED, null, "DRAFT", "other-project", "event-2"));

        List<AuditLog> results = auditRepository.findByOrganisationIdAndProjectIdOrderByCreatedAtAsc("org-1", 1L);

        assertThat(results).containsExactly(first);
        assertThat(auditRepository.findByOrganisationIdAndDeduplicationKey("org-2", "event-1"))
                .isPresent();
    }

    @Test
    void notificationQueriesFilterRecipientEventAndDateRange() {
        Instant created = Instant.parse("2026-09-24T00:00:00Z");
        Notification notification = notificationRepository.save(new Notification("org-1", 42L, 1L,
                ProjectEventType.PROJECT_STATUS_CHANGED, "Update", "moved", "event-1"));

        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notificationRepository.findByOrganisationIdAndUserIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                "org-1", 42L, ProjectEventType.PROJECT_STATUS_CHANGED,
                created.minusSeconds(1), Instant.now().plusSeconds(1))).containsExactly(notification);
        assertThat(notificationRepository.findByOrganisationIdAndUserIdAndDeduplicationKey(
                "org-1", 43L, "event-1")).isEmpty();
    }
}