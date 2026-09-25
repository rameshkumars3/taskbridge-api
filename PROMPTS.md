## Prompt: Create the assessment workspace
- Execution Order: 1
- Exact Prompt:
  Create the workspace folders as below.
  New-Item -ItemType Directory -Force TaskBridge-Assessment 
  Set-Location TaskBridge-Assessment 
  New-Item -ItemType Directory -Force taskbridge-api 
  New-Item -ItemType Directory -Force screenshots 
  New-Item -ItemType Directory -Force evidence 
  New-Item -ItemType Directory -Force submission 
  Set-Location taskbridge-api 
  code . 
- Mode: Agent
- Techniques: Project Scaffolding, Environment Setup, Task Automation
- Result and Corrections: Created the TaskBridge-Assessment workspace structure. Created folders: taskbridge-api,screenshots,evidence and submission.Opened taskbridge-api in VS Code for project development.
- Human Validation: Verified folder structure manually using VS Code Explorer and PowerShell. Confirmed: TaskBridge-Assessment, taskbridge-api, screenshots, evidence and submission folders exists.VS Code opened in taskbridge-api root.
- Screenshot: 01_Create the assessment workspace.png

## Prompt: Verify tools, Copilot account, modes, and commands
- Execution Order: 2
- Exact Prompt:
  Verify the version of technology stack as below.
  java -version
  mvn -version
  git --version
  code --version
- Mode: Agent
- Techniques: Environment Setup, Verification, Command Execution
- Result and Corrections: Validated local development environment.
Verified: Java 17 installed,Maven available in PATH,Git available in PATH, VS Code installed and GitHub Copilot authenticated.No code files were created or modified.
- Human Validation: Reviewed command output manually.Confirmed: Java version is 17,Maven command executes successfully,Git command executes successfully,VS Code command executes successfully and Copilot account/license information is visible and No corrective action required.
- Screenshot: 02_A_Verify tools, Copilot account, modes, and commands.png, 02_B_Verify tools, Copilot account, modes, and commands.png

## Prompt: Create the Spring Boot Maven project
- Execution Order: 3
- Exact Prompt:
  Create the Spring Boot Maven project : Spring Initializr: Create a Maven Project, select Java, approved Spring Boot version, group com.taskbridge, artifact taskbridge-api, Jar, Java 17, then add Spring Web, Spring Data JPA, Validation, H2 Database, and Spring Boot Test
- Mode: Agent
- Techniques: Project Scaffolding,Technology-Specific Generation,Environment Configuration,Structured Project Setup,Framework Initialization
- Result and Corrections: Generated a Spring Boot Maven project named taskbridge-api.
  Created:
    pom.xml
    src/main/java
    src/main/resources
    src/test/java
    Spring Boot application class

  Configured dependencies:
    spring-boot-starter-web
    spring-boot-starter-data-jpa
    spring-boot-starter-validation
    h2 database
    spring-boot-starter-test
  Correction performed:
    Verified Java version is set to 17.
    Verified all required dependencies are present in pom.xml.
- Human Validation: Reviewed generated project structure in VS Code.
  Confirmed:
    pom.xml exists
    Application main class exists
    src/main/java exists
    src/test/java exists
    Maven project imports successfully
    Java version is 17
    Required dependencies are present

  Executed:
    mvn clean test
    Build completed successfully.

  No additional dependencies were added beyond assessment requirements.
- Screenshot: 03_Create the Spring Boot Maven project.png

## Prompt: Create required packages and Markdown files
- Execution Order: 4
- Exact Prompt:
  `/setup` Create required package structure and documentation files for the TaskBridge assessment project.
  .github/copilot-instructions.md
  README.md
  SPEC.md
  REVIEW.md
  IMPACT_ANALYSIS.md
  PROMPTS.md
  PR_DESCRIPTION.md
  TOOL_STRATEGY.md
  ARCHITECTURE.md
  $dirs = @(
  '.github','docs',
  'src/main/java/com/taskbridge/projects/model',
  'src/main/java/com/taskbridge/projects/repository',
  'src/main/java/com/taskbridge/projects/service',
  'src/main/java/com/taskbridge/projects/controller',
  'src/main/java/com/taskbridge/projects/dto',
  'src/main/java/com/taskbridge/notifications/model',
  'src/main/java/com/taskbridge/notifications/repository',
  'src/main/java/com/taskbridge/notifications/service',
  'src/main/java/com/taskbridge/notifications/controller',
  'src/main/java/com/taskbridge/notifications/dto',
  'src/main/java/com/taskbridge/common/exception',
  'src/main/java/com/taskbridge/common/security',
  'src/test/java/com/taskbridge'
  )
  $dirs | ForEach-Object { New-Item -ItemType Directory -Force -Path $_ }
  $files = @(
  '.github/copilot-instructions.md','README.md','SPEC.md','REVIEW.md',
  'IMPACT_ANALYSIS.md','PROMPTS.md','PR_DESCRIPTION.md','TOOL_STRATEGY.md','ARCHITECTURE.md'
  )
  $files | ForEach-Object { New-Item -ItemType File -Force -Path $_ }
