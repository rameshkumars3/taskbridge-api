package com.taskbridge.projects;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Optional<AuditLog> findByOrganisationIdAndDeduplicationKey(String organisationId, String deduplicationKey);

    List<AuditLog> findByOrganisationIdAndProjectIdOrderByCreatedAtAsc(String organisationId, Long projectId);

    List<AuditLog> findByOrganisationIdAndProjectIdAndEventTypeOrderByCreatedAtAsc(
            String organisationId, Long projectId, ProjectEventType eventType);

    List<AuditLog> findByOrganisationIdAndProjectIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            String organisationId, Long projectId, Instant from, Instant to);

    List<AuditLog> findByOrganisationIdAndProjectIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtAsc(
            String organisationId, Long projectId, ProjectEventType eventType, Instant from, Instant to);
}
