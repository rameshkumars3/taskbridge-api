package com.taskbridge.projects;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/** Thin HTTP boundary for the Project service. */
@RestController
@RequestMapping("/api/projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @GetMapping
    public Page<ProjectResponse> list(Pageable pageable) { return projectService.getAllProjects(pageable); }

    @GetMapping("/{projectId}")
    public ProjectResponse get(@PathVariable Long projectId) { return projectService.getProjectById(projectId); }

    @GetMapping(params = "teamId")
    public List<ProjectResponse> byTeam(@RequestParam String teamId) { return projectService.getProjectsByTeamId(teamId); }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PutMapping("/{projectId}")
    public ProjectResponse update(@PathVariable Long projectId, @Valid @RequestBody ProjectRequest request) {
        return projectService.updateProject(projectId, request);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}