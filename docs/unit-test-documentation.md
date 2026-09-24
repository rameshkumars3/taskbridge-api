# Unit Test Documentation

## Purpose
This document describes the unit-test strategy for the TaskBridge API and explains the key validation and enforcement paths covered in the project.

## Test Scope
The current test suite focuses on the core application behavior for:

- project service workflows
- state transitions and validation rules
- audit and notification flows
- controller request and response handling
- authorization and organisation-scoped access patterns

## Patterns Used
- Service-layer tests validate business logic and domain rules.
- Controller tests verify status codes, validation failures, and expected responses.
- Multi-tenant tests ensure access is restricted to the active organisation.
- Failure-path tests cover invalid transitions, forbidden actions, and missing resources.

## Typical Validation Areas
1. Happy path execution for core project operations.
2. Validation failures for malformed or inconsistent inputs.
3. Authorization failures when a user lacks access or permissions.
4. Multi-tenant separation to ensure organisation boundaries are respected.
5. Error handling through the global exception mechanism.

## Guidance
When adding or adjusting functionality, tests should validate observable behavior rather than implementation details. Prefer real service invocation and assertion of result state, exceptions, or API responses.

## Execution
Tests are run using Maven with the project build lifecycle, typically through:

```bash
./mvnw test
```

This should be used to verify that the application continues to satisfy the defined business and integration expectations.
