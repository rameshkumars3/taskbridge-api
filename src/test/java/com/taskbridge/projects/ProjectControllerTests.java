package com.taskbridge.projects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProjectControllerTests {
    @Autowired private MockMvc mockMvc;
    @MockBean private ProjectService projectService;

    @Test
    void listsProjectsWithPagination() throws Exception {
        when(projectService.getAllProjects(any())).thenReturn(new PageImpl<>(List.of(
                new ProjectResponse(1L, "Alpha", "desc", "team-1", ProjectStatus.DRAFT))));

        mockMvc.perform(get("/api/projects").param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alpha"));
    }

    @Test
    void getsProjectsByIdAndTeam() throws Exception {
        when(projectService.getProjectById(1L))
                .thenReturn(new ProjectResponse(1L, "Alpha", null, "team-1", ProjectStatus.ACTIVE));
        when(projectService.getProjectsByTeamId("team-1")).thenReturn(List.of(
                new ProjectResponse(1L, "Alpha", null, "team-1", ProjectStatus.ACTIVE)));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        mockMvc.perform(get("/api/projects").param("teamId", "team-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value("team-1"));
    }

    @Test
    void rejectsInvalidCreatePayloadAtBoundary() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"teamId\":\"team-1\",\"status\":\"DRAFT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createsUpdatesAndDeletesProject() throws Exception {
        ProjectResponse response = new ProjectResponse(1L, "Alpha", "desc", "team-1", ProjectStatus.DRAFT);
        when(projectService.createProject(any(ProjectRequest.class))).thenReturn(response);
        when(projectService.updateProject(eq(1L), any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/projects").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha\",\"description\":\"desc\",\"teamId\":\"team-1\",\"status\":\"DRAFT\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mockMvc.perform(put("/api/projects/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha\",\"description\":\"desc\",\"teamId\":\"team-1\",\"status\":\"DRAFT\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/projects/1")).andExpect(status().isNoContent());
        verify(projectService).deleteProject(1L);
    }

    @Test
    void rejectsNonNumericProjectId() throws Exception {
        mockMvc.perform(get("/api/projects/not-a-number"))
                .andExpect(status().isBadRequest());
    }
}