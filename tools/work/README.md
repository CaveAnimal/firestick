# Developer Work Area

This folder contains per-developer task documents and helper scripts to manage progress independently from the master plan.

## Files

- tasksDEV1.md — Developer 1 task list (Backend)
- tasksDEV2.md — Developer 2 task list (Frontend)
- dev1/scripts/
  - dev1-check.ps1 — Quick status for Developer 1
  - dev1-update.ps1 — Update summaries (personal + master)
- dev2/scripts/
  - dev2-check.ps1 — Quick status for Developer 2
  - dev2-update.ps1 — Update summaries (personal + master)

## How it works

These wrappers call the shared scripts in `tools/plans/scripts` with the appropriate task file path and summary header. This allows each developer to work in their own document while keeping the same counting logic. Scripts now live in each dev’s `scripts/` subfolder for convenience.

## Conventions

- Each task file should include a summary section header so the update script can find it:
  - Developer 1: `## Task Summary (DEV1)`
  - Developer 2: `## Task Summary (DEV2)`
- Use the standard task markers: `[ ]`, `[-]`, `[X]`, `[V]`, `[!]`, `[>]`.

## Usage

From the repo root or anywhere (PowerShell):

```powershell
# Developer 1
tools\work\dev1\scripts\dev1-check.ps1
tools\work\dev1\scripts\dev1-update.ps1

# Developer 2
tools\work\dev2\scripts\dev2-check.ps1
tools\work\dev2\scripts\dev2-update.ps1
```
