package com.taskbridge.projects;

import java.util.Collection;

/** Resolves trusted organisation team membership for notification recipients. */
public interface TeamMemberDirectory {
    Collection<Long> membersOf(String organisationId, String teamId);
}
