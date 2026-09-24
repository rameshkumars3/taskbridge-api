## Prompt: Repository Instructions
- Execution Order: 1
- Exact Prompt:
  “Act as a senior Java, Spring Boot, application security, and testing architect.

  Draft the repository-wide `copilot-instructions.md` file for TaskBridge,

  a multi-tenant B2B SaaS assessment application.

  Include requirement grounding, Java 17/Spring Boot/Maven/JPA, layered architecture,

  DTOs, constructor injection, organisation-scoped access, trusted identity context,

  authorisation, validation, specific exceptions, centralized errors, SLF4J logging,

  sensitive-data protection, transactions, immutable audits, notification idempotency,

  testing, small reviewable changes, no invented requirements, and no unrelated edits.

  Return only Markdown. Do not modify implementation files.”
- Mode: Ask
- Techniques: Repository guidance drafting, requirement grounding, security and architecture review, documentation-only output
- Result and Corrections: Drafted repository-wide instructions. No corrections required; the content aligns with the TaskBridge product requirements and repository guidance as provided.
- Human Validation: Missing or corrected rules: none at this stage; the drafted content is aligned with the TaskBridge requirements and repository guidance as provided.
- Screenshot: N/A

## Prompt: Required Low-Effort Project Generation
- Execution Order: 2
- Exact Prompt:
  Generate a Project model and a Project service with create, update status, get by team, and delete functions. Use a database.
- Mode: Agent
- Techniques: Rapid scaffolding, domain model generation, service implementation, persistence integration
- Result and Corrections: A low-effort Project model and service were generated as a draft. Saved unchanged for later review.
- Human Validation: Saved unchanged for later review.
- Screenshot: N/A

## Prompt: Security Review of Project Model and Service
- Execution Order: 3
- Exact Prompt:
  “Act as a senior application security reviewer for a multi-tenant B2B SaaS application. Review the attached unmodified Project model and Project service for files Project.java and ProjectService.java. Check cross-organisation access, authentication, authorisation, IDOR, request-supplied identity, input validation, unsafe database access, sensitive-data exposure, transactions, and concurrency. For every confirmed issue provide file and method, category, severity, exact code evidence, SaaS impact, and recommended correction. Do not modify files and do not report speculative findings.”
- Mode: Ask
- Techniques: Security review, tenant-isolation analysis, access-control validation, evidence-based issue reporting
- Result and Corrections: Confirmed security issues were documented in REVIEW.md.
- Human Validation: Review findings were accepted as confirmed issues in the repository review record.
- Screenshot: N/A

## Prompt: Architecture Review of Project Model and Service
- Execution Order: 4
- Exact Prompt:
  “Act as a senior Java and Spring Boot architect. Review the unmodified Project model and Project service against .github/copilot-instructions.md. Check model/repository/service/controller separation, Spring Data JPA use, raw database access, DTO/entity separation, validation, transactions, specific exceptions, centralized errors, structured logging, public documentation, query efficiency, naming, type safety, and testability. For every confirmed issue provide file and method, severity, exact evidence, impact, and recommended fix. Do not modify files and do not speculate.”
- Mode: Ask
- Techniques: Architecture assessment, code quality review, requirement mapping, evidence-based remediation planning
- Result and Corrections: Confirmed architecture findings were documented in REVIEW.md.
- Human Validation: The findings were validated as architecture issues consistent with repository instructions and project standards.
- Screenshot: N/A

## Prompt: Focused Review of deleteProject
- Execution Order: 5
- Exact Prompt:
  “Act as a senior application security and Spring transaction reviewer. Review only the selected `deleteProject(Long projectId)` method in `src/main/java/com/taskbridge/projects/ProjectService.java`, using the related Project entity and ProjectRepository only as supporting context. Assess security, authentication and authorisation, IDOR, organisation or tenant isolation, input validation, transaction boundaries, concurrency, and data integrity. For every confirmed risk provide the exact code evidence, impact, severity where justified, and a grounded recommendation. Distinguish confirmed omissions from behavior that cannot be established from the repository. Do not modify files.”
