package com.taskbridge.projects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ApplicationUserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(new ApplicationUser("alice", passwordEncoder.encode("correct horse battery"),
                "org-1", "project:read"));
    }

    @Test
    void rejectsProtectedRequestWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void logsInAndUsesVerifiedClaimsForTenantScopedRequest() throws Exception {
        String token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"correct horse battery\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString()
                .replaceAll(".*\\\"accessToken\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/projects/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}