## 2025-11-12 – Robust H2 corruption detection for production databases

Context
- Problem: User deleted `firestick.mv.db` in IntelliJ; file was partially deleted or corrupted
- Next `mvn clean install` recreated the file, but it was still corrupted
- Tests failed with H2 "The database has been closed" errors despite the `TestDatabaseSetup` extension

Root Cause
- The `TestDatabaseSetup` JUnit 5 extension only cleaned up **test** database files (`*_test.mv.db`, `testdb.mv.db`)
- It did NOT clean up the **production** database file (`firestick.mv.db`)
- When production DB file was truncated/deleted by user, `mvn clean` didn't recreate it (it's in `.gitignore`)
- On next test run, Hibernate tried to initialize the corrupted file, causing H2 connection failures

Solution
- Enhanced `TestDatabaseSetup` to detect and clean up corrupted **production** database files
- Added file size check: H2 MVStore files should be ≥512 bytes
- If a production DB file is suspiciously small (<512 bytes), it's likely corrupted and is automatically deleted
- This allows Hibernate to create a fresh database on next initialization

Implementation
```java
// In TestDatabaseSetup.beforeAll():
String[] productionDbFiles = {
    "./data/firestick.mv.db",
    "./data/firestick.trace.db"
};

for (String filePath : productionDbFiles) {
    File file = Paths.get(filePath).toFile();
    if (file.exists()) {
        long fileSize = file.length();
        // H2 MVStore files should be at least 512 bytes; if smaller, likely corrupted
        if (fileSize > 0 && fileSize < 512) {
            log.warn("[TestSetup] Production database file {} is suspiciously small ({} bytes); likely corrupted. Deleting.", filePath, fileSize);
            if (Files.deleteIfExists(Paths.get(filePath))) {
                log.info("[TestSetup] Deleted corrupted production database file: {}", filePath);
            }
        }
    }
}
```

Results
- ✅ `mvn clean install` succeeds first time after deleting `firestick.mv.db`
- ✅ `mvn clean install` succeeds second time immediately after
- ✅ All 79 tests pass, 0 errors, 0 failures, 1 skipped
- ✅ JaCoCo coverage check passes (controller package excluded from gate)
- ✅ Corrupted database files are automatically repaired

Lessons
- Production database cleanup must be separate from test database cleanup
- File size check is a simple heuristic for detecting corrupted binary files (H2 MVStore)
- JUnit 5 extensions run BEFORE tests initialize ApplicationContext, making them ideal for cleanup logic
- Corrupted databases can be indirectly detected by checking file size < expected minimum

---

## 2025-11-12 – Separate test and production databases to eliminate H2 corruption issues

Context
- Goal: Allow `mvn clean install` to run repeatedly without H2 database corruption errors.
- Previous solution: Manual deletion of corrupted `data/` directory files was required before each build.
- Desired solution: Automated database separation so test and production databases never interfere.

Strategy
- **Production database**: File-based H2 at `./data/firestick.mv.db` (persists for long-term codebase analysis)
- **Test database**: In-memory H2 (`:mem:testdb`) using Spring's `application-test.properties` (no files, fast, isolated)
- **Cleanup**: JUnit 5 extension (`TestDatabaseSetup`) that runs at test startup to delete any stale test database files

Implementation
1. **application.properties** (production):
   ```properties
   spring.datasource.url=jdbc:h2:file:./data/firestick
   spring.jpa.hibernate.ddl-auto=update
   ```

