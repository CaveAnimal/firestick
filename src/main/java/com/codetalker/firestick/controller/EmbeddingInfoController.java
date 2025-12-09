package com.codetalker.firestick.controller;

import com.codetalker.firestick.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Exposes current embedding configuration (mode, dimension, model path, etc.) so users can
 * verify whether ONNX is active or the application is still running in MOCK mode.
 */
@RestController
@RequestMapping(path = "/api/embedding", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmbeddingInfoController {

    private final EmbeddingService embeddingService;
    private final Environment environment;

    public EmbeddingInfoController(EmbeddingService embeddingService, Environment environment) {
        this.embeddingService = embeddingService;
        this.environment = environment;
    }

    @GetMapping("/info")
    @Operation(summary = "Show current embedding configuration (mode, dimension, model paths, active profiles)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Map.class)))
    })
    public Map<String, Object> info() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("mode", embeddingService.getMode().name());
        out.put("dimension", embeddingService.getDimension());
        out.put("modelPath", emptyIfBlank(embeddingService.getModelPath()));
        out.put("tokenizerPath", emptyIfBlank(embeddingService.getTokenizerPath()));
        out.put("maxSeqLen", embeddingService.getMaxSeqLen());
        out.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        return out;
    }

    private String emptyIfBlank(String s) {
        return (s == null || s.isBlank()) ? "" : s;
    }
}

