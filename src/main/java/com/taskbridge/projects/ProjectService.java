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

/** Application service enforcing project validation, authorisation, and tenant scope. */
@Service
public class ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final OrganisationContext organisationContext;
    private final ProjectAuthorizer projectAuthorizer;
    private final Validator validator;

    public ProjectService(ProjectRepository projectRepository, OrganisationContext organisationContext,
            ProjectAuthorizer projectAuthorizer, Validator validator) {
        this.projectRepository = projectRepository;
        this.organisationContext = organisationContext;
        this.projectAuthorizer = projectAuthorizer;
        this.validator = validator;
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
        return ProjectResponse.from(projectRepository.save(project));
    }

    /** Updates a project after resolving it inside the active organisation. */
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        projectAuthorizer.requireUpdate();
        validateId(projectId);
        validate(request);
        Project project = projectRepository.findByOrganisationIdAndId(organisation(), projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        project.updateDetails(request.name(), request.description(), request.teamId(), request.status());
        LOGGER.info("Project {} transitioned to {}", projectId, request.status());
        return ProjectResponse.from(projectRepository.save(project));
    }

    /** Hard-deletes a project within the active organisation. */
    @Transactional
    public void deleteProject(Long projectId) {
        projectAuthorizer.requireDelete();
        validateId(projectId);
        Project project = projectRepository.findByOrganisationIdAndId(organisation(), projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        projectRepository.delete(project);
        LOGGER.info("Project {} deleted", projectId);
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
