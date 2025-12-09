package com.codetalker.firestick.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.exception.ErrorResponse;
import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.IndexingJobRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard")
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final CodeFileRepository codeFileRepository;
    private final CodeChunkRepository codeChunkRepository;
    private final IndexingJobRepository indexingJobRepository;

    public DashboardController(CodeFileRepository codeFileRepository,
            CodeChunkRepository codeChunkRepository,
            IndexingJobRepository indexingJobRepository) {
        this.codeFileRepository = codeFileRepository;
        this.codeChunkRepository = codeChunkRepository;
        this.indexingJobRepository = indexingJobRepository;
    }

    @Operation(summary = "Dashboard summary", description = "Aggregate repository stats, hotspots, and recent indexing jobs.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Summary",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/summary")
    public Map<String, Object> summary() {
        long totalFiles = codeFileRepository.count();
        long totalClasses = safeCountByType("class");
        long totalMethods = safeCountByType("method");

        // Build chart data
        List<Map<String, Object>> chart = new ArrayList<>();
        chart.add(entry("Files", totalFiles));
        chart.add(entry("Classes", totalClasses));
        chart.add(entry("Methods", totalMethods));

        // Hotspots: top files by method count (best-effort, limited for perf)
        List<Map<String, Object>> hotspots = new ArrayList<>();
        List<CodeFile> files = codeFileRepository.findAll();
        int considered = 0;
        java.util.Map<Long, Integer> methodsPerFile = new java.util.HashMap<>();
        for (CodeFile f : files) {
            // Count methods in this file
            int count = 0;
            List<CodeChunk> chunks = codeChunkRepository.findByFile(f);
            if (chunks != null) for (CodeChunk c : chunks) {
                if (c.getType() != null && c.getType().equalsIgnoreCase("method")) count++;
            }
            methodsPerFile.put(f.getId(), count);
            if (++considered >= 500) break; // soft cap to avoid scanning huge datasets
        }
        methodsPerFile.entrySet().stream()
            .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(10)
            .forEach(e -> {
                CodeFile f = files.stream().filter(x -> x.getId().equals(e.getKey())).findFirst().orElse(null);
                if (f != null) hotspots.add(map("name", f.getFilePath(), "count", e.getValue()));
            });

        // Recent indexing jobs
        List<Map<String, Object>> recentJobs = new ArrayList<>();
        List<IndexingJob> jobs;
        try {
            jobs = indexingJobRepository.findTop10ByOrderByStartedAtDesc();
        } catch (Exception ex) {
            // fallback if method not present (older repos)
            jobs = indexingJobRepository.findAll().stream()
                .sorted((a,b) -> java.util.Comparator.nullsLast(java.util.Comparator.<java.time.Instant>naturalOrder())
                    .compare(b.getStartedAt(), a.getStartedAt()))
                .limit(10)
                .toList();
        }
        for (IndexingJob j : jobs) {
            recentJobs.add(map(
                "id", j.getId(),
                "status", j.getStatus() == null ? null : j.getStatus().name(),
                "startedAt", j.getStartedAt(),
                "endedAt", j.getEndedAt()
            ));
        }

        Map<String, Object> out = new HashMap<>();
        out.put("stats", map(
            "totalFiles", totalFiles,
            "totalClasses", totalClasses,
            "totalMethods", totalMethods,
            "hotspotCount", hotspots.size()
        ));
        out.put("chart", chart);
        out.put("hotspots", hotspots);
        out.put("recentJobs", recentJobs);
        log.info("[Dashboard] summary files={}, classes={}, methods={}", totalFiles, totalClasses, totalMethods);
        return out;
    }

    private long safeCountByType(String type) {
        try { return codeChunkRepository.countByType(type); }
        catch (Exception ex) { return 0L; }
    }

    private static Map<String, Object> entry(String name, long value) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("value", value);
        return m;
    }
    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i=0; i<kv.length-1; i+=2) m.put(String.valueOf(kv[i]), kv[i+1]);
        return m;
    }
}
