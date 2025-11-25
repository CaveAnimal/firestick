```markdown
# dev5 Insurance — Stability, tests, and release-ready checklist

Purpose
-------
This is a focused "insurance" checklist for the Indexing Console work (PRD + planning + task work in tools/planc and tools/work/dev5).
Add this file to `dev5` to ensure the feature is fully stable, properly tested, and production-ready — especially after we saw multiple long-lived problems caused by underspecified updates.

How to use this file
--------------------
- Treat each top-level section as a required insurance area (backend, db, api, frontend, tests, CI/CD, observability, docs, DX).
- Each subtask is small and actionable. Mark it complete ([x]) when done.
- Tasks are prioritized: green (must), amber (should), blue (nice-to-have).

Acceptance criteria (how we know we're done)
-------------------------------------------
- Indexing Console end-to-end flows work reliably in CI and can be run locally with easy reproducible steps.
- Frontend displays live per-object progress and aggregated counters with SSE + polling fallback in all supported browsers and platforms.
- SSE events are published with consistent shapes and backed by persistence such that late joiners and reconnect scenarios recover gracefully.
- CI gate enforces tests and prevents regressions; performance and telemetry are in place.

Essential (MUST) — immediate priorities ✅
---------------------------------------
[ ] 1. Formal end-to-end acceptance test (Playwright): create a CI-run E2E that runs a tiny synthetic indexing job and asserts SSE events and UI updates (object-start / object-progress / object-end / progress / object-skipped).
[ ] 2. Synthetic job harness: provide a small, deterministic synthetic indexing job runner used by tests and local dev so all engineers can reproduce the same behavior quickly.
[ ] 3. API contract tests (backend): unit + integration tests validating every SSE event shape and the snapshot API contract (snapshot JSON includes default values, timestamps, and currentObject payload).
[ ] 4. Polling fallback test (client+server): assert UI enters polling mode after SSE disconnect; polling snapshot merges correctly with current in-memory state.
[ ] 5. DB migration & data safety: add definitive DB migrations and a safety migration test to guarantee job/object telemetry columns exist and rollback is safe.
[ ] 6. CI gating: require tests (unit/integration/e2e) to pass on PRs before allowing merge; make a minimal failing E2E test to validate gating.
[ ] 7. PR review checklist: add required PR checklist items to ensure the PR includes tests, docs, and rollout notes for any backend schema change.

High priority (SHOULD) — correctness & robustness 🟧
------------------------------------------------
[ ] 8. SSE performance & throttling: add throttling for object-progress events, tunable via configuration; include tests that show throttling behavior and ensure server doesn't flood clients.
[ ] 9. Late-join recovery tests: verify that a UI joining mid-job gets an accurate snapshot via the snapshot endpoint and that journaled per-object entries match persisted state.
[ ] 10. Reconnect & ordering tests: test SSE reconnect logic and verify event ordering (object-start → object-progress* → object-end) under simulated network reorder/latency.
[ ] 11. Edge-case handling tests: create tests for skipped objects, zero-length objects, large-object progress reporting, and missing totals (unknown totalFiles) scenarios.
[ ] 12. Accessibility & Cross-Browser QA: validate progress components with automated axe checks and manual spot-checks on Chrome, Edge, Firefox and Safari on macOS / windows.

Medium priority (NICE) — observability & diagnostics 🔵
-----------------------------------------------------
[ ] 13. Metrics: publish backend metrics for SSE publishes, connection count, reconnect count, throttle count and missed-events; add dashboard panels and alerts.
[ ] 14. Tracing: add correlation IDs across emitted SSE events to link UI actions to server traces (use existing request/trace headers if present).
[ ] 15. Log enrichment: ensure all SSE emission points add structured logs with jobId/objectId/objectType and eventType for faster triage.
[ ] 16. Testing for telemetry: include tests to validate the metrics are emitted when events fire (unit/integration level checks).

Lower priority (HARDENING & DX) — polish and prevention 🟦
-------------------------------------------------------
[ ] 17. Pre-commit / pre-push hooks: add or tighten hooks that prevent committing large files (models, venvs, build artifacts) and ensure consistent formatting and test run before allowing commits.
[ ] 18. .gitattributes & LFS policy: ensure binary model files are not tracked accidentally; add documentation to avoid re-introducing huge files into history (and set LFS patterns only for allowed assets below remote limits).
[ ] 19. CI smoke & speed: add a lightweight smoke stage that runs synthetic indexing job emulation under 10s so CI can validate essential behavior quickly before full E2E runs.
[ ] 20. Local dev scripts: add `scripts/dev5/run-synthetic-indexing.sh|ps1` that starts a small server, kicks a synthetic job and prints event sequence for manual validation.
[ ] 21. Developer troubleshooting docs (dev5): add a short troubleshooting page with common failures and how to re-run synthetic jobs and collect logs.

Security, privacy & release cautions ⚠️
------------------------------------
[ ] 22. Auth & SSE scoping: ensure SSE endpoints are authenticated and scoped to allowed viewers (jobs must not leak across tenants/projects).
[ ] 23. Input sanitization: sanitize path values and any user-supplied fields in SSE events to prevent accidental leakage of sensitive information.
[ ] 24. Release notes & rollback plan: for any DB changes or event-shape changes include a rollback/compatibility plan and announce deprecation windows in PR description and release notes.

AI / team process safeguards (learn from earlier failures) 🛡️
---------------------------------------------------------
[ ] 25. Enforce PR checklist: require PRs changing indexing behavior to include tests (unit + integration + required playback E2E), migration notes and a runbook entry.
[ ] 26. No-AI-only merge rule for critical paths: require at least one human reviewer with knowledge of the Indexing subsystem sign-off for changes to events, DB migrations or client behavior.
[ ] 27. Test-first policy: encourage adding unit/integration tests before large changes; CI should fail PRs missing tests for event logic.
[ ] 28. Post-merge verification: automatic smoke job that runs after merge (or nightly) validating a synthetic indexing workflow publishes events and UI still renders correctly.
[ ] 29. Incident playbook: add an incident-runbook example to `dev5` describing steps to triage SSE problems, recover a broken rollout, and re-run synthetic jobs locally.

Appendix — quick local dev helpers
---------------------------------
Add these small helpers and include them in `dev5/scripts/` or `scripts/` so new contributors can verify the system locally:

1. run-synthetic-indexing.ps1 / .sh — creates a tiny indexing job (2-4 objects) and prints published SSE events to console.
2. run-ui-dev-and-synthetic.ps1 — starts the frontend dev server, then fires the synthetic job so devs can watch UI updates and SSE behavior.
3. ci-smoke.sh — a very small check that runs in CI and validates the synthetic job finishes and at least one progress event and one object-end are observed within a short timeout.

Notes
-----
This insurance checklist is deliberately conservative. The goal is to make the Indexing Console feature resilient, testable, and easy for any developer to validate locally or in CI.

If you want, I can now:
- Create the `dev5/scripts/` helper scripts and a tiny synthetic indexing job harness (Python or Java) and wire it into CI as a quick smoke test.
- Implement the Playwright E2E and the CI gate changes (in a focused branch), or split the work into smaller PRs and iteratively land the most important items first.

---

Created on: 2025-11-24
Author: dev5 team / copilot
