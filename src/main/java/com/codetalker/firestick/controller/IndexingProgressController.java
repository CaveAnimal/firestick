package com.codetalker.firestick.controller;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.codetalker.firestick.config.SseProperties;
import com.codetalker.firestick.exception.ErrorResponse;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.ProgressBus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping(path = "/api/indexing")
public class IndexingProgressController {

    private final ProgressBus progressBus;
    private final IndexingJobRepository jobRepository;
    private final ScheduledExecutorService heartbeatExecutor;
    private final SseProperties sseProperties;

    public IndexingProgressController(ProgressBus progressBus,
                                      IndexingJobRepository jobRepository,
                                      ScheduledExecutorService sseHeartbeatExecutor,
                                      SseProperties sseProperties) {
        this.progressBus = progressBus;
        this.jobRepository = jobRepository;
        this.heartbeatExecutor = sseHeartbeatExecutor;
        this.sseProperties = sseProperties;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Job progress stream")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SSE stream"),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SseEmitter> stream(@RequestParam(name = "jobId", required = false)
                             @jakarta.validation.constraints.Min(1) Long jobId) {
        Long id = jobId;
        if (id == null) {
            id = jobRepository.findTopByOrderByStartedAtDesc().map(j -> j.getId()).orElse(0L);
        }
        SseEmitter emitter = progressBus.register(id);
        // Send an initial no-op comment to immediately commit the response and set Content-Type
        try {
            emitter.send(SseEmitter.event().comment("open"));
        } catch (Exception ignored) {
            // If the client disconnects instantly or sending fails, we still return the emitter
        }
        // Schedule lightweight heartbeat comments to keep connections alive through proxies
        final long interval = Math.max(1000L, sseProperties.getHeartbeatMs());
        final ScheduledFuture<?> future = heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("h"));
            } catch (Exception ignored) {
                // If sending fails (client gone), cancel on next callback cleanup
            }
        }, interval, interval, TimeUnit.MILLISECONDS);
        // Ensure cleanup of scheduled heartbeat when emitter lifecycle ends
        Runnable cancel = () -> {
            try { future.cancel(true); } catch (Exception ignored2) {}
        };
        emitter.onCompletion(cancel);
        emitter.onTimeout(cancel);
        emitter.onError(ex -> cancel.run());
        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(emitter);
    }
}
