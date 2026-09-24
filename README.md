# TaskBridge API

TaskBridge is a Spring Boot API for organisation-scoped projects, project lifecycle audit history, and user notifications.

## Stack

- Java 17
- Spring Boot 3.5.3
- Spring Web, Spring Data JPA, Spring Validation, Spring Security, and JWT bearer authentication
- Hibernate/JPA persistence
- H2 as the runtime database dependency
- Maven Wrapper

## Prerequisites

- JDK 17 or newer
- A shell that can run the Maven Wrapper (`mvnw.cmd` on Windows, `./mvnw` on Unix-like systems)
- An authenticated Spring Security principal implementing `OrganisationAwarePrincipal`, with an organisation ID, user ID, and required authorities

## Build, Run, and Test

Windows commands:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

The application name is `taskbridge-api`. With the default configuration it uses Spring Boot's embedded database auto-configuration and `spring.jpa.hibernate.ddl-auto=update`.

Set `APP_JWT_SECRET` to a unique random value of at least 32 characters before starting the application. Tokens expire after 15 minutes by default; this can be changed with `APP_JWT_LIFETIME` using an ISO-8601 duration. To provision the first user, set `APP_BOOTSTRAP_USER_ENABLED=true` together with `APP_BOOTSTRAP_USER_USERNAME`, `APP_BOOTSTRAP_USER_PASSWORD` (at least 12 characters), `APP_BOOTSTRAP_USER_ORGANISATION_ID`, and optionally `APP_BOOTSTRAP_USER_AUTHORITIES`.

Swagger UI is available at `http://localhost:8080/swagger-ui.html`, and the generated OpenAPI document is available at `http://localhost:8080/v3/api-docs`.

## API

All endpoints are under `/api` and return JSON. Request validation failures return `400` with an `ApiError` body.

### Authentication

| Method | Path | Behavior |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Validate persisted credentials and return a short-lived JWT bearer token. |

Send the token on protected requests with `Authorization: Bearer <token>`. The token contains the server-side user ID, organisation ID, and authorities; request payloads cannot override them.

In Swagger UI, click **Authorize** and paste only the raw JWT value into the `bearerAuth` field. Swagger adds the `Bearer` prefix automatically.

### Projects

| Method | Path | Behavior |
| --- | --- | --- |
| `GET` | `/api/projects` | Return a pageable, organisation-scoped project list. |
| `GET` | `/api/projects/{projectId}` | Return one project in the active organisation. |
| `GET` | `/api/projects?teamId={teamId}` | Return projects for a team in the active organisation. |
| `POST` | `/api/projects` | Create a project; returns `201`. Body: `name`, optional `description`, `teamId`, and `status`. |
| `PUT` | `/api/projects/{projectId}` | Update project details and apply a valid status transition. |
| `DELETE` | `/api/projects/{projectId}` | Hard-delete a project; returns `204`. |

Project statuses are `DRAFT`, `ACTIVE`, `COMPLETED`, and `ARCHIVED`. Allowed transitions are `DRAFT -> ACTIVE`, `ACTIVE -> COMPLETED`, `COMPLETED -> ARCHIVED`, and `COMPLETED -> ACTIVE`.

### Audit

| Method | Path | Behavior |
| --- | --- | --- |
| `POST` | `/api/audit` | Append or return a deduplicated audit record; returns `201`. Body requires positive `projectId`, `eventType`, and `deduplicationKey`; `message` is limited to 500 characters. |
| `GET` | `/api/audit/{projectId}` | Return organisation-scoped audit history in ascending creation order. Optional query parameters: `from`, `to`, and `eventType`. |

Supported event types are `PROJECT_CREATED`, `PROJECT_STATUS_CHANGED`, `PROJECT_DELETED`, `PROJECT_UPDATED`, and `MILESTONE_REOPENED`.

### Notifications

| Method | Path | Behavior |
| --- | --- | --- |
| `GET` | `/api/notifications/{userId}` | Return notifications for the authenticated user. Optional query parameters: `from`, `to`, and `eventType`. |
| `PATCH` | `/api/notifications/{id}/read` | Mark the authenticated user's notification as read. |

Project mutations generate audit and team-notification records for the relevant project events. Notification creation is deduplicated by organisation, recipient, and event key.

## Security and Tenant Isolation

The API derives tenant and actor identity only from a signature-verified JWT and never from request JSON. Services read both from the authenticated `OrganisationAwarePrincipal` in the Spring Security context. Project permissions are checked through these authorities:

- `project:read`
- `project:create`
- `project:update`
- `project:delete`

Missing authentication returns `401`; missing permissions or notification ownership returns `403`. Repository queries include the active organisation, and a project outside that organisation is treated as not found. Cross-user notification access is rejected.

## Assumptions

- Users are persisted in the application database and passwords are stored with BCrypt hashes; raw passwords and signing keys are supplied through environment variables.
- Team membership is supplied through `TeamMemberDirectory`; the included implementation resolves only the current authenticated user after confirming the active organisation.
- Clients provide unique, stable deduplication keys for direct audit requests and generated project events.

## Limitations

- JWT access tokens are stateless and short-lived; refresh tokens, token revocation, account lockout, and password reset are not included.
- The default team directory does not notify multiple team members; deployments must replace it for that behavior.
- Database schema management is configured as Hibernate `update`; production migrations and an external database configuration are not included.
- Project deletion is a hard delete. Audit records retain the project ID, but audit history requires the project to still exist.
