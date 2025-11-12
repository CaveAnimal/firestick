package com.codetalker.firestick.controller;

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
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;

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

    public IndexingJobController(IndexingJobRepository repository) {
        this.repository = repository;
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
    public ResponseEntity<IndexingJob> latest() {
        Optional<IndexingJob> job = repository.findTopByOrderByStartedAtDesc();
        return job.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<IndexingJob> byId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
}
