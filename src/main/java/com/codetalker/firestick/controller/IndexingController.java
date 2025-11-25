package com.codetalker.firestick.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.exception.ErrorResponse;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.IndexingJobControl;
import com.codetalker.firestick.service.IndexingService;
import com.codetalker.firestick.service.dto.IndexingReport;
import com.codetalker.firestick.service.dto.IndexingRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping("/api/indexing")
public class IndexingController {
    private static final Logger log = LoggerFactory.getLogger(IndexingController.class);

    private final IndexingService indexingService;
    private final IndexingJobRepository jobRepository;
    private final IndexingJobControl jobControl;
    private final com.codetalker.firestick.service.AppRenameService appRenameService;

    public IndexingController(
            IndexingService indexingService,
            IndexingJobRepository jobRepository,
            IndexingJobControl jobControl,
            com.codetalker.firestick.service.AppRenameService appRenameService) {
        this.indexingService = indexingService;
        this.jobRepository = jobRepository;
        this.jobControl = jobControl;
        this.appRenameService = appRenameService;
    }

    @GetMapping("/run")
    @Operation(summary = "Trigger indexing run")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Indexing started",
            content = @Content(schema = @Schema(implementation = IndexingReport.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<IndexingReport> run(
        @RequestParam("root") @jakarta.validation.constraints.NotBlank String rootPath,
            @RequestParam(value = "appName", required = false) String appName,
            @RequestParam(value = "app", defaultValue = "default") String legacyAppName,
            @RequestParam(value = "excludeDirs", required = false) String excludeDirsCsv,
            @RequestParam(value = "excludeGlobs", required = false) String excludeGlobsCsv) {
        List<String> excludeDirs = csvToList(excludeDirsCsv);
        List<String> excludeGlobs = csvToList(excludeGlobsCsv);
        // Use appName if provided, otherwise use legacy app parameter
        String finalAppName = (appName != null && !appName.isBlank()) ? appName : legacyAppName;
        IndexingRequest req = new IndexingRequest(rootPath,
                excludeDirs == null || excludeDirs.isEmpty() ? null : excludeDirs,
                excludeGlobs == null || excludeGlobs.isEmpty() ? null : excludeGlobs,
                finalAppName);
        log.info("[API] Indexing run requested: app={}, root={}, excludeDirs={}, excludeGlobs={} ", finalAppName, rootPath, excludeDirs, excludeGlobs);
        // Start indexing work in a background thread so the API returns immediately
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                indexingService.index(req);
            } catch (Exception ex) {
                log.error("Background indexing failed", ex);
            }
        });

        // Attempt to locate the newly created job quickly so callers receive the job id
        long deadline = System.currentTimeMillis() + 1500;
        while (System.currentTimeMillis() < deadline) {
            var latest = jobRepository.findTopByOrderByStartedAtDesc();
            if (latest.isPresent()) {
                var job = latest.get();
                // Return a minimal report so frontend can subscribe to SSE immediately
                int discovered = job.getFilesDiscovered();
                int parsed = job.getFilesParsed();
                int skipped = Math.max(0, discovered - parsed);
                return ResponseEntity.ok(new IndexingReport(
                    job.getId(), job.getStatus() == null ? "UNKNOWN" : job.getStatus().name(), req.rootPath(),
                    discovered,
                    job.getTotalFolders(),
                    job.getTotalMethods(),
                    parsed,
                    skipped,
                    job.getChunksProduced(),
                    job.getDocumentsIndexed(),
                    job.getEmbeddingsGenerated(),
                    job.getStartedAt() == null ? System.currentTimeMillis() : job.getStartedAt().toEpochMilli(),
                    0L, new java.util.ArrayList<>(), 0, 0, 0, new java.util.ArrayList<>()
                ));
            }
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }

