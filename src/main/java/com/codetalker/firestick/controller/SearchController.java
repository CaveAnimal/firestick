package com.codetalker.firestick.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.exception.ErrorResponse;
import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.CodeSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * REST endpoint to search indexed code snippets.
 */
@RestController
@Validated
@RequestMapping(path = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final CodeSearchService codeSearchService;
    private final CodeFileRepository fileRepository;
    private final CodeChunkRepository chunkRepository;

    public SearchController(CodeSearchService codeSearchService,
                            CodeFileRepository fileRepository,
                            CodeChunkRepository chunkRepository) {
        this.codeSearchService = codeSearchService;
        this.fileRepository = fileRepository;
        this.chunkRepository = chunkRepository;
    }

    @GetMapping
    @Operation(summary = "Search code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results"),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SearchResponse> search(
    @RequestParam(name = "q") @NotBlank String query,
    @RequestParam(name = "app", defaultValue = "default") String app,
        @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
        @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(200) int pageSize,
            @RequestParam(name = "path", required = false) String pathIncludes,
            @RequestParam(name = "lang", required = false) String language
    ) {
        try {
            List<String> ids = codeSearchService.searchCode(query, app);

            // optional filters (path/lang) not implemented yet
            List<SearchResult> all = new ArrayList<>();
            for (String id : ids) {
                ParsedId parsed = ParsedId.parse(id);
                if (parsed == null) continue;
                if (pathIncludes != null && !pathIncludes.isBlank() && !parsed.filePath.contains(pathIncludes)) {
                    continue;
                }

                String snippet = "";
                Optional<CodeFile> cf = fileRepository.findByFilePathAndAppName(parsed.filePath, app);
                if (cf.isPresent()) {
                    Optional<CodeChunk> chunk = chunkRepository.findByFileAndStartLineAndEndLine(cf.get(), parsed.startLine, parsed.endLine);
                    snippet = chunk.map(CodeChunk::getContent).orElse("");
                }

                all.add(new SearchResult(id, parsed.filePath, parsed.startLine, snippet));
            }

            int total = all.size();
            int from = Math.max(0, Math.min((page - 1) * pageSize, total));
            int to = Math.max(0, Math.min(from + pageSize, total));
            List<SearchResult> pageResults = all.subList(from, to);

            return ResponseEntity.ok(new SearchResponse(total, pageResults));
        } catch (Exception e) {
            log.warn("Search failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(new SearchResponse(0, List.of()));
        }
    }

    /**
     * Minimal DTO for a single search result compatible with UI needs.
     */
    public record SearchResult(String id, String filePath, int line, String snippet) {}

    /** Wrapper DTO for paginated responses. */
    public record SearchResponse(int total, List<SearchResult> results) {}

    /** Utility to parse document id produced by IndexingService#buildDocId. */
    static class ParsedId {
        final String filePath;
        final String type;
        final int startLine;
        final int endLine;

        ParsedId(String filePath, String type, int startLine, int endLine) {
            this.filePath = filePath;
            this.type = type;
            this.startLine = startLine;
            this.endLine = endLine;
        }

        static ParsedId parse(String id) {
            // format: {filePath}#{type}:{start}-{end}
            if (id == null) return null;
            int hash = id.lastIndexOf('#');
            int colon = id.lastIndexOf(':');
            int dash = id.lastIndexOf('-');
            if (hash <= 0 || colon <= hash || dash <= colon) return null;
            try {
                String filePath = id.substring(0, hash);
                String type = id.substring(hash + 1, colon);
                int start = Integer.parseInt(id.substring(colon + 1, dash));
                int end = Integer.parseInt(id.substring(dash + 1));
                return new ParsedId(filePath, type, start, end);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
