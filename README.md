# firestick

A Spring Boot application for code analysis, embeddings, and search capabilities.

## Technologies

### Backend
- **Java 21** - Latest LTS version of Java
- **Spring Boot 3.5.6** - Spring Boot framework with embedded Tomcat
- **Maven** - Build and dependency management

### Frontend (UI)
- **React 18 + Vite + TypeScript** (in the `ui/` directory)
  - Dev server: `http://localhost:5173` (proxies API + health to backend on `8080`)
  - Key libs: MUI (layout & components), Monaco (code viewing), Cytoscape (graphs), Recharts (metrics), Prism (syntax highlighting)
  - See `ui/README.md` for deeper details (routing, wireframes, scripts)

### Code Analysis
- **JavaParser 3.26.3** - Java source code parsing and AST analysis
- **JGraphT 1.5.2** - Graph library for dependency analysis
- **Apache Lucene 9.12.0** - Full-text search and indexing

### Data & Embeddings
- **ONNX Runtime 1.20.0** - Machine learning inference for Sentence-Transformers
- **DJL 0.31.1** - Deep Java Library for ML integration
- **H2 Database** - Embedded SQL database

### Vector Store
- **Chroma** - Vector database (manual installation required)

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- (Optional for UI) Node.js 18+ & npm 9+ if you want to run the React frontend

### Build

```bash
mvn clean package
```

### Run (Backend Only)

```bash
java -jar target/firestick-1.0.0-SNAPSHOT.jar
```

Or use Maven:

```bash
mvn spring-boot:run
```

### (Optional) Run with ONNX Profile
Use real embeddings and alternate port / DB:

PowerShell:
```powershell
$env:SPRING_PROFILES_ACTIVE='onnx'
mvn spring-boot:run
```

cmd.exe:
```bat
set SPRING_PROFILES_ACTIVE=onnx && mvn spring-boot:run
```
Notes:
- Port: 8081 (so default mock profile can stay on 8080)
- DB path: `./data/firestick_onnx`
- Re-index after switching profiles for fresh vectors.

### Run the React UI (Optional Frontend)
From the repository root:
```bash
cd ui
npm install
npm run dev
```
Then open: http://localhost:5173

Dev server proxy behavior:
- Proxies `/api/*` and `/health` to `http://localhost:8080` by default (configured in `ui/vite.config.ts`).
If your backend is running with the ONNX profile on port 8081, you have two options:
1. Start backend on 8080 instead (PowerShell example):
   ```powershell
   $env:SPRING_PROFILES_ACTIVE='onnx'
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
   ```
   cmd.exe equivalent:
   ```bat
   set SPRING_PROFILES_ACTIVE=onnx && mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
   ```
2. Temporarily adjust the proxy targets in `ui/vite.config.ts` from 8080 to 8081, then restart `npm run dev`.

Production UI build:
```bash
cd ui
npm run build
npm run preview   # serves the built assets (default: http://localhost:4173)
```
You can also copy the `ui/dist/` folder behind any static file server (Nginx, Apache, S3 + CDN, etc.).

### Access the Application

