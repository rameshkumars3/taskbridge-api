package com.taskbridge.projects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.Validation;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProjectServiceTests {
    @Mock private ProjectRepository repository;
    @Mock private OrganisationContext context;
    @Mock private ProjectAuthorizer authorizer;
    @Mock private AuditService auditService;
    @Mock private NotificationService notificationService;
    private ProjectService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ProjectService(repository, context, authorizer,
            Validation.buildDefaultValidatorFactory().getValidator(), auditService, notificationService);
        when(context.currentOrganisationId()).thenReturn("org-1");
        when(context.currentUserId()).thenReturn(42L);
    }

    @Test
    void getProjectUsesOrganisationScopedLookup() {
        Project project = Project.create("org-1", "Alpha", "desc", "team-1", ProjectStatus.DRAFT);
        when(repository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.of(project));

        ProjectResponse response = service.getProjectById(1L);

        assertThat(response.name()).isEqualTo("Alpha");
        verify(repository).findByOrganisationIdAndId("org-1", 1L);
    }

    @Test
    void missingProjectIsNotFound() {
        when(repository.findByOrganisationIdAndId("org-1", 9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProjectById(9L)).isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void invalidRequestDoesNotReachRepository() {
        ProjectRequest request = new ProjectRequest("", "desc", "team-1", ProjectStatus.DRAFT);

        assertThatThrownBy(() -> service.createProject(request)).isInstanceOf(ValidationException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void crossOrganisationProjectIsConcealed() {
        when(repository.findByOrganisationIdAndId(eq("org-1"), eq(4L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteProject(4L)).isInstanceOf(ProjectNotFoundException.class);
        verify(repository, never()).delete(any(Project.class));
    }

    @Test
    void invalidStatusTransitionIsRejected() {
        Project project = Project.create("org-1", "Alpha", "desc", "team-1", ProjectStatus.DRAFT);
        when(repository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.of(project));
        ProjectRequest request = new ProjectRequest("Alpha", "desc", "team-1", ProjectStatus.COMPLETED);

        assertThatThrownBy(() -> service.updateProject(1L, request))
                .isInstanceOf(InvalidProjectStatusTransitionException.class);
    }

    @Test
    void milestoneUpdateRecordsStatusChangeAudit() {
        Project project = Project.create("org-1", "Alpha", "desc", "team-1", ProjectStatus.ACTIVE);
        when(repository.findByOrganisationIdAndId("org-1", 1L)).thenReturn(Optional.of(project));
        when(repository.save(project)).thenReturn(project);
        ProjectRequest request = new ProjectRequest("Alpha", "desc", "team-1", ProjectStatus.COMPLETED);

        service.updateProject(1L, request);

        verify(auditService).record(isNull(), eq(ProjectEventType.PROJECT_STATUS_CHANGED),
            eq("ACTIVE"), eq("COMPLETED"), any(String.class), any(String.class));
        verify(notificationService).notifyTeam(isNull(), eq("team-1"),
            eq(ProjectEventType.PROJECT_STATUS_CHANGED), any(String.class), any(String.class));
    }
}