        // If job isn't visible yet, return accepted (202) so caller knows indexing was triggered
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/run")
    @Operation(summary = "Trigger indexing run (JSON)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Indexing started",
            content = @Content(schema = @Schema(implementation = IndexingReport.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<IndexingReport> run(@RequestBody @jakarta.validation.Valid IndexingRequest request) {
        log.info("[API] Indexing run requested (POST): app={}, root={}", request.appName(), request.rootPath());
        // run asynchronously so API caller returns immediately and can attach to SSE
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                indexingService.index(request);
            } catch (Exception ex) {
                log.error("Background indexing failed", ex);
            }
        });

        // try to find the new job quickly and return a minimal report
        long deadline = System.currentTimeMillis() + 1500;
        while (System.currentTimeMillis() < deadline) {
            var latest = jobRepository.findTopByOrderByStartedAtDesc();
            if (latest.isPresent()) {
                var job = latest.get();
                int discovered = job.getFilesDiscovered();
                int parsed = job.getFilesParsed();
                int skipped = Math.max(0, discovered - parsed);
                return ResponseEntity.ok(new IndexingReport(
                    job.getId(), job.getStatus() == null ? "UNKNOWN" : job.getStatus().name(), request.rootPath(), discovered, job.getTotalFolders(), job.getTotalMethods(), parsed, skipped,
                    job.getChunksProduced(), job.getDocumentsIndexed(), job.getEmbeddingsGenerated(),
                    job.getStartedAt() == null ? System.currentTimeMillis() : job.getStartedAt().toEpochMilli(),
                    0L, new java.util.ArrayList<>(), 0, 0, 0, new java.util.ArrayList<>()
                ));
            }
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/browse")
    @Operation(summary = "Browse directory contents")
    public ResponseEntity<DirectoryListing> browse(@RequestParam(value = "path", required = false) String pathStr) {
        try {
            Path path;
            if (pathStr == null || pathStr.trim().isEmpty()) {
                // List user home directory as starting point
                path = Path.of(System.getProperty("user.home"));
            } else {
                path = Path.of(pathStr).toAbsolutePath().normalize();
            }

            if (!Files.isDirectory(path)) {
                return ResponseEntity.badRequest().body(new DirectoryListing(pathStr, List.of(), "Path is not a directory"));
            }

            List<DirectoryEntry> entries = new ArrayList<>();
            
            // Add parent directory option if not at root
            Path parent = path.getParent();
            if (parent != null) {
                entries.add(new DirectoryEntry("..", parent.toAbsolutePath().toString(), true));
            }
            
            try (var stream = Files.list(path)) {
                stream.forEach(p -> {
                    try {
                        entries.add(new DirectoryEntry(
                            p.getFileName().toString(),
                            p.toAbsolutePath().toString(),
                            Files.isDirectory(p)
                        ));
                    } catch (Exception e) {
                        // Skip entries we can't access
                    }
                });
            }

            // Sort: parent first, then directories, then files, alphabetically
            entries.sort((a, b) -> {
                if (a.name.equals("..")) return -1;
                if (b.name.equals("..")) return 1;
                if (a.isDirectory != b.isDirectory) {
                    return a.isDirectory ? -1 : 1;
                }
                return a.name.compareToIgnoreCase(b.name);
            });

            return ResponseEntity.ok(new DirectoryListing(path.toAbsolutePath().toString(), entries, null));
        } catch (InvalidPathException | SecurityException e) {
            log.warn("Failed to browse path: {}", pathStr, e);
            return ResponseEntity.status(403).body(new DirectoryListing(pathStr, List.of(), "Access denied or invalid path"));
        } catch (IOException e) {
            log.error("Error browsing directory: {}", pathStr, e);
            return ResponseEntity.status(500).body(new DirectoryListing(pathStr, List.of(), "Error reading directory"));
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "Request cancellation of an indexing job")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Cancellation requested"),
        @ApiResponse(responseCode = "404", description = "Job not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "No running job to cancel",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> cancel(
            @RequestParam(value = "jobId", required = false)
            @jakarta.validation.constraints.Min(1) Long jobId) {
        Long id = jobId;
        if (id == null) {
            id = jobRepository.findTopByStatusOrderByStartedAtDesc(IndexingJob.Status.RUNNING)
                    .map(IndexingJob::getId)
                    .orElse(null);
            if (id == null) {
                // No running job to cancel
                return ResponseEntity.status(409).build();
            }
        } else if (!jobRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jobControl.requestCancel(id);
        log.info("[API] Cancellation requested for indexing job {}", id);
        return ResponseEntity.accepted().build();
    }

    private static List<String> csvToList(String csv) {
        if (csv == null || csv.isBlank()) return null;
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @PostMapping("/apps/{oldName}/rename")
    @Operation(summary = "Rename an application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application renamed",
            content = @Content(schema = @Schema(implementation = AppRenameResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AppRenameResponse> renameApp(
            @org.springframework.web.bind.annotation.PathVariable String oldName,
            @RequestBody AppRenameRequest request) {
        if (appRenameService == null) {
            return ResponseEntity.status(500).body(new AppRenameResponse(false, 0, 0, 0, "AppRenameService not available"));
        }
        try {
            var result = appRenameService.rename(oldName, request.newAppName);
            log.info("[API] App renamed: {} -> {}", oldName, request.newAppName);
            return ResponseEntity.ok(new AppRenameResponse(result.success, result.filesUpdated, result.chunksUpdated, result.jobsUpdated, null));
        } catch (IllegalArgumentException e) {
            log.warn("[API] Invalid rename request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AppRenameResponse(false, 0, 0, 0, e.getMessage()));
        } catch (Exception e) {
            log.error("[API] Error renaming app: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new AppRenameResponse(false, 0, 0, 0, e.getMessage()));
        }
    }

    // Inner DTOs for directory browsing and app management
    public record DirectoryListing(String currentPath, List<DirectoryEntry> entries, String error) {}

    public record DirectoryEntry(String name, String path, boolean isDirectory) {}

    @GetMapping("/apps")
    @Operation(summary = "Get all available application names")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of applications",
            content = @Content(schema = @Schema(implementation = AppListResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AppListResponse> getApps() {
        try {
            var apps = indexingService.getAvailableApps();
            log.info("[API] Retrieved {} available apps", apps.size());
            return ResponseEntity.ok(new AppListResponse(apps, null));
        } catch (Exception e) {
            log.error("[API] Error retrieving apps", e);
            return ResponseEntity.status(500).body(new AppListResponse(List.of(), e.getMessage()));
        }
    }

    public record AppRenameRequest(String newAppName) {}

    public record AppRenameResponse(boolean success, long filesUpdated, long chunksUpdated, long jobsUpdated, String error) {}

    public record AppListResponse(List<String> apps, String error) {}
}
