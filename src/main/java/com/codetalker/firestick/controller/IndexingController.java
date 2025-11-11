package com.codetalker.firestick.controller;

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

    public IndexingController(IndexingService indexingService, IndexingJobRepository jobRepository, IndexingJobControl jobControl) {
        this.indexingService = indexingService;
        this.jobRepository = jobRepository;
        this.jobControl = jobControl;
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
            @RequestParam(value = "app", defaultValue = "default") String appName,
            @RequestParam(value = "excludeDirs", required = false) String excludeDirsCsv,
            @RequestParam(value = "excludeGlobs", required = false) String excludeGlobsCsv) {
        List<String> excludeDirs = csvToList(excludeDirsCsv);
        List<String> excludeGlobs = csvToList(excludeGlobsCsv);
        IndexingRequest req = new IndexingRequest(rootPath,
                excludeDirs == null || excludeDirs.isEmpty() ? null : excludeDirs,
                excludeGlobs == null || excludeGlobs.isEmpty() ? null : excludeGlobs,
                appName);
        log.info("[API] Indexing run requested: app={}, root={}, excludeDirs={}, excludeGlobs={} ", appName, rootPath, excludeDirs, excludeGlobs);
        IndexingReport report = indexingService.index(req);
        return ResponseEntity.ok(report);
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
        IndexingReport report = indexingService.index(request);
        return ResponseEntity.ok(report);
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
}