- Mode: Agent
- Techniques: Project Scaffolding, Structured Generation, Context-Aware Repository Setup, File System Automation
- Result and Corrections: Generated Assessment Repository Structure. Created: All mandatory package folders, Github directory, Documentation files,Test directory structure,No business implementation code generated.Folder structure matched assessment guidance. Minor correction: Verified file names match exact assessment deliverable names.
- Human Validation: Reviewed generated structure in VS Code Explorer. Confirmed:All required packages exist,All required markdown files exist, Folder naming conventions are correct,No unnecessary folders created,No implementation classes generated accidentally and Folder structure aligns with assessment requirements.
- Screenshot: 04_Create required packages and Markdown files.png

## Prompt: Populate default repository instructions
- Execution Order: 5
- Exact Prompt:
  `/setup` Act as a senior Java, Spring Boot, application security, and testing architect. Draft the repository-wide `copilot-instructions.md` file for TaskBridge, a multi-tenant B2B SaaS assessment application.Include requirement grounding, Java 17/Spring Boot/Maven/JPA, layered architecture, DTOs, constructor injection, organisation-scoped access, trusted identity context, authorisation, validation, specific exceptions, centralized errors, SLF4J logging, sensitive-data protection, transactions, immutable audits, notification idempotency, testing, small reviewable changes, no invented requirements, and no unrelated edits. Return only Markdown. Do not modify implementation files.
- Mode: Ask
- Techniques: Repository guidance drafting, requirement grounding, security and architecture review, documentation-only output
- Result and Corrections: Drafted repository-wide instructions. No corrections required; the content aligns with the TaskBridge product requirements and repository guidance as provided.
- Human Validation: Missing or corrected rules: none at this stage; the drafted content is aligned with the TaskBridge requirements and repository guidance as provided.
- Screenshot: 05_A_Populate default repository instructions-Prompt.png, 05_B_Populate default repository instructions-Final.png, 05_C_Save prompt in Prompt file.png

## Prompt: Generate the inherited weak Project code
- Execution Order: 6
- Exact Prompt:
  `/setup` Generate a Project model and a Project service with create, update status, get by team, and delete functions. Use a database.
- Mode: Ask
- Techniques: Rapid scaffolding, domain model generation, service implementation, persistence integration
- Result and Corrections: A low-effort Project model and service were generated as a draft. Saved unchanged for later review.
- Human Validation: Saved unchanged for later review.
- Screenshot: 07_A_Generate the inherited weak Project code-Prompt.png, 07_B_Generate the inherited weak Project code-Final.png, 07_C_Generate the inherited weak Project code-Result.png, 07_D_Save prompt in Prompt file.png

## Prompt: Security Review of Project Model and Service
- Execution Order: 7
- Exact Prompt:
  `/ask` Act as a senior application security reviewer for a multi-tenant B2B SaaS application. Review the attached unmodified Project model and Project service for files Project.java and ProjectService.java. Check cross-organisation access, authentication, authorisation, IDOR, request-supplied identity, input validation, unsafe database access, sensitive-data exposure, transactions, and concurrency. For every confirmed issue provide file and method, category, severity, exact code evidence, SaaS impact, and recommended correction. Do not modify files and do not report speculative findings.
- Mode: Ask
- Techniques: Security review, tenant-isolation analysis, access-control validation, evidence-based issue reporting
- Result and Corrections: Confirmed security issues were documented in REVIEW.md.
- Human Validation: Review findings were accepted as confirmed issues in the repository review record.
- Screenshot: 08_A_Security Review of Project Model and Service-Prompt.png, 08_B_Security Review of Project Model and Service-Final.png

