# OpenAPI Baseline

This directory holds the committed OpenAPI contract baseline used for drift detection in CI.

How it works:
- CI generates the current contract at `target/openapi/openapi.json` and validates the schema.
- CI compares that generated file with `docs/openapi/openapi.json`.
- If they differ, CI fails with a drift error. If no baseline exists yet, the drift check is skipped.

To establish or update the baseline:
1. Run the application locally and export OpenAPI JSON (or download the `openapi-json` artifact from a successful CI run).
2. Copy the JSON to `docs/openapi/openapi.json`.
3. Commit the change in the same PR as the API modifications.

Notes:
- Keep this file versioned to intentionally track API changes.
- Large diffs may indicate accidental changes in annotations or configuration.
