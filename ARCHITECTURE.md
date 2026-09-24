1. `Project` is the organisation-owned aggregate; it stores the team, lifecycle status, and version, while `AuditLog` and `Notification` store records derived from project events.
2. A project create, status-changing update, or delete records an audit event and attempts to notify the project team using the same deduplication key.
3. Audit records are append-only in the implementation: their entity fields are created once, timestamps are non-updatable, and duplicate keys return the existing record.
4. Notifications are user-visible event records; the current directory implementation resolves the active authenticated user as the team recipient, and duplicate organisation/user keys are skipped.
5. The integration contract is `ProjectService -> AuditService.record(...)` plus `NotificationService.notifyTeam(...)`, with `ProjectEventType`, status values, message, and deduplication key as the event data.
6. Controllers validate HTTP input and return DTOs; services enforce validation, permissions, tenant scope, lifecycle rules, orchestration, and transactions.
7. Repositories are Spring Data JPA interfaces over the `Project`, `AuditLog`, and `Notification` entities; organisation identifiers are included in lookups and filters.
8. `SecurityOrganisationContext` reads the active organisation and user only from an authenticated `OrganisationAwarePrincipal`; client request fields cannot select a tenant or actor.
9. `ProjectAuthorizer` requires `project:read`, `project:create`, `project:update`, or `project:delete` as appropriate, and notification reads additionally require the requested user to be the authenticated user.
10. Project writes, audit recording, notification creation, and notification read updates use service-layer transactions; project event side effects therefore execute in the project write flow.
11. Project status moves only `DRAFT -> ACTIVE -> COMPLETED -> ARCHIVED`, with the implemented `COMPLETED -> ACTIVE` milestone reopening exception; invalid transitions return conflict responses.
12. The design favors explicit synchronous persistence and repository-level tenant predicates over an event broker; it is simple and consistent for this implementation, but couples project writes to audit and notification storage.
