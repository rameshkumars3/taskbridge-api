package com.taskbridge.projects;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	Page<Project> findByOrganisationId(String organisationId, Pageable pageable);
	Optional<Project> findByOrganisationIdAndId(String organisationId, Long id);
	List<Project> findByOrganisationIdAndTeamId(String organisationId, String teamId);
}