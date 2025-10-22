# React Dev2: Task Sync Plan

## Inputs
- Source: `firestickReactTasks.md` (exact path unknown)
- Target: `/work/dev2/tasksDev2.md` (likely `tools/work/dev2/tasksDev2.md`)

## Outputs
- Updated `tasksDev2.md` including every task from `firestickReactTasks.md` in correct sections/ordering
- Captain’s log entry and task list status updated

## Constraints
- Avoid loading entire files at once; process in chunks
- Preserve existing structure, checkboxes, and section order in `tasksDev2.md`

## Step-by-step plan
1) Locate files
	 - Workspace search queries:
		 - `firestickReactTasks.md`
		 - `tasksDev2.md`
	 - Note exact file paths for both.
2) Snapshot headers only (low-token)
	 - Extract only the top-level outline from both files:
		 - All headings (e.g., `#`, `##`, `###`)
		 - Any task IDs, day numbers, or section anchors
	 - Build a section map for each file: section title → line range.
3) Define matching anchors
	 - Prefer exact matches by:
		 - Day/Week numbers (e.g., “Day 43”, “Phase 5”)
		 - Section titles (e.g., “React Project Setup”, “Search Interface”)
		 - Task IDs if present
	 - Prepare fallback fuzzy match rules (normalized titles, case-insensitive, trimmed).
4) Chunked extraction of tasks (source)
	 - For each section in `firestickReactTasks.md`:
		 - Read only that section’s lines (based on the map) to extract task bullets/checkbox items.
		 - Build a canonical task list: `[sectionKey] → [normalized task strings]`.
5) Chunked extraction of tasks (target)
	 - For each corresponding section in `tasksDev2.md` (matched by anchors from Step 3):
		 - Read just that section.
		 - Build an existing task set for quick membership checks.
6) Compare per-section
	 - For each `sectionKey`:
		 - Missing = `sourceTasks − targetTasks` (normalized string compare).
		 - Extra (optional) = `targetTasks − sourceTasks` (log only; do not remove unless instructed).
7) Decide insertion points
	 - Within each matched section in `tasksDev2.md`:
		 - Maintain original ordering: insert missing tasks after the closest preceding related task if found.
		 - If section exists but no obvious neighbor, append missing tasks at the end of that section.
		 - If section doesn’t exist, create the section in the correct hierarchical position using the source outline.
8) Apply updates incrementally
	 - For each affected section:
		 - Edit only the section range and insert missing tasks with correct checkbox markers (default `[ ]` unless your standard requires otherwise).
		 - Preserve formatting (indent, list symbols, numbering).
		 - Save after each section to avoid large diffs.
9) Validate formatting
	 - Re-scan updated sections to ensure:
		 - Headings render correctly.
		 - List indentation is consistent.
		 - No duplicate tasks introduced (normalize and de-dup).
10) Spot checks (token-light)
		- Randomly pick 3 sections:
			- Confirm all source tasks are present in target.
			- Confirm relative order makes sense.
11) Update logs and status
		- Captain’s Log: add entry with plan, actions, outcomes, and any blockers.
		- Mark the specific Dev2 task as `[V]/[X]` per TheRules in `tools/work/dev2/tasksDEV2.md` (if applicable).
		- If you maintain Dev2 summaries, update them via the existing scripts.
12) Optional automation hooks
		- If a script exists (e.g., `update-task-summary.ps1`), run it to refresh summaries.
		- Keep commits small: one per section or logical group.

## Done criteria
- Every task from `firestickReactTasks.md` appears in `tasksDev2.md` in the correct section and format.
- No duplication or formatting regressions.
- Logs and task status updated.

## Quick tests
- Test 1: Pick a known section from `firestickReactTasks.md`; confirm all its tasks exist verbatim in `tasksDev2.md`.
- Test 2: Search for 2–3 unique task phrases from different sections in `tasksDev2.md`; all found.
- Test 3: Lint headings/lists visually; no broken Markdown.

## VS Code search helpers
- Find headings (low token):
	- Source: `file:firestickReactTasks.md ^#{1,3}\s`
	- Target: `file:tasksDev2.md ^#{1,3}\s`
- Validate specific tasks exist:
	- `file:tasksDev2.md <unique task phrase>`