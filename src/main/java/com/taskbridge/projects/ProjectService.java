package com.taskbridge.projects;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/** Application service enforcing project validation, authorisation, and tenant scope. */
@Service
public class ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final OrganisationContext organisationContext;
    private final ProjectAuthorizer projectAuthorizer;
    private final Validator validator;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, OrganisationContext organisationContext,
            ProjectAuthorizer projectAuthorizer, Validator validator, AuditService auditService,
            NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.organisationContext = organisationContext;
        this.projectAuthorizer = projectAuthorizer;
        this.validator = validator;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    ProjectService(ProjectRepository projectRepository, OrganisationContext organisationContext,
            ProjectAuthorizer projectAuthorizer, Validator validator) {
        this(projectRepository, organisationContext, projectAuthorizer, validator, null, null);
    }

    /** Lists only projects visible to the active organisation. */
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        projectAuthorizer.requireRead();
        return projectRepository.findByOrganisationId(organisation(), pageable).map(ProjectResponse::from);
    }

    /** Gets one project, concealing cross-organisation identifiers as not found. */
    public ProjectResponse getProjectById(Long projectId) {
        projectAuthorizer.requireRead();
        validateId(projectId);
        ProjectResponse response = projectRepository.findByOrganisationIdAndId(organisation(), projectId)
                .map(ProjectResponse::from)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        LOGGER.debug("Project lookup completed within the authorised organisation scope");
        return response;
    }

    /** Lists projects in a team within the active organisation. */
    public List<ProjectResponse> getProjectsByTeamId(String teamId) {
        projectAuthorizer.requireRead();
        validateText(teamId, "teamId");
        return projectRepository.findByOrganisationIdAndTeamId(organisation(), teamId).stream()
                .map(ProjectResponse::from).collect(Collectors.toList());
    }

    /** Creates a project owned by the active organisation. */
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        projectAuthorizer.requireCreate();
        validate(request);
        Project project = Project.create(organisation(), request.name(), request.description(), request.teamId(),
                request.status());
        Project saved = projectRepository.save(project);
        emit(saved, ProjectEventType.PROJECT_CREATED, null, saved.getStatus(),
            "Project " + saved.getId() + " was created", eventKey(saved, ProjectEventType.PROJECT_CREATED, null, saved.getStatus()));
        return ProjectResponse.from(saved);
    }

    /** Updates a project after resolving it inside the active organisation. */
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        projectAuthorizer.requireUpdate();
        validateId(projectId);
        validate(request);
        Project project = projectRepository.findByOrganisationIdAndId(organisation(), projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        String previousStatus = project.getStatus();
        project.updateDetails(request.name(), request.description(), request.teamId(), request.status());
        LOGGER.info("Project {} transitioned to {}", projectId, request.status());
        Project saved = projectRepository.save(project);
        if (!previousStatus.equals(saved.getStatus())) {
            emit(saved, ProjectEventType.PROJECT_STATUS_CHANGED, previousStatus, saved.getStatus(),
                "Project " + saved.getId() + " moved to " + saved.getStatus(),
                eventKey(saved, ProjectEventType.PROJECT_STATUS_CHANGED, previousStatus, saved.getStatus()));
        }
        return ProjectResponse.from(saved);
    }

    /** Hard-deletes a project within the active organisation. */
    @Transactional
    public void deleteProject(Long projectId) {
        projectAuthorizer.requireDelete();
        validateId(projectId);
        Project project = projectRepository.findByOrganisationIdAndId(organisation(), projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        projectRepository.delete(project);
        emit(project, ProjectEventType.PROJECT_DELETED, project.getStatus(), null,
                "Project " + project.getId() + " was deleted",
                eventKey(project, ProjectEventType.PROJECT_DELETED, project.getStatus(), null));
        LOGGER.info("Project {} deleted", projectId);
    }

    private void emit(Project project, ProjectEventType eventType, String previousStatus,
            String newStatus, String message, String key) {
        if (auditService == null || notificationService == null) {
            return;
        }
        auditService.record(project.getId(), eventType, previousStatus, newStatus, message, key);
        notificationService.notifyTeam(project.getId(), project.getTeamId(), eventType, message, key);
    }

    private String eventKey(Project project, ProjectEventType eventType, String previousStatus, String newStatus) {
        return organisation() + "|project-" + project.getId() + "|" + eventType + "|"
                + organisationContext.currentUserId() + "|" + previousStatus + "|" + newStatus;
    }

    private String organisation() {
        return organisationContext.currentOrganisationId();
    }

    private void validate(ProjectRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.stream().map(ConstraintViolation::getMessage).sorted()
                    .collect(Collectors.joining(", ")));
        }
    }

    private void validateId(Long projectId) {
        if (projectId == null || projectId < 1) {
            throw new ValidationException("projectId must be positive");
        }
    }

    private void validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field + " must not be blank");
        }
    }
}
