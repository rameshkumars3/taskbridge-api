## Prompt: Repository Instructions
- Mode: Ask
- Exact prompt: “Act as a senior Java, Spring Boot, application security, and testing architect.

Draft the repository-wide `copilot-instructions.md` file for TaskBridge,

a multi-tenant B2B SaaS assessment application.

Include requirement grounding, Java 17/Spring Boot/Maven/JPA, layered architecture,

DTOs, constructor injection, organisation-scoped access, trusted identity context,

authorisation, validation, specific exceptions, centralized errors, SLF4J logging,

sensitive-data protection, transactions, immutable audits, notification idempotency,

testing, small reviewable changes, no invented requirements, and no unrelated edits.

Return only Markdown. Do not modify implementation files.”
- Result: Drafted repository-wide instructions.
- Human validation: Missing or corrected rules: none at this stage; the drafted content is aligned with the TaskBridge requirements and repository guidance as provided.

## Prompt: Required Low-Effort Project Generation

- Mode: Agent

- Exact prompt: Generate a Project model and a Project service with create, update status, get by team, and delete functions. Use a database.

- Human validation: Saved unchanged for later review.

## Prompt: Security Review of Project Model and Service
- Mode: Ask
- Exact prompt: “Act as a senior application security reviewer for a multi-tenant B2B SaaS application. Review the attached unmodified Project model and Project service for files Project.java and ProjectService.java. Check cross-organisation access, authentication, authorisation, IDOR, request-supplied identity, input validation, unsafe database access, sensitive-data exposure, transactions, and concurrency. For every confirmed issue provide file and method, category, severity, exact code evidence, SaaS impact, and recommended correction. Do not modify files and do not report speculative findings.”
- Result: Confirmed issues were documented in REVIEW.md.

## Prompt: Architecture Review of Project Model and Service
- Mode: Ask
- Exact prompt: “Act as a senior Java and Spring Boot architect. Review the unmodified Project model and Project service against .github/copilot-instructions.md. Check model/repository/service/controller separation, Spring Data JPA use, raw database access, DTO/entity separation, validation, transactions, specific exceptions, centralized errors, structured logging, public documentation, query efficiency, naming, type safety, and testability. For every confirmed issue provide file and method, severity, exact evidence, impact, and recommended fix. Do not modify files and do not speculate.”
- Result: Confirmed architecture findings were documented in REVIEW.md.

## Prompt: Focused Review of deleteProject
- Mode: Ask
- Exact prompt: “Act as a senior application security and Spring transaction reviewer. Review only the selected `deleteProject(Long projectId)` method in `src/main/java/com/taskbridge/projects/ProjectService.java`, using the related Project entity and ProjectRepository only as supporting context. Assess security, authentication and authorisation, IDOR, organisation or tenant isolation, input validation, transaction boundaries, concurrency, and data integrity. For every confirmed risk provide the exact code evidence, impact, severity where justified, and a grounded recommendation. Distinguish confirmed omissions from behavior that cannot be established from the repository. Do not modify files.”
- Result: The focused findings were added to REVIEW.md and summarized as Response 4A in evidence.md.

## Prompt: Human Review of Security and Spring Transaction Findings
- Mode: Ask
- Exact prompt: “Act as a senior application security and Spring transaction reviewer performing a human code review of the current TaskBridge repository. Review the existing Project entity, ProjectRepository, and ProjectService, together with the repository instructions and current tests. Identify only concrete, reviewable defects involving organisation or tenant isolation, authentication and authorisation, IDOR, client-controlled identity, mass assignment, input validation, transaction boundaries, concurrency, data integrity, and unbounded queries. For each finding provide severity, file and method, exact code evidence, impact, and a minimal recommendation. Separate confirmed omissions in the reviewed code from controls that cannot be verified because no controller or security configuration is present. Do not modify implementation files, do not invent requirements, and do not report speculative findings. Return concise Markdown suitable for REVIEW.md.”
- Result: Human review findings were added to REVIEW.md and summarized as Response 5A in evidence.md.

## Prompt: Architectural Decisions Copilot Cannot Safely Infer
- Mode: Ask
- Exact prompt: “Act as a senior software architect reviewing the current TaskBridge Project model, repository, and service. Identify at least two issues that cannot be safely resolved by Copilot alone because the repository lacks an explicit product, security, or data-governance decision. Use concrete code evidence and actual examples, including cross-tenant trust boundaries and domain-specific project status transitions. For each issue, explain the decision that a security, product, or domain owner must make, why guessing would create risk, and what implementation can safely proceed after the decision. Also address deletion and concurrency policy if it is decision-dependent. Distinguish confirmed omissions from unverifiable external controls. Do not modify implementation files, invent requirements, or report speculative defects. Return concise Markdown suitable for REVIEW.md and evidence.md.”
- Result: Identified the cross-tenant trust boundary, project status state machine, and deletion/concurrency policy as decision-dependent issues. Findings were added to REVIEW.md and summarized as Response 6A in evidence.md.