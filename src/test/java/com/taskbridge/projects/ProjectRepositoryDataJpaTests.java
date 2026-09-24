package com.taskbridge.projects;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ProjectRepositoryDataJpaTests {
    @Autowired private ProjectRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    @Test
    void findsOnlyProjectsWithinOrganisationAndTeam() {
        Project orgOne = repository.save(Project.create("org-1", "Alpha", null, "team-1", ProjectStatus.DRAFT));
        repository.save(Project.create("org-2", "Other", null, "team-1", ProjectStatus.ACTIVE));
        repository.save(Project.create("org-1", "Beta", null, "team-2", ProjectStatus.ACTIVE));

        assertThat(repository.findByOrganisationIdAndId("org-1", orgOne.getId())).contains(orgOne);
        assertThat(repository.findByOrganisationIdAndTeamId("org-1", "team-1"))
                .extracting(Project::getName).containsExactly("Alpha");
        assertThat(repository.findByOrganisationIdAndId("org-2", orgOne.getId())).isEmpty();
    }

    @Test
    void returnsPageWithRequestedBoundarySize() {
        repository.save(Project.create("org-1", "Alpha", null, "team-1", ProjectStatus.DRAFT));
        repository.save(Project.create("org-1", "Beta", null, "team-1", ProjectStatus.ACTIVE));

        Page<Project> page = repository.findByOrganisationId("org-1", PageRequest.of(0, 1));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }
}