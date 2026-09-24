# Swagger Test Sequence

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## 1. Start the Application

Configure a local JWT secret and bootstrap user before starting the application:

```powershell
$env:APP_JWT_SECRET="local-development-secret-key-with-at-least-32-characters"
$env:APP_BOOTSTRAP_USER_ENABLED="true"
$env:APP_BOOTSTRAP_USER_USERNAME="admin"
$env:APP_BOOTSTRAP_USER_PASSWORD="correct horse battery"
$env:APP_BOOTSTRAP_USER_ORGANISATION_ID="org-1"
$env:APP_BOOTSTRAP_USER_AUTHORITIES="project:read,project:create,project:update,project:delete"

.\mvnw.cmd spring-boot:run
```

## 2. Authenticate

Execute `POST /api/auth/login`:

```json
{
  "username": "admin",
  "password": "correct horse battery"
}
```

Copy the returned `accessToken`. Click **Authorize** in Swagger and paste only the raw token. Do not include the `Bearer ` prefix; Swagger adds it automatically.

## 3. Test Projects

Execute the following sequence:

1. `POST /api/projects`

```json
{
  "name": "Swagger Test Project",
  "description": "Created through Swagger",
  "teamId": "team-1",
  "status": "DRAFT"
}
```

Expected response: `201 Created`. Save the returned `id` as `projectId`.

2. `GET /api/projects`

Expected response: `200 OK`.

3. `GET /api/projects/{projectId}`

Replace `{projectId}` with the created project ID. Expected response: `200 OK`.

4. `GET /api/projects?teamId=team-1`

Expected response: `200 OK`.

5. `PUT /api/projects/{projectId}`

```json
{
  "name": "Swagger Test Project",
  "description": "Updated through Swagger",
  "teamId": "team-1",
  "status": "ACTIVE"
}
```

Expected response: `200 OK`.

6. `GET /api/audit/{projectId}`

Expected response: `200 OK`, including the project lifecycle audit entries.

## 4. Test Direct Audit Creation

Execute `POST /api/audit`:

```json
{
  "projectId": 1,
  "eventType": "PROJECT_UPDATED",
  "previousStatus": "DRAFT",
  "newStatus": "ACTIVE",
  "message": "Direct Swagger audit test",
  "deduplicationKey": "swagger-audit-test-1"
}
```

Replace `projectId` and the deduplication key with values appropriate for the project under test.

Expected response: `201 Created`.

## 5. Test Notifications

Use the authenticated user ID from the JWT `userId` claim.

1. `GET /api/notifications/{userId}`

Expected response: `200 OK`. Save a returned notification `id` as `notificationId`.

2. `PATCH /api/notifications/{notificationId}/read`

Expected response: `200 OK` with `read: true`.

## 6. Clean Up

Execute `DELETE /api/projects/{projectId}`.

Expected response: `204 No Content`.

## Expected Status Codes

| Operation | Expected status |
| --- | ---: |
| Login | `200` |
| Create project | `201` |
| Read, list, filter, update | `200` |
| Create audit | `201` |
| Read audit history | `200` |
| Read notifications | `200` |
| Mark notification read | `200` |
| Delete project | `204` |

## Negative Checks

- Protected endpoint without authorization: `401 Unauthorized`.
- Invalid project payload: `400 Bad Request`.
- Project ID `0` or another non-positive ID: `400 Bad Request`.
- Nonexistent project in the active organisation: `404 Not Found`.
- Invalid project status transition: `409 Conflict`.
- Notification access for another user: `403 Forbidden`.
