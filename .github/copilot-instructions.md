# TaskBridge Copilot Instructions

## Mission and scope

TaskBridge is a multi-tenant B2B SaaS assessment application. All work must remain grounded in the explicit product requirements, architecture intent, bug reports, acceptance criteria, and existing code patterns. Do not invent requirements, business rules, data model fields, endpoints, roles, or workflows that are not already specified or clearly implied by the current implementation and the project brief.

Apply the smallest safe change that satisfies the requirement. Keep edits narrow, reviewable, and directly aligned to the issue being solved. Do not broaden scope, refactor unrelated code, or clean up areas outside the task without an explicit reason tied to the requirement.

## Core engineering standards

- Use Java 17, Spring Boot, Maven, and JPA/Hibernate as the project baseline.
- Follow a layered architecture: controller, service, persistence/repository, and domain model layers.
- Keep HTTP/API contracts clear and explicit with DTOs instead of exposing persistence entities directly.
- Prefer constructor injection for dependencies and avoid field injection.
- Keep business logic in services, not in controllers or repositories.
- Use repository methods for persistence concerns only; keep validation and orchestration in the service layer.
- Preserve the existing package structure and naming conventions unless the task requires a deliberate, minimal extension.

## Requirement grounding

- Treat the issue description, technical spec, architecture notes, existing tests, and current implementation as the source of truth.
- If a requirement is unclear, ambiguous, or missing, stop and ask for clarification instead of making assumptions.
- Do not add hidden features, convenience APIs, or speculative behaviors.
- Do not “improve” unrelated code patterns as part of a bug fix or feature task.
- Prefer implementation that is easy to review in a small pull request with a single clear intent.

## Multi-tenant and organisation-scoped access

- The application is multi-tenant. Every access path must respect organisation boundaries.
- Data access must be scoped to the active organisation and never rely on a client-supplied organisation value.
- Use the trusted identity context from the authenticated principal, security context, or token claims as the authority for tenant resolution.
- Enforce organisation membership, permission checks, and role-based access in the service layer and at API boundaries.
- Reject access to resources outside the caller’s organisation with clear, consistent authorization failures.
- Do not bypass tenant filtering through ad hoc repository logic or direct access patterns.

## Security and authorisation

- Treat all incoming data as untrusted until validated.
- Authorisation must be explicit and consistent: check identity, roles, permissions, and organisation scope before mutating or reading sensitive data.
- Use the trusted identity context to derive user, organisation, and authority information; never trust client-controlled identity fields.
- Validate input at request boundaries and enforce business rules in service logic.
- Reject invalid, missing, or hostile input with clear, typed exceptions and user-safe error responses.
- Keep authentication and authorization logic predictable and centralized.

## Validation, exception handling, and error responses

- Validate request payloads, IDs, IDs inside payloads, dates, and business constraints before processing.
- Use specific exception types, such as:
  - ResourceNotFoundException
  - ValidationException
  - ConflictException
  - UnauthorizedAccessException
  - ForbiddenOperationException
  - DuplicateEntityException
- Do not use generic exception swallowing or broad catch blocks that hide the root cause.
- Centralize error handling with a controller advice or equivalent global exception handler.
- Return consistent API error objects with actionable status codes, messages, and correlation or trace identifiers where appropriate.
- Keep exception messages safe and useful without exposing system internals or sensitive data.

## Logging and sensitive data protection

- Use SLF4J logging consistently with meaningful messages and relevant context.
- Log business-state transitions, authorization decisions, security events, and exceptional outcomes when useful, but keep logs concise and actionable.
- Never log secrets, tokens, passwords, API keys, raw credentials, or sensitive personal or organisational data.
- Mask or redact sensitive values before logging.
- Avoid logging full payloads that may contain PII, confidential assessment data, or security-relevant information.
- Include enough context for operational debugging without exposing protected data.

## Persistence, transactions, and data integrity

- Use JPA entities and repositories appropriately; do not expose entities directly through API layers.
- Keep transaction boundaries at the service layer for business operations that require atomicity.
- Use transactions for multi-step writes, state changes, and notifications that must succeed or fail together.
- Ensure data integrity rules are enforced at the application layer and through persistence constraints where appropriate.
- Preserve immutability for audit data and system-generated history. Do not overwrite or mutate immutable audit records after creation.
- Keep audit fields consistent with the project’s business model and do not invent audit semantics without requirement support.

## Notifications and idempotency

- Treat notifications and outbound messaging as operationally important and potentially retryable.
- Implement idempotency for notification delivery and processing using a stable deduplication key or equivalent mechanism.
- Prevent duplicate notifications from being emitted for the same business event.
- Keep notification logic deterministic and safe under retries or repeated requests.
- Do not create speculative notification flows that are not required by the current use case or accepted contract.

## Testing expectations

- Write or update tests as part of the change when behavior is being added or fixed.
- Follow a practical testing strategy:
  - unit tests for domain/service logic
  - integration tests for Spring Boot application behavior
  - controller or API tests for status codes, validation, and authorization outcomes
  - security/tenant tests for organisation-scoped access
- TDD is required for bug fixes and feature changes: add a failing test first when practical, then implement the minimal code required to satisfy it.
- Prefer real behavior tests over mock-only assertions. Do not assert that a mock was called in place of verifying the actual application outcome.
- Cover at least the key happy path, validation failure path, authorization failure path, and multi-tenant enforcement path where relevant.
- Keep tests focused and readable; avoid brittle tests that over-specify implementation details.

## Code quality and change hygiene

- Keep changes small, explicit, and easy to review.
- One logical change per patch. Do not mix unrelated fixes or broad refactors.
- Avoid unrelated formatting churn and large “cleanup” edits.
- Prefer straightforward, readable Java code that matches the project’s existing style.
- Use clear names, explicit types, and meaningful guard clauses.
- Do not add dead code, speculative abstractions, or unused dependencies.
- Do not modify implementation files outside the scope of the task unless a required dependency or compatibility fix is explicitly needed.

## Prohibited patterns

- Do not invent requirements, data structures, roles, endpoints, or policies.
- Do not bypass organisation checks or trusted identity validation.
- Do not expose entities directly in controller responses.
- Do not use field injection or hidden dependency wiring.
- Do not log secrets, tokens, passwords, or sensitive business data.
- Do not add broad refactors, cleanup, or formatting-only changes to unrelated files.
- Do not rely on client-controlled values for tenant, user, or authorization decisions.
- Do not make “maybe helpful” changes that are not directly tied to the issue.
- Do not create new business rules without explicit requirement grounding.

## Review standard

Before finalizing a change, confirm:

- The solution matches the requirement and existing system design.
- The code remains within the intended architecture.
- Organisation-scoped access is enforced.
- Input validation and authorization are preserved.
- Sensitive data is protected.
- Error handling is centralized and consistent.
- Tests cover the changed behavior.
- The patch is small, reviewable, and free of unrelated edits.

When a requirement is uncertain, the correct action is to ask for clarification rather than to guess.