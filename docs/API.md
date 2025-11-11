# API Overview

This document describes the stable REST endpoints used by the UI.

## Health

- GET `/api/health`
	- 200: `"OK"` (text)

## Search

- GET `/api/search`
	- Query params:
		- `q` (string, required, minLength 1) — search query
		- `app` (string, optional, default `default`) — logical application namespace
		- `page` (int, optional, default 1, minimum 1)
		- `pageSize` (int, optional, default 10, minimum 1, maximum 200)
		- `path` (string, optional) — path contains filter
		- `lang` (string, optional) — language filter
	- 200: `{ total: number, results: Array<{ id: string, filePath: string, line: number, snippet: string }> }`
	- 400: Standard validation error when `q` is blank or paging is out of range

## Indexing

- GET `/api/indexing/run`
	- Query params:
		- `root` (string, required, minLength 1) — root path
		- `app` (string, optional, default `default`) — logical application namespace
		- `excludeDirs` (csv, optional)
		- `excludeGlobs` (csv, optional)
	- 200: `IndexingReport`
	- 400: Standard validation error when `root` is missing/blank

- POST `/api/indexing/run`
	- Body (application/json): `IndexingRequest`
		- `rootPath` (string, required, minLength 1)
		- `excludeDirNames` (string[], optional)
		- `excludeGlobPatterns` (string[], optional)
		- `appName` (string, optional, default `default`)
	- 200: `IndexingReport`
	- 400: Standard validation error with field-level details for invalid body

- GET `/api/indexing/jobs/latest`
	- 200: `IndexingJob` or 404 if none

- GET `/api/indexing/jobs/{id}`
	- 200: `IndexingJob` or 404

- GET `/api/indexing/jobs?limit={n}`
	- Query params:
		- `limit` (int, optional, default 10, minimum 1, maximum 100)
	- 200: `IndexingJob[]` (most recent first)
	- 400: Standard validation error when `limit` is out of range

- GET `/api/indexing/stream?jobId={id}` (SSE)
	- Query params:
		- `jobId` (long, optional, minimum 1) — if omitted, streams the latest job
	- 200: `text/event-stream`
	- 400: Standard validation error when `jobId` < 1
  
  Notes on SSE:
  - The stream sends an initial comment event to flush headers early so clients can immediately observe `Content-Type: text/event-stream`.
  - Keepalive/heartbeat comments may be sent to keep connections alive behind proxies.
  - Clients should reconnect on network errors and resume consuming events for the current job.

- POST `/api/indexing/cancel?jobId={id}`
	- Query params:
		- `jobId` (long, optional, minimum 1) — if omitted, cancels the latest RUNNING job
	- 202: Cancellation request accepted
	- 404: Job not found when `jobId` is provided but does not exist
	- 409: No running job to cancel when `jobId` is omitted and no RUNNING job exists
	- 400/500: Standard error responses

## Code Content

- GET `/api/code/file?path={path}`
	- 200: `{ path: string, content: string }`
	- 400: Standard validation error when `path` is blank

- GET `/api/code/chunk?docId={id}`
	- 200: `{ docId: string, path: string, startLine: number, endLine: number, content: string }`
	- 400: Standard validation error when `docId` is blank or malformed
	- 404: Chunk not found

## Graph

- GET `/api/graph/basic?limit={n}`
	- Query params: `limit` (int, optional, minimum 1)
	- 200: `GraphResponse`
	- 400: Standard validation error when `limit` < 1

- GET `/api/graph/enriched?limit={n}&include=containment,imports,calls,inheritance&nodeTypes=F,C,M,I`
	- Query params: `limit` (int, optional, minimum 1)
	- 200: `GraphResponse`
	- 400: Standard validation error when `limit` < 1

`GraphResponse`:
```json
{
	"nodes": [{ "id": "string", "label": "string", "type": "F|C|M|I" }],
	"edges": [{ "source": "string", "target": "string", "type": "string" }],
	"metadata": { }
}
```

## Dashboard

- GET `/api/dashboard/summary`
	- 200: `DashboardSummary`:
```json
{
	"stats": { "totalFiles": 0, "totalClasses": 0, "totalMethods": 0, "hotspotCount": 0 },
	"chart": [{ "name": "string", "value": 0 }],
	"hotspots": [{ "name": "string", "count": 0 }],
	"recentJobs": [{ "id": 1, "status": "string", "startedAt": "", "endedAt": "" }]
}
```

## Error responses

All endpoints may return a standardized error body on failures:

	```json
	{
		"timestamp": "2025-11-05T12:00:00Z",
		"path": "/api/search",
		"status": 400,
		"error": "Bad Request",
		"code": "VALIDATION_ERROR",
		"message": "Query must not be empty",
		"details": [ { "field": "q", "message": "must not be blank" } ]
	}
	```

See `docs/ERRORS.md` for the full schema, codes, and mapping guidelines.

---

See also: `docs/openapi/openapi.json` for the source-of-truth OpenAPI specification and `docs/openapi/README.md` for how we manage drift in CI.

---

See also: `docs/openapi/README.md` for the baseline contract and CI drift checks.
