package com.taskbridge.projects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ApplicationUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ApplicationUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        ApplicationUser user = repository.findByUsername(request.username().trim().toLowerCase())
                .filter(ApplicationUser::isEnabled)
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid username or password"));
        return new LoginResponse(jwtService.issue(user), "Bearer", jwtService.expiresInSeconds());
    }
}