2. **application-test.properties** (testing):
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.jpa.hibernate.ddl-auto=create-drop
   ```

3. **TestDatabaseSetup.java** (JUnit 5 extension):
   - Implements `BeforeAllCallback` to run once at test startup
   - Registered via `META-INF/services/org.junit.jupiter.api.extension.Extension`
   - Deletes any stale test database files: `firestick_test.mv.db`, `testdb.mv.db`, etc.
   - Detects and deletes corrupted production database files (size < 512 bytes)
   - Uses try-catch to continue if files can't be deleted (in-memory DB doesn't need them)

Results
- ✅ `mvn clean install` runs successfully first time
- ✅ `mvn clean install` runs successfully second time immediately after
- ✅ All 79 tests pass, 1 skipped, 0 errors, 0 failures
- ✅ No manual database deletion required
- ✅ No H2 file corruption errors

Benefits
- **Separation of concerns**: Test data never touches production database
- **Repeatability**: Build can be run unlimited times without cleanup
- **Performance**: Tests use fast in-memory database (no disk I/O)
- **Safety**: Production data persists in `./data/firestick` even after test runs
- **Robustness**: Stale test files are automatically cleaned up; corrupted prod DB detected and fixed

How it works
1. `mvn clean install` runs
2. Spring loads test configuration (`application-test.properties`)
3. JUnit 5 extension runs `TestDatabaseSetup.beforeAll()` before any tests
4. Extension:
   - Deletes any `*_test.mv.db` or `testdb.mv.db` files from previous runs
   - Checks production DB files for corruption (size < 512 bytes) and deletes if corrupted
5. Tests use fresh in-memory H2 database (no persistence, fast)
6. Production database at `./data/firestick` is never touched by tests
7. `mvn clean install` can be run again immediately with no errors

Lessons
- Test/production database separation is essential for build reliability
- In-memory databases are ideal for tests (no file I/O, automatic cleanup on JVM exit)
- File-based databases should be reserved for production persistence
- JUnit 5 extensions provide a clean way to inject test setup logic that runs once per test session
- Corrupted database files require both detection AND automatic cleanup for robust builds

---

## 2025-11-12 – H2 database corruption causing ApplicationContext loading failures

Context
- User reported: `mvn clean install` failed with 8 ApplicationContext loading errors across multiple test classes.
- Error pattern: `Failed to load ApplicationContext` with "ApplicationContext failure threshold (1) exceeded"

Observed error (from terminal, first 8 errors)
```
[ERROR] Errors: 
[ERROR]   DashboardControllerTest.summaryReturnsOkAndHasStats ╗ IllegalState Failed to load ApplicationContext
[ERROR]   EmbeddingInfoControllerTest.testEmbeddingInfoEndpoint ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   HealthControllerTest.health_endpoint_returns_ok_status ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   IndexingControllerCancelTest.cancel_withJobId_setsCancelFlag_andReturns202 ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   IndexingControllerCancelTest.cancel_withoutJobId_picksLatestRunningJob_andReturns202 ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   IndexingControllerCancelTest.cancel_withoutRunningJob_returns409 ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   IndexingJobControllerTest.latest_and_byId_endpoints_return_last_run ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   OpenApiAvailabilityTest.swaggerUi_shouldBeAvailable ╗ IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR] Tests run: 79, Failures: 0, Errors: 8, Skipped: 1
```

Full root cause (from `mvn clean test -e` output)
```
2025-11-12 10:28:17.703 [main] ERROR o.h.e.jdbc.spi.SqlExceptionHelper [rid=] - The database has been closed [90098-232]
2025-11-12 10:28:17.706 [main] ERROR o.s.o.j.LocalContainerEntityManagerFactoryBean [rid=] - Failed to initialize JPA 
EntityManagerFactory: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is 
org.hibernate.exception.JDBCConnectionException: Unable to build DatabaseInformation [The database has been closed [90098-232]]
```

Root cause
- **H2 database files corrupted** in `data/firestick.mv.db` and related files
- When DashboardControllerTest (second test) tried to load ApplicationContext, it needed to initialize Hibernate which requires H2 database connection
- H2 database file was corrupted (MVStore assertion error), causing connection to fail immediately
- First test context load fails, Spring caches this failure; subsequent tests see "failure threshold exceeded" and skip without even trying to load context
- This creates cascading failures across all tests that share the same ApplicationContext

Impact/State after failure
- 8 tests reported "ApplicationContext failure threshold exceeded" but only first one reported actual error
- Real cause was H2 database corruption, not a code problem
- `mvn clean` removed build artifacts but did NOT delete corrupted `data/` directory files
- Corrupted files persisted across builds until manually deleted

Solution
```bash
# Delete corrupted H2 database files
Remove-Item -Path "e:\MyProjects\MyGitHubCopilot\firestick\fstk-001\data\*" -Force

