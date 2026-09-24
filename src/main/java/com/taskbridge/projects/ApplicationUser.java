package com.taskbridge.projects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/** Persisted identity used to issue organisation-scoped access tokens. */
@Entity
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String organisationId;

    @Column(nullable = false, length = 500)
    private String authorities;

    @Column(nullable = false)
    private boolean enabled = true;

    protected ApplicationUser() {
    }

    public ApplicationUser(String username, String passwordHash, String organisationId, String authorities) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.organisationId = organisationId;
        this.authorities = authorities;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getOrganisationId() { return organisationId; }
    public String getAuthorities() { return authorities; }
    public boolean isEnabled() { return enabled; }
}