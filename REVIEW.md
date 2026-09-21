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

## Focused Finding – deleteProject

The selected deletion method combines the following confirmed risks:

### Security and organisation isolation
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method: `deleteProject`
- Exact code evidence:
  - `getProjectById(projectId);`
  - `projectRepository.deleteById(projectId);`
- Finding:
  - Both operations use only the caller-supplied project ID. The method contains no authentication, authorisation, organisation-membership, or tenant-scope check. A caller who can reach the method and obtain another organisation's project ID could delete that project.
- Recommended correction:
  - Resolve the active organisation from the trusted authenticated identity and perform an organisation-scoped lookup and delete. Enforce the caller's delete permission before accessing the repository.

### Transaction and concurrency
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method: `deleteProject`
- Exact code evidence:
  - `getProjectById(projectId);`
  - `projectRepository.deleteById(projectId);`
  - No `@Transactional` annotation is present on the method.
- Finding:
  - The existence check and delete are separate persistence operations without an explicit service-level transaction or visible locking/version check. A concurrent request can remove or change the row after the check and before the delete, producing race-dependent behavior.
- Recommended correction:
  - Define the deletion workflow within a service transaction and use an appropriate concurrency strategy where concurrent modifications are supported.

### Validation and data integrity
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Method: `deleteProject`
- Exact code evidence:
  - `public void deleteProject(Long projectId) {`
  - `getProjectById(projectId);`
  - `projectRepository.deleteById(projectId);`
- Finding:
  - The method performs only an existence check. It does not explicitly validate a null or otherwise invalid identifier, confirm that the project is in a deletable state, record an audit event, or preserve a recovery path. `deleteById` is a hard delete, and the visible entity contains no relationship or cascade configuration that documents how dependent records are handled.
- Recommended correction:
  - Validate the identifier and deletion preconditions at the service boundary, define the required dependent-record behavior, and apply the product-approved audit or soft-delete policy.

## Conclusion
The reviewed code does not enforce multi-tenant boundaries, does not derive identity from a trusted security context, and accepts client-controlled identifiers without ownership checks. The most severe finding is the combination of raw repository access and mutable ID/team values, which creates direct cross-tenant access and IDOR risk.

## Human Review Findings

### 1) Critical – Missing organisation scoping and authorisation
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Methods: getAllProjects, getProjectById, getProjectsByTeamId, createProject, updateProject, deleteProject
- Evidence:
  - `projectRepository.findAll()`
  - `projectRepository.findById(projectId)`
  - `projectRepository.findByTeamId(teamId)`
  - `projectRepository.save(project)`
  - `projectRepository.deleteById(projectId)`
- Finding: No operation derives organisation identity from a trusted authenticated principal or verifies membership and permission before repository access. The repository queries are not organisation-scoped.
- Impact: If these service methods are reachable through an API, a caller may read, modify, or delete another organisation's project by supplying a project or team identifier.
- Recommendation: Resolve the active organisation from trusted identity context and use organisation-scoped repository methods for every read and write. Enforce operation-specific permissions before persistence access.

### 2) High – Client-controlled identity and mass assignment
- Files: src/main/java/com/taskbridge/projects/ProjectService.java; src/main/java/com/taskbridge/projects/Project.java
- Methods: createProject, updateProject
- Evidence:
  - `return projectRepository.save(project);`
  - `project.setId(projectId);`
  - Mutable setters for `id`, `teamId`, and `status`
- Finding: The service accepts a persistence entity directly and permits caller-supplied fields to be saved. The create path does not establish a server-owned identifier, while the update path replaces the entity ID and persists the supplied object.
- Impact: A client may attempt to overwrite identity, change project ownership metadata, or alter protected state without an explicit field-level authorization decision.
- Recommendation: Use separate validated request and response DTOs, resolve the existing tenant-scoped entity, and copy only fields permitted by the operation. Generate or assign identifiers server-side.

### 3) High – Update and delete workflows lack explicit transaction and concurrency controls
- File: src/main/java/com/taskbridge/projects/ProjectService.java
- Methods: updateProject, deleteProject
- Evidence:
  - `getProjectById(projectId);`
  - `project.setId(projectId);`
  - `projectRepository.save(project);`
  - `projectRepository.deleteById(projectId);`
  - No visible `@Transactional` boundary or `@Version` field
