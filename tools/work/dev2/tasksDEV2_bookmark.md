## Dev2 Tasks Bookmark (minimal)

Use the resolver script for a one-step jump back to the last work area.

- Primary file: `tools/work/dev2/tasksDEV2.md`
- JSON config: `tools/work/dev2/tasksDEV2_bookmark.json`
- Jump script: `tools/work/dev2/scripts/dev2-resume.ps1`

Quick usage (opens in VS Code if CLI is available):

PowerShell:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File tools/work/dev2/scripts/dev2-resume.ps1 -OpenInVSCode
```

If VS Code CLI isn’t on PATH, the script prints a `path:line` you can paste into Ctrl+P.