## Prompt: Architecture Review of Project Model and Service
- Execution Order: 8
- Exact Prompt:
  `/ask` Act as a senior Java and Spring Boot architect. Review the unmodified Project model and Project service against .github/copilot-instructions.md. Check model/repository/service/controller separation, Spring Data JPA use, raw database access, DTO/entity separation, validation, transactions, specific exceptions, centralized errors, structured logging, public documentation, query efficiency, naming, type safety, and testability. For every confirmed issue provide file and method, severity, exact evidence, impact, and recommended fix. Do not modify files and do not speculate.
- Mode: Ask
- Techniques: Architecture assessment, code quality review, requirement mapping, evidence-based remediation planning
- Result and Corrections: Confirmed architecture findings were documented in REVIEW.md.
- Human Validation: The findings were validated as architecture issues consistent with repository instructions and project standards.
- Screenshot: 09_A_Architecture Review of Project Model and Service-Prompt.png, 09_B_Architecture Review of Project Model and Service-Final-1.png, 09_C_Architecture Review of Project Model and Service-2.png

## Prompt: Focused Review of deleteProject
- Execution Order: 9
- Exact Prompt:
  `/ask` Explain the security, organisation-isolation, transaction, validation, and data-integrity risks in this selected method. Cite the exact code. Do not modify the method. 
- Mode: Inline
- Techniques: Focused method review, security and transaction analysis, risk classification, evidence tracking
- Result and Corrections: The focused findings were added to REVIEW.md and summarized as Response 4A in evidence.md.
- Human Validation: The review distinguishes confirmed omissions from behavior not verifiable from the repository alone.
- Screenshot: 10_A_Focused Review of deleteProject-Prompt.png, 10_B_Focused Review of deleteProject-Final.png

## Prompt: Architectural Decisions Copilot Cannot Safely Infer
- Execution Order: 10
- Exact Prompt:
  `/ask` Act as a senior software architect reviewing the current TaskBridge Project model, repository, and service. Identify at least two issues that cannot be safely resolved by Copilot alone because the repository lacks an explicit product, security, or data-governance decision. Use concrete code evidence and actual examples, including cross-tenant trust boundaries and domain-specific project status transitions. For each issue, explain the decision that a security, product, or domain owner must make, why guessing would create risk, and what implementation can safely proceed after the decision. Also address deletion and concurrency policy if it is decision-dependent. Distinguish confirmed omissions from unverifiable external controls. Do not modify implementation files, invent requirements, or report speculative defects. Return concise Markdown suitable for REVIEW.md and evidence.md.
- Mode: Agent
- Techniques: Decision-boundary analysis, product/security risk review, explicit owner signoff identification
- Result and Corrections: Identified the cross-tenant trust boundary, project status state machine, and deletion/concurrency policy as decision-dependent issues. Findings were added to REVIEW.md and summarized as Response 6A in evidence.md.
- Human Validation: The issues were captured as product or security decisions requiring explicit owner signoff rather than guessed requirements.
- Screenshot: 12_Architectural Decisions Copilot Cannot Safely Infer.png

## Prompt: Remediation Plan for Project Security, DTO, and Status Hardening
- Execution Order: 11
- Exact Prompt:
  `/plan` Act as a senior Spring Boot technical lead. Using #file:REVIEW.md , #sym:# TaskBridge Copilot Instructions , and the inherited Project files, prepare a remediation plan for Project entity, status enum, repository, service, controller, request/response DTOs, organisation-scoped access, trusted identity, authorisation, validation, documented transitions, transactions, domain exceptions, global error handling, structured logging, Javadoc, and unit tests. Map every REVIEW.md finding to an action. Do not modify files. Do not invent business rules.
- Mode: Plan
- Techniques: Remediation planning, traceability mapping, status-hardening design, requirement-grounded blueprint generation
- Result and Corrections: The remediation plan was captured in REVIEW.md and summarized as Response 7A in evidence.md.
- Human Validation: The plan maps confirmed review gaps to explicit actions while distinguishing owner-signoff items from implementation-ready fixes.
- Screenshot: 14_A_Remediation Plan for Project Security, DTO, and Status Hardening-Prompt.png, 14_B_Remediation Plan for Project Security, DTO, and Status Hardening-Final.png

