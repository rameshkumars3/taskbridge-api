# Security Review Findings – Project Model and Service

## Scope
Reviewed:
- src/main/java/com/taskbridge/projects/Project.java
- src/main/java/com/taskbridge/projects/ProjectService.java

## Confirmed findings

### 1) Critical – Cross-organisation access / tenant scoping
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): getAllProjects, getProjectById, getProjectsByTeamId, createProject, updateProject, deleteProject
- Code evidence:
  - `return projectRepository.findAll();`
  - `return projectRepository.findById(projectId)...`
  - `return projectRepository.findByTeamId(teamId);`
  - `return projectRepository.save(project);`
  - `projectRepository.deleteById(projectId);`
- SaaS impact:
  - This service exposes project data across the entire database without any tenant, organisation, or membership check. In a multi-tenant SaaS, any caller who can reach these methods can enumerate or mutate another tenant’s project records.
- Recommended correction:
  - Enforce organisation scope from the authenticated principal and reject access outside the active tenant. Add repository/service methods that are explicitly scoped to the caller’s organisation, e.g. `findByOrganisationIdAndId(...)`, and apply the same pattern to list, update, and delete operations.

### 2) Critical – IDOR / request-supplied identity
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): getProjectById, updateProject, deleteProject
- Code evidence:
  - `getProjectById(Long projectId)`
  - `project.setId(projectId);`
  - `projectRepository.save(project);`
  - `projectRepository.deleteById(projectId);`
- Additional evidence:
  - File: src/main/java/com/taskbridge/projects/Project.java
  - `private String teamId;`
  - `public void setTeamId(String teamId) { this.teamId = teamId; }`
  - `private Long id;`
  - `public void setId(Long id) { this.id = id; }`
- SaaS impact:
  - A caller can choose arbitrary projectId or teamId values to access or overwrite another tenant’s data. This is a classic IDOR pattern when the object identity is accepted from the client without ownership validation.
- Recommended correction:
  - Remove client-controlled identity authority for project ownership. Resolve the project using a tenant-scoped authenticated identity context and verify that the target project belongs to the caller’s organisation before update or delete.

### 3) High – Missing authentication and authorisation in the service layer
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): getAllProjects, getProjectById, getProjectsByTeamId, createProject, updateProject, deleteProject
- Code evidence:
  - There are no `Principal`, JWT, role, permission, or organisation checks anywhere in the service.
  - Every method calls the repository directly without guard clauses or security validation.
- SaaS impact:
  - If this service is exposed via a controller or endpoint, unauthorised users can read or mutate project data without any proven identity or role enforcement.
- Recommended correction:
  - Require explicit auth checks in the service layer for all project operations, including identity resolution, role or permission validation, and organisation membership checks before any repository access.

### 4) High – Input validation and unsafe data model
- File: src/main/java/com/taskbridge/projects/Project.java
- Method(s): all setters on the entity
- Code evidence:
  - `private Long id;`
  - `public void setId(Long id) { this.id = id; }`
  - `private String name;`
  - `public void setName(String name) { this.name = name; }`
  - `private String description;`
  - `public void setDescription(String description) { this.description = description; }`
  - `private String teamId;`
  - `public void setTeamId(String teamId) { this.teamId = teamId; }`
  - `private String status;`
  - `public void setStatus(String status) { this.status = status; }`
- SaaS impact:
  - Invalid, null, oversized, or malicious input values can be persisted, and the mutable entity ID allows identity override from external input. This weakens data integrity and can create invalid project state.
- Recommended correction:
  - Validate request payloads at API boundaries using DTOs and Bean Validation annotations (`@NotBlank`, `@Size`, `@Pattern`, etc.). Do not accept externally supplied IDs for persistence; generate or assign IDs server-side only.

### 5) Medium – Transactions and concurrency risk
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): updateProject, deleteProject
- Code evidence:
  - `getProjectById(projectId);`
  - `project.setId(projectId);`
  - `return projectRepository.save(project);`
  - `getProjectById(projectId);`
  - `projectRepository.deleteById(projectId);`
- SaaS impact:
  - This is a read-then-write/delete sequence without a transaction or locking strategy. Concurrent requests can race and cause lost updates, stale writes, or inconsistent state.
- Recommended correction:
  - Add `@Transactional` to mutating methods and use optimistic locking or other concurrency controls to prevent races when updating or deleting projects.

### 6) Medium – Sensitive-data exposure
- File: src/main/java/com/taskbridge/projects/Project.java
- Method(s): getDescription, getTeamId, getStatus
- Code evidence:
  - `public String getDescription() { return description; }`
  - `public String getTeamId() { return teamId; }`
  - `public String getStatus() { return status; }`
  - `return projectRepository.findAll();`
  - `return projectRepository.save(project);`
- SaaS impact:
  - Raw entity objects are returned directly from the service, so tenant metadata and internal status data can be exposed without filtering, redaction, or role-aware DTO mapping.
- Recommended correction:
  - Return DTOs instead of persistence entities and redact or omit fields such as `teamId` or internal status based on caller role and tenant scope.

## Conclusion
The reviewed code does not enforce multi-tenant boundaries, does not derive identity from a trusted security context, and accepts client-controlled identifiers without ownership checks. The most severe finding is the combination of raw repository access and mutable ID/team values, which creates direct cross-tenant access and IDOR risk.