- Finding: Read-then-write and read-then-delete workflows have no explicit service transaction or optimistic-locking mechanism in the reviewed code.
- Impact: Concurrent requests can produce stale writes or race-dependent deletion behavior. A failure in a larger multi-step workflow may also leave partial state unless an outer transaction exists.
- Recommendation: Put each atomic business workflow behind a service transaction and add an appropriate concurrency policy, such as optimistic locking, where concurrent modification is supported.

### 4) Medium – Missing validation and unbounded collection access
- Files: src/main/java/com/taskbridge/projects/Project.java; src/main/java/com/taskbridge/projects/ProjectService.java
- Methods: createProject, updateProject, getProjectById, getProjectsByTeamId, getAllProjects
- Evidence:
  - Entity fields have no visible Bean Validation constraints.
  - `return projectRepository.findAll();`
  - No explicit null, format, length, or status validation is performed before persistence or querying.
- Finding: Invalid values can reach persistence, and `getAllProjects` loads the entire project table into memory.
- Impact: Invalid business state may be stored, while large datasets can cause excessive memory use and slow responses.
- Recommendation: Validate request DTOs and service-level business rules, reject invalid identifiers, and use tenant-scoped pagination for collection queries.

### Review boundary

No controller, Spring Security configuration, database schema, or outbound workflow is present in the reviewed source tree. Therefore, this review confirms omissions in the service and entity code, but cannot establish whether an external layer adds compensating controls. The existing context-load test does not verify any of these security, transaction, validation, or concurrency behaviors.

## Issues Requiring Human Architectural Decisions

The following issues cannot be safely resolved by Copilot alone because the repository does not define the required product or security policy. They require an explicit decision before implementation.

### 1) Cross-tenant trust boundary and authorisation policy

- Files: `src/main/java/com/taskbridge/projects/ProjectService.java`; `src/main/java/com/taskbridge/projects/ProjectRepository.java`
- Evidence:
  - `projectRepository.findById(projectId)`
  - `projectRepository.findByTeamId(teamId)`
  - `projectRepository.save(project)`
  - `projectRepository.deleteById(projectId)`
- Decision required: Define how the active organisation is derived from the trusted authenticated identity, which roles may read, update, and delete projects, and whether a team member must also have organisation membership. Define whether an out-of-scope resource returns `403 Forbidden` or is deliberately concealed as `404 Not Found`.
- Why Copilot cannot safely decide: Choosing an identity claim, membership source, permission model, or response policy would establish a security boundary and could create either cross-tenant exposure or an incompatible API contract.
- Safe implementation after decision: Add organisation-scoped repository queries, service-level membership and permission checks, DTO mapping, and cross-tenant authorization tests using the approved identity source.

### 2) Domain-specific project status state machine

- File: `src/main/java/com/taskbridge/projects/Project.java`
- Methods: `getStatus`, `setStatus`, `createProject`, `updateProject`
- Evidence:
  - `private String status;`
  - `public void setStatus(String status) { this.status = status; }`
  - `projectRepository.save(project)`
- Decision required: Define the valid statuses, initial status, permitted transitions, roles allowed to perform each transition, and any required audit or notification side effects. For example, the repository does not establish whether `DRAFT -> ACTIVE` is valid, whether `ACTIVE -> COMPLETED` requires approval, or whether `COMPLETED -> DRAFT` must be rejected.
- Why Copilot cannot safely decide: Replacing the string with an enum or adding transition rules without domain approval could encode an incorrect business workflow and affect reporting, audit history, or notifications.
- Safe implementation after decision: Introduce the approved status type and transition policy, enforce it in the service transaction, and add tests for permitted and rejected transitions.

### 3) Deletion and concurrency policy

- File: `src/main/java/com/taskbridge/projects/ProjectService.java`
- Method: `deleteProject`
- Evidence:
  - `getProjectById(projectId);`
  - `projectRepository.deleteById(projectId);`
  - No visible `@Transactional` boundary or `@Version` field.
- Decision required: Establish whether deletion is hard or soft, whether audit and dependent records must be retained, and whether optimistic locking is required for concurrent updates and deletes.
- Why Copilot cannot safely decide: These choices affect retention, recovery, compliance, referential integrity, and user-visible behavior.