## Prompt: Implement Approved Project Remediation
- Execution Order: 12
- Exact Prompt:
  `/fix` Implement the approved Project remediation plan. Refer to the Remediation Blueprint – Action Map for the Project Slice in REVIEW.md. Create or update the Project entity, ProjectStatus enum, ProjectRepository, ProjectService, request/response DTOs, ProjectController, domain exceptions, global exception handler, and ProjectService tests. Follow .github/copilot-instructions.md. Use Spring Data JPA, constructor injection, organisation-scoped repository operations, trusted organisation context, authorisation, Bean Validation, documented state transitions, specific errors, centralized handling, parameterized SLF4J, appropriate transactions, and Javadoc. Do not modify Notification or Audit code or invent requirements. Show proposed files and the complete diff before applying changes. For unresolved product or security decisions, choose and document the narrowest explicit approach that preserves tenant isolation and existing behavior.
- Mode: Agent
- Techniques: Implementation, tenant-scoped repository design, DTO/API separation, validation, status transitions, transaction boundaries, tests, diff review
- Result and Corrections: Implemented the Project remediation slice with tenant-scoped access, trusted identity resolution, permission checks, DTO/API separation, validation, status transitions, transactions, exception handling, logging, and focused service tests. No Notification or Audit code was modified.
- Human Validation: The final implementation is aligned with the approved remediation plan and repository instructions.
- Screenshot: 15_A_Implement Approved Project Remediation-Prompt.png, 15_B_Implement Approved Project Remediation-Final.png

## Prompt: Modify Only Selected Method
- Execution Order: 13
- Exact Prompt:
  `/fix` Modify only this selected method. Load the project using project ID and trusted organisation ID, verify documented authorisation, validate the operation or state transition, throw a specific domain exception, use parameterized logging without sensitive data, preserve the public contract, and do not modify unrelated code. Show the diff before applying it.
- Mode: Inline
- Techniques: Narrow fix, method-level remediation, access validation, exception handling, logging discipline
- Result and Corrections: Modified the selected method and summarized the Response 10A in evidence.md.
- Human Validation: The implemented changes are aligned with the functional implementation of the selected method.
- Screenshot: 16_A_Modify only this selected method by Inline-Prompt.png, 16_B_Modify only this selected method by Inline-Final.png, 16_C_Modify only this selected method by Inline-Test Setup.png

## Prompt: TaskBridge Notification and Audit Specification
- Execution Order: 14
- Exact Prompt:
  `/plan` Act as a senior solution architect. Draft a 1-2 page SPEC.md for TaskBridge Notification and Audit using the supplied requirements, remediated Project Service, and .github/copilot-instructions.md. Include scope/non-goals, actors and authorization, AuditLog and Notification fields with exact Java types, API request/response contracts for POST /audit, GET /audit/{projectId}, GET /notifications/{userId}, PATCH /notifications/{id}/read, from/to/eventType filters, Project create/update-status/delete integration, audit immutability, organisation isolation, validation, error responses, transactions, assumptions, Copilot contribution, and human corrections. Do not implement code. Exclude MILESTONE_REOPENED and actor IP. Return only Markdown.
- Mode: Agent
- Techniques: Solution specification drafting, API contract authoring, requirement abstraction, architecture alignment
- Result and Corrections: Drafted the TaskBridge Notification and Audit specification in SPEC.md.
- Human Validation: The specification is aligned with the Project remediation, tenant-safe architecture, and repository instructions; no implementation files were changed.
- Screenshot: 17_A_TaskBridge Notification and Audit Specification-Prompt.png, 17_B_TaskBridge Notification and Audit Specification-Final.png

## Prompt: Notification and Audit Solution Design (No Code Changes)
- Execution Order: 15
- Exact Prompt:
  `/plan` Act as a senior Java and Spring Boot solution architect. Using SPEC.md, .github/copilot-instructions.md, and the remediated Project feature, design Notification and Audit without modifying files. Provide entities, relationships, DTOs, repository methods, service responsibilities, organisation-isolation and authorization rules, event triggers, transaction boundaries, duplicate-notification control, error scenarios, and at least eight test scenarios. Do not invent business rules; list assumptions separately. Return concise but complete Markdown suitable for design documentation.
- Mode: Ask
- Techniques: Design documentation, entity and repository design, test scenario planning, assumption tracking
- Result and Corrections: Prepared a requirement-grounded Notification and Audit design aligned with the Project remediation, tenant-safe service model, and specification constraints; no implementation files were modified.
- Human Validation: The design remains within the product scope and avoids speculative business rules or file changes.
- Screenshot: 18_A_Notification and Audit Solution Design-Prompt.png, 18_B_Notification and Audit Solution Design-Final.png

