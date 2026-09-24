package com.taskbridge.projects;

import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;

/** Default directory for the current security slice; deployments can replace it with team membership storage. */
@Component
public class AuthenticatedUserTeamMemberDirectory implements TeamMemberDirectory {
    private final OrganisationContext organisationContext;

    public AuthenticatedUserTeamMemberDirectory(OrganisationContext organisationContext) {
        this.organisationContext = organisationContext;
    }

    @Override
    public Collection<Long> membersOf(String organisationId, String teamId) {
        if (!organisationId.equals(organisationContext.currentOrganisationId())) {
            throw new ForbiddenOperationException("Team is outside the active organisation");
        }
        return List.of(organisationContext.currentUserId());
    }
}
