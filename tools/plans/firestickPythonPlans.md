# Firestick — Python 3.12 Integration & Migration Plans

Date: 2025-10-22

Purpose
- Capture specific, actionable changes to the PRD, Planning, Tasks, and Examples so that using Python 3.12 for auxiliary tooling (local Chroma server, model conversion/management, developer scripts, CI helpers) becomes an explicit, supported part of the project. This file lists recommended edits, new tasks, scripts to add, and a short migration recipe.

Assumptions
- The core application remains Java 21 / Spring Boot — we are only standardizing the Python runtime used by supporting tooling (Chroma, embedding tooling, local tests, helper scripts).
- Target Python runtime for these tools: CPython 3.12 (stable, good wheel support). Avoid Python 3.14 for now because of wheel availability.
- Platform targets: Windows (primary), macOS, Linux.

Checklist (what I'll add to the repo / what you should follow)
- [ ] Update `firestickPRD.md` to declare Python 3.12 as the supported tooling runtime and note the roles Python plays.
- [ ] Update `firestickPLANNING.md` to add a small workstream for Python toolchain stabilization (install scripts, venv management, pinned requirements, CI matrix).
- [ ] Update `firestickTASKS.md` with concrete single-line tasks to create/commit env scripts and pin compatible package versions.
- [ ] Add small helper scripts under `tools/scripts/`:
  - `prepare_python_env.ps1` (Windows) and `prepare_python_env.sh` (POSIX)
  - `freeze_python_reqs.sh` (to refreeze `tools/requirements-python.txt`) or a PowerShell equivalent
  - `check_python_wheels.py` (optional) to validate wheel compatibility for target Python
- [ ] Add `tools/requirements-python.txt` containing pinned packages known to work on Python 3.12; document how to regenerate safely.
- [ ] Add CI updates (GitHub Actions / Windows runner) to include a Python 3.12 job for the tooling tests.
- [ ] Document migration steps (capture current requirements, install Python 3.12, recreate venv, reinstall, test imports, refreeze requirements, commit).

PRD changes (high level edits to `firestickPRD.md`)
- Add a short section under "Dependencies & Assumptions" and "Implementation Phases":
  - State that Python 3.12 is the supported runtime for local tooling used by the project: Chroma server, model conversion scripts, embedding pre-processing, packaging helpers, and developer utility scripts.
  - Clarify that these Python components are separate from the Java application and will be packaged or documented for end-users only when required (e.g., bundling Chroma server or prebuilt embeddings).
- Add to Non-Functional Requirements (NFR):
  - Tooling reproducibility: Provide pinned Python tooling requirements (`tools/requirements-python.txt`) and setup scripts so developer environments remain consistent.
- Rationale note: explain that many ML/wheels are packaged for CPython up to 3.12 at the moment; using 3.12 reduces friction on Windows for packages like onnxruntime, faiss, torch, hnswlib, optimum.

Planning changes (concrete edits to `firestickPLANNING.md`)
- Add a small "Python Tooling Stabilization" line item in the Phase 1/2 timeframe (Week 3-4). Example tasks to add to Sprint 1/2:
  - Week X: Standardize Python runtime to 3.12 for tooling; create `tools/requirements-python.txt` and OS-aware install scripts (PowerShell + Bash).
  - Add 1 day to Phase 2 to test and verify commonly used wheels (onnxruntime, torch, faiss-cpu, hnswlib) work under Windows Python 3.12; if any incompatible package remains, record guidance (e.g., use Conda or system binaries).
- CI: Add a short task to add a GitHub Actions job that runs on windows-latest / ubuntu-latest using Python 3.12 to run the tooling smoke tests (import check, pip freeze + --require-hashes optional).
- Risk register: add an explicit entry "Python tooling version mismatch" with mitigation: pin versions, provide instructions for alternative installs (conda), validate wheels early.

Tasks changes (what to add to `firestickTASKS.md`)
- Add a small set of focused tasks (one action per task):
  - `tools:standardize-python-venv` — create `tools/scripts/prepare_python_env.*` scripts, add instructions to README.
  - `tools:create-python-reqs` — produce `tools/requirements-python.txt` from a verified venv and commit it.
  - `tools:add-python-ci` — add GitHub Actions job to test Python 3.12 toolchain (simple import tests + pip install -r tools/requirements-python.txt).
  - `tools:chroma-embed-tests` — run smoke tests: chroma (pip), onnxruntime import, optimum/onnx import, faiss/hnswlib import, torch import (CPU).
  - `tools:refreeze-on-migration` — script + instructions to "unfreeze & refreeze" requirements when switching Python runtimes.
  - `tools:document-python-migration` — add step-by-step migration guide to repo (this file acts as it for now).

Examples / documentation edits (`firestickEXAMPLES.md`)
- Add an "Environment and Tooling" subsection showing exact commands to:
  - Export current python requirements from a venv (PowerShell & Bash) safely.
  - Install Python 3.12 using OS-appropriate instructions; include fallback if winget not available (direct python.org link, Chocolatey, or manual install). Example (PowerShell, Windows):
    - Check current python version: `python --version` or `py -3.12 --version`.
    - Recreate venv: `py -3.12 -m venv .venv` then `& .\.venv\Scripts\Activate.ps1` then `python -m pip install -U pip setuptools wheel` then `pip install -r tools/requirements-python.txt`.
  - Validate imports with a single command that checks key packages (onnxruntime, chromadb, hnswlib, faiss, torch, sentence-transformers). Provide a single-line Python snippet example for Windows PowerShell (escape properly) and POSIX shell.
- Provide a clear example of how to capture and commit new pinned requirements after a verified install: `pip freeze > tools/requirements-python.txt`.
- Add a short PowerShell example that avoids problems we saw (no HTML spam, correct Select-String syntax) for file scanning and for venv lifecycle.

Concrete scripts to add (recommended filenames & purpose)
- `tools/scripts/prepare_python_env.ps1` (Windows PowerShell, idempotent)
  - Detect installed py launcher or python; prefer `py -3.12`.
  - If Python 3.12 not present, print instructions and an actionable link; do NOT attempt to install system Python automatically without consent.
  - Create `.venv` in repo root, activate, upgrade pip/setuptools/wheel, install `-r tools/requirements-python.txt`.
  - Run smoke import checks and print a summary exit code.
- `tools/scripts/prepare_python_env.sh` (POSIX)
  - Same as above but uses `python3.12` or `python3`.
- `tools/scripts/refreeze_requirements.ps1` and `.sh`
  - Activate venv, run `pip freeze > tools/requirements-python.txt` and optionally `pip check`.
- `tools/scripts/check_wheels.py`
  - Small script to import a list of critical packages and print OK/FAIL lines. Useful for CI job.

tools/requirements-python.txt (recommended contents / approach)
- Pin minimally, prefer versions known to work on Python 3.12 on Windows, e.g. (example; update/refreeze after verification):
  - chromadb==1.2.1
  - onnxruntime==1.23.2
  - optimum==2.0.0
  - tokenizers==0.21.4
  - transformers==4.55.4
  - sentence-transformers==5.1.1
  - faiss-cpu==1.12.0    # if available via pip on target platform; if not, document Conda alternative
  - hnswlib==0.8.0
  - torch==2.9.0         # CPU-only wheel availability differs by platform: verify
  - numpy==2.3.4
- Note: some packages (faiss, torch) vary by platform and installer; document Conad/whl alternatives; prefer `pip` where wheels exist for Python 3.12.

Migration recipe (one concise sequence you can run)
- Capture current venv state (if you have a venv you want to preserve):
  - Windows PowerShell (from repo):
    - `pip freeze > tools/requirements-before-migration.txt`
- Install Python 3.12 (manual if winget missing). Ensure `py -3.12` or `python3.12` is available.
- Remove or move existing `.venv` folder (or create new `.venv-python312`).
- Create new venv with Python 3.12:
  - `py -3.12 -m venv .venv` (Windows) or `python3.12 -m venv .venv` (POSIX)
- Activate and prepare:
  - `& .\.venv\Scripts\Activate.ps1` (PowerShell)
  - `python -m pip install -U pip setuptools wheel`
- Install pinned tooling deps:
  - `pip install -r tools/requirements-python.txt`
- Run smoke import checks (scripts/check_wheels.py) and any sample commands to ensure runtime ok.
- If all good: `pip freeze > tools/requirements-python.txt` (refreeze) and commit.

Notes about refreeze vs unfreeze
- When switching Python runtimes you may need to "refreeze" because binary wheels are platform+python-version-specific. The steps above record the workflow: capture old requirements, recreate venv with Python 3.12, reinstall, run smoke tests, then `pip freeze` to update `tools/requirements-python.txt`.
- For reproducibility prefer `pip-tools` or use a constraints file with `--require-hashes` in future; for now a pinned `requirements-python.txt` is acceptable.

CI changes
- Add a GitHub Actions job under `.github/workflows/ci-python-tools.yml` (or add to existing) that:
  - Runs on windows-latest and ubuntu-latest using `actions/setup-python@v4` with `python-version: '3.12'`
  - Creates and activates venv, installs `-r tools/requirements-python.txt`, runs `tools/scripts/check_wheels.py`, and exits non-zero on failures.
  - Optionally caches `~/.cache/pip` to speed installs.

Examples to include in `tools/plans/firestickPythonPlans.md` (short snippets to paste)
- PowerShell create venv with Python 3.12 (single-step):
  - `py -3.12 -m venv .venv; & .\.venv\Scripts\Activate.ps1; python -m pip install -U pip setuptools wheel; pip install -r tools/requirements-python.txt; python tools/scripts/check_wheels.py`
- POSIX create venv:
  - `python3.12 -m venv .venv && source .venv/bin/activate && python -m pip install -U pip setuptools wheel && pip install -r tools/requirements-python.txt && python tools/scripts/check_wheels.py`
- Validation snippet (Python) to check key imports:
  - `python -c "import importlib,sys

pkgs=['chromadb','onnxruntime','hnswlib','faiss','torch','sentence_transformers']
for p in pkgs:
    try:
        importlib.import_module(p)
        print(p+': OK')
    except Exception as e:
        print(p+': FAIL ->', e); sys.exit(2)"
` (wrap appropriately for Windows/PowerShell)

Risk & mitigation
- faiss / torch binary availability: if pip wheels are not available for Windows+3.12, document Conda/Conda-Forge workflows and keep those instructions in `tools/README-python.md`.
- GPU vs CPU: default to CPU-only installs in tooling requirements; make GPU optional with separate documented steps.
- Diverse dev environments: include a `tools/scripts/check_python_version.ps1` that fails with helpful instructions if wrong Python detected.

Verification & Acceptance (minimal tests to run after changes)
- `tools/scripts/prepare_python_env.*` runs successfully and returns 0.
- `tools/scripts/check_wheels.py` prints OK for required packages.
- CI Python 3.12 job passes on windows-latest and ubuntu-latest.
- `pip freeze > tools/requirements-python.txt` after verified install is committed.

Next steps I can take for you (pick one):
- I can create the helper scripts and add a starter `tools/requirements-python.txt` with conservative pins.
- I can add a CI job YAML (draft) to `.github/workflows` for Python 3.12 tooling checks.
- I can open PR-style edits to `firestickPRD.md`, `firestickPLANNING.md`, and `firestickTASKS.md` with the short lines suggested above.

If you want me to proceed with any of the next steps, tell me which single step and I'll perform it now.