## Prompt: Persistence Layer Only for Notification and Audit
- Execution Order: 16
- Exact Prompt:
  `/fix` Implement only the persistence layer from SPEC.md. Create AuditLog, Notification, required enums, AuditLogRepository, and NotificationRepository. Use Spring Data JPA, organisation scope, Instant timestamps, safe previous/new snapshots, immutable audit records, tenant-scoped repository methods, and appropriate indexes/constraints. Do not create services or controllers, modify unrelated Project files, add MILESTONE_REOPENED, or add actor IP. Show the diff first.
- Mode: Agent
- Techniques: Persistence modeling, JPA entity design, tenant-scoped repository methods, diff-reviewed implementation
- Result and Corrections: Added the Notification and Audit persistence model and repositories, scoped to the active organisation and aligned with the TaskBridge specification; no service or controller code was created and no unrelated Project files were altered.
- Human Validation: The persistence layer change remains narrow, requirement-grounded, and compliant with the documented design and retention of tenant isolation.
- Screenshot: 19_A_Persistence Layer Only for Notification and Audit-Prompt.png, 19_B_Persistence Layer Only for Notification and Audit-Final.png

## Prompt: Implement Notification and Audit Services
- Execution Order: 17
- Exact Prompt:
  `/fix` Implement AuditService and NotificationService according to SPEC.md and integrate them with Project create, status update, and delete. Capture actor user ID and organisation, previous and new state snapshots, and server timestamp. Create equal notifications for every relevant team member. Support project audit history with optional from, to, and eventType filters. Return unread notifications for an authorised user. Allow only the authorised recipient to mark a notification as read. Enforce organisation-scoped access, audit immutability, specific errors, transactions, safe logging, and idempotency where required. Add service tests. Do not add MILESTONE_REOPENED or actor IP. Show the diff.
- Mode: Agent
- Techniques: Transactional service implementation, lifecycle event integration, trusted identity resolution, tenant isolation, notification fan-out, idempotency, API contract implementation, focused service testing, diff review
- Result and Corrections: Added AuditService and NotificationService, audit and notification response/request models, controllers, lifecycle event emission from ProjectService, trusted actor user ID resolution, organisation-scoped queries, recipient authorization, read-state enforcement, immutable audit persistence, server timestamps, deduplication, and focused service tests. The implementation excludes MILESTONE_REOPENED and actor IP fields. Team recipient resolution is isolated behind TeamMemberDirectory so deployments can provide their authoritative team-membership source.
- Human Validation: Maven tests passed with 10 successful tests, including the Spring application context. The implementation preserves the existing project API and transaction boundaries; pre-existing changes in PROMPTS.md and SPEC.md were retained.
- Screenshot: 20_A_Implement Notification and Audit Services-Prompt.png, 20_B_Implement Notification and Audit Services-Final.png

## Prompt: Implement Notification and Audit API Layer
- Execution Order: 18
- Exact Prompt:
  `/fix` Implement only the API layer from SPEC.md: POST /audit, GET /audit/{projectId}?from=&to=&eventType=, GET /notifications/{userId}, and PATCH /notifications/{id}/read. Use request/response DTOs, Jakarta Bean Validation, trusted organisation and user context, resource and recipient ownership checks, centralized error handling, and appropriate HTTP status codes. Do not expose JPA entities, trust request-supplied organisation ID, change service business rules, or add scope-change fields. Add controller tests and show the diff.
- Mode: Agent
- Techniques: Spring MVC API implementation, DTO validation, trusted tenant context, ownership enforcement, centralized error mapping, MockMvc controller testing, diff review
- Result and Corrections: Completed the audit and notification API contracts with validated request and path parameters, DTO-only responses, tenant and recipient checks delegated through the existing service layer, centralized handling for validation and type-mismatch failures, and appropriate 201, 200, 400, and 403 responses. Added an additive read-only notification query for the specified all-notifications endpoint while preserving the existing unread method and business rules. No request-supplied organisation or actor identity is trusted, and no scope-change fields were added.
- Human Validation: Focused controller tests passed with 7 successful tests. The full Maven suite passed with 16 successful tests, and workspace diagnostics reported no errors in the changed files. Existing unrelated working-tree changes were retained.
- Screenshot: 21_A_Implement Notification and Audit API Layer-Prompt.png, 21_B_Implement Notification and Audit API Layer-Final.png

