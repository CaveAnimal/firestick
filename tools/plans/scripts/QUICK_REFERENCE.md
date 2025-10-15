# Quick Reference Card

## Task Management Scripts

### Quick Commands

```powershell
# Check status (read-only, fast)
.\check-task-status.ps1

# Update task summary (modifies file, creates backup)
.\update-task-summary.ps1
```

---

## Task Status Symbols

| Symbol | Meaning | Counted As |
|--------|---------|------------|
| `[ ]` | Not Started | Not Started |
| `[-]` | In Progress | In Progress |
| `[X]` | Completed | Completed |
| `[V]` | Tested & Verified | Completed |
| `[!]` | Blocked | Blocked |
| `[>]` | Deferred | Deferred |

---

## Workflow

### Daily/Weekly Updates

1. **Check Current Status**
   ```powershell
   .\check-task-status.ps1
   ```

2. **Update Task Statuses**
   - Change `[ ]` to `[-]` when you start a task
   - Change `[-]` to `[X]` when you finish a task
   - Change `[X]` to `[V]` after testing

3. **Update Summary**
   ```powershell
   .\update-task-summary.ps1
   ```
    - Personal files (optional, direct):
       - `update-task-summary.ps1 -TasksFilePath "..\work\dev1\tasksDEV1.md" -SummaryHeader "## Task Summary (DEV1)"`
       - `update-task-summary.ps1 -TasksFilePath "..\work\dev2\tasksDEV2.md" -SummaryHeader "## Task Summary (DEV2)"`
    - Or run your dev update script to sync both personal and master summaries:
      - DEV1: `tools\work\dev1\scripts\dev1-update.ps1`
      - DEV2: `tools\work\dev2\scripts\dev2-update.ps1`

4. **Commit Changes**
   ```powershell
   git add ../firestickTASKS.md
   git commit -m "Update task status - X tasks completed"
   git push
   ```

---

## File Locations

```
tools/
├── plans/
│   ├── firestickTASKS.md        ← Master task document (summary updated by scripts)
│   ├── firestickEXAMPLES.md     ← Examples document
│   └── scripts/                 ← This directory
│       ├── update-task-summary.ps1
│       ├── check-task-status.ps1
│       ├── README.md
│       └── QUICK_REFERENCE.md
├── work/
│   ├── dev1/tasksDEV1.md        ← Developer 1 task list
│   └── dev2/tasksDEV2.md        ← Developer 2 task list
└── backups/
   └── firestickTASKS_*.md
```

---

## Troubleshooting

**Script won't run:**
```powershell
# Enable script execution (run as Administrator)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

**Wrong counts:**
- Verify task status format: `` - `[X]` Task name``
- Check for typos in status symbols
- Ensure backticks (`) are used, not quotes (')

**Need to restore backup:**
```powershell
# List backups
Get-ChildItem ../backups | Sort-Object LastWriteTime -Descending

# Restore a backup
Copy-Item ../backups/firestickTASKS_YYYYMMDD_HHMMSS.md ../firestickTASKS.md
```

---

## Tips

- Run `check-task-status.ps1` frequently - it's fast and read-only
- Run `update-task-summary.ps1` before committing changes to git (or run your `dev*-update.ps1` to update personal + master in one step)
- Backups are kept forever - clean them up periodically
- Use descriptive commit messages with task counts

---

**Created:** October 14, 2025  
**Version:** 1.0
