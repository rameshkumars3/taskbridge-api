package com.taskbridge.projects;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final TeamMemberDirectory teamMemberDirectory;
    private final OrganisationContext organisationContext;
    private final ProjectAuthorizer projectAuthorizer;

    public NotificationService(NotificationRepository repository,
            TeamMemberDirectory teamMemberDirectory, OrganisationContext organisationContext,
            ProjectAuthorizer projectAuthorizer) {
        this.repository = repository;
        this.teamMemberDirectory = teamMemberDirectory;
        this.organisationContext = organisationContext;
        this.projectAuthorizer = projectAuthorizer;
    }

    @Transactional
    public void notifyTeam(Long projectId, String teamId, ProjectEventType eventType,
            String message, String deduplicationKey) {
        String organisationId = organisation();
        validate(projectId, eventType, deduplicationKey);
        Collection<Long> members = teamMemberDirectory.membersOf(organisationId, teamId);
        for (Long userId : members) {
            if (userId == null || userId < 1) {
                continue;
            }
            if (repository.findByOrganisationIdAndUserIdAndDeduplicationKey(
                    organisationId, userId, deduplicationKey).isEmpty()) {
                repository.save(new Notification(organisationId, userId, projectId, eventType,
                        "Project update", message, deduplicationKey));
            }
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll(Long requestedUserId, Instant from, Instant to,
            ProjectEventType eventType) {
        projectAuthorizer.requireRead();
        Long userId = authorisedUser(requestedUserId);
        validateRange(from, to);
        return find(userId, from, to, eventType).stream()
                .map(NotificationResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> unread(Long requestedUserId, Instant from, Instant to,
            ProjectEventType eventType) {
        projectAuthorizer.requireRead();
        Long userId = authorisedUser(requestedUserId);
        validateRange(from, to);
        List<Notification> notifications = find(userId, from, to, eventType);
        return notifications.stream().filter(notification -> !notification.getRead())
                .map(NotificationResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markRead(Long notificationId) {
        projectAuthorizer.requireRead();
        if (notificationId == null || notificationId < 1) {
            throw new ValidationException("notificationId must be positive");
        }
        Notification notification = repository.findByOrganisationIdAndUserIdAndId(
                organisation(), organisationContext.currentUserId(), notificationId)
                .orElseThrow(() -> new ProjectNotFoundException(notificationId));
        notification.markRead();
        return NotificationResponse.from(repository.save(notification));
    }

    private List<Notification> find(Long userId, Instant from, Instant to, ProjectEventType eventType) {
        String organisationId = organisation();
        if (from != null && to != null && eventType != null) {
            return repository.findByOrganisationIdAndUserIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                    organisationId, userId, eventType, from, to);
        }
        if (from != null && to != null) {
            return repository.findByOrganisationIdAndUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                    organisationId, userId, from, to);
        }
        if (eventType != null) {
            return repository.findByOrganisationIdAndUserIdAndEventTypeOrderByCreatedAtDesc(
                    organisationId, userId, eventType);
        }
        return repository.findByOrganisationIdAndUserIdOrderByCreatedAtDesc(organisationId, userId);
    }

    private Long authorisedUser(Long requestedUserId) {
        Long currentUserId = organisationContext.currentUserId();
        if (requestedUserId == null || requestedUserId < 1) {
            throw new ValidationException("userId must be positive");
        }
        if (!currentUserId.equals(requestedUserId)) {
            throw new ForbiddenOperationException("Notifications belong to the authenticated user");
        }
        return currentUserId;
    }

    private String organisation() {
        return organisationContext.currentOrganisationId();
    }

    private void validate(Long projectId, ProjectEventType eventType, String deduplicationKey) {
        if (projectId == null || projectId < 1) {
            throw new ValidationException("projectId must be positive");
        }
        if (eventType == null) {
            throw new ValidationException("eventType is required");
        }
        if (deduplicationKey == null || deduplicationKey.isBlank()) {
            throw new ValidationException("deduplicationKey is required");
        }
    }

    private void validateRange(Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("from must be before or equal to to");
        }
    }
}
