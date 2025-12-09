package com.codetalker.firestick.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping(path = "/api/code", produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeContentController {

    private static final Logger log = LoggerFactory.getLogger(CodeContentController.class);

    private final CodeFileRepository fileRepository;
    private final CodeChunkRepository chunkRepository;

    public CodeContentController(CodeFileRepository fileRepository, CodeChunkRepository chunkRepository) {
        this.fileRepository = fileRepository;
        this.chunkRepository = chunkRepository;
    }

    @GetMapping("/file")
    @Operation(summary = "Get full file content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File content",
            content = @Content(schema = @Schema(implementation = FileResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FileResponse> getFile(@RequestParam("path") @jakarta.validation.constraints.NotBlank String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            if (!Files.exists(path)) {
                // try resolving relative paths from JVM working dir (already default)
                log.debug("File not found: {}", pathStr);
                return ResponseEntity.notFound().build();
            }
            String content = Files.readString(path, StandardCharsets.UTF_8);
            return ResponseEntity.ok(new FileResponse(path.toString(), content));
        } catch (IOException e) {
            log.warn("Failed reading file {}: {}", pathStr, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/chunk")
    @Operation(summary = "Get file chunk by document id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chunk content",
            content = @Content(schema = @Schema(implementation = ChunkResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ChunkResponse> getChunk(@RequestParam("docId") @jakarta.validation.constraints.NotBlank String docId) {
        ParsedId parsed = ParsedId.parse(docId);
        if (parsed == null) return ResponseEntity.badRequest().build();
        return fileRepository.findByFilePath(parsed.filePath)
                .flatMap(cf -> chunkRepository.findByFileAndStartLineAndEndLine(cf, parsed.startLine, parsed.endLine)
                        .map(chunk -> ResponseEntity.ok(new ChunkResponse(
                                parsed.filePath,
                                parsed.startLine,
                                parsed.endLine,
                                chunk.getContent()
                        ))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record FileResponse(String path, String content) {}
    public record ChunkResponse(String path, int startLine, int endLine, String content) {}

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
