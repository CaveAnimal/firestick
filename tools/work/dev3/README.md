# DEV3 Workspace

Purpose: Acts as a focused task list for re-scoped current phase work migrated from master backlog. Mirrors DEV1/DEV2 structure.

## Files
- `tasksDEV3.md` – Primary task list with summary header updated by scripts.
- `dev3CAPTAINS_LOG.md` – Log entries before/after work segments.
- `scripts/dev3-update.ps1` – Update summary counts & master.
- `scripts/dev3-check.ps1` – Read-only summary.

## Status Markers
Same as TheRules: `[ ]` Not Started, `[-]` In Progress, `[X]` Completed, `[V]` Tested, `[!]` Blocked, `[>]` Deferred.

## Usage
Run update after changing statuses:
```
./scripts/dev3-update.ps1
```
