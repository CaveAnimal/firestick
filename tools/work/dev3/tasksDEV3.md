## Task Summary (DEV3)

**Total Tasks:** 7 tasks
**Completed/Tested:** 0 tasks
**In Progress:** 0 tasks  
**Blocked:** 0 tasks  
**Percent Complete:** 0%
**Last Updated:** 2025-11-24

## Current Phase — Dev3 Focus

- `[V]` Initialize Dev3 workspace (folders, scripts, captain's log)
- `[V]` Exclude legend/deferred sections from task counts (update `tools/plans/scripts/update-task-summary.ps1` and add skip markers)
- `[V]` Rescope master task list and seed Dev3 execution lane for current sprint
- `[V]` ONNX mode profile configured and model/tokenizer inventory verified
- `[V]` GlobalExceptionHandler in place with ErrorResponse.requestId contract and tests
- `[V]` OpenAPI baseline generated with drift-check test enforced in CI
- `[ ]` Add Docker/Docs for deterministic local E2E (backend + UI) to enable verification tasks
- `[ ]` Migrate a small, verifiable subset of master tasks into Dev3 (e.g., docs/tests reconciliation items), then mark completed when verified
- `[V]` Auto app name derivation method implemented and compiling (Phase 1 of app names feature)
- `[-]` App name editing UI field (Phase 1 continued)
- `[ ]` App rename endpoint with H2 + Chroma consistency (Phase 2)

## Phase 4b: CodeLlama 7B LLM Integration (NEW WORKSTREAM)

### Strategic Addition (November 13, 2025)

After architecture review, LLM integration was elevated from "Out of Scope" to Phase 4b enhancement. CodeLlama 7B selected for code-specific understanding.

**Planned Timeline:** Weeks 7-8 (Dec 2-8, 2025), ~40-48 hours effort  
**Team:** Backend Developer + DevOps (Python)  
**Status:** Planning phase complete, ready for implementation in next sprint

**Deliverables:**
- [ ] Python FastAPI microservice (llm-service/)
- [ ] CodeLlama 7B model integration
- [ ] 4 explanation endpoints (summarize, analyze-relationship, generate-docs, detect-patterns)
- [ ] Java RestTemplate client with circuit breaker
- [ ] Spring Boot LLMController integration
- [ ] End-to-end integration tests
- [ ] Complete documentation & performance benchmarks

**Why CodeLlama 7B over Mistral 7B:**
- Code-trained (better for Java code understanding)
- Smaller context overhead
- Better pattern recognition for legacy codebases
- Industry standard for code explanation tasks

**Resource Requirements:**
- 6GB RAM (model: 4-5GB, runtime: 1-2GB)
- 2-5 second response times (acceptable for UI explanations)
- Python 3.12 with transformers, torch, fastapi

**Upcoming Dev3 Tracking:**
- Allocate Phase 4b tasks to Dev3 execution lane after App Names feature completes
- Parallel development possible if team expands
- Full task breakdown in tools/plans/firestickTASKS.md (Phase 4b section)

## Notes
- Dev3 operates as the execution lane for re-scoped phase tasks to improve percent complete without touching long-term backlog yet.
- New focus area: Auto-Derived App Names feature enabling multi-app support with data isolation (3 phases, ~10-12 days total)
- **Phase 4b (LLM) queued for execution after App Names completion or in parallel if resources available**

---

## Auto-Derived App Names Feature (November 13, 2025)

### Implementation Plan Overview

Feature Goal: Auto-derive application names from folder names with override capability, rename endpoint, and search filtering.

Current Status: Phase 1 (Auto-Derivation) COMPLETE
- deriveAppNameFromPath() method implemented and compiling
- Ready for UI implementation

*Updated November 13, 2025 - App Names Implementation Plan*
