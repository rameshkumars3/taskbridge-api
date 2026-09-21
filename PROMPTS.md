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

