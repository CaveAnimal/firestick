## 2025-10-22 – Python 3.12 migration one-liner failed (missing winget)

Context
- Goal: Migrate project venv from Python 3.14 to 3.12 with a single PowerShell command.

Observed error (from terminal)
```
Python 3.14.0
winget : The term 'winget' is not recognized as the name of a cmdlet, function, script file, or operable program. Check the spelling of the name, or if a path was included, verify that the path is correct and try again.
CategoryInfo          : ObjectNotFound: (winget:String) [], ParentContainsErrorRecordException
FullyQualifiedErrorId : CommandNotFoundException
```

Root cause
- Windows App Installer (winget) is not installed/enabled or not on PATH on this machine, so the one-liner aborted when `$ErrorActionPreference='Stop'` was set.

Impact/State after failure
- requirements.txt was exported from the 3.14 venv.
- Python 3.12 was not installed; the venv was not recreated; dependencies were not reinstalled.

Lessons
- Do not assume `winget` availability on Windows hosts. Provide a fallback path (python.org installer, Chocolatey, or manual download) when scripting environment setup.

Next steps (actionable)
- Install Python 3.12 via one of:
  - Download MSI/EXE from https://www.python.org/downloads/release/python-312*/ and install (ensure "Add python.exe to PATH").
  - Or install App Installer (winget) from Microsoft Store, then rerun the scripted migration.
  - Or use Chocolatey: `choco install python312` (if Chocolatey is available).
- Re-run the venv recreation and dependency install using Python 3.12.

## 2025-10-22 – Repo scan one-liner failed (PowerShell paste contained HTML markup)

Context
- Goal: Scan the repo (esp. `tools/plans`) for Python 3.14 pins and risky pip usage.

Observed error (from terminal)
```
Get-ChildItem -Path . -Recurse -File | Where-Object { <span>_.Name -match '\\.(ps1|psm1|bat|cmd|sh|py|yml|yaml|toml|cfg|ini|md|txt)</span>' } | Select-String -Pattern @('py -3.14','python3.14','python-version: 3.14','python-version: ''3.14''','python-version: "3.14"','pip install ','python -m pip','distutils',' imp ') -AllMatches | ForEach-Object { "{0}:{1}: {2}" -f <span>_.Path,</span>.LineNumber, $.Line.Trim() }
At line:1 char:368
+ ... ls',' imp ') -AllMatches | ForEach-Object { "{0}:{1}: {2}" -f <span>_ ...
+                                                                  ~
You must provide a value expression following the '-f' operator.
At line:1 char:369
+ ... ',' imp ') -AllMatches | ForEach-Object { "{0}:{1}: {2}" -f <span>_.P ...
+                                                                 ~
The '<' operator is reserved for future use.
At line:1 char:381
+ ... AllMatches | ForEach-Object { "{0}:{1}: {2}" -f <span>_.Path,</span>. ...
+                                                                 ~
Unexpected token ',' in expression or statement.
At line:1 char:382
+ ... AllMatches | ForEach-Object { "{0}:{1}: {2}" -f <span>_.Path,</span>. ...
+                                                                  ~
Missing expression after unary operator ','.
At line:1 char:382
+ ... llMatches | ForEach-Object { "{0}:{1}: {2}" -f <span>_.Path,</span>.L ...
+                                                                 ~
The '<' operator is reserved for future use.
At line:1 char:400
+ ... h-Object { "{0}:{1}: {2}" -f <span>_.Path,</span>.LineNumber, $.Line. ...
+                                                                 ~
Unexpected token ',' in expression or statement.
At line:1 char:401
+ ... h-Object { "{0}:{1}: {2}" -f <span>_.Path,</span>.LineNumber, $.Line. ...
+                                                                  ~
Missing expression after unary operator ','.
At line:1 char:402
+ ...  { "{0}:{1}: {2}" -f <span>_.Path,</span>.LineNumber, $.Line.Trim() }
+                                                           ~~~~~~~~~~~
Unexpected token '$.Line.Trim' in expression or statement.
At line:1 char:414
+ ...  { "{0}:{1}: {2}" -f <span>_.Path,</span>.LineNumber, $.Line.Trim() }
+                                                                       ~
An expression was expected after '('.
CategoryInfo          : ParserError: (:) [], ParentContainsErrorRecordException
FullyQualifiedErrorId : ExpectedValueExpression
```

Root cause
- The pasted command contained HTML/markup tags (e.g., `<span>…</span>`), corrupting PowerShell tokens like `$_`, `$_ .Path`, and format args, which led to parse errors.

Impact/State after failure
- The scan did not run; no results were produced.

