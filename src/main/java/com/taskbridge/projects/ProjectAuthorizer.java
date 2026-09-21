package com.taskbridge.projects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Enforces operation permissions before project repository access. */
@Component
public class ProjectAuthorizer {
    public void requireRead() { require("project:read"); }
    public void requireCreate() { require("project:create"); }
    public void requireUpdate() { require("project:update"); }
    public void requireDelete() { require("project:delete"); }

    private void require(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Authentication is required");
        }
        if (authentication.getAuthorities().stream().noneMatch(granted -> authority.equals(granted.getAuthority()))) {
            throw new ForbiddenOperationException("Missing permission: " + authority);
        }
    }
}