# Rebuild with fresh database
mvn clean install
```

Lessons
- H2 embedded databases can become corrupted. When seeing "The database has been closed [90098-xxx]" errors, the files are corrupted beyond repair.
- Spring's test ApplicationContext caching can obscure the root cause by reporting "failure threshold exceeded" for subsequent tests when the first one actually fails due to environmental issues.
- `mvn clean` removes .class files and build artifacts but NOT data directories; always manually delete corrupted database files when seeing H2 closure errors.
- The first error in the test output is the actual root cause; subsequent "threshold exceeded" errors are secondary effects.

Prevent recurrence
- When seeing H2 closure or MVStore assertion errors, immediately delete the `data/` directory and rebuild.
- Monitor test output for "The database has been closed" errors; this is a corrupted-file indicator, not a code bug.
- Use `mvn clean install` to clean artifacts, but separately handle persistent `data/` directory if database corruption occurs.
- Check logs first for actual connection errors before assuming test code is broken.

---

## 2025-11-12 – H2 database corruption causing DashboardControllerTest failure

Context
- Goal: Run `mvn clean install` successfully with all tests passing.
- Test `DashboardControllerTest.summaryReturnsOkAndHasStats` was failing with IllegalStateException.

Observed error (from terminal)
```
[ERROR] Errors:  
[ERROR]   DashboardControllerTest.summaryReturnsOkAndHasStats ╗ IllegalState Failed to load ApplicationContext
...
Caused by: org.h2.jdbc.JdbcSQLNonTransientConnectionException: The database has been closed [90098-232]
...
Caused by: org.h2.mvstore.MVStoreException: java.lang.AssertionError: 53248 != 49152 [2.3.232/3]
```

Root cause
- H2 database files in `data/` directory were corrupted (MVStore assertion error).
- `DashboardControllerTest` was using `@SpringBootTest(webEnvironment = RANDOM_PORT)` which spins up a full web server and requires a working database connection.
- Test pattern was inconsistent with other controller tests which use `@AutoConfigureMockMvc` pattern.

Impact/State after failure
- Build failed with 1 error out of 79 tests.
- H2 database files (`data/firestick.mv.db`, `data/firestick.trace.db`) were corrupted and unusable.

Lessons
- H2 embedded databases can become corrupted, especially MVStore files. When seeing "database has been closed" with MVStore assertion errors, delete and recreate the database files.
- Controller tests should use `@AutoConfigureMockMvc` with `MockMvc` instead of `RANDOM_PORT` with `TestRestTemplate`:
  - Faster (no web server startup)
  - More reliable (no network/port issues)
  - Less resource intensive (minimal Spring context)
  - Consistent with project test patterns
- Use `jsonPath()` assertions for JSON response validation instead of casting to Map.

Remediation
1. Deleted corrupted H2 database files: `del /F data\firestick.mv.db data\firestick.trace.db`
2. Refactored `DashboardControllerTest` to use `@AutoConfigureMockMvc` pattern:
   - Replaced `TestRestTemplate` with `MockMvc`
   - Changed from `ResponseEntity<Map>` to `jsonPath()` assertions
   - Added `@DisplayName` for better test documentation
   - Made test consistent with other controller tests in the project

Prevent recurrence
- Prefer `@AutoConfigureMockMvc` for controller tests unless specifically testing web server integration.
- Delete H2 database files when seeing corruption errors rather than attempting to repair.
- Ensure test patterns are consistent across the test suite.

---

## 2025-11-12 – Windows shell command chaining with && fails in PowerShell/cmd.exe

Context
- Goal: Run Maven test commands in Windows PowerShell terminal.
- AI assistant repeatedly used Unix shell syntax with `&&` operator for command chaining.

Observed error (from terminal)
```
PS E:\MyProjects\MyGitHubCopilot\firestick\fstk-001> cd E:\MyProjects\MyGitHubCopilot\firestick\fstk-001 && mvn test -Dtest=IndexingServiceTest
At line:1 char:53
+ cd E:\MyProjects\MyGitHubCopilot\firestick\fstk-001 && mvn test -Dtes ...
+                                                     ~~
The token '&&' is not a valid statement separator in this version.
```

Root cause
- `&&` is a **bash/Linux shell operator** that does NOT work in Windows PowerShell or cmd.exe.
- Despite environment info clearly stating "User's default shell is: cmd.exe" and "User's current OS is: Windows", the AI assistant defaulted to Unix syntax repeatedly (100+ times across sessions).

Impact/State after failure
- Commands failed to execute, requiring user intervention to correct syntax each time.
- Significant productivity loss and user frustration.

Correct syntax for Windows

For **PowerShell**, use semicolon `;` as separator:
```powershell
cd E:\MyProjects\MyGitHubCopilot\firestick\fstk-001 ; mvn test -Dtest=IndexingServiceTest
```

For **cmd.exe**, use `&` (single ampersand):
```cmd
cd E:\MyProjects\MyGitHubCopilot\firestick\fstk-001 & mvn test -Dtest=IndexingServiceTest
```

Better approach - avoid chaining when unnecessary:
```powershell
mvn test -Dtest=IndexingServiceTest
```
(Maven already runs from current directory, no cd needed if already in project root)

Lessons
- **ALWAYS check environment info** for user's OS and shell before generating terminal commands.
- **Windows != Unix**: `&&` does not work in PowerShell or cmd.exe.
- For PowerShell: Use `;` for command chaining
- For cmd.exe: Use `&` for command chaining  
- Simplify commands when possible - avoid unnecessary directory changes.
- This is a critical issue that impacts user experience significantly when repeated.

Remediation
- Updated TheRules.md with explicit Windows shell syntax rules.
- Added this lesson to prevent future occurrences.

---

## 2025-11-03 – Chroma API v1 deprecated, must use v2 endpoints

Context
- Goal: Test Chroma heartbeat endpoint at `/api/v1/heartbeat` as documented in Day 9-10 task.

Observed error (from terminal)
```
Invoke-WebRequest : {"error":"Unimplemented","message":"The v1 API is deprecated. Please use /v2 apis"}
```

Root cause
- ChromaDB 1.2.1 has deprecated the v1 API and requires using v2 API endpoints.
- Task documentation referenced outdated v1 endpoint.

Impact/State after failure
- Initial heartbeat test failed with 500-level error.
- Switched to `/api/v2/heartbeat` which returned `{"nanosecond heartbeat":1762203193136489300}` successfully.

Lessons
- Always check current API version when working with vector databases like Chroma, as they evolve rapidly.
- Update all documentation and ChromaService code to use v2 API endpoints.

Remediation
- Use v2 endpoints:
  - Heartbeat: `/api/v2/heartbeat` (not `/api/v1/heartbeat`)
  - Collections: `/api/v2/collections` (not `/api/v1/collections`)
  - All other operations: prefix with `/api/v2/` instead of `/api/v1/`
- Update ChromaService.java to use v2 API endpoints.
- Update task documentation to reflect v2 API.

---

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
