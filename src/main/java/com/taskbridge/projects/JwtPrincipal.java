package com.taskbridge.projects;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Trusted identity reconstructed only after a JWT signature has been verified. */
public class JwtPrincipal implements OrganisationAwarePrincipal, UserDetails {
    private final String username;
    private final Long userId;
    private final String organisationId;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtPrincipal(String username, Long userId, String organisationId,
            Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.userId = userId;
        this.organisationId = organisationId;
        this.authorities = authorities;
    }

    @Override public String getName() { return username; }
    @Override public String getUsername() { return username; }
    @Override public Long getUserId() { return userId; }
    @Override public String getOrganisationId() { return organisationId; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}