package com.taskbridge.projects;

public class InvalidProjectStatusTransitionException extends RuntimeException {
    public InvalidProjectStatusTransitionException(ProjectStatus current, ProjectStatus next) {
        super("Project cannot transition from " + current + " to " + next);
    }
}