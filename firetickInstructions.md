# Firestick Instructions

## 1. Getting Started (Fast Path to Results)
This section gives you the shortest path from cloning the repo to asking questions about your legacy code *without* downloading any large model.

### Prerequisites
- Java 21 (required for Spring Boot 3.5.x)
- Maven 3.9+
- (Optional) Node.js 18+ if you want to run the UI front-end

### Step-by-Step
1. Clone the repository:
   ```powershell
   git clone <your-fork-or-repo-url>
   cd firestick/fstk-001
   ```
2. Build & start the backend (mock embeddings by default):
   ```powershell
   mvn clean package
   mvn spring-boot:run
   ```
3. Confirm health:
   ```powershell
   Invoke-RestMethod http://localhost:8080/api/health
   ```
4. Run an indexing job over a legacy project folder (replace path):
   ```powershell
   Invoke-RestMethod "http://localhost:8080/api/indexing/run?root=E:\path\to\legacy\repo&excludeDirs=.git,target&excludeGlobs=**\*.min.js"
   ```
   - You can also POST JSON:
     ```powershell
     $body = @{ rootPath = "E:\path\to\legacy\repo"; excludeDirNames = @(".git","target"); excludeGlobPatterns = @("**/*.min.js") } | ConvertTo-Json
     Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/indexing/run -Body $body -ContentType application/json
     ```
5. Search for a concept:
   ```powershell
   Invoke-RestMethod "http://localhost:8080/api/search?q=transaction%20rollback"
   ```
   - Response includes `id` values like: `E:\path\to\legacy\repo\src\main\java\com\acme\Foo.java#method:42-73`
6. Fetch exact chunk content:
   ```powershell
   Invoke-RestMethod "http://localhost:8080/api/code/chunk?docId=E:\path\to\legacy\repo\src\main\java\com\acme\Foo.java#method:42-73"
   ```
7. (Optional) Open API docs:
   - Visit: `http://localhost:8080/swagger-ui.html` to explore and invoke endpoints.

### What You Have Now
- Full pipeline: discover → parse → chunk → index (Lucene) → embed (mock) → search.
- Deterministic mock embeddings allow tests and retrieval without large downloads.

## 2. H2 vs Chroma (Databases Explained)
The application uses **two distinct storage layers** with different roles.

| Aspect | H2 (Relational) | Chroma (Vector Store) |
|--------|-----------------|------------------------|
| Purpose | Persist structural metadata: files (`CodeFile`), chunks (`CodeChunk`), indexing jobs (`IndexingJob`) | Store and query high-dimensional embeddings for semantic similarity |
| Technology | Embedded file-based RDBMS (JDBC) | External service (HTTP API) or local server with vector DB capabilities |
| Persistence Location | `./data/firestick.mv.db` (file path via `spring.datasource.url`) | Dependent on Chroma server configuration (not stored by this repo) |
| Access Pattern | CRUD via Spring Data JPA | REST calls (planned/optional) to Chroma endpoints |
| When Used | After indexing to save parsed file/chunk metadata | For advanced semantic search / RAG (future integration; current tests mock it) |
| Setup Needed | None (auto-created) | Need a running Chroma server (v2 API) |

### Current State
- Lucene in-memory index powers keyword searches now.
- Chroma integration is stubbed/mocked for pipeline tests (no hard dependency to run).
- You can operate purely with H2 + Lucene for structural and keyword exploration.

## 3. Advanced Configurations
Ways to customize performance, accuracy, or behavior.

### 3.1 Embedding Modes
Property source: `application.properties` (default) and optional profile `application-onnx.properties`.

| Property | Default | Purpose |
|----------|---------|---------|
| `embedding.mode` | `mock` | Switch between `mock` (fast, deterministic) and `onnx` (real embeddings). |
| `embedding.dimension` | `384` (example) | Vector size expected downstream. |
| `embedding.model-path` | (commented) | Path to ONNX model file (enable in ONNX profile). |
| `embedding.tokenizer-path` | (commented) | Directory containing tokenizer artifacts (vocab, config). |
| `indexing.file-extensions` | (internal default) | Limit to certain file types (e.g., `.java`). |
| `indexing.exclude-directories` | `target,build,.git,test` (internal defaults) | Skip noisy folders. |
| `indexing.exclude-patterns` | (optional glob list) | Pattern-based file exclusion. |

Switch to ONNX (real vectors):
- Ensure model files exist (already included in this repo layout):
  - `models/model_onnx/onnx/model.onnx`
  - tokenizer artifacts under `models/model_onnx/` (config/tokenizer/vocab files)
- Start with the ONNX profile (choose your shell):
  - PowerShell (no && chaining needed):
    ```powershell
    $env:SPRING_PROFILES_ACTIVE='onnx'
    mvn spring-boot:run
    ```
  - cmd.exe (supports && chaining):
    ```bat
    set SPRING_PROFILES_ACTIVE=onnx && mvn spring-boot:run
    ```
- Notes when ONNX profile is active:
  - It uses a separate H2 DB: `jdbc:h2:file:./data/firestick_onnx`
  - It is configured to listen on port `8081` (so default profile can still run on `8080`)
- Verify and re-index on port 8081:
  - Health check (PowerShell):
    ```powershell
    Invoke-RestMethod http://localhost:8081/api/health
    ```
  - Index a folder (PowerShell; replace path):
    ```powershell
    Invoke-RestMethod "http://localhost:8081/api/indexing/run?root=E:\path\to\legacy\repo&excludeDirs=.git,target&excludeGlobs=**\*.min.js"
    ```
  - Health check (cmd.exe):
    ```bat
    curl.exe -sS http://localhost:8081/api/health
    ```

