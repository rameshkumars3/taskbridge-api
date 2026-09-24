package com.taskbridge.projects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuditController.class, NotificationController.class})
@Import(GlobalExceptionHandler.class)
class AuditNotificationControllerTests {
    @Autowired private MockMvc mockMvc;
    @MockBean private AuditService auditService;
    @MockBean private NotificationService notificationService;

    @Test
    void createsAuditAndDoesNotAcceptOrganisationFromRequest() throws Exception {
        AuditLog audit = new AuditLog("org-1", 7L, 42L, ProjectEventType.PROJECT_CREATED,
                null, "DRAFT", "created", "org-1|project-7|created");
        when(auditService.record(7L, ProjectEventType.PROJECT_CREATED, null, "DRAFT", "created",
                "org-1|project-7|created")).thenReturn(audit);

        mockMvc.perform(post("/api/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"projectId":7,"actorUserId":999,"organisationId":"attacker",
                         "eventType":"PROJECT_CREATED","newStatus":"DRAFT","message":"created",
                         "deduplicationKey":"org-1|project-7|created"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organisationId").value("org-1"))
                .andExpect(jsonPath("$.actorUserId").value(42));

        verify(auditService).record(7L, ProjectEventType.PROJECT_CREATED, null, "DRAFT", "created",
                "org-1|project-7|created");
    }

    @Test
    void rejectsInvalidAuditRequestAtTheHttpBoundary() throws Exception {
        mockMvc.perform(post("/api/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectId\":0,\"eventType\":\"PROJECT_CREATED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void retrievesTenantScopedAuditHistoryWithFilters() throws Exception {
        when(auditService.history(eq(7L), any(Instant.class), any(Instant.class),
                eq(ProjectEventType.PROJECT_STATUS_CHANGED))).thenReturn(List.of());

        mockMvc.perform(get("/api/audit/7")
                .param("from", "2026-09-01T00:00:00Z")
                .param("to", "2026-09-30T00:00:00Z")
                .param("eventType", "PROJECT_STATUS_CHANGED"))
                .andExpect(status().isOk());

        verify(auditService).history(eq(7L), any(Instant.class), any(Instant.class),
                eq(ProjectEventType.PROJECT_STATUS_CHANGED));
    }

    @Test
    void listsAllNotificationsForTheRequestedRecipient() throws Exception {
        when(notificationService.findAll(42L, null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/42"))
                .andExpect(status().isOk());

        verify(notificationService).findAll(42L, null, null, null);
    }

    @Test
    void marksNotificationReadThroughServiceOwnershipCheck() throws Exception {
        NotificationResponse response = new NotificationResponse(9L, "org-1", 42L, 7L,
                ProjectEventType.PROJECT_CREATED, "Project update", "created", true,
                Instant.parse("2026-09-24T00:00:00Z"), Instant.parse("2026-09-24T00:01:00Z"), "event-9");
        when(notificationService.markRead(9L)).thenReturn(response);

        mockMvc.perform(patch("/api/notifications/9/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true))
                .andExpect(jsonPath("$.userId").value(42));

        verify(notificationService).markRead(9L);
    }

    @Test
    void rejectsCrossRecipientPathAtValidationBoundary() throws Exception {
        mockMvc.perform(get("/api/notifications/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

        @Test
        void mapsRecipientOwnershipFailureToForbidden() throws Exception {
                when(notificationService.findAll(43L, null, null, null))
                                .thenThrow(new ForbiddenOperationException("Notifications belong to the authenticated user"));

                mockMvc.perform(get("/api/notifications/43"))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.status").value(403));
        }
}