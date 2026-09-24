package com.taskbridge.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@EnableWebSecurity
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    public SecurityConfig(ObjectMapper objectMapper, JwtService jwtService) {
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> { })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .contentTypeOptions(contentType -> { })
                        .frameOptions(frame -> frame.deny())
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

        @Bean
        UserDetailsService userDetailsService(ApplicationUserRepository repository) {
        return username -> repository.findByUsername(username.trim().toLowerCase())
            .filter(ApplicationUser::isEnabled)
            .map(user -> new JwtPrincipal(user.getUsername(), user.getId(), user.getOrganisationId(),
                java.util.Arrays.stream(user.getAuthorities().split(","))
                    .map(String::trim)
                    .filter(authority -> !authority.isBlank())
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                    .toList()))
            .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
        }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) -> writeError(response, 401, "Unauthorized", "Authentication is required");
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> writeError(response, 403, "Forbidden", "Access is denied");
    }

    private void writeError(jakarta.servlet.http.HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                Map.of("timestamp", Instant.now(), "status", status, "error", error, "message", message));
    }
}