### 3.2 ONNX vs Other Model Options
- **ONNX Local Encoder**: Fast CPU inference; good for code chunk embedding.
- **Alternative Sentence Embedders** (e.g., different MiniLM variants): Export to ONNX (using Python + `optimum` or `transformers`) and drop into `models/...` with matching tokenizer.
- **DJL / Native Backends**: You could load PyTorch or other engines via DJL, but ONNX provides consistent CPU portability.

### 3.3 MOCK Mode Explained
MOCK mode provides **deterministic pseudo-embeddings** so tests and indexing succeed without downloading a real model.
Benefits:
- Zero setup friction.
- Predictable output for assertions.
Trade-offs:
- No semantic similarity—keyword search only.
Use Cases:
- Local development, CI smoke tests, rapid indexing iteration.
Switch to ONNX when you need semantic ranking or RAG-quality retrieval.

### 3.4 Performance Tuning
| Lever | Effect | Notes |
|-------|--------|-------|
| Smaller chunk size (future config) | More granular search | Increased total docs & memory. |
| Exclude patterns aggressively | Faster indexing | Risk of omitting relevant code; document exclusions. |
| ONNX quantized model | Lower RAM usage | Ensure opset compatibility; test outputs. |
| Increase Lucene result window | Broader candidate set | Only helpful once semantic reranking is present. |

### 3.5 Switching Embedding Models
Basic steps:
1. Export or obtain ONNX model + tokenizer.
2. Set paths in `application-onnx.properties` (or override via env vars).
3. Run with `onnx` profile.
4. Re-index so new vectors replace old (delete H2 data or use an index versioning approach).

### 3.6 Externalizing H2
For multi-user or durability:
- Switch to PostgreSQL or MySQL by updating `spring.datasource.*` and driver dependencies.
- Migrate schema automatically (Hibernate) or manage via migrations (Flyway/Liquibase).

## 4. Upgrades & Extensibility

### 4.1 Adding a Local LLM (Retrieval-Augmented Generation)
Current workflow returns raw snippets; an LLM can synthesize answers across multiple code chunks.
Upgrade path:
1. Keep existing indexing + retrieval (high signal, low cost).
2. Add a **sidecar inference service** (e.g., a 3–4B instruct model quantized via llama.cpp or vLLM). Reasonable starting points: Phi-3 Mini, Llama 3.x 3B, Qwen 2.5 3B.
3. Implement a `/api/ask` endpoint:
   - Retrieve top N snippets via Lucene (and ONNX vectors when enabled).
   - Build a prompt with: system role + snippets + user question.
   - Call sidecar, return synthesized explanation + source references.
4. Add guardrails:
   - If no snippets found → respond with fallback (“No relevant code found”).
   - Enforce token limits by truncating snippet text.

Expected improvements:
| Capability | Without LLM | With LLM |
|------------|------------|----------|
| Raw code lookup | Yes | Yes |
| Natural-language summary | No | Yes |
| Cross-file reasoning | Manual (user reads) | Automated synthesis |
| Follow-up conversational context | Requires user layering | Maintained in dialog state |

### 4.2 Embedding Quality Improvements
- Swap model to more domain-aware encoder (e.g., Java code specific model) for better cluster/semantic search.
- Add re-ranking (e.g., vector similarity first, keyword second, or a small cross-encoder). This can come before LLM for quality boosts.

### 4.3 Observability & Scaling
- Introduce per-job metrics (already started with `IndexingProgress` events) into Prometheus endpoints.
- Move Lucene from in-memory to persistent FS or a hosted search engine if index size grows.

### 4.4 Future: Chunk Strategies
- Smart chunking (AST-based methods/classes) vs fixed line ranges to reduce irrelevant context for RAG.
- Deduplicate similar chunks (hash-based) to trim index size.

## 5. Summary Cheat Sheet
| Goal | Action |
|------|--------|
| Quick health | `GET /api/health` |
| Index code | `GET /api/indexing/run?root=...` or POST `/api/indexing/run` |
| Cancel job | `POST /api/indexing/cancel` |
| Search code | `GET /api/search?q=...` |
| Fetch chunk | `GET /api/code/chunk?docId=...` |
| Enable ONNX | Run with `-Dspring-boot.run.profiles=onnx` + model paths set (endpoints on `http://localhost:8081`) |
| Add LLM (future) | Sidecar + new `/api/ask` endpoint |

## 6. Glossary
- **Chunk**: A contiguous segment of source code (e.g., method block) stored for retrieval.
- **Embedding**: Numeric vector representing content semantics; mock embedding is a placeholder.
- **Lucene**: Keyword search engine; indexes chunk text for term-based lookup.
- **Chroma**: Vector database (future optional integration) for semantic similarity queries.
- **H2**: Embedded relational database storing code metadata & indexing jobs.
- **ONNX**: Open format for machine learning models enabling portable inference.

---
**Next Suggested Enhancements:**
1. Implement `/api/ask` (no LLM) that stitches top snippets into a textual summary.
2. Add ONNX profile smoke test to CI (skipped if model missing).
3. Introduce chunk-type (class, method, import block) to improve future semantic precision.

Feel free to request a follow-up file with a ready-made LLM integration plan.