- **API Health Check**: http://localhost:8080/api/health
- **Swagger / OpenAPI UI**: http://localhost:8080/swagger-ui.html (and `:8081` if ONNX profile)
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/firestick`
  - Username: `sa`
  - Password: (empty)
- **H2 (ONNX profile)**: http://localhost:8081/h2-console (URL: `jdbc:h2:file:./data/firestick_onnx`)

## Features

### Code Parser Service
Parse Java source code and analyze AST using JavaParser.

### Dependency Graph Service
Create and analyze dependency graphs using JGraphT.

### Code Search Service
Index and search code using Apache Lucene.

### Indexing Orchestrator and API
- Orchestrates: discover → parse → chunk → index → embed (mock)
- Trigger via REST:
  - POST /api/indexing/run with body: { "rootPath": "<path>", "excludeDirNames": [..], "excludeGlobPatterns": [..] }
  - GET /api/indexing/run?rootPath=<path>&excludeDirs=a,b&excludeGlobs=glob1,glob2
- Response includes an IndexingReport with: jobId, rootPath, filesDiscovered, filesParsed, filesSkipped, chunksProduced,
  documentsIndexed, embeddingsGenerated, startedAtMillis, endedAtMillis, errors.
- Indexing jobs are tracked in H2 (table indexing_jobs). Status endpoints:
  - GET /api/indexing/jobs/latest
  - GET /api/indexing/jobs/{id}

### End-to-End Pipeline (Docs)

See `src/main/resources/docs/PIPELINE.md` for the full parse → chunk → embed → store (Chroma) → query flow, configuration, and troubleshooting.

### Incremental Indexing

Indexing is incremental based on file modification time:

- If a file's lastModified timestamp is unchanged since the last run, it is skipped and counted in `filesSkipped`.
- Changed files are re-parsed and re-chunked; stored chunks for that file are replaced with the new set.
- File and chunk metadata are persisted in H2 tables `code_files` and `code_chunks`.

Force a full reindex (options):

- Clean the H2 data: stop the app and delete the local files under `./data/firestick*` (or truncate `code_files`, `code_chunks`, `indexing_jobs`).
- Alternatively, "touch" files to update their lastModified timestamp so they are reprocessed on the next run.

## Project Structure

```
firestick/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/caveanimal/firestick/
│   │   │       ├── FirestickApplication.java
│   │   │       ├── controller/
│   │   │       │   └── HealthController.java
│   │   │       └── service/
│   │   │           ├── CodeParserService.java
│   │   │           ├── CodeSearchService.java
│   │   │           └── DependencyGraphService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/caveanimal/firestick/
│               └── FirestickApplicationTests.java
├── ui/                # React + Vite frontend (see ui/README.md)
└── pom.xml
```

## Configuration

The application can be configured via `application.properties`:

- Server port: `server.port=8080`
- H2 Database settings
- JPA/Hibernate settings

### Chroma configuration (v2)

Chroma v2 servers may expose collection routes in two ways. Firestick supports both automatically:

- Non-namespaced (default): `/api/v2/collections/...`
- Namespaced by tenant/database: `/api/v2/tenants/{tenant}/databases/{database}/collections/...`

Set these properties in `src/main/resources/application.properties`:

```
# Base URL for your Chroma server (default shown)
chroma.base-url=http://localhost:8000

# Optional namespacing (uncomment both to enable namespaced routes)
# chroma.tenant=default_tenant
# chroma.database=default_database
```

Behavior:
- If both `chroma.tenant` and `chroma.database` are set, the namespaced routes are used.
- If either is unset/blank, Firestick falls back to the non-namespaced v2 routes.

### File Discovery / Indexing configuration

File discovery defaults are externalized via `IndexingConfig` (prefix `indexing`). Defaults:

- Extensions: `.java`
- Exclude directories: `target, build, .git, test`
- Exclude glob patterns: _(none by default)_

Override them in `src/main/resources/application.properties` (or via environment-specific property files):

```
# indexing.file-extensions=.java
# indexing.exclude-directories=target,build,.git,test
# indexing.exclude-patterns=**/*Generated.java
```

Notes:
- These are optional; leaving them commented retains the built-in defaults.
- Custom exclusions are merged with the defaults when using the overloads on `FileDiscoveryService`.

## Testing

```bash
mvn test
```

## Notes

- The Chroma Java client requires manual installation as it's not available in Maven Central
- ONNX Runtime is configured for embeddings support via Sentence-Transformers
- DJL provides additional ML capabilities
- React UI is optional and lives in `ui/` (run it separately if you want a web interface)

## Contributing

See [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) for guidelines.

## License

MIT License — see [LICENSE](LICENSE).

## Code Chunking Strategy

Firestick uses hierarchical code chunking to break Java source files into method, class, file, and module-level chunks. Each chunk is enriched with metadata (type, boundaries, parent/child relationships, code attributes, relationships, documentation, and quality metrics) and supports builder pattern and debugging via toString.

### Chunk Types
- **Method**: Primary chunk, includes signature, body, comments, and Javadoc.
- **Class**: Secondary chunk, includes class declaration, fields, method signatures, inner classes, and docs.
- **File**: Tertiary chunk, includes package, imports, class/function signatures, and file-level comments.
- **Module/Package**: Context chunk, includes module path, summary, key classes, entry points, and dependencies.

### Metadata Schema
Metadata is stored as a JSON string in each chunk, e.g.:
`{"chunkType":"method","parentId":123,"children":[456,789],"startLine":10,"endLine":20}`

### Integration Points
- **CodeChunkingService**: Provides APIs to chunk files, classes, and methods, returning hierarchical chunk DTOs.
- **DependencyGraphService**: Integrates chunk metadata for graph construction, reflecting chunk relationships in graph edges and nodes.
- **Unit Tests**: Validate chunking logic and hierarchy in `CodeChunkingServiceTest`.

See `tools/plans/firestickCodeChunkTasks.md` for detailed implementation roadmap.
