package com.taskbridge.projects;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {
    private final AuditLogRepository repository;
    private final ProjectRepository projectRepository;
    private final OrganisationContext organisationContext;
        private final ProjectAuthorizer projectAuthorizer;

    public AuditService(AuditLogRepository repository, ProjectRepository projectRepository,
            OrganisationContext organisationContext, ProjectAuthorizer projectAuthorizer) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.organisationContext = organisationContext;
        this.projectAuthorizer = projectAuthorizer;
    }

    @Transactional
    public AuditLog record(Long projectId, ProjectEventType eventType, String previousStatus,
            String newStatus, String message, String deduplicationKey) {
        projectAuthorizer.requireCreate();
        String organisationId = organisation();
        validateEvent(projectId, eventType, previousStatus, newStatus, message, deduplicationKey);
        AuditLog existing = repository.findByOrganisationIdAndDeduplicationKey(organisationId, deduplicationKey)
                .orElse(null);
        if (existing != null) {
            return existing;
        }
        return repository.save(new AuditLog(organisationId, projectId, organisationContext.currentUserId(), eventType,
                previousStatus, newStatus, message == null ? null : message.trim(), deduplicationKey));
    }

    @Transactional(readOnly = true)
    public List<AuditResponse> history(Long projectId, Instant from, Instant to, ProjectEventType eventType) {
        projectAuthorizer.requireRead();
        String organisationId = organisation();
        validateId(projectId);
        validateRange(from, to);
        if (projectRepository.findByOrganisationIdAndId(organisationId, projectId).isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        List<AuditLog> audits;
        if (from != null && to != null && eventType != null) {
            audits = repository.findByOrganisationIdAndProjectIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtAsc(
                    organisationId, projectId, eventType, from, to);
        } else if (from != null && to != null) {
            audits = repository.findByOrganisationIdAndProjectIdAndCreatedAtBetweenOrderByCreatedAtAsc(
                    organisationId, projectId, from, to);
        } else if (eventType != null) {
            audits = repository.findByOrganisationIdAndProjectIdAndEventTypeOrderByCreatedAtAsc(
                    organisationId, projectId, eventType);
        } else {
            audits = repository.findByOrganisationIdAndProjectIdOrderByCreatedAtAsc(organisationId, projectId);
        }
        return audits.stream().map(AuditResponse::from).collect(Collectors.toList());
    }

    private String organisation() {
        return organisationContext.currentOrganisationId();
    }

    private void validateEvent(Long projectId, ProjectEventType eventType, String previousStatus,
            String newStatus, String message, String deduplicationKey) {
        validateId(projectId);
        if (eventType == null) {
            throw new ValidationException("eventType is required");
        }
        if (eventType == ProjectEventType.PROJECT_STATUS_CHANGED
                && (previousStatus == null || previousStatus.isBlank() || newStatus == null || newStatus.isBlank())) {
            throw new ValidationException("previousStatus and newStatus are required for status changes");
        }
        if (message != null && message.trim().length() > 500) {
            throw new ValidationException("message must not exceed 500 characters");
        }
        if (deduplicationKey == null || deduplicationKey.isBlank()) {
            throw new ValidationException("deduplicationKey is required");
        }
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new ValidationException("projectId must be positive");
        }
    }

    private void validateRange(Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("from must be before or equal to to");
        }
    }
}
