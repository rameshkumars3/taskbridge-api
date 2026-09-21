package com.taskbridge.projects;

/** Resolves the organisation from trusted authentication state. */
public interface OrganisationContext {
    String currentOrganisationId();
}