- Mode: Ask
- Techniques: Focused method review, security and transaction analysis, risk classification, evidence tracking
- Result and Corrections: The focused findings were added to REVIEW.md and summarized as Response 4A in evidence.md.
- Human Validation: The review distinguishes confirmed omissions from behavior not verifiable from the repository alone.
- Screenshot: N/A

## Prompt: Human Review of Security and Spring Transaction Findings
- Execution Order: 6
- Exact Prompt:
  “Act as a senior application security and Spring transaction reviewer performing a human code review of the current TaskBridge repository. Review the existing Project entity, ProjectRepository, and ProjectService, together with the repository instructions and current tests. Identify only concrete, reviewable defects involving organisation or tenant isolation, authentication and authorisation, IDOR, client-controlled identity, mass assignment, input validation, transaction boundaries, concurrency, data integrity, and unbounded queries. For each finding provide severity, file and method, exact code evidence, impact, and a minimal recommendation. Separate confirmed omissions in the reviewed code from controls that cannot be verified because no controller or security configuration is present. Do not modify implementation files, do not invent requirements, and do not report speculative findings. Return concise Markdown suitable for REVIEW.md.”
- Mode: Ask
- Techniques: Human peer review, security validation, tenant-boundary review, concrete defect isolation
- Result and Corrections: Human review findings were added to REVIEW.md and summarized as Response 5A in evidence.md.
- Human Validation: The findings remain scoped to concrete code defects and separate controls that cannot be verified without controller or security configuration.
- Screenshot: N/A

## Prompt: Architectural Decisions Copilot Cannot Safely Infer
- Execution Order: 7
- Exact Prompt:
  “Act as a senior software architect reviewing the current TaskBridge Project model, repository, and service. Identify at least two issues that cannot be safely resolved by Copilot alone because the repository lacks an explicit product, security, or data-governance decision. Use concrete code evidence and actual examples, including cross-tenant trust boundaries and domain-specific project status transitions. For each issue, explain the decision that a security, product, or domain owner must make, why guessing would create risk, and what implementation can safely proceed after the decision. Also address deletion and concurrency policy if it is decision-dependent. Distinguish confirmed omissions from unverifiable external controls. Do not modify implementation files, invent requirements, or report speculative defects. Return concise Markdown suitable for REVIEW.md and evidence.md.”
- Mode: Ask
- Techniques: Decision-boundary analysis, product/security risk review, explicit owner signoff identification
- Result and Corrections: Identified the cross-tenant trust boundary, project status state machine, and deletion/concurrency policy as decision-dependent issues. Findings were added to REVIEW.md and summarized as Response 6A in evidence.md.
- Human Validation: The issues were captured as product or security decisions requiring explicit owner signoff rather than guessed requirements.
- Screenshot: N/A

## Prompt: Remediation Plan for Project Security, DTO, and Status Hardening
- Execution Order: 8
- Exact Prompt:
  “Act as a senior Spring Boot technical lead for TaskBridge. Use the current Project entity, repository, service, and review findings as the source of truth. Prepare a remediation plan for the Project domain covering entity hardening, status enum and transition policy, repository changes, DTO and controller separation, organisation-scoped access, trusted identity, authorisation, validation, documented transitions, transactions, domain exceptions, global error handling, structured logging, Javadoc, and unit tests. Map every confirmed review finding to a concrete action. Do not modify files. Do not invent business rules. Distinguish confirmed gaps from product/security decisions that require explicit owner signoff. Return concise Markdown suitable for REVIEW.md and evidence.md.”
- Mode: Ask
- Techniques: Remediation planning, traceability mapping, status-hardening design, requirement-grounded blueprint generation
- Result and Corrections: The remediation plan was captured in REVIEW.md and summarized as Response 7A in evidence.md.
- Human Validation: The plan maps confirmed review gaps to explicit actions while distinguishing owner-signoff items from implementation-ready fixes.
- Screenshot: N/A

