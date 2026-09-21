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