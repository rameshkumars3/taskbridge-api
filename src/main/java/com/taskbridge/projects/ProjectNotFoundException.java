package com.taskbridge.projects;

public class ProjectNotFoundException extends RuntimeException {

	public ProjectNotFoundException(Long projectId) {
		super("Project not found: " + projectId);
	}
}