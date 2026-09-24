package com.taskbridge.projects;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final Duration lifetime;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.lifetime:PT15M}") Duration lifetime) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.security.jwt.secret must contain at least 32 characters");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.lifetime = lifetime;
    }

    public String issue(ApplicationUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("organisationId", user.getOrganisationId())
                .claim("authorities", authorities(user.getAuthorities()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(lifetime)))
                .signWith(signingKey)
                .compact();
    }

    public JwtPrincipal parse(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload();
        Number userId = claims.get("userId", Number.class);
        String organisationId = claims.get("organisationId", String.class);
        if (claims.getSubject() == null || userId == null || userId.longValue() < 1
                || organisationId == null || organisationId.isBlank()) {
            throw new UnauthorizedAccessException("Invalid authentication token");
        }
        List<?> authorityClaims = claims.get("authorities", List.class);
        if (authorityClaims == null) {
            throw new UnauthorizedAccessException("Invalid authentication token");
        }
        List<SimpleGrantedAuthority> granted = authorityClaims.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new JwtPrincipal(claims.getSubject(), userId.longValue(), organisationId, granted);
    }

    public long expiresInSeconds() { return lifetime.toSeconds(); }

    private Collection<String> authorities(String value) {
        return Arrays.stream(value.split(",")).map(String::trim).filter(item -> !item.isBlank()).distinct().toList();
    }
}