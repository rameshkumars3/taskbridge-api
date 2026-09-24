package com.taskbridge.projects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserBootstrap {
    @Bean
    @ConditionalOnProperty(name = "app.security.bootstrap.enabled", havingValue = "true")
    CommandLineRunner bootstrapUser(ApplicationUserRepository repository, PasswordEncoder encoder,
            @Value("${app.security.bootstrap.username}") String username,
            @Value("${app.security.bootstrap.password}") String password,
            @Value("${app.security.bootstrap.organisation-id}") String organisationId,
            @Value("${app.security.bootstrap.authorities}") String authorities) {
        return args -> {
            String normalizedUsername = username.trim().toLowerCase();
            if (password.length() < 12 || organisationId.isBlank() || authorities.isBlank()) {
                throw new IllegalStateException("Bootstrap user configuration is incomplete or insecure");
            }
            if (repository.findByUsername(normalizedUsername).isEmpty()) {
                repository.save(new ApplicationUser(normalizedUsername, encoder.encode(password), organisationId.trim(), authorities));
            }
        };
    }
}