# CodeTalker – Technical Architecture and Package Usage

Date: 2025-11-05
Owner: CodeTalker Team
Audience: Security Reviewers and Technical Leads

Purpose
- Explain how CodeTalker is built and how specific pinned Python packages are used in a controlled, offline way.
- Provide the rationale to mirror selected packages into the internal Nexus.

## 1) Application architecture (concise)
- Core runtime: Java 21, Spring Boot 3.5.x.
- Analysis/Indexing:
  - JavaParser (AST), JGraphT (graphs), Lucene (BM25 keyword index), H2 (metadata).
  - ONNX Runtime executes a local embedding model (sentence-transformers variant exported to ONNX) to embed code chunks.
  - Chroma vector DB stores embeddings locally; accessed from Java via REST.
- UI: Browser-based; code viewer (Monaco), graphs (D3/Cytoscape).
- All assets (models, indexes, DB files) are stored on the local machine.

## 2) Role of the Python toolchain
The Python toolchain is used for developer workflows and packaging, not as a production service:
- Managing tokenizer/config and exporting/validating ONNX models.
- One-off or CI smoke tests against onnxruntime/Chroma.
- Utilities for caching and parallel preprocessing when preparing embeddings or test fixtures.

The Java application remains the primary runtime for end users. Python is scoped to tooling and validation.

## 3) Requested Nexus packages – rationale and usage

### transformers==4.55.4
- Purpose in CodeTalker:
  - Load and manage Hugging Face tokenizer/config artifacts that correspond to the local embedding model.
  - Facilitate model export flows (via the `optimum` toolchain) and compatibility checks for ONNX.
- How it is used:
  - Local, offline operations against tokenizer.json, vocab files, and config stored under `models/`.
  - No calls to external model hubs are required by our scripts; downloads are disabled in CI and developer guidance.
- Why this version:
  - Compatible with `tokenizers==0.21.x` and the current sentence-transformers stack in the repo.
  - Stable API surface for ONNX export/validation workflows documented in plans.

### torch==2.9.0 (CPU)
- Purpose in CodeTalker:
  - Backend for `transformers` during conversion/export and for parity checks against ONNX outputs.
  - Enables local, CPU-only validation of embeddings for deterministic tests.
- How it is used:
  - In isolated developer/CI environments; not required for the Spring Boot runtime.
  - No GPU requirement; CPU wheels suffice for our smoke tests and exports.
- Why this version:
  - Matches the pinned ecosystem (tokenizers/transformers) and has available wheels for Python 3.12 on supported platforms.

### joblib==1.5.2
- Purpose in CodeTalker:
  - Caching and light-weight parallelism for preprocessing (e.g., tokenization batches) and for repeated test fixtures.
- How it is used:
  - Local scripts optionally cache intermediate artifacts (e.g., tokenized text) to avoid recomputation.
  - No network use; caches live under the repo’s local directories.
- Why this version:
  - Widely compatible with Python 3.12; stable, minimal transitive impact.

### onnx==1.19.1
- Purpose in CodeTalker:
  - ONNX model tooling for graph inspection, opset checks, and conversion pipelines.
  - Complements `onnxruntime==1.23.2` (runtime executor) which is separately pinned for actual inference.
- How it is used:
  - Developer/CI validation of exported models; ensuring compatibility with onnxruntime and our ONNX graph expectations.
  - No external calls; all artifacts are local files under `models/`.
- Why this version:
  - Aligns with onnxruntime 1.23.x and the typical opset compatibility matrix used in our plans.

Related, already-pinned packages (informational)
- onnxruntime==1.23.2 – executes ONNX models locally; used for Python smoke tests and (separately) the Java runtime via its Java bindings.
- sentence-transformers==5.1.1, optimum==2.0.0, tokenizers==0.21.4 – part of the local embedding/model toolchain; no external services required.

## 4) End-to-end flow – where the packages appear

1) Chunking (Java):
   - CodeChunkingService prepares semantically coherent units (method/class/file/module) with metadata.
2) Embedding (Java/ONNX Runtime):
   - Java service loads an ONNX model and generates embeddings locally.
3) Optional developer validation (Python):
   - transformers + torch load the original model/tokenizer to compare small sample embeddings vs ONNX outputs.
   - onnx is used to inspect or tweak the exported ONNX graphs if needed.
   - joblib caches tokenization or validation outputs for repeatable CI checks.
4) Storage and Search (Java):
   - Embeddings → Chroma (vector DB); text fields → Lucene; metadata/graph → H2.

No steps require external API calls; all models and data are local.

## 5) Security posture and Nexus approval notes

Controls we follow
- Version pinning: exact versions are pinned in `requirements.txt` (and mirrored to internal Nexus) to enable deterministic builds.
- Provenance: consume only from the approved private Nexus registry after mirroring; public PyPI is not used in CI or packaging.
- Offline operation: tooling and the application run without internet access; models and data are local artifacts.
- Least footprint: the Python toolchain is restricted to developer/CI workflows; end users run the Java application only.

Recommendations (we will adopt these in the team’s process)
- Mirror source distributions and wheels for the four requested packages (and any required transitive wheels) to the private Nexus, with retention of exact SHAs.
- Enforce installation via `pip --index-url <nexus> --require-hashes -r requirements.txt` in CI to pin artifacts by hash.
- Maintain an SBOM and monitor upstream disclosures; update pins when a vulnerability is disclosed.
- Gate model downloads: tokenizer/config and ONNX model files are committed or bundled; scripts error out if a download would occur.

Network behavior
- None of the requested packages require network access for our workflows. Any optional telemetry or hub access is disabled.

Scope of approval requested
- Approve mirroring of:
  - transformers==4.55.4
  - torch==2.9.0
  - joblib==1.5.2
  - onnx==1.19.1
- Acknowledge that onnxruntime==1.23.2 and tokenizers==0.21.4 are related dependencies pinned in the same toolchain and may need mirroring if not already present.

## 6) Operational notes (Windows-focused)
- The repo provides PowerShell scripts to create/activate a local venv and install only from Nexus.
- Typical developer flow:
  ```powershell
  # (from repo root, behind the corporate proxy)
  py -3.12 -m venv .venv; & .\.venv\Scripts\Activate.ps1;
  python -m pip install -U pip setuptools wheel;
  pip install --index-url <YOUR_NEXUS_SIMPLE_URL> --require-hashes -r requirements.txt
  ```
- CI performs the same steps and runs a small smoke check to import these packages and perform one local inference.

## 7) Acceptance criteria for security approval (what we will provide)
- Pinned requirements with hashes (upon mirroring) and a short SBOM excerpt listing direct deps.
- A smoke test log showing no network access and successful local operation.
- Documentation in `tools/plans/` and `README.md` describing offline modes and no data egress.

With these controls, mirroring the specified packages into Nexus enables secure, reproducible, offline operation of CodeTalker without expanding the attack surface or creating external service dependencies.

 
