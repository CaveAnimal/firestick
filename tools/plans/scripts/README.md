# Task Management Scripts

This directory contains PowerShell scripts for managing the Firestick development tasks.

---

## Scripts

### update-task-summary.ps1

**Purpose:** Automatically updates the Task Summary section in `firestickTASKS.md` (master) or a specified personal tasks file with current completion statistics.

**What it does:**
- Counts total tasks (main tasks + sub-tasks)
- Counts completed/tested tasks (marked with `[V]` or `[X]`)
- Counts in-progress tasks (marked with `[-]`)
- Counts blocked tasks (marked with `[!]`)
- Calculates percentage complete
- Updates the Task Summary section with current statistics
- Adds timestamp in Central Standard Time
- Creates a backup of the file before updating

**Usage:**

```powershell
# From the scripts directory (updates master tasks file)
.\update-task-summary.ps1

# Update a specific personal tasks file (new)
.\update-task-summary.ps1 -TasksFilePath "..\work\dev1\tasksDEV1.md" -SummaryHeader "## Task Summary (DEV1)"
.\update-task-summary.ps1 -TasksFilePath "..\work\dev2\tasksDEV2.md" -SummaryHeader "## Task Summary (DEV2)"

# From anywhere in the project
E:\MyProjects\MyGitHubCopilot\firestick\fstk-001\tools\plans\scripts\update-task-summary.ps1 -TasksFilePath "tools\work\dev1\tasksDEV1.md" -SummaryHeader "## Task Summary (DEV1)"
```

**Requirements:**
- PowerShell 5.1 or higher
- Read/write access to `tools/plans/firestickTASKS.md`

**Output Example:**
```
=== Task Summary Update Script ===
Processing: E:\...\firestickTASKS.md

Counting tasks...

Task Statistics:
  Total Tasks:        1,747
  Completed/Tested:   32 ([V] or [X])
  In Progress:        5 ([-])
  Blocked:            2 ([!])
  Not Started:        1,708 ([ ])
  Percent Complete:   1.83%
  Timestamp:          October 14, 2025    4:22 PM Central Standard Time

Updating Task Summary section...
✓ Task summary updated successfully!

Backup created: E:\...\backups\firestickTASKS_20251014_162200.md

=== Update Complete ===
```

**Backups:**
- Backups are automatically created in `tools/plans/backups/` directory
- Backup filename format: `firestickTASKS_YYYYMMDD_HHMMSS.md`
- Useful for recovering previous versions if needed

---

### check-task-status.ps1

**Purpose:** Quick status check without modifying the file.

**What it does:**
- Reads `firestickTASKS.md` (master) or a specified personal tasks file and displays current statistics
- Shows visual progress bar
- Breaks down tasks by status (completed, in-progress, blocked, deferred, not started)
- Read-only operation - does not modify the file

**Usage:**

```powershell
# From the scripts directory (checks master tasks file)
.\check-task-status.ps1

# Check a specific tasks file (new)
.\check-task-status.ps1 -TasksFilePath "..\work\dev1\tasksDEV1.md"
.\check-task-status.ps1 -TasksFilePath "..\work\dev2\tasksDEV2.md"

# From anywhere in the project
E:\MyProjects\MyGitHubCopilot\firestick\fstk-001\tools\plans\scripts\check-task-status.ps1 -TasksFilePath "tools\work\dev1\tasksDEV1.md"
```

**Output Example:**
```
╔═══════════════════════════════════════════════════════════╗
║          FIRESTICK TASK STATUS SUMMARY                   ║
╚═══════════════════════════════════════════════════════════╝

Total Tasks:          1,747

✓ Completed/Tested:   32 tasks (1.83%)
⚙ In Progress:        1 tasks
⊗ Blocked:            1 tasks
▶ Deferred:           1 tasks
○ Not Started:        1712 tasks

Overall Progress:     1.89% (including in-progress)

Progress Bar: [█░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░]

File: E:\...\firestickTASKS.md
```

---

## Task Status Indicators

The scripts recognize these task status indicators:

- `[ ]` - Not Started
- `[-]` - In Progress
- `[X]` - Completed
- `[V]` - Tested & Verified (considered complete)
- `[!]` - Blocked
- `[>]` - Deferred

---

## Best Practices

1. **Run the script regularly** - Update statistics after making significant progress
2. **Check backups** - Old backups accumulate in `backups/` directory; clean up periodically
3. **Commit changes** - After running the script, commit the updated `firestickTASKS.md` to git
4. **Verify accuracy** - Spot-check the counts to ensure they match your expectations

---

## Troubleshooting

**Error: "Tasks file not found"**
- Ensure you're running the script from the correct location
- Verify the file path in the script matches your directory structure

**Error: "Could not find Task Summary section"**
- Ensure `firestickTASKS.md` has a section starting with `## Task Summary`
- Check that the file hasn't been corrupted

**Wrong task counts:**
- Verify all tasks use the correct checkbox format: `` `[ ]` ``
- Check for any malformed task markers

---

## Future Enhancements

Potential improvements for task management:
- [ ] Add progress tracking over time (save history)
- [ ] Generate visual charts/graphs of progress
- [ ] Create task completion reports by phase
- [ ] Add email notifications for milestones
- [ ] Integration with project management tools
- [ ] Automated task status updates from git commits

---

**Last Updated:** October 14, 2025  
**Author:** Firestick Development Team
