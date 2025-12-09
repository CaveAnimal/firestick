# Error Handling

This document defines the error response format and guidance for consistent frontend/backed error handling.

## Error Schema

```json
{
  "timestamp": "2025-11-05T12:00:00Z",
  "path": "/api/search",
  "requestId": "5d3c2f7e-3a3d-4e0d-9f1a-7a1e2c3b4d5e",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Query must not be empty",
  "details": [
    { "field": "q", "message": "must not be blank" }
  ]
}
```

- timestamp: ISO 8601
- path: request path
- requestId: correlation id (also returned in the `X-Request-Id` response header)
- status: HTTP status code
- error: reason phrase
- code: stable, machine-readable error code
- message: human-readable summary
- details: optional structured details

## Mapping Guidelines (ControllerAdvice)

- 400 VALIDATION_ERROR: Bean validation and bad parameters
  - MissingServletRequestParameterException (missing required query/param)
  - ConstraintViolationException (invalid query/path parameters)
  - MethodArgumentNotValidException (invalid request body fields via `@Valid`)
- 404 NOT_FOUND: Missing resources (e.g., chunk/file not found)
- 409 CONFLICT: Duplicate or conflicting operations
- 422 UNPROCESSABLE_ENTITY: Semantically invalid requests
- 500 INTERNAL_ERROR: Uncaught exceptions

Spring example (pseudocode):

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ErrorResponse handleValidation(MethodArgumentNotValidException ex) { /* ... */ }
```

### Field-level details for request body validation

When handling `MethodArgumentNotValidException`, we include field-specific errors in `details`:

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    { "field": "rootPath", "message": "must not be blank" },
    { "field": "excludeDirNames[0]", "message": "must not be blank" }
  ]
}
```

For query/path validation (`ConstraintViolationException`), `field` corresponds to the parameter name (e.g., `limit`, `jobId`, `q`).

## Frontend Handling Plan

- Show toast for generic failures; inline messages for field issues
- Prefer retry for transient 5xx errors where safe
- Normalize error shape in UI fetch layer before components consume it

## Developer Notes

- Add tests to assert correct HTTP status and error schema for controllers
- Keep error codes documented in this file to avoid drift
- Ensure OpenAPI (`docs/openapi/openapi.json`) is updated alongside any API behavior changes to keep contract and docs aligned
- Each response includes `X-Request-Id` header. On errors, the same id is also included in the JSON body as `requestId` to aid troubleshooting.