## Prompt: Implement Approved Project Remediation
- Execution Order: 9
- Exact Prompt:
  “Implement the approved Project remediation plan. Refer to the Remediation Blueprint – Action Map for the Project Slice in REVIEW.md. Create or update the Project entity, ProjectStatus enum, ProjectRepository, ProjectService, request/response DTOs, ProjectController, domain exceptions, global exception handler, and ProjectService tests. Follow .github/copilot-instructions.md. Use Spring Data JPA, constructor injection, organisation-scoped repository operations, trusted organisation context, authorisation, Bean Validation, documented state transitions, specific errors, centralized handling, parameterized SLF4J, appropriate transactions, and Javadoc. Do not modify Notification or Audit code or invent requirements. Show proposed files and the complete diff before applying changes. For unresolved product or security decisions, choose and document the narrowest explicit approach that preserves tenant isolation and existing behavior.”
- Mode: Agent
- Techniques: Implementation, tenant-scoped repository design, DTO/API separation, validation, status transitions, transaction boundaries, tests, diff review
- Result and Corrections: Implemented the Project remediation slice with tenant-scoped access, trusted identity resolution, permission checks, DTO/API separation, validation, status transitions, transactions, exception handling, logging, and focused service tests. No Notification or Audit code was modified.
- Human Validation: The final implementation is aligned with the approved remediation plan and repository instructions.
- Screenshot: N/A

## Prompt: Final Project Remediation Evidence Capture
- Execution Order: 10
- Exact Prompt:
  “Act as a senior Java and Spring Boot reviewer. Re-read the approved Project remediation and repository instructions, then confirm the final status of the Project slice, including tenant isolation, trusted identity, DTO separation, validation, status transitions, transaction scope, error handling, logging, and tests. Confirm which product/security decisions were explicitly narrowed, which files changed, and which files remained untouched. Return concise Markdown suitable for evidence.md.”
- Mode: Ask
- Techniques: Final evidence review, requirement traceability, scope confirmation, implementation verification
- Result and Corrections: Final status confirmed and summarized as Response 9A in evidence.md.
- Human Validation: The final implementation is aligned with the approved remediation plan and repository instructions.
- Screenshot: N/A

## Prompt: Modify Only Selected Method
- Execution Order: 11
- Exact Prompt:
  “Modify only this selected method. Load the project using project ID and trusted organisation ID, verify documented authorisation, validate the operation or state transition, throw a specific domain exception, use parameterized logging without sensitive data, preserve the public contract, and do not modify unrelated code. Show the diff before applying it.”
- Mode: Agent
- Techniques: Narrow fix, method-level remediation, access validation, exception handling, logging discipline
- Result and Corrections: Modified the selected method and summarized the Response 10A in evidence.md.
- Human Validation: The implemented changes are aligned with the functional implementation of the selected method.
- Screenshot: N/A

## Prompt: TaskBridge Notification and Audit Specification
- Execution Order: 12
- Exact Prompt:
  “Act as a senior solution architect. Draft a 1-2 page SPEC.md for TaskBridge Notification and Audit using the supplied requirements, remediated Project Service, and .github/copilot-instructions.md. Include scope/non-goals, actors and authorization, AuditLog and Notification fields with exact Java types, API request/response contracts for POST /audit, GET /audit/{projectId}, GET /notifications/{userId}, PATCH /notifications/{id}/read, from/to/eventType filters, Project create/update-status/delete integration, audit immutability, organisation isolation, validation, error responses, transactions, assumptions, Copilot contribution, and human corrections. Do not implement code. Exclude MILESTONE_REOPENED and actor IP. Return only Markdown.”
- Mode: Ask
- Techniques: Solution specification drafting, API contract authoring, requirement abstraction, architecture alignment
- Result and Corrections: Drafted the TaskBridge Notification and Audit specification in SPEC.md.
- Human Validation: The specification is aligned with the Project remediation, tenant-safe architecture, and repository instructions; no implementation files were changed.
- Screenshot: N/A

## Prompt: Notification and Audit Solution Design (No Code Changes)
- Execution Order: 13
- Exact Prompt:
  “Act as a senior Java and Spring Boot solution architect. Using SPEC.md, .github/copilot-instructions.md, and the remediated Project feature, design Notification and Audit without modifying files. Provide entities, relationships, DTOs, repository methods, service responsibilities, organisation-isolation and authorization rules, event triggers, transaction boundaries, duplicate-notification control, error scenarios, and at least eight test scenarios. Do not invent business rules; list assumptions separately. Return concise but complete Markdown suitable for design documentation.”
