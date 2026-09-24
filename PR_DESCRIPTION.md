# Pull Request Description

## Summary

This change keeps the scope aligned with the repository’s current evidence: it hardens the Project slice to enforce organisation-scoped access and service-layer validation, and it adds the audit and notification flows needed for project lifecycle tracking and team-visible updates.

The implementation in the current codebase reflects a narrow remediation path rather than a speculative broad feature expansion:

- Project access is scoped to the authenticated organisation via `OrganisationContext` and repository lookups such as `findByOrganisationIdAndId(...)`.
- Project operations are protected by `ProjectAuthorizer` before repository access, with explicit read/create/update/delete authority checks.
- Project creation, update, and delete are transactional and log audit/notification events when lifecycle changes occur.
- Audit and notification APIs are present under `/api/audit` and `/api/notifications`, with tenant-scoped filtering and recipient ownership checks.
- Validation and API errors are centralized through `GlobalExceptionHandler`; invalid IDs, invalid transitions, missing permissions, and missing resources map to consistent HTTP responses.

The current repository evidence also shows this is intentionally a controlled fix: it avoids speculative lifecycle rules beyond the approved domain and preserves the existing organisation-scoped trust model.

## Why the change was needed

The repository review evidence and implementation history describe the underlying issue as a multi-tenant remediation requirement rather than a cosmetic change.

The review notes repeatedly call out that the Project slice previously exposed unscoped repository access and client-controlled identity flows. The evidence in `REVIEW.md` and the service code shows the original risk pattern:

- project lookups and writes were not organisation-scoped
- mutation paths had no explicit service-layer authorization checks
- request data was being trusted too far into the persistence boundary
- invalid status transitions were not constrained at the domain layer
- actions like delete were not isolated behind a clear transaction and authorization boundary

The implemented code addresses the repository’s explicit remediation goals: trusted organisation resolution from authentication state, DTO-based service contracts, validation before persistence, permission checks before repository access, and project lifecycle enforcement.

## Architecture

The current architecture follows the layered structure reflected in the repository:

- Controller layer: `ProjectController`, `AuditController`, `NotificationController`
- Service layer: `ProjectService`, `AuditService`, `NotificationService`
- Repository layer: `ProjectRepository`, `AuditLogRepository`, `NotificationRepository`
- Domain model layer: `Project`, `AuditLog`, `Notification`, `ProjectStatus`, `ProjectEventType`
- Security / tenant layer: `OrganisationContext`, `ProjectAuthorizer`
- Shared HTTP error handling: `GlobalExceptionHandler`

This remains consistent with the repository instructions: keep business logic in services, keep HTTP contracts explicit with DTOs, resolve tenant identity from trusted context, and avoid exposing persistence entities directly.

## Project remediation

The Project slice is the primary remediation area in the current implementation and it is visible in the code:

- `ProjectService` now resolves the active organisation from `OrganisationContext.currentOrganisationId()` and operates with explicit `project:read`, `project:create`, `project:update`, and `project:delete` permission checks.
- `ProjectController` exposes a thin HTTP boundary and uses `ProjectRequest` / `ProjectResponse` instead of exposing the entity directly.
- `Project` now uses a generated ID, `ProjectStatus` enum values, optimistic versioning, and a forward-only lifecycle transition model.
- `ProjectStatus` permits the flow `DRAFT -> ACTIVE -> COMPLETED -> ARCHIVED`, with `COMPLETED -> ACTIVE` reopen support as a valid milestone transition.
- `Project.updateDetails(...)` and `Project.transitionTo(...)` reject invalid transitions via `InvalidProjectStatusTransitionException`.
- `ProjectService.createProject(...)`, `updateProject(...)`, and `deleteProject(...)` are transactional and only act on the active organisation’s project records.
- `ProjectService.getProjectById(...)` hides cross-organisation matches as `ProjectNotFoundException`, which is the repo’s current tenant-isolation pattern.
- `GlobalExceptionHandler` maps validation, not-found, forbidden, unauthorized, and conflict outcomes to consistent HTTP results.

This is the component-level evidence for the remediation scope in the current repository.

## Notification / Audit implementation

The audit and notification paths are implemented as additional, organisation-scoped side effects on the project lifecycle:

- `AuditLog` stores organisation, project, actor user, event type, pre/post status snapshots, message, dedupe key, and server-derived actor IP when available.
- `Notification` stores organisation, recipient user, project, event type, title, message, read state, timestamps, and a per-recipient deduplication key.
- `AuditService.record(...)` validates event inputs, prevents duplicate writes via `findByOrganisationIdAndDeduplicationKey(...)`, and saves the audit row in the same protected tenant context.
- `NotificationService.notifyTeam(...)` fans out project events to team members in the same organisation and suppresses duplicate notifications using the deduplication key.
- `NotificationService.markRead(...)` only allows the authenticated user to mark their own notification as read.
- `AuditService.history(...)` and `NotificationService.findAll(...)` / `unread(...)` enforce organisation and ownership checks before returning data.

This behaviour is consistent with the repository specification and the current repository code paths.

## Integration contract

The actual API surface currently implemented in the repo is:

- `POST /api/audit`
  - creates an immutable audit row
  - validates `projectId`, `eventType`, status transitions, message length, and dedupe key

- `GET /api/audit/{projectId}`
  - retrieves project-scoped audit history for the active organisation
  - supports optional `from`, `to`, and `eventType` filters