Remediation (use this corrected command)
```
Get-ChildItem -Path . -Recurse -File |
  Where-Object { $_.Name -match '\\.(ps1|psm1|bat|cmd|sh|py|yml|yaml|toml|cfg|ini|md|txt)$' } |
  Select-String -Pattern @(
    'py -3.14',
    'python3.14',
    'python-version: 3.14',
    'python-version: ''3.14''',
    'python-version: "3.14"',
    'pip install ',
    'python -m pip',
    'distutils',
    ' imp '
  ) -AllMatches |
  ForEach-Object { '{0}:{1}: {2}' -f $_.Path, $_.LineNumber, $_.Line.Trim() }
```

Notes
- Avoid copying commands that include hidden HTML/markdown spans. Prefer plain-text code blocks or retype symbols like `$_` manually in PowerShell.

## 2025-10-22 – Repo scan retry failed again (hidden HTML in pasted command)

Context
- Retried the repo scan one-liner in PowerShell to find Python 3.14 pins and risky pip usage.

Observed error (from terminal)
```
Get-ChildItem -Path . -Recurse -File | Where-Object { <span>_.Name -match '\\.(ps1|psm1|bat|cmd|sh|py|yml|yaml|toml|cfg|ini|md|txt)</span>' } | Select-String -Pattern @('py -3.14','python3.14','python-version: 3.14','python-version: ''3.14''','python-version: "3.14"','pip install ','python -m pip','distutils',' imp ') -AllMatches | ForEach-Object { '{0}:{1}: {2}' -f <span>_.Path,</span>.LineNumber, $.Line.Trim() }
... (same parser errors as previous incident) ...
```

Root cause
- The clipboard content again included HTML/markdown spans (e.g., `<span>…</span>`), corrupting PowerShell tokens during paste, causing parse errors.

Impact
- The scan did not execute; no results captured.

Mitigation implemented
- Added a safe script to the repo to avoid copy/paste issues: `tools/plans/scripts/scan_python_pins.ps1`.
  - Run it from the repo root: `powershell -ExecutionPolicy Bypass -File .\tools\plans\scripts\scan_python_pins.ps1`

Notes
- When pasting into the terminal, use plain-text paste. If in doubt, type `$_` manually rather than paste from formatted sources.

## 2025-10-22 – .venv search returned no output (Select-String pattern / SimpleMatch misuse)

Context
- Goal: Confirm `.venv/` was added to `.gitignore` by searching the file with PowerShell's `Select-String`.

Observed command (user):
```
Select-String -Path .gitignore -Pattern '\.venv' -SimpleMatch
```
Observed behavior
- Command produced no output despite `.venv/` being present in the file.

Root cause
- `-SimpleMatch` treats the pattern as a literal string. Using `\.` (a backslash then a dot) searches for a literal backslash character followed by a dot, which does not exist in the `.gitignore` file. In other words, the pasted pattern contained an escaped dot intended for a regex, but with `-SimpleMatch` the backslash becomes part of the literal search and mismatches the file contents.

Impact
- The verification step incorrectly reported nothing found which caused confusion about whether the `.gitignore` edit actually applied.

Remediation / Correct commands
- Use a plain literal match for the text that actually appears in the file:

PowerShell (one-liner to run from repo root):

    Select-String -Path .gitignore -Pattern '.venv/' -SimpleMatch

- Or use a regex (and escape the dot) without `-SimpleMatch`:

    Select-String -Path .gitignore -Pattern '\.venv/'

Notes / Prevent recurrence
- When using `-SimpleMatch` paste plain text (no regex escapes). If you copy/paste from formatted sources, retype characters like `.` or `$_` to avoid hidden escapes or markup.
- Prefer the exact literal string that appears in the file (e.g., `.venv/`) when verifying `.gitignore` entries.

## 2025-10-22 – Single-line PowerShell import-check (paste-safe)

Context
- Goal: Provide a single-line PowerShell command that developers can paste into their prompt (including locked-down terminals) to verify critical Python tooling packages import correctly under the active Python interpreter/venv.
- Motivation: Earlier multi-line `python -c` snippets and pasted one-liners were corrupted when copied from formatted sources (HTML spans) or executed in PowerShell where multi-line constructs caused paste/parse errors.

Observed problem
- Multi-line python -c payloads and pasted commands containing markup or backslash escapes broke in PowerShell (parse errors or incorrect literal searches). This caused repeated failures of verification steps and confusion about whether the venv/config changes applied.

