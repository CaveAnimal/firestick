# CodeTalker – Executive Summary (for Security Review)

Date: 2025-11-05
Owner: CodeTalker Team

Purpose of this document
- Provide a concise, non-technical summary of what CodeTalker does and why it exists.
- Support the security request to mirror/pin specific Python packages in the internal Nexus to enable offline development and packaging.

What CodeTalker is
- CodeTalker is a standalone, offline desktop web application that helps engineers explore large legacy Java codebases (1M+ lines) quickly and safely, without sending source code to external services.
- It combines static analysis (AST parsing), dependency graphing, full‑text search, and semantic (vector) search to let users find relevant code, understand dependencies, and assess change impact.

What problems it solves
- Locating business logic across very large projects is slow with keyword-only tools.
- Understanding cross-class/method dependencies is tedious and error-prone.
- Legacy code often lacks documentation; developers need fast structural and semantic context.

How it delivers value
- Hybrid search (semantic + keyword) surfaces relevant code within seconds.
- Dependency graphs and call hierarchies show callers/callees and architectural relationships.
- Everything runs locally: no external APIs, accounts, or data egress.

Key capabilities (from current plans)
- Code indexing pipeline: discover → parse → chunk → embed → store.
- Semantic search backed by a local vector DB (Chroma) and ONNX embeddings.
- Keyword search via Apache Lucene; graph analysis via JGraphT; parsing via JavaParser.
- Embedded H2 database for metadata; all artifacts remain on the local machine.

Why we request Nexus mirroring of specific Python packages
- The project uses a small, pinned Python toolchain for local ML utilities and developer tooling (e.g., tokenizer/model management, ONNX conversion checks, running or validating local embedding models, and operating a local Chroma server).
- Mirroring the exact versions into our private Nexus gives us:
  - Reproducibility: consistent, deterministic environments across machines/CI.
  - Speed and reliability: faster installs without reaching public registries.
  - Control: centralized approval with the ability to block or update vulnerable versions.

Packages requested for Nexus mirror (pinned)
- transformers==4.55.4
- torch==2.9.0
- joblib==1.5.2
- onnx==1.19.1

Assurances
- All usage is local/offline; no telemetry or external API calls are required by CodeTalker to function.
- Versions are pinned to reduce supply chain risk and to support SBOM generation and vulnerability monitoring.
- The team will monitor upstream advisories and update pins when necessary.

Requested decision
- Approve mirroring of the four packages above (and closely related runtime dependencies as needed by our lockfile) into the internal Nexus repository to enable secure, offline development and packaging of CodeTalker.
