# Impact Analysis: MILESTONE_REOPENED and actor IP audit capture

## Summary

This change is not a simple enum addition. It spans the shared event taxonomy, persisted audit model, audit service/business validation, notification fan-out, public API contract, tenant security model, and privacy controls. The repository currently explicitly excludes both behaviors:

- [src/main/java/com/taskbridge/projects/ProjectEventType.java](src/main/java/com/taskbridge/projects/ProjectEventType.java) states that MILESTONE_REOPENED is intentionally excluded.
- [SPEC.md](SPEC.md) explicitly lists both MILESTONE_REOPENED and actor IP capture as non-goals.

Because the project already treats audit and notification records as tenant-scoped, append-only, and identity-derived, the change must be reviewed as both a domain extension and a privacy-sensitive data-retention change.

## 1. Affected files, modules, models, enums, APIs, repositories, services, controllers, tests, and docs

### Event taxonomy and domain model

- [src/main/java/com/taskbridge/projects/ProjectEventType.java](src/main/java/com/taskbridge/projects/ProjectEventType.java)
  - Current event enum excludes MILESTONE_REOPENED.
  - Classification: Additive at the application level, but potentially breaking for any consumer with a strict enum allowlist.

- [src/main/java/com/taskbridge/projects/ProjectStatus.java](src/main/java/com/taskbridge/projects/ProjectStatus.java)
  - Current project lifecycle is forward-only: DRAFT -> ACTIVE -> COMPLETED -> ARCHIVED.
  - A milestone reopen signal would require business semantics that do not currently exist.
  - Classification: Breaking / product-definition required.

- [src/main/java/com/taskbridge/projects/Project.java](src/main/java/com/taskbridge/projects/Project.java)
  - The domain model does not represent milestones or reopening behavior.
  - The reopen trigger would need to be introduced at a business transition point, not simply in the audit layer.
  - Classification: Breaking / product-definition required.

### Audit persistence and schema

- [src/main/java/com/taskbridge/projects/AuditLog.java](src/main/java/com/taskbridge/projects/AuditLog.java)
  - Stores organisation, project, actor, event type, status snapshot, message, created time, and deduplication key.
  - Adding actor IP means a schema change and a privacy-sensitive field.
  - Classification: Migration.

- [src/main/java/com/taskbridge/projects/AuditLogRepository.java](src/main/java/com/taskbridge/projects/AuditLogRepository.java)
  - Query patterns remain valid but may need new indexing for IP-based filtering or export use cases.
  - Classification: Migration.

### Audit service and validation

- [src/main/java/com/taskbridge/projects/AuditService.java](src/main/java/com/taskbridge/projects/AuditService.java)
  - Validates event type and deduplication key before saving.
  - Must accept the new enum constant and record actor IP from trusted request context.
  - Classification: Additive + Migration.

### Notification model and delivery

- [src/main/java/com/taskbridge/projects/Notification.java](src/main/java/com/taskbridge/projects/Notification.java)
  - Holds the project event type, title, message, read state, dedupe key, and timestamps.
  - New event type needs to be supported in the same fan-out path.
  - Classification: Additive.

- [src/main/java/com/taskbridge/projects/NotificationService.java](src/main/java/com/taskbridge/projects/NotificationService.java)
  - Sends notifications to all team members for the project and suppresses duplicates via dedupe key.
  - New event enum is supported as long as it is included in the project lifecycle semantics.
  - Classification: Additive.

- [src/main/java/com/taskbridge/projects/NotificationRepository.java](src/main/java/com/taskbridge/projects/NotificationRepository.java)
  - Query methods by organisation, user, event type, and time range remain unaffected structurally.
  - Classification: Additive.

### API contract and controller layer

- [src/main/java/com/taskbridge/projects/AuditRequest.java](src/main/java/com/taskbridge/projects/AuditRequest.java)
  - Request contract currently does not include actor IP.
  - If the design moves to trusted server-side capture, request DTO should remain unchanged.
  - Classification: Additive or Breaking depending on API contract choice.

- [src/main/java/com/taskbridge/projects/AuditResponse.java](src/main/java/com/taskbridge/projects/AuditResponse.java)
  - If raw actor IP is exposed here, this becomes a public API/privacy change.
  - Classification: Breaking if raw IP is returned by default.

- [src/main/java/com/taskbridge/projects/AuditController.java](src/main/java/com/taskbridge/projects/AuditController.java)
  - The audit API is the place where trusted request metadata is often attached.
  - A server-side IP source may be injected here only if the HTTP trust boundary is correctly established.
  - Classification: Additive + Security-sensitive.

