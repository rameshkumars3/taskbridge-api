package com.taskbridge.projects;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByOrganisationIdAndId(String organisationId, Long id);

    Optional<Notification> findByOrganisationIdAndUserIdAndId(String organisationId, Long userId, Long id);

    Optional<Notification> findByOrganisationIdAndUserIdAndDeduplicationKey(
            String organisationId, Long userId, String deduplicationKey);

    List<Notification> findByOrganisationIdAndUserIdOrderByCreatedAtDesc(String organisationId, Long userId);

    List<Notification> findByOrganisationIdAndUserIdAndEventTypeOrderByCreatedAtDesc(
            String organisationId, Long userId, ProjectEventType eventType);

    List<Notification> findByOrganisationIdAndUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String organisationId, Long userId, Instant from, Instant to);

    List<Notification> findByOrganisationIdAndUserIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            String organisationId, Long userId, ProjectEventType eventType, Instant from, Instant to);
}
