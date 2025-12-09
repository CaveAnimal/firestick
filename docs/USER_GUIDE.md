# User Guide

- Start the app and open http://localhost:8080.
- Use Dashboard for overview, Indexing to scan a project, Search to find code, Graph to view dependencies.

## Multi-App Usage (Logical Multi-Tenancy)

Firestick supports working with multiple legacy apps in a single runtime by tagging data with an "app" name:

- In the UI Search page, use the "App" dropdown to select which app to query. The default is "default".
- When starting an indexing run from the UI, the selected app is passed to the backend so files/chunks are stored under that app.
- All search results and file/chunk lookups are scoped to the selected app.

Notes:
- The backend also accepts an `app` query parameter on supported endpoints, and a `X-App` header. A request filter sets a per-request tenant context with fallback to `default`.
- Vector store (Chroma) collections are named per app using `code_{app}` (sanitized). Re-index each app separately.
- Future option: physical separation by schema/database or dynamic DataSource can be added without changing the UI flow.
