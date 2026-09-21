package com.taskbridge.projects;

/** API representation that does not expose the JPA entity. */
public record ProjectResponse(Long id, String name, String description, String teamId, ProjectStatus status) {
    static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getTeamId(),
                ProjectStatus.valueOf(project.getStatus()));
    }
}