### Tenant and authorization model

- [src/main/java/com/taskbridge/projects/OrganisationContext.java](src/main/java/com/taskbridge/projects/OrganisationContext.java)
  - Trusted tenant resolution remains the authority for org scope.
  - Classification: Additive / unchanged.

- [src/main/java/com/taskbridge/projects/ProjectAuthorizer.java](src/main/java/com/taskbridge/projects/ProjectAuthorizer.java)
  - Enforces permission checks before repository access.
  - Still essential for any new event type.
  - Classification: Additive / unchanged.

- [src/main/java/com/taskbridge/projects/ProjectRepository.java](src/main/java/com/taskbridge/projects/ProjectRepository.java)
  - Existing org-scoped repository access must remain tenant-safe.
  - Classification: Unchanged but required for enforcement.

### Triggering path and lifecycle emission

- [src/main/java/com/taskbridge/projects/ProjectService.java](src/main/java/com/taskbridge/projects/ProjectService.java)
  - This is the event emission point for create, status change, and delete.
  - A milestone reopen event must be triggered here or in the equivalent milestone state transition layer.
  - Classification: Additive if the event is valid; Breaking if the business lifecycle does not exist.

### Tests

- [src/test/java/com/taskbridge/projects/ProjectServiceTests.java](src/test/java/com/taskbridge/projects/ProjectServiceTests.java)
  - Must cover project lifecycle change audit emission and event key semantics.
  - Classification: Additive.

- [src/test/java/com/taskbridge/projects/AuditNotificationServiceTests.java](src/test/java/com/taskbridge/projects/AuditNotificationServiceTests.java)
  - Must validate deduplication, notification fan-out, and the new event/IP behavior.
  - Classification: Additive + Migration.

- [src/test/java/com/taskbridge/projects/AuditNotificationControllerTests.java](src/test/java/com/taskbridge/projects/AuditNotificationControllerTests.java)
  - Must cover acceptance of the new enum and safe public response model.
  - Classification: Additive or Breaking depending on API contract exposure.

### Documentation and design records

- [SPEC.md](SPEC.md)
  - Currently states the non-goals explicitly: no milestone lifecycle beyond the current project workflow and no actor IP capture.
  - The document would need to be revised before implementing the change.
  - Classification: Breaking.

- [REVIEW.md](REVIEW.md)
  - Existing findings and decisions about lifecycle and domain boundaries may need to be updated.
  - Classification: Migration.

- [evidence.md](evidence.md)
  - Current evidence notes that the repo excludes milestone reopen and actor IP capture.
  - Classification: Breaking if the product wants to change the approved scope.

- [IMPACT_ANALYSIS.md](IMPACT_ANALYSIS.md)
  - This document is the impact record for the proposed change.
  - Classification: Additive.

- [ARCHITECTURE.md](ARCHITECTURE.md)
  - Should describe lifecycle semantics and privacy constraints if the change is accepted.
  - Classification: Migration.

## 2. Classification matrix

| Change area | Classification | Notes |
|---|---|---|
| Add MILESTONE_REOPENED to enum | Additive | Works as a new event type if the lifecycle supports it |
| Emit reopen notification and audit entry | Additive | Requires a business trigger point |
| Add actor IP column to audit_log | Migration | Requires DB schema and data-handling decisions |
| Expose raw IP in API response by default | Breaking | Privacy and API compatibility risk |
| Update docs and design spec to re-open the lifecycle | Breaking | Current repo explicitly excludes this feature |
| Leave tenant org scoping unchanged | Additive | Required to preserve security posture |
| Add masked or hashed IP storage | Additive with privacy value | Better than raw IP for compliance and reduced risk |

## 3. Database migration requirements

A database migration is required if the audit record captures actor IP.

Likely schema changes:

- Add a nullable column to the audit_log table, for example:
  - actor_ip_address VARCHAR(45) NULL
  - or actor_ip_masked VARCHAR(64) NULL
  - or actor_ip_hash VARCHAR(128) NULL

Recommended migration considerations:

- Keep the column nullable to avoid breaking old rows.
- Add an index only if the product will search or filter by IP or export by masked value.
- Do not create an index that is not justified by an actual use case; IP-based search can become expensive and privacy-sensitive.
- Keep the change small and explicit; do not widen the database change to unrelated tables.

Operational migration risk:

- Old rows remain valid with null IP values.
- Database-level enum compatibility must be checked if the system uses a DB-native enum instead of a string-backed enum.
- Deploy code and schema together to avoid mismatches.

## 4. Backward compatibility assessment

