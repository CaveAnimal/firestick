# dev5 — Shared Templates & Common Files

This directory (dev5) collects the common templates and artifacts present in dev1, dev2, dev3 and dev4 so a new developer
can start from a single canonical location.

Contents:
- `README.md` — this file
- `scripts/` — shared script templates (check / update)
- `tasksDEV5.md` — starter tasks checklist (beginner-friendly)
- `dev5CAPTAINS_LOG.md` — place to record daily logs and notes

Why dev5
- Consolidates commonly-used templates and examples across the other developer folders
- Helps on-board new contributors quickly by giving a single, authoritative starting point
- Keeps a clean set of small helpers that can be copied into per-developer folders if needed

How to use
1. Read `tasksDEV5.md` for a simple step-by-step onboarding or first tasks.
2. Use `scripts/` as-is or copy-modify into your personal devN folder.
3. Keep `dev5CAPTAINS_LOG.md` updated with notes that are useful for everyone.

If you'd like I can make `tasksDEV5.md` an exact merge of the common subtasks from dev1-4 — say the word and I'll do that next.

## Synthetic SSE harness (dev5 helper)

dev5 includes a small synthetic SSE server under `tools/work/dev5/scripts/synthetic_indexing_server.py` that emits deterministic indexing events for local development and short CI smoke checks.

Quick start (PowerShell):

```powershell
& .\.venv\Scripts\Activate.ps1  # if you use the repo venv
python tools/work/dev5/scripts/synthetic_indexing_server.py --port 9001 --job-id 123
```

Then connect to: http://127.0.0.1:9001/sse?jobId=123 and watch SSE events stream. There are helper wrappers:

- `tools/work/dev5/scripts/run-synthetic-indexing.ps1` (PowerShell)
- `tools/work/dev5/scripts/run-synthetic-indexing.sh` (bash)

Pre-commit / local hook helper
--------------------------------
We provide a small helper to block accidentally committing very large files (helpful given earlier issues with large model artifacts):

Install the pre-commit hook locally (PowerShell):

```powershell
.
tools\work\dev5\scripts\install-git-hooks.ps1
```

This copies `prevent_large_commit.sh` into `.git/hooks/pre-commit` — edit the script or hook to tune max size as needed.

