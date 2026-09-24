package com.taskbridge.projects;

import java.security.Principal;

/** Authenticated principal contract carrying the trusted active organisation. */
public interface OrganisationAwarePrincipal extends Principal {
    String getOrganisationId();

    Long getUserId();
}