## Prompt: Expand Notification and Audit Test Coverage
- Execution Order: 19
- Exact Prompt:
  `/tests` Generate or improve JUnit 5 and Mockito tests for Notification and Audit. Include equal notification dispatch to all relevant team members, correct audit after milestone update, audit cannot be deleted or overwritten, date-range filtering, event-type filtering, cross-organisation audit access rejection, unread notification retrieval, authorised mark-as-read, and unauthorised mark-as-read rejection. Use Arrange-Act-Assert, verify observable behavior, and do not weaken production code.
- Mode: Agent
- Techniques: Focused service testing, Mockito interaction capture, tenant-isolation verification, audit immutability testing, notification authorization testing, lifecycle integration testing
- Result and Corrections: Expanded the existing service tests to cover equal notification fan-out, lifecycle status-change audit emission, append-only audit behavior, combined date-range and event-type filtering, cross-organisation audit rejection, unread notification retrieval, and authorized or unauthorized notification read transitions. Tests assert observable results and repository/service boundaries without changing production code.
- Human Validation: The focused Notification, Audit, and Project service tests passed with 18 successful tests. The complete available test suite passed with 28 successful tests and no failures.
- Screenshot: 22_A_Expand Notification and Audit Test Coverage-Prompt.png, 22_B_Expand Notification and Audit Test Coverage-Final.png

## Prompt: Impact Analysis for MILESTONE_REOPENED and Actor IP Audit Capture
- Execution Order: 20
- Exact Prompt:
  `/ask` Act as a senior software architect and privacy-aware security reviewer. Analyze this change without modifying code: add MILESTONE_REOPENED; it triggers audit logging and notifications; audit entries also capture the actor IP address. Identify every affected file/module/model/enum/API/repository/service/controller/test/document, classify each change as additive, breaking, or migration, document database migration, backward compatibility, tenant authorization, privacy, retention, masking, export, logging exposure, IP trust, implementation order, required tests, rollback, assumptions, and how Copilot assisted. Do not modify files.
- Mode: Agent
- Techniques: Impact analysis, architecture review, security and privacy review, tenant-boundary assessment, migration planning, documentation-only analysis
- Result and Corrections: Documented the full impact assessment in IMPACT_ANALYSIS.md, covering event taxonomy, persistence, API contracts, service flow, security boundaries, DB migration, privacy and retention, implementation sequencing, back-compat risk, rollback, and assumptions. No implementation files were changed.
- Human Validation: The impact analysis is grounded in the current repository design and current implementation, and it distinguishes feature scope, data privacy, and migration impacts without inventing unsupported requirements.
- Screenshot: 23_A_Impact Analysis for MILESTONE_REOPENED and Actor IP Audit Capture-Prompt.png, 23_B_Impact Analysis for MILESTONE_REOPENED and Actor IP Audit Capture-Final.png

## Prompt: Implement Approved MILESTONE_REOPENED and Actor IP Audit Capture
- Execution Order: 21
- Exact Prompt:
  `/fix` Implement the approved changes from IMPACT_ANALYSIS.md. Add MILESTONE_REOPENED, corresponding audit and team notifications, actor IP on audit records, compatible persistence or migration handling, validation, authorization, and tests. Obtain IP from trusted server request context; do not blindly trust forwarding headers; do not log IP in ordinary logs; preserve older record compatibility; do not modify unrelated behavior. Show the diff before applying changes.
- Mode: Agent
- Techniques: Domain transition implementation, tenant-scoped audit and notification integration, trusted request metadata capture, nullable persistence compatibility, privacy-preserving API design, validation and authorization testing, diff review
- Result and Corrections: Added the `COMPLETED -> ACTIVE` milestone reopen transition and `MILESTONE_REOPENED` event. Reopen actions use the existing tenant-authorized audit and team-notification fan-out with deduplication. Audit records capture only `HttpServletRequest.getRemoteAddr()`, ignore forwarding headers and client fields, store the nullable value without exposing it in API responses, and preserve older constructor and null-column compatibility. Hibernate schema updates handle the nullable audit column for existing databases. Updated the specification and evidence documentation without changing unrelated runtime behavior.
- Human Validation: The proposed diff was shown before application. The complete focused test suite passed with 33 successful tests, production diagnostics reported no errors, and `git diff --check` passed.
- Screenshot: 24_A_Implement Approved MILESTONE_REOPENED and Actor IP Audit Capture-Prompt.png, 24_B_Implement Approved MILESTONE_REOPENED and Actor IP Audit Capture-Final.png

