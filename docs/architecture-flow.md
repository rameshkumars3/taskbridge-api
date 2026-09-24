# Architecture Flow

## Overview
TaskBridge is a multi-tenant B2B assessment platform. The application is structured around a standard layered Spring Boot design:

- Controller layer: exposes HTTP endpoints and request validation
- Service layer: carries business logic, authorisation, organisation scoping, and workflow orchestration
- Repository layer: persists and retrieves domain data using JPA repositories
- Domain layer: contains entities, value objects, and business rules

## Request Flow
1. A client sends an HTTP request to a controller.
2. The controller validates the request contract and delegates to the appropriate service.
3. The service resolves the active organisation and authenticated user from the security context.
4. Business rules are applied, including access checks and status transitions.
5. Persistence occurs via repository methods.
6. The result is mapped to a DTO or response object and returned to the client.

## Multi-tenant Processing
The application must never trust client-supplied organisation identifiers. Tenant context is derived from the authenticated principal or security context, and all access is filtered to that organisation.

## Key Responsibilities
- Authentication and authorization enforce the active user identity and scope.
- Project and assessment operations are validated before state changes.
- Audit and notification events are generated as part of the workflow.
- Exceptions are normalized through a global advice layer for consistent API error responses.

## Operational Notes
- Business logic remains in service classes.
- Repositories are focused on persistence operations.
- DTOs are used to protect persistence entities from direct API exposure.
- Transactions are maintained around multi-step writes that must succeed together.