### Additive compatibility

- Adding a new enum value is additive for application code if all clients tolerate the new event type.
- Audit entries can be appended without altering previous rows.
- Notification updates remain append-only except for read state flag.

### Breaking compatibility risks

- Changing the public JSON schema to include a raw IP field is a breaking API change for existing clients, especially if they deserialize strictly.
- Changing the documented lifecycle to allow reopen behavior may break assumptions in downstream reporting or analytics.
- Changing the audit model to include IP without explicit policy support may break export pipelines and retention processes.

### Safe compatibility posture

- Prefer a server-populated, optional, masked IP field over a raw public field.
- Keep the enum addition additive, but do not widen the API contract unnecessarily.
- Preserve all existing audit payloads unless a controlled admin-only response is explicitly needed.

## 5. Tenant authorization and trust boundary

The current implementation correctly resolves tenant and user scope from trusted security state rather than request data:

- [src/main/java/com/taskbridge/projects/OrganisationContext.java](src/main/java/com/taskbridge/projects/OrganisationContext.java)
- [src/main/java/com/taskbridge/projects/ProjectAuthorizer.java](src/main/java/com/taskbridge/projects/ProjectAuthorizer.java)
- [src/main/java/com/taskbridge/projects/AuditService.java](src/main/java/com/taskbridge/projects/AuditService.java)
- [src/main/java/com/taskbridge/projects/NotificationService.java](src/main/java/com/taskbridge/projects/NotificationService.java)

This must remain true with the new event:

- organisation must come from the authenticated principal or trusted security context
- project and audit records must remain scoped by the active org
- event type must be derived from the business action, not a client-supplied value
- actor user ID must come from trusted identity, not from request payload
- actor IP must be captured from the trusted HTTP layer, not inferred from a client field

### IP trust rules

- Never trust raw client-supplied IP headers as authoritative.
- Only trust the request IP if the app is behind a known secure proxy chain that terminates TLS and strips spoofable headers.
- If the app is not behind a trusted reverse proxy, do not use X-Forwarded-For or similar headers.
- Prefer a proxy-controlled trusted source and log the trust choice explicitly.

## 6. Privacy, retention, masking, export, and logging exposure

### Privacy

Actor IP is sensitive network metadata. It can identify users or devices and may trigger privacy obligations.

### Recommended handling

- Prefer storing masked or hashed values rather than raw IP addresses in the standard audit trail.
- Keep raw IP in a tightly controlled incident-response store only if required by policy.
- Restrict access to raw IP values to a small set of privileged roles.

### Retention

There is no current retention policy in the repo for either audit logs or notifications.

Needed decisions:

- how long IP may be retained
- whether IP is kept in raw form or only in masked form
- whether the retention applies to all audit events or just security-sensitive ones

### Masking guidance

- IPv4: partially mask the last octet or preserve only the network prefix.
- IPv6: mask most of the address and keep only a safe prefix or hash.
- Do not log raw IP values at INFO or DEBUG level in application logs.

### Export considerations

Any export of audit history must:

- keep organisation scope intact
- redact or mask IP before export to general downstream consumers
- avoid exposing raw IP in CSV/JSON exports unless explicitly authorised
- document the export retention and access rules

### Logging exposure

The app currently logs operational messages such as project transitions in [src/main/java/com/taskbridge/projects/ProjectService.java](src/main/java/com/taskbridge/projects/ProjectService.java). The change must avoid:

- logging raw client IPs
- logging full request headers that contain spoofable metadata
- logging full payloads or identifying details beyond the required minimal context

## 7. Implementation order

1. Confirm the business semantics of MILESTONE_REOPENED.
   - Does the product have a milestone lifecycle separate from project status?
   - Is reopen a formal transition or a special event?

2. Confirm privacy rules for actor IP.
   - raw, masked, or hashed
   - admin-only exposure or normal audit exposure
   - retention policy and allowed use cases

3. Add the schema migration.
   - add the IP field as nullable if the data is optional
   - add indexes only if needed

4. Extend the audit and notification event model.
   - [src/main/java/com/taskbridge/projects/ProjectEventType.java](src/main/java/com/taskbridge/projects/ProjectEventType.java)
   - [src/main/java/com/taskbridge/projects/AuditLog.java](src/main/java/com/taskbridge/projects/AuditLog.java)
   - [src/main/java/com/taskbridge/projects/Notification.java](src/main/java/com/taskbridge/projects/Notification.java)

