# Firestick API Documentation

## Health Endpoint
- `GET /api/health` — Returns `OK` if backend is running

## Search Endpoint
- `POST /api/search`
  - Request body:
    - `query` (string): Search query
    - `topK` (integer, default 10): Number of results
    - `filter` (object, optional): Additional filters
  - Response:
    - `results` (array): List of code search results
      - `id` (string)
      - `content` (string)
      - `filePath` (string)
      - `lineNumber` (integer)
      - `score` (number)

## Analysis Endpoint
- `GET /api/analysis/complexity`
  - Response:
    - `average` (number)
    - `max` (number)
    - `histogram` (object)

## Indexing Endpoint
- `POST /api/index`
  - Request body:
    - `files` (array): List of files to index
  - Response:
    - `status` (string): Indexing status

## File Access Endpoint
- `GET /api/file/{id}`
  - Response:
    - `content` (string): File contents
    - `filePath` (string)

## Error Codes
- 400 Bad Request
- 404 Not Found
- 500 Internal Server Error
