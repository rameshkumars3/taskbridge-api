package com.taskbridge.projects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Reads tenant identity only from an authenticated organisation-aware principal. */
@Component
public class SecurityOrganisationContext implements OrganisationContext {
    @Override
    public String currentOrganisationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof OrganisationAwarePrincipal principal)
                || principal.getOrganisationId() == null || principal.getOrganisationId().isBlank()) {
            throw new UnauthorizedAccessException("An authenticated organisation context is required");
        }
        return principal.getOrganisationId();
    }
}