package com.codetalker.firestick.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.exception.ErrorResponse;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.FolderSummaryRepository;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.dto.IndexingReport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping("/api/indexing/jobs")
public class IndexingJobController {

    private final IndexingJobRepository repository;
    private final CodeFileRepository codeFileRepository;
    private final CodeChunkRepository codeChunkRepository;
    private final FolderSummaryRepository folderSummaryRepository;

    private final com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository;

    public IndexingJobController(IndexingJobRepository repository,
                                 CodeFileRepository codeFileRepository,
                                 CodeChunkRepository codeChunkRepository,
                                 FolderSummaryRepository folderSummaryRepository,
                                 com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository) {
        this.repository = repository;
        this.codeFileRepository = codeFileRepository;
        this.codeChunkRepository = codeChunkRepository;
        this.folderSummaryRepository = folderSummaryRepository;
        this.indexingObjectRepository = indexingObjectRepository;
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest job")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Latest job",
            content = @Content(schema = @Schema(implementation = IndexingJob.class))),
        @ApiResponse(responseCode = "404", description = "No job"),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
        public ResponseEntity<IndexingReport> latest() {
        Optional<IndexingJob> jobOpt = repository.findTopByOrderByStartedAtDesc();
        if (jobOpt.isEmpty()) return ResponseEntity.notFound().build();
        IndexingJob job = jobOpt.get();

        // Build a lightweight IndexingReport from the persisted job and related DB state
        int filesDiscovered = job.getFilesDiscovered();
        int filesParsed = job.getFilesParsed();
        int filesSkipped = Math.max(0, filesDiscovered - filesParsed);

        // Prefer persisted counters if provided, otherwise compute from DB
        int filesSummarized = job.getFilesSummarized();
        int methodsSummarized = job.getMethodsSummarized();
        int foldersSummarized = job.getFoldersSummarized();
        int totalFolders = job.getTotalFolders();
        int totalMethods = job.getTotalMethods();
        java.util.List<com.codetalker.firestick.service.dto.IndexingReport.SkippedFile> skipped = java.util.List.of();
        if (filesSummarized == 0 || methodsSummarized == 0 || foldersSummarized == 0) {
            // Need to compute from DB as fallback. Be defensive: appName may be null in older jobs
            try {
                String app = job.getAppName();
                List<CodeFile> files;
                if (app == null || app.isBlank()) {
                    // safe fallback: scan all files and filter by rootPath
                    files = codeFileRepository.findAll().stream()
                        .filter(cf -> cf.getFilePath() != null && cf.getFilePath().startsWith(job.getRootPath()))
                        .toList();
                } else {
                    files = codeFileRepository.findByAppName(app).stream()
                        .filter(cf -> cf.getFilePath() != null && cf.getFilePath().startsWith(job.getRootPath()))
                        .toList();
                }
            if (filesSummarized == 0) filesSummarized = (int) files.stream().filter(cf -> cf.getSummary() != null && !cf.getSummary().isBlank()).count();
            if (methodsSummarized == 0) {
                for (CodeFile cf : files) {
                    methodsSummarized += codeChunkRepository.findByFile(cf).stream()
                        .filter(cc -> "method".equalsIgnoreCase(cc.getType()) && cc.getSummary() != null && !cc.getSummary().isBlank()).count();
                }
            }
                if (foldersSummarized == 0) {
                    if (app == null || app.isBlank()) {
                        foldersSummarized = (int) folderSummaryRepository.findAll().stream()
                            .filter(fs -> fs.getFolderPath() != null && fs.getFolderPath().startsWith(job.getRootPath()))
                            .count();
                    } else {
                        foldersSummarized = (int) folderSummaryRepository.findByAppName(app).stream()
                            .filter(fs -> fs.getFolderPath() != null && fs.getFolderPath().startsWith(job.getRootPath()))
                            .count();
                    }
                }
            // compute total folder & method counts if 0
            if (totalMethods == 0) {
                for (CodeFile cf : files) {
                    totalMethods += codeChunkRepository.findByFile(cf).stream().filter(cc -> "method".equalsIgnoreCase(cc.getType())).count();
                }
            }
            if (totalFolders == 0) {
                java.util.Set<String> parents = new java.util.HashSet<>();
                for (CodeFile cf : files) {
                    if (cf.getFilePath() != null) {
                        java.nio.file.Path p = java.nio.file.Path.of(cf.getFilePath());
                        if (p.getParent() != null) parents.add(p.getParent().toString());
                    }
                }
                totalFolders = parents.size();
            }
            } catch (Exception e) {
                // Defensive: if counting fails for any reason, retain zero values and continue
                // Avoid bubbling up to caller (which would return 500). Log the problem.
                org.slf4j.LoggerFactory.getLogger(IndexingJobController.class).warn("Failed to compute fallback summarization counters for job {}: {}", job.getId(), e.getMessage());
            }
        }

        // Parse skippedFiles JSON persisted on the job if present
        if (job.getSkippedFiles() != null && !job.getSkippedFiles().isBlank()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
                skipped = java.util.Arrays.asList(m.readValue(job.getSkippedFiles(), com.codetalker.firestick.service.dto.IndexingReport.SkippedFile[].class));
            } catch (Exception ignored) {
                // ignore parsing failure and return empty list
            }
        }

