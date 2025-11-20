package com.codetalker.firestick.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.SearchIndexRebuildService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST endpoint for search index diagnostics and rebuilding.
 * Allows checking if data exists in database and rebuilding Lucene index from database.
 */
@RestController
@RequestMapping(path = "/api/search/index", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchIndexDiagnosticsController {

    private static final Logger log = LoggerFactory.getLogger(SearchIndexDiagnosticsController.class);

    private final CodeFileRepository fileRepository;
    private final CodeChunkRepository chunkRepository;
    private final SearchIndexRebuildService indexRebuildService;
    private final com.codetalker.firestick.service.CodeSearchService codeSearchService;

    public SearchIndexDiagnosticsController(
            CodeFileRepository fileRepository,
            CodeChunkRepository chunkRepository,
            SearchIndexRebuildService indexRebuildService,
            com.codetalker.firestick.service.CodeSearchService codeSearchService) {
        this.fileRepository = fileRepository;
        this.chunkRepository = chunkRepository;
        this.indexRebuildService = indexRebuildService;
        this.codeSearchService = codeSearchService;
    }

    @GetMapping("/status")
    @Operation(summary = "Check search index status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Index status info"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<IndexStatusResponse> status() {
        long totalFiles = fileRepository.count();
        long totalChunks = chunkRepository.count();
        boolean hasLuceneIndices = !codeSearchService.getAvailableApps().isEmpty();
        
        IndexStatusResponse response = new IndexStatusResponse(
            totalFiles,
            totalChunks,
            (totalFiles > 0 && totalChunks > 0) || hasLuceneIndices
        );
        
        log.info("Index status: {} files, {} chunks, indexed: {} (lucene: {})", 
                totalFiles, totalChunks, response.hasIndexedData, hasLuceneIndices);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rebuild")
    @Operation(summary = "Rebuild search index from database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rebuild started/completed"),
        @ApiResponse(responseCode = "400", description = "No data to index"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<RebuildResponse> rebuild() {
        // Check if there's data to rebuild
        long totalChunks = chunkRepository.count();
        if (totalChunks == 0) {
            log.warn("Rebuild requested but no chunks in database");
            return ResponseEntity.badRequest()
                .body(new RebuildResponse("NO_DATA", "No indexed data in database. Please run indexing first.", 0, 0));
        }
        
        log.info("Starting search index rebuild from database ({} chunks)...", totalChunks);
        
        try {
            long startTime = System.currentTimeMillis();
            long indexedCount = indexRebuildService.rebuildIndexFromDatabase();
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("Search index rebuild completed: {} chunks indexed in {} ms", indexedCount, duration);
            return ResponseEntity.ok(
                new RebuildResponse("SUCCESS", "Search index rebuilt from database", indexedCount, duration));
        } catch (Exception e) {
            log.error("Failed to rebuild search index", e);
            return ResponseEntity.status(500)
                .body(new RebuildResponse("ERROR", "Failed to rebuild index: " + e.getMessage(), 0, 0));
        }
    }

    /**
     * Response for index status
     */
    public static class IndexStatusResponse {
        public long totalFiles;
        public long totalChunks;
        public boolean hasIndexedData;

        public IndexStatusResponse(long totalFiles, long totalChunks, boolean hasIndexedData) {
            this.totalFiles = totalFiles;
            this.totalChunks = totalChunks;
            this.hasIndexedData = hasIndexedData;
        }
    }

    /**
     * Response for rebuild operation
     */
    public static class RebuildResponse {
        public String status;  // SUCCESS, NO_DATA, ERROR
        public String message;
        public long indexedCount;
        public long durationMs;

        public RebuildResponse(String status, String message, long indexedCount, long durationMs) {
            this.status = status;
            this.message = message;
            this.indexedCount = indexedCount;
            this.durationMs = durationMs;
        }
    }
}