- Mode: Ask
- Techniques: Design documentation, entity and repository design, test scenario planning, assumption tracking
- Result and Corrections: Prepared a requirement-grounded Notification and Audit design aligned with the Project remediation, tenant-safe service model, and specification constraints; no implementation files were modified.
- Human Validation: The design remains within the product scope and avoids speculative business rules or file changes.
- Screenshot: N/A

## Prompt: Persistence Layer Only for Notification and Audit
- Execution Order: 14
- Exact Prompt:
  “Implement only the persistence layer from SPEC.md. Create AuditLog, Notification, required enums, AuditLogRepository, and NotificationRepository. Use Spring Data JPA, organisation scope, Instant timestamps, safe previous/new snapshots, immutable audit records, tenant-scoped repository methods, and appropriate indexes/constraints. Do not create services or controllers, modify unrelated Project files, add MILESTONE_REOPENED, or add actor IP. Show the diff first.”
- Mode: Agent
- Techniques: Persistence modeling, JPA entity design, tenant-scoped repository methods, diff-reviewed implementation
- Result and Corrections: Added the Notification and Audit persistence model and repositories, scoped to the active organisation and aligned with the TaskBridge specification; no service or controller code was created and no unrelated Project files were altered.
- Human Validation: The persistence layer change remains narrow, requirement-grounded, and compliant with the documented design and retention of tenant isolation.

## Prompt: Implement Notification and Audit Services
- Execution Order: 15
- Exact Prompt:
  “Implement AuditService and NotificationService according to SPEC.md and integrate them with Project create, status update, and delete. Capture actor user ID and organisation, previous and new state snapshots, and server timestamp. Create equal notifications for every relevant team member. Support project audit history with optional from, to, and eventType filters. Return unread notifications for an authorised user. Allow only the authorised recipient to mark a notification as read. Enforce organisation-scoped access, audit immutability, specific errors, transactions, safe logging, and idempotency where required. Add service tests. Do not add MILESTONE_REOPENED or actor IP. Show the diff.”
- Mode: Agent
- Techniques: Transactional service implementation, lifecycle event integration, trusted identity resolution, tenant isolation, notification fan-out, idempotency, API contract implementation, focused service testing, diff review
- Result and Corrections: Added AuditService and NotificationService, audit and notification response/request models, controllers, lifecycle event emission from ProjectService, trusted actor user ID resolution, organisation-scoped queries, recipient authorization, read-state enforcement, immutable audit persistence, server timestamps, deduplication, and focused service tests. The implementation excludes MILESTONE_REOPENED and actor IP fields. Team recipient resolution is isolated behind TeamMemberDirectory so deployments can provide their authoritative team-membership source.
- Human Validation: Maven tests passed with 10 successful tests, including the Spring application context. The implementation preserves the existing project API and transaction boundaries; pre-existing changes in PROMPTS.md and SPEC.md were retained.
- Screenshot: N/A

## Prompt: Implement Notification and Audit API Layer
- Execution Order: 16
- Exact Prompt:
  "Implement only the API layer from SPEC.md: POST /audit, GET /audit/{projectId}?from=&to=&eventType=, GET /notifications/{userId}, and PATCH /notifications/{id}/read. Use request/response DTOs, Jakarta Bean Validation, trusted organisation and user context, resource and recipient ownership checks, centralized error handling, and appropriate HTTP status codes. Do not expose JPA entities, trust request-supplied organisation ID, change service business rules, or add scope-change fields. Add controller tests and show the diff."
- Mode: Agent
- Techniques: Spring MVC API implementation, DTO validation, trusted tenant context, ownership enforcement, centralized error mapping, MockMvc controller testing, diff review
- Result and Corrections: Completed the audit and notification API contracts with validated request and path parameters, DTO-only responses, tenant and recipient checks delegated through the existing service layer, centralized handling for validation and type-mismatch failures, and appropriate 201, 200, 400, and 403 responses. Added an additive read-only notification query for the specified all-notifications endpoint while preserving the existing unread method and business rules. No request-supplied organisation or actor identity is trusted, and no scope-change fields were added.
- Human Validation: Focused controller tests passed with 7 successful tests. The full Maven suite passed with 16 successful tests, and workspace diagnostics reported no errors in the changed files. Existing unrelated working-tree changes were retained.
- Screenshot: N/A