        // done computing persisted/fallback counters and skipped list above

        java.util.List<String> errors = job.getErrorSummary() == null ? java.util.List.of() : java.util.Arrays.asList(job.getErrorSummary().split("\n"));

            IndexingReport report = new IndexingReport(
            job.getId(),
            job.getStatus() == null ? "UNKNOWN" : job.getStatus().name(),
            job.getRootPath(),
            filesDiscovered,
            totalFolders,
            totalMethods,
            filesParsed,
            filesSkipped,
            job.getChunksProduced(),
            job.getDocumentsIndexed(),
            job.getEmbeddingsGenerated(),
            job.getStartedAt() == null ? 0L : job.getStartedAt().toEpochMilli(),
            job.getEndedAt() == null ? System.currentTimeMillis() : job.getEndedAt().toEpochMilli(),
            errors,
            filesSummarized,
            foldersSummarized,
            methodsSummarized,
            skipped
        );

        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job",
            content = @Content(schema = @Schema(implementation = IndexingJob.class))),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
        public ResponseEntity<IndexingReport> byId(@PathVariable Long id) {
        Optional<IndexingJob> jobOpt = repository.findById(id);
        if (jobOpt.isEmpty()) return ResponseEntity.notFound().build();
        IndexingJob job = jobOpt.get();

        int filesDiscovered = job.getFilesDiscovered();
        int filesParsed = job.getFilesParsed();
        int filesSkipped = Math.max(0, filesDiscovered - filesParsed);

        // Prefer persisted values and parse skippedFiles JSON
        int filesSummarized = job.getFilesSummarized();
        int methodsSummarized = job.getMethodsSummarized();
        int foldersSummarized = job.getFoldersSummarized();
        int totalFolders = job.getTotalFolders();
        int totalMethods = job.getTotalMethods();
        java.util.List<com.codetalker.firestick.service.dto.IndexingReport.SkippedFile> skippedList = java.util.List.of();
        if (filesSummarized == 0 || methodsSummarized == 0 || foldersSummarized == 0) {
            // fallback to DB computations
            try {
                String app = job.getAppName();
                List<CodeFile> filesB;
                if (app == null || app.isBlank()) {
                    filesB = codeFileRepository.findAll().stream()
                        .filter(cf -> cf.getFilePath() != null && cf.getFilePath().startsWith(job.getRootPath()))
                        .toList();
                } else {
                    filesB = codeFileRepository.findByAppName(app).stream()
                        .filter(cf -> cf.getFilePath() != null && cf.getFilePath().startsWith(job.getRootPath()))
                        .toList();
                }
            if (filesSummarized == 0) filesSummarized = (int) filesB.stream().filter(cf -> cf.getSummary() != null && !cf.getSummary().isBlank()).count();
            if (methodsSummarized == 0) {
                for (CodeFile cf : filesB) {
                    methodsSummarized += codeChunkRepository.findByFile(cf).stream()
                        .filter(cc -> "method".equalsIgnoreCase(cc.getType()) && cc.getSummary() != null && !cc.getSummary().isBlank()).count();
                }
            }
                if (foldersSummarized == 0) {
                    if (app == null || app.isBlank()) {
                        foldersSummarized = (int) folderSummaryRepository.findAll().stream()
                            .filter(fs -> fs.getFolderPath() != null && fs.getFolderPath().startsWith(job.getRootPath()))
                            .count();
                    } else {
                        foldersSummarized = (int) folderSummaryRepository.findByAppName(app).stream()
                            .filter(fs -> fs.getFolderPath() != null && fs.getFolderPath().startsWith(job.getRootPath()))
                            .count();
                    }
                }
            // compute total folder & method counts if 0
            if (totalMethods == 0) {
                for (CodeFile cf : filesB) {
                    totalMethods += codeChunkRepository.findByFile(cf).stream().filter(cc -> "method".equalsIgnoreCase(cc.getType())).count();
                }
            }
            if (totalFolders == 0) {
                java.util.Set<String> parentsB = new java.util.HashSet<>();
                for (CodeFile cf : filesB) {
                    if (cf.getFilePath() != null) {
                        java.nio.file.Path p = java.nio.file.Path.of(cf.getFilePath());
                        if (p.getParent() != null) parentsB.add(p.getParent().toString());
                    }
                }
                totalFolders = parentsB.size();
            }
            } catch (Exception e) {
                org.slf4j.LoggerFactory.getLogger(IndexingJobController.class).warn("Failed to compute fallback summarization counters for job {}: {}", job.getId(), e.getMessage());
            }
        }
        if (job.getSkippedFiles() != null && !job.getSkippedFiles().isBlank()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
                skippedList = java.util.Arrays.asList(m.readValue(job.getSkippedFiles(), com.codetalker.firestick.service.dto.IndexingReport.SkippedFile[].class));
            } catch (Exception ignored) { }
        }

        java.util.List<String> errors = job.getErrorSummary() == null ? java.util.List.of() : java.util.Arrays.asList(job.getErrorSummary().split("\n"));

            IndexingReport report = new IndexingReport(
            job.getId(),
            job.getStatus() == null ? "UNKNOWN" : job.getStatus().name(),
            job.getRootPath(),
            filesDiscovered,
            totalFolders,
            totalMethods,
            filesParsed,
            filesSkipped,
            job.getChunksProduced(),
            job.getDocumentsIndexed(),
            job.getEmbeddingsGenerated(),
            job.getStartedAt() == null ? 0L : job.getStartedAt().toEpochMilli(),
            job.getEndedAt() == null ? System.currentTimeMillis() : job.getEndedAt().toEpochMilli(),
            errors,
            filesSummarized,
            foldersSummarized,
            methodsSummarized,
            skippedList
        );

        return ResponseEntity.ok(report);
    }

    @GetMapping
    @Operation(summary = "List recent jobs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent jobs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = IndexingJob.class)))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public java.util.List<IndexingJob> recent(@RequestParam(name = "limit", defaultValue = "10")
                                              @jakarta.validation.constraints.Min(1)
                                              @jakarta.validation.constraints.Max(100) int limit) {
        int pageSize = Math.max(1, Math.min(limit, 100));
        return repository.findAll(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "startedAt")))
                .getContent();
    }

    @GetMapping("/{id}/objects")
    @Operation(summary = "Get indexing objects recorded for a job")
    public ResponseEntity<?> objects(@PathVariable Long id,
                                     @RequestParam(name = "page", required = false) Integer page,
                                     @RequestParam(name = "limit", required = false) Integer limit,
                                     @RequestParam(name = "objectType", required = false) String objectType,
                                     @RequestParam(name = "q", required = false) String q) {
        try {
            if (!repository.existsById(id)) return ResponseEntity.notFound().build();

            // If no pagination params provided, preserve the original behavior and return full list
            if (page == null && limit == null) {
                java.util.List<com.codetalker.firestick.model.IndexingObject> list = indexingObjectRepository.findByJobId(id);
                return ResponseEntity.ok(list);
            }

            int p = page == null ? 0 : Math.max(0, page);
            int s = limit == null ? 50 : Math.max(1, Math.min(limit, 1000));
            org.springframework.data.domain.Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));

            org.springframework.data.domain.Page<com.codetalker.firestick.model.IndexingObject> pageResult;
            if (objectType != null && objectType.trim().length() > 0 && q != null && q.trim().length() > 0) {
                pageResult = indexingObjectRepository.findByJobIdAndObjectTypeAndObjectNameContainingIgnoreCase(id, objectType.trim(), q.trim(), pageable);
            } else if (objectType != null && objectType.trim().length() > 0) {
                pageResult = indexingObjectRepository.findByJobIdAndObjectType(id, objectType.trim(), pageable);
            } else if (q != null && q.trim().length() > 0) {
                pageResult = indexingObjectRepository.findByJobIdAndObjectNameContainingIgnoreCase(id, q.trim(), pageable);
            } else {
                pageResult = indexingObjectRepository.findByJobId(id, pageable);
            }

            // Return a small envelope with items and pagination metadata
            record ObjectsPage(java.util.List<com.codetalker.firestick.model.IndexingObject> items, int page, int size, long total) {}

            ObjectsPage out = new ObjectsPage(pageResult.getContent(), pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements());
            return ResponseEntity.ok(out);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