## Architecture Review Findings

### 1) High – Persistence entities are used as the service contract
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): getAllProjects, getProjectById, getProjectsByTeamId, createProject, updateProject
- Exact evidence:
  - `public List<Project> getAllProjects()`
  - `public Project getProjectById(Long projectId)`
  - `public List<Project> getProjectsByTeamId(String teamId)`
  - `public Project createProject(Project project)`
  - `public Project updateProject(Long projectId, Project project)`
- Impact:
  - The service accepts and returns the JPA entity defined in Project.java, coupling callers to persistence state and allowing mutable entity fields to cross the application boundary. No controller or DTO mapping layer exists in src/main/java.
- Recommended fix:
  - Define request and response DTOs and map them in the service or a dedicated mapper. Keep Project as a persistence/domain model rather than the external contract.

### 2) High – Mutating service operations are not transactional
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): updateProject, deleteProject
- Exact evidence:
  - `getProjectById(projectId);`
  - `project.setId(projectId);`
  - `return projectRepository.save(project);`
  - `projectRepository.deleteById(projectId);`
  - Neither method has a `@Transactional` annotation.
- Impact:
  - Update and delete perform multiple persistence operations without an explicit service-level atomicity boundary. Concurrent requests can also race because Project has no version field or other visible concurrency control.
- Recommended fix:
  - Add service transaction boundaries for mutating workflows and introduce an explicit optimistic-locking strategy if concurrent updates are part of the supported behavior.

### 3) Medium – No validation is enforced at the model or service boundary
- File: src/main/java/com/taskbridge/projects/Project.java; src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): Project setters; createProject, updateProject, getProjectById, getProjectsByTeamId
- Exact evidence:
  - `private String name;`, `private String teamId;`, and `private String status;` have no Bean Validation annotations.
  - `public Project createProject(Project project) { return projectRepository.save(project); }`
  - `public Project updateProject(Long projectId, Project project) { ... return projectRepository.save(project); }`
  - No null, format, length, or identifier validation is performed before repository access.
- Impact:
  - Invalid values can reach persistence and required-field or business-state rules are not enforced by this slice.
- Recommended fix:
  - Validate API request DTOs with Bean Validation and enforce service-level business constraints before saving or querying.

### 4) Medium – No centralized error response handling is present
- File: repository-wide under src/main/java
- Method(s): ProjectNotFoundException constructor; no handler exists
- Exact evidence:
  - `public class ProjectNotFoundException extends RuntimeException`
  - `super("Project not found: " + projectId);`
  - No `@ControllerAdvice` or `@ExceptionHandler` declaration exists under src/main/java.
- Impact:
  - The specific exception has no centralized mapping to a consistent API error response. There is also no controller in the current source tree to define the HTTP boundary.
- Recommended fix:
  - Add a controller advice when the API boundary is implemented, mapping domain exceptions to consistent typed error responses.

### 5) Medium – Unbounded list query
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method: getAllProjects
- Exact evidence:
  - `public List<Project> getAllProjects() {`
  - `return projectRepository.findAll();`
- Impact:
  - The method loads and returns every project, so memory use and response time grow with the table size.
- Recommended fix:
  - Use Spring Data pagination with `Pageable` and return a paged response DTO for collection retrieval.

### 6) Medium – No focused tests cover the Project slice
- Files: src/test/java/com/taskbridge/TaskbridgeApiApplicationTests.java; src/test/java/com/taskbridge/projects
- Method(s): contextLoads; no Project test methods
- Exact evidence:
  - The only visible test is `void contextLoads()`, which only verifies that the application context starts.
  - The `src/test/java/com/taskbridge/projects` directory contains no test files.
- Impact:
  - CRUD behavior, not-found handling, validation, transaction behavior, query behavior, and authorization boundaries have no focused regression coverage.
- Recommended fix:
  - Add service and repository tests for the supported Project operations, including success and failure paths relevant to the service contract.

### 7) Low – No structured service logging
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): all public methods
- Exact evidence:
  - ProjectService declares no SLF4J logger and contains no logging calls.
- Impact:
  - Project state transitions and exceptional outcomes cannot be correlated through service-level logs.
- Recommended fix:
  - Add concise SLF4J logging for relevant business transitions and failures, excluding project payloads or other sensitive data.

### 8) Low – Public model and service contracts lack documentation
- File: src/main/java/com/taskbridge/projects/Project.java; src/main/java/com/taskbridge/projects/ProjectService.java
- Method(s): public classes, getters/setters, and service methods
- Exact evidence:
  - The public classes and methods contain no Javadocs or other contract documentation.
- Impact:
  - Field semantics, accepted values, validation rules, and exception behavior are not documented in the public code contract.
- Recommended fix:
  - Document the public service operations and domain-field semantics once those rules are defined by the product contract.

## Architecture Review Scope Notes

- ProjectRepository correctly uses Spring Data JPA through `JpaRepository` and the derived `findByTeamId` query.
- No raw JDBC, SQL, EntityManager, or manual database access was found.
- ProjectService uses constructor injection.
- No controller exists, so no controller-specific separation defect can be confirmed beyond the absence of an API/DTO boundary.
- No additional naming or type-safety defect was classified because the repository does not define the required domain vocabulary or status type.