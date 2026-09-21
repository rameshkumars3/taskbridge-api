package com.taskbridge.projects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

/** Persistence model for an organisation-owned project. */
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organisationId;
    private String name;
    private String description;
    private String teamId;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Version
    private long version;

    protected Project() {
    }

    Project(String organisationId, String name, String description, String teamId, ProjectStatus status) {
        this.organisationId = organisationId;
        this.name = name;
        this.description = description;
        this.teamId = teamId;
        this.status = status;
    }

    static Project create(String organisationId, String name, String description, String teamId, ProjectStatus status) {
        return new Project(organisationId, name, description, teamId, status);
    }

    /** Returns the generated persistence identifier. */
    public Long getId() { return id; }
    /** Returns the owning organisation identifier. */
    public String getOrganisationId() { return organisationId; }
    /** Returns the project name. */
    public String getName() {
        return name;
    }

    /** Returns the project description. */
    public String getDescription() {
        return description;
    }

    /** Returns the owning team identifier. */
    public String getTeamId() {
        return teamId;
    }

    /** Returns the current lifecycle status. */
    public String getStatus() {
        return status.name();
    }

    /** Applies an approved forward-only lifecycle transition. */
    void transitionTo(ProjectStatus nextStatus) {
        if (!status.canTransitionTo(nextStatus)) {
            throw new InvalidProjectStatusTransitionException(status, nextStatus);
        }
        status = nextStatus;
    }

    void updateDetails(String name, String description, String teamId, ProjectStatus nextStatus) {
        this.name = name;
        this.description = description;
        this.teamId = teamId;
        if (status != nextStatus) {
            transitionTo(nextStatus);
        }
    }
}