package com.taskbridge.projects;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    public List<Project> getProjectsByTeamId(String teamId) {
        return projectRepository.findByTeamId(teamId);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Long projectId, Project project) {
        getProjectById(projectId);
        project.setId(projectId);
        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId) {
        getProjectById(projectId);
        projectRepository.deleteById(projectId);
    }
}