5. Update auditing and notification services.
   - [src/main/java/com/taskbridge/projects/AuditService.java](src/main/java/com/taskbridge/projects/AuditService.java)
   - [src/main/java/com/taskbridge/projects/NotificationService.java](src/main/java/com/taskbridge/projects/NotificationService.java)
   - [src/main/java/com/taskbridge/projects/ProjectService.java](src/main/java/com/taskbridge/projects/ProjectService.java)

6. Update API surfaces and access rules.
   - [src/main/java/com/taskbridge/projects/AuditController.java](src/main/java/com/taskbridge/projects/AuditController.java)
   - [src/main/java/com/taskbridge/projects/AuditResponse.java](src/main/java/com/taskbridge/projects/AuditResponse.java)

7. Update tests.
   - verify happy path, deduplication, tenant rejection, and masked IP behavior

8. Update design and product docs.
   - [SPEC.md](SPEC.md)
   - [REVIEW.md](REVIEW.md)
   - [evidence.md](evidence.md)
   - [ARCHITECTURE.md](ARCHITECTURE.md)

## 8. Required test scenarios

The existing tests should be expanded in:

- [src/test/java/com/taskbridge/projects/ProjectServiceTests.java](src/test/java/com/taskbridge/projects/ProjectServiceTests.java)
- [src/test/java/com/taskbridge/projects/AuditNotificationServiceTests.java](src/test/java/com/taskbridge/projects/AuditNotificationServiceTests.java)
- [src/test/java/com/taskbridge/projects/AuditNotificationControllerTests.java](src/test/java/com/taskbridge/projects/AuditNotificationControllerTests.java)

Required scenarios:

- milestone reopen emits a new audit record
- milestone reopen triggers a team notification
- duplicate reopen event does not create a duplicate dedupe record
- tenant-scoped audit access remains restricted to the active organisation
- actor user ID is derived from trusted security context, not request input
- actor IP is captured only from the trusted HTTP boundary
- client-supplied IP headers are ignored
- API returns masked IP or omits raw IP unless admin-only access is granted
- backwards-compatible reads of audit rows with null IP succeed
- export or response rendering redacts IP before external display

## 9. Rollback and operational safety

If the feature causes privacy or stability issues, rollback should be operationally safe:

- disable the business transition that emits MILESTONE_REOPENED
- keep the DB migration but make the new IP field nullable and no-op on writes
- avoid exporting raw IP values from the audit API until trust and privacy controls are reviewed
- leave the event enum in place but stop causing it to be emitted if the lifecycle semantics are not approved

Operational caution:

- do not roll back the migration before deciding whether historical IP data must be preserved or purged
- deploy code and migration together to avoid runtime mismatch

## 10. Assumptions and open product decisions

The change requires explicit product/security decisions before implementation:

1. Is MILESTONE_REOPENED a formal lifecycle event or a project status alias?
2. Is there a separate milestone domain in the product beyond the current project lifecycle?
3. Is raw actor IP required for compliance or is masked/hashed storage sufficient?
4. Is the IP field visible to all project members or restricted to privileged admins?
5. Is the deployment behind a trusted reverse proxy that sanitizes spoofable headers?
6. What is the retention window for IP data?
7. Should export APIs automatically redact IP values before sending them to clients?
8. Is the project status model allowed to reopen at all, or is the product state machine intentionally forward-only?

Without these answers, the change is under-specified and carries a material privacy and lifecycle risk.

## 11. Risk verdict

This change is feasible only with explicit guardrails:

- The enum extension is low-risk if the product domain truly supports a reopen action.
- The audit/IP capture is high-risk because it introduces sensitive metadata into a persisted audit trail.
- A raw IP field should not be added without a security review, proxy trust model, retention policy, and export rules.
- The safest default is to capture a masked or hashed IP value and expose nothing raw in the standard API.

## 12. How Copilot assisted

Copilot assisted by:

- searching the workspace for the current event model and audit/notification flow
- confirming the repo’s explicit exclusions in [ProjectEventType.java](src/main/java/com/taskbridge/projects/ProjectEventType.java) and [SPEC.md](SPEC.md)
- tracing the emit path through [src/main/java/com/taskbridge/projects/ProjectService.java](src/main/java/com/taskbridge/projects/ProjectService.java), [src/main/java/com/taskbridge/projects/AuditService.java](src/main/java/com/taskbridge/projects/AuditService.java), and [src/main/java/com/taskbridge/projects/NotificationService.java](src/main/java/com/taskbridge/projects/NotificationService.java)
- mapping affected test and documentation surfaces without changing any production code

This review is grounded in the repository’s current scope, security model, and implementation patterns rather than assuming new business rules.
