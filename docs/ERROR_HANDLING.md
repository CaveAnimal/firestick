# Error Handling Guide

## Error Response Format (API)
```json
{
  "error": {
    "code": 404,
    "type": "NotFound",
    "message": "Resource not found.",
    "details": "Optional details for debugging."
  }
}
```

## Error Codes
- 400 Bad Request: Invalid input or request
- 401 Unauthorized: Authentication required
- 403 Forbidden: Insufficient permissions
- 404 Not Found: Resource does not exist
- 409 Conflict: Resource conflict
- 422 Unprocessable Entity: Validation error
- 500 Internal Server Error: Unexpected backend failure

## Error Messages
- Use clear, actionable language
- Avoid leaking sensitive details
- Include `details` only for debugging (never in production)

## Frontend Error Handling
- Display user-friendly error messages
- Show error code/type for support
- Log errors for diagnostics
- Retry or suggest next steps if possible

## Example Usage
```js
// Example frontend error handler
function handleApiError(error) {
  if (error?.response?.data?.error) {
    alert(`Error: ${error.response.data.error.message}`);
  } else {
    alert('An unexpected error occurred.');
  }
}
```