## Prompt: Expand Notification and Audit Test Coverage
- Execution Order: 17
- Exact Prompt:
  “Generate or improve JUnit 5 and Mockito tests for Notification and Audit. Include equal notification dispatch to all relevant team members, correct audit after milestone update, audit cannot be deleted or overwritten, date-range filtering, event-type filtering, cross-organisation audit access rejection, unread notification retrieval, authorised mark-as-read, and unauthorised mark-as-read rejection. Use Arrange-Act-Assert, verify observable behavior, and do not weaken production code.”
- Mode: Agent
- Techniques: Focused service testing, Mockito interaction capture, tenant-isolation verification, audit immutability testing, notification authorization testing, lifecycle integration testing
- Result and Corrections: Expanded the existing service tests to cover equal notification fan-out, lifecycle status-change audit emission, append-only audit behavior, combined date-range and event-type filtering, cross-organisation audit rejection, unread notification retrieval, and authorized or unauthorized notification read transitions. Tests assert observable results and repository/service boundaries without changing production code.
- Human Validation: The focused Notification, Audit, and Project service tests passed with 18 successful tests. The complete available test suite passed with 28 successful tests and no failures.
- Screenshot: N/A

## Prompt: Impact Analysis for MILESTONE_REOPENED and Actor IP Audit Capture
- Execution Order: 18
- Exact Prompt:
  “Act as a senior software architect and privacy-aware security reviewer. Analyze this change without modifying code: add MILESTONE_REOPENED; it triggers audit logging and notifications; audit entries also capture the actor IP address. Identify every affected file/module/model/enum/API/repository/service/controller/test/document, classify each change as additive, breaking, or migration, document database migration, backward compatibility, tenant authorization, privacy, retention, masking, export, logging exposure, IP trust, implementation order, required tests, rollback, assumptions, and how Copilot assisted. Do not modify files.”
- Mode: Ask
- Techniques: Impact analysis, architecture review, security and privacy review, tenant-boundary assessment, migration planning, documentation-only analysis
- Result and Corrections: Documented the full impact assessment in IMPACT_ANALYSIS.md, covering event taxonomy, persistence, API contracts, service flow, security boundaries, DB migration, privacy and retention, implementation sequencing, back-compat risk, rollback, and assumptions. No implementation files were changed.
- Human Validation: The impact analysis is grounded in the current repository design and current implementation, and it distinguishes feature scope, data privacy, and migration impacts without inventing unsupported requirements.
- Screenshot: N/A

## Prompt: Implement Approved MILESTONE_REOPENED and Actor IP Audit Capture
- Execution Order: 19
- Exact Prompt:
  “Implement the approved changes from IMPACT_ANALYSIS.md. Add MILESTONE_REOPENED, corresponding audit and team notifications, actor IP on audit records, compatible persistence or migration handling, validation, authorization, and tests. Obtain IP from trusted server request context; do not blindly trust forwarding headers; do not log IP in ordinary logs; preserve older record compatibility; do not modify unrelated behavior. Show the diff before applying changes.”
- Mode: Agent
- Techniques: Domain transition implementation, tenant-scoped audit and notification integration, trusted request metadata capture, nullable persistence compatibility, privacy-preserving API design, validation and authorization testing, diff review
- Result and Corrections: Added the `COMPLETED -> ACTIVE` milestone reopen transition and `MILESTONE_REOPENED` event. Reopen actions use the existing tenant-authorized audit and team-notification fan-out with deduplication. Audit records capture only `HttpServletRequest.getRemoteAddr()`, ignore forwarding headers and client fields, store the nullable value without exposing it in API responses, and preserve older constructor and null-column compatibility. Hibernate schema updates handle the nullable audit column for existing databases. Updated the specification and evidence documentation without changing unrelated runtime behavior.
- Human Validation: The proposed diff was shown before application. The complete focused test suite passed with 33 successful tests, production diagnostics reported no errors, and `git diff --check` passed.
- Screenshot: N/A