## Prompt: Draft PR Description from Current Repo Evidence
- Execution Order: 22
- Exact Prompt:
  `/ask` Draft #file:PR_DESCRIPTION.md using only the current repository evidence. Include summary, why the change was needed, architecture, Project remediation, Notification/Audit implementation, integration contract, security and tenant isolation, AI Tool Disclosure, most-used mode, accepted and overridden AI output, reasonable AI-generated/manual percentages, test coverage, known gaps, one genuine risk/trade-off, self-review checklist, and three specific actionable peer-review comments including one AI blind spot. Do not invent work or features.
- Mode: Agent
- Techniques: Requirement-grounded PR drafting, evidence-based summary, security and architecture traceability, evidence-only documentation
- Result and Corrections: Drafted the PR description in PR_DESCRIPTION.md using only the existing repository evidence, code, and recorded review artifacts; it stays within the implemented scope and avoids undocumented work or speculative features.
- Human Validation: The draft is grounded in the repository’s actual implementation, tests, and documented review findings, and it explicitly calls out AI usage, limits, gaps, and review comments without inventing product behavior.
- Screenshot: 25_A_Draft PR Description from Current Repo Evidence-Prompt.png, 25_B_Draft PR Description from Current Repo Evidence-Final.png

## Prompt: Draft Copilot Tool Strategy
- Execution Order: 23
- Exact Prompt:
  `/ask` Draft TOOL_STRATEGY.md using only actual activity in PROMPTS.md and the repository. Include at least six Copilot usage entries covering at least four features. For each entry, state task, reason, outcome, and human verification. Answer the six required scenarios: 600-line legacy service; validation across ten handlers; JWT expiry/tampering; lint and coverage enforcement; contractor service review; consistent tenant isolation. Document three genuine limitations with prompt/activity, problem, detection, correction, and improved future approach. Do not claim unavailable features or zero limitations.
- Mode: Agent
- Techniques: Evidence-based tool-strategy drafting, activity traceability,scenario analysis, limitation tracking, documentation-only editing
- Result and Corrections: Drafted TOOL_STRATEGY.md from PROMPTS.md and the repository. The document records seven Copilot usage entries, all six required scenarios, and three genuine limitations. It explicitly identifies the absence of JWT implementation evidence, ten-handler coverage, and lint or coverage enforcement rather than claiming those features.
- Human Validation: Required-content and formatting checks passed. The draft was reviewed against the recorded prompts, repository build configuration, implementation files, tests, and existing evidence documents.
- Screenshot: 26_A_Draft Copilot Tool Strategy-Prompt.png, 26_B_Draft Copilot Tool Strategy-Final.png

## Prompt: Draft Architecture and README from Final Implementation
- Execution Order: 24
- Exact Prompt:
  `/ask` Using only the final TaskBridge implementation, draft:
  1. ARCHITECTURE.md in 10-15 numbered lines covering Project and Notification/Audit relationship, integration contract, Controller-Service-Repository-Entity flow, tenant isolation, audit immutability, transactions, decisions, and trade-offs.
  2. README.md with stack, prerequisites, build, run, test, endpoints, security, assumptions, and limitations.
  Do not describe behavior that is not implemented.
- Mode: Agent
- Techniques: Implementation-grounded documentation, architecture summarization, API documentation, scope and limitation verification
- Result and Corrections: Drafted and verified ARCHITECTURE.md and README.md using only the final TaskBridge implementation. The documents describe the implemented Project, Notification, and Audit flows, tenant controls, transactions, endpoints, assumptions, and limitations without adding unsupported behavior.
- Human Validation: The documents were checked against the final services, controllers, repositories, entities, security context, project configuration, and available tests. No unimplemented behavior was added.
- Screenshot: 27_A_Draft Architecture and README from Final Implementation-Prompt.png, 27_B_Draft Architecture and README from Final Implementation-Prompt.png, 27_C_Draft Architecture and README from Final Implementation-Final-1.png, 27_D_Draft Architecture and README from Final Implementation-Final-2.png