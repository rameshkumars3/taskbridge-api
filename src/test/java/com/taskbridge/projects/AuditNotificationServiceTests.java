package com.taskbridge.projects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuditNotificationServiceTests {
    @Mock private AuditLogRepository auditRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private OrganisationContext context;
    @Mock private ProjectAuthorizer authorizer;
    @Mock private TeamMemberDirectory members;
    private AuditService auditService;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditService = new AuditService(auditRepository, projectRepository, context, authorizer);
        notificationService = new NotificationService(notificationRepository, members, context, authorizer);
        when(context.currentOrganisationId()).thenReturn("org-1");
        when(context.currentUserId()).thenReturn(42L);
    }

    @Test
    void auditIsTenantScopedAndIdempotent() {
        Project project = Project.create("org-1", "Alpha", "desc", "team-1", ProjectStatus.ACTIVE);
        when(projectRepository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.of(project));
        when(auditRepository.findByOrganisationIdAndDeduplicationKey("org-1", "event-1"))
                .thenReturn(Optional.empty());
        AuditLog saved = new AuditLog("org-1", 1L, 42L, ProjectEventType.PROJECT_CREATED,
                null, "ACTIVE", "created", "event-1");
        when(auditRepository.save(any(AuditLog.class))).thenReturn(saved);

        AuditLog result = auditService.record(1L, ProjectEventType.PROJECT_CREATED,
                null, "ACTIVE", "created", "event-1");

        assertThat(result.getOrganisationId()).isEqualTo("org-1");
        assertThat(result.getActorUserId()).isEqualTo(42L);
        verify(auditRepository).save(any(AuditLog.class));
    }

    @Test
    void auditRejectsInvalidFilterRange() {
        assertThatThrownBy(() -> auditService.history(1L,
                java.time.Instant.parse("2026-09-02T00:00:00Z"),
                java.time.Instant.parse("2026-09-01T00:00:00Z"), null))
                .isInstanceOf(ValidationException.class);
        verify(projectRepository, never()).findByOrganisationIdAndId(any(), any());
    }

    @Test
    void auditHistoryAppliesDateRangeAndEventTypeFilters() {
        Project project = Project.create("org-1", "Alpha", "desc", "team-1", ProjectStatus.ACTIVE);
        Instant from = Instant.parse("2026-09-01T00:00:00Z");
        Instant to = Instant.parse("2026-09-30T00:00:00Z");
        when(projectRepository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.of(project));
        when(auditRepository.findByOrganisationIdAndProjectIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtAsc(
                "org-1", 1L, ProjectEventType.PROJECT_STATUS_CHANGED, from, to)).thenReturn(List.of());

        List<AuditResponse> result = auditService.history(1L, from, to,
                ProjectEventType.PROJECT_STATUS_CHANGED);

        assertThat(result).isEmpty();
        verify(auditRepository).findByOrganisationIdAndProjectIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtAsc(
                "org-1", 1L, ProjectEventType.PROJECT_STATUS_CHANGED, from, to);
    }

    @Test
    void auditCannotBeDeletedOrOverwritten() {
        AuditLog existing = new AuditLog("org-1", 1L, 42L, ProjectEventType.PROJECT_CREATED,
                null, "DRAFT", "created", "event-1");
        when(auditRepository.findByOrganisationIdAndDeduplicationKey("org-1", "event-1"))
                .thenReturn(Optional.of(existing));

        AuditLog result = auditService.record(1L, ProjectEventType.PROJECT_CREATED,
                null, "DRAFT", "replacement", "event-1");

        assertThat(result).isSameAs(existing);
        verify(auditRepository, never()).save(any(AuditLog.class));
        verify(auditRepository, never()).delete(any(AuditLog.class));
    }

    @Test
    void auditHistoryRejectsProjectOutsideActiveOrganisation() {
        when(projectRepository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditService.history(1L, null, null, null))
                .isInstanceOf(ProjectNotFoundException.class);
        verify(auditRepository, never()).findByOrganisationIdAndProjectIdOrderByCreatedAtAsc(any(), any());
    }

    @Test
    void notificationsAreCreatedForEveryTeamMember() {
        Project project = Project.create("org-1", "Alpha", "desc", "team-1", ProjectStatus.ACTIVE);
        when(projectRepository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.of(project));
        when(members.membersOf("org-1", "team-1")).thenReturn(List.of(42L, 43L));
        when(notificationRepository.findByOrganisationIdAndUserIdAndDeduplicationKey(
                "org-1", 42L, "event-1")).thenReturn(Optional.empty());
        when(notificationRepository.findByOrganisationIdAndUserIdAndDeduplicationKey(
                "org-1", 43L, "event-1")).thenReturn(Optional.empty());

        notificationService.notifyTeam(1L, "team-1", ProjectEventType.PROJECT_STATUS_CHANGED,
                "moved", "event-1");

        var saved = org.mockito.ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, org.mockito.Mockito.times(2)).save(saved.capture());
        assertThat(saved.getAllValues()).extracting(Notification::getUserId)
                .containsExactlyInAnyOrder(42L, 43L);
    }

    @Test
    void unreadNotificationsUseDateRangeAndEventTypeFilters() {
        Instant from = Instant.parse("2026-09-01T00:00:00Z");
        Instant to = Instant.parse("2026-09-30T00:00:00Z");
        when(notificationRepository
                .findByOrganisationIdAndUserIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                        "org-1", 42L, ProjectEventType.PROJECT_STATUS_CHANGED, from, to))
                .thenReturn(List.of(new Notification("org-1", 42L, 1L,
                        ProjectEventType.PROJECT_STATUS_CHANGED, "Project update", "moved", "event-1")));

        List<NotificationResponse> result = notificationService.unread(42L, from, to,
                ProjectEventType.PROJECT_STATUS_CHANGED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).eventType()).isEqualTo(ProjectEventType.PROJECT_STATUS_CHANGED);
        verify(notificationRepository)
                .findByOrganisationIdAndUserIdAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                        "org-1", 42L, ProjectEventType.PROJECT_STATUS_CHANGED, from, to);
    }

    @Test
    void authorisedUserCanMarkNotificationAsRead() {
        Notification notification = new Notification("org-1", 42L, 1L,
                ProjectEventType.PROJECT_CREATED, "Project update", "created", "event-1");
        when(notificationRepository.findByOrganisationIdAndUserIdAndId("org-1", 42L, 9L))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        NotificationResponse response = notificationService.markRead(9L);

        assertThat(response.read()).isTrue();
        assertThat(notification.getReadAt()).isNotNull();
        verify(notificationRepository).save(notification);
    }

    @Test
    void unauthorisedUserCannotMarkNotificationAsRead() {
        when(notificationRepository.findByOrganisationIdAndUserIdAndId("org-1", 42L, 9L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markRead(9L))
                .isInstanceOf(ProjectNotFoundException.class);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void notificationReadRequiresTheAuthenticatedRecipient() {
        when(context.currentUserId()).thenReturn(42L);

        assertThatThrownBy(() -> notificationService.unread(43L, null, null, null))
                .isInstanceOf(ForbiddenOperationException.class);
        verify(notificationRepository, never()).findByOrganisationIdAndUserIdOrderByCreatedAtDesc(any(), any());
    }
}
