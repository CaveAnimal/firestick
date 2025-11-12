Incident 0001: Tried to rename old dev task files to timestamped backups via terminal; no output.
Symptom: No output; appears stuck.
Cause: VS Code trust/execution policy blocked execution.
Do now: Run the provided one-line PowerShell manually; continue other work.
Prevent: 15s/60s timeouts; echo intent; validate paths; timestamped backups.

Incident 0002 (2025-11-05 16:48 CST): npm install failed in ui/ due to missing package `vitest-junit-reporter` (404).
Symptom: npm E404 Not Found for package; UI deps couldn't install; TS/JSX types missing.
Cause: Dev dependency no longer exists/renamed; Vitest v2 already ships a built-in `junit` reporter, so extra package unnecessary.
Work done: Removed `vitest-junit-reporter` from ui/package.json; installed deps; excluded Playwright specs from Vitest; fixed minor TS types; added @types/prismjs.
Result/Prevent: UI builds, tests, and typecheck pass; prefer built-in reporters; keep e2e tests under separate runner and exclude from unit runner.