Remediation / Single-line command (paste this exact line into PowerShell from repo root; your venv may be active already):

    $failed=0; try { python -c "import chromadb"; Write-Output 'chromadb: OK' } catch { Write-Output "chromadb: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import onnxruntime"; Write-Output 'onnxruntime: OK' } catch { Write-Output "onnxruntime: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import hnswlib"; Write-Output 'hnswlib: OK' } catch { Write-Output "hnswlib: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import faiss"; Write-Output 'faiss: OK' } catch { Write-Output "faiss: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import torch"; Write-Output 'torch: OK' } catch { Write-Output "torch: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import sentence_transformers"; Write-Output 'sentence-transformers: OK' } catch { Write-Output "sentence-transformers: FAIL -> $($_.Exception.Message)"; $failed=1 }; if ($failed -ne 0) { exit 2 } else { exit 0 }

Notes
- This command is single-line and PowerShell-friendly. It uses separate `try { ... } catch { ... }` blocks per package so a failure in one package doesn't abort the whole line parsing. The final exit code is 0 on full success, 2 when any package failed.
- Use the active `python` in your session (activate your `.venv` first if needed). If you prefer `py -3.12` or `python3.12`, replace the `python` tokens in the line accordingly.
- When pasting commands from docs or web pages into PowerShell, prefer single-line commands or dedicated scripts in `tools/scripts/` to avoid hidden HTML markup and paste corruption.

Example: to run (single-step) after activating `.venv` in PowerShell

    & '.\.venv\Scripts\Activate.ps1'; <paste-the-above-single-line-command>

## 2025-10-22 – Paste contained HTML markup causing PowerShell '<' token error

Context
- Attempted to run the single-line PowerShell import-check while the repo's venv prompt was active. The pasted command had embedded HTML/markup spans (e.g. `<span>...</span>`), which injected literal `<` characters into the command stream.

Observed (corrupted) paste (user transcript):
```
(.venv) PS E:\MyProjects\MyGitHubCopilot\firestick\fstk-001> <span>failed=0; try { python -c "import chromadb"; Write-Output 'chromadb: OK' } catch { Write-Output "chromadb: FAIL -></span>(<span>_.Exception.Message)";</span>failed=1 }; try { python -c "import onnxruntime"; Write-Output 'onnxruntime: OK' } catch { Write-Output "onnxruntime: FAIL -> <span>(</span>.Exception.Message)"; <span>failed=1 }; ... if (</span>failed -ne 0) { exit 2 } else { exit 0 }
```

Observed PowerShell error
```
The term '<' is not recognized as the name of a cmdlet, function, script file, or operable program.
At line:1 char:1
+ <span>failed=0; try { python -c "import chromadb"; Write-Output 'chro ...
+ ~
    + CategoryInfo          : ObjectNotFound: (<:String) [], ParentContainsErrorRecordException
    + FullyQualifiedErrorId : CommandNotFoundException
```

Root cause
- The clipboard content contained HTML/markdown spans which turned into literal `<` tokens when pasted into PowerShell. PowerShell attempted to interpret `<span>` as a command, producing the "term '<' is not recognized" error. Hidden or formatted markup in copied commands is a recurring source of parse failures in PowerShell.

Impact
- The verification command never executed. This blocked the import-check step and caused confusion about whether the venv/tools installation succeeded.

Remediation (short)
- Never paste commands copied from formatted/HTML sources directly into PowerShell without verifying there's no markup. Prefer one of these safe options:
  1. Use the single-line PowerShell command (paste-safe) provided below.
  2. Save the intended command into a UTF-8 text script file (e.g., `tools/scripts/check_wheels.ps1`) and run the script with `powershell -ExecutionPolicy Bypass -File .\tools\scripts\check_wheels.ps1`.
  3. If pasting, inspect the pasted text before pressing Enter (or paste into Notepad first to strip markup).

Safe single-line PowerShell command (paste this exact line into PowerShell after activating your venv; replace `python` with `py -3.12` if desired):

    $failed=0; try { python -c "import chromadb"; Write-Output 'chromadb: OK' } catch { Write-Output "chromadb: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import onnxruntime"; Write-Output 'onnxruntime: OK' } catch { Write-Output "onnxruntime: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import hnswlib"; Write-Output 'hnswlib: OK' } catch { Write-Output "hnswlib: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import faiss"; Write-Output 'faiss: OK' } catch { Write-Output "faiss: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import torch"; Write-Output 'torch: OK' } catch { Write-Output "torch: FAIL -> $($_.Exception.Message)"; $failed=1 }; try { python -c "import sentence_transformers"; Write-Output 'sentence-transformers: OK' } catch { Write-Output "sentence-transformers: FAIL -> $($_.Exception.Message)"; $failed=1 }; if ($failed -ne 0) { exit 2 } else { exit 0 }

Notes
- This entry records the exact failure mode so future devs learn to avoid pasting HTML-annotated commands into PowerShell. Consider adding `tools/scripts/check_wheels.ps1` to the repo (uncommitted until approved) to provide a paste-free test helper.