- `GET /api/notifications/{userId}`
  - returns notifications for the authenticated user within the active organisation
  - supports optional `from`, `to`, and `eventType` filters

- `PATCH /api/notifications/{id}/read`
  - marks a specific notification as read only when it belongs to the authenticated user

The controller layer keeps the public contract explicit and validated, while the service layer owns the business logic and tenant scoping.

## Security and tenant isolation

The current implementation is grounded in the repository’s security expectations and demonstrates the intended tenant boundary model:

- `OrganisationContext` is the source of the organisation identity and is treated as the trusted auth-derived context.
- `ProjectAuthorizer` enforces permissions before project and audit-side actions are allowed.
- Repository methods are organisation-aware, for example `findByOrganisationIdAndId(...)` instead of broad unscoped queries.
- `AuditService` resolves the organisation from the current security context and enforces project existence inside that organisation before returning history.
- `NotificationService.authorisedUser(...)` rejects mismatched user IDs and ensures a caller cannot read or update another user’s notifications.
- `ProjectService.getProjectById(...)` conceals out-of-scope project IDs as a not-found result instead of exposing cross-tenant objects.

This is consistent with the repo’s security review and the project brief: no client-supplied organisation value is trusted as the authorising boundary.

## AI Tool Disclosure

The repository evidence shows AI was used as a review and remediation aid, not as an autonomous decision-maker for business rules outside the existing spec.

Most-used mode: Ask Mode.

Evidence from the repository review documents states that Ask Mode was used to review the project model, validate architecture risks, and confirm where tenant-scoping and validation changes were required. The implementation then used those reviewed constraints to produce the current remediation rather than introducing speculative features.

Accepted AI output:

- service-layer authorization guardrails
- tenant-aware repository patterns
- DTO-based API boundaries
- centralized exception handling
- explicit project lifecycle validation
- deduplication and event-recording patterns for audit/notification operations

Overridden AI output / explicit non-acceptance:

- speculative milestone or actor-IP expansion beyond the repo’s current approved scope was not accepted because `SPEC.md` and `evidence.md` explicitly describe those areas as non-goals or decision-gated items
- AI-generated ideas that would broaden scope beyond the current contract were rejected in favor of the implemented, narrow remediation

## AI-generated vs manual contribution

This is an approximate estimate, not a formal metric from the repository tooling:

- AI-assisted / AI-reviewed contribution: approximately 30-40%
- Manual implementation and validation: approximately 60-70%

This estimate is reasonable based on the code evidence: the repo contains explicit human-authored review and remediation decisions, while the AI role was concentrated on validation, architecture sanity checks, and implementation alignment rather than product-definition work.

## Test coverage

The repository contains focused tests for the Project remediation, and the current workspace verification confirms the suite passes.

Verified with the repository command:

- `./mvnw test -q`

Result: exit code 0, no test failures reported by the Maven run.

The test evidence in the repo includes:

- `ProjectServiceTests` covering organisation-scoped lookup, not-found behaviour, validation rejection, cross-organisation deletion concealment, invalid status transition rejection, and lifecycle audit emission
- additional audit/notification controller and service tests present in the test tree for the notification and audit flow

## Known gaps

The current repository evidence does not show the following items as implemented or fully specified:

- a production-grade authentication integration test covering real JWT/Principal behaviour beyond service-layer checks
- a formal retention policy for audit and notification records
- a centralised privacy policy for actor IP storage beyond the current code path
- a full external security suite covering end-to-end tenant enforcement across all controllers and routes
- a complete migration strategy for historic data if schema-level behavioural changes are required

These are not regressions in the current patch; they are explicit product and operational gaps in the visible repo evidence.

## One genuine risk / trade-off

The current implementation preserves a hard-delete path for projects while emitting audit and notification events. That is a genuine trade-off: it keeps the code simple and aligned with the existing product behavior, but it does not offer a soft-delete or recovery window for accidental deletions. The repository evidence does not describe a broader archival or retention workflow, so the current fix is intentionally narrow rather than introducing a speculative deletion policy.

## Self-review checklist

- [x] Scope stays aligned with the repository’s current product and security evidence
- [x] Tenant resolution is derived from trusted auth context rather than request data
- [x] Project operations are permission-checked before repository access
- [x] DTO boundaries are used instead of directly exposing persistence entities
- [x] Validation errors and domain conflicts are mapped centrally
- [x] Mutating operations are transactional
- [x] Lifecycle transitions are constrained to valid project states
- [x] Audit and notification records are deduplicated and organisation-scoped
- [x] The current code is verified by the repository test suite
- [x] No unrelated refactor or speculative feature additions were introduced

## Peer review comments

1. Please confirm whether the active organisation identity should remain the sole trust source for all project, audit, and notification access paths before the next feature expansion. This preserves the current repo’s tenant boundary and keeps API behavior predictable.

2. The current implementation is well-scoped and security-aware, but I would specifically review the hard-delete behaviour against the product’s retention and recovery expectations. A soft-delete or archive workflow may be required later, but it is not part of the current evidence.

3. AI blind spot: verify that any future status or event-type expansion is grounded in the repository specification before code changes are merged. The current repo explicitly excludes some lifecycle and privacy behaviors, and a model that “fills in missing product intent” can quietly widen scope beyond the approved contract.

## Conclusion

The repository evidence supports a narrow but meaningful remediation: organisation-scoped project access, validation, authorization, transactional mutation, and lifecycle-aligned audit/notification side effects. The patch remains reviewable, security-conscious, and consistent with the project’s current architecture and specification.
