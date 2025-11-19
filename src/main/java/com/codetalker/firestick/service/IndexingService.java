package com.codetalker.firestick.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.dto.IndexingReport;
import com.codetalker.firestick.service.dto.IndexingRequest;

/**
 * Coordinates the indexing workflow: discover -> parse -> chunk -> index -> embed (mock).
 * Persistence is intentionally out-of-scope for this initial orchestrator.
 */
@Service
public class IndexingService {
    private static final Logger log = LoggerFactory.getLogger(IndexingService.class);

    private final FileDiscoveryService fileDiscoveryService;
    private final CodeParserService codeParserService;
    private final CodeSearchService codeSearchService;
    private final EmbeddingService embeddingService;
    private final IndexingJobRepository jobRepository;
    private final CodeFileRepository codeFileRepository;
    private final CodeChunkRepository codeChunkRepository;
    private final ProgressBus progressBus;
    private final IndexingJobControl jobControl;

    public IndexingService(
            FileDiscoveryService fileDiscoveryService,
            CodeParserService codeParserService,
            CodeSearchService codeSearchService,
            EmbeddingService embeddingService,
            IndexingJobRepository jobRepository,
            CodeFileRepository codeFileRepository,
            CodeChunkRepository codeChunkRepository,
            ProgressBus progressBus,
            IndexingJobControl jobControl) {
        this.fileDiscoveryService = fileDiscoveryService;
        this.codeParserService = codeParserService;
        this.codeSearchService = codeSearchService;
        this.embeddingService = embeddingService;
        this.jobRepository = jobRepository;
        this.codeFileRepository = codeFileRepository;
        this.codeChunkRepository = codeChunkRepository;
        this.progressBus = progressBus;
        this.jobControl = jobControl;
    }

    @Transactional
    public IndexingReport index(IndexingRequest request) {
    long startedAt = System.currentTimeMillis();
    IndexingJob job = new IndexingJob();
    job.setRootPath(request.rootPath());
    
    // Auto-derive app name from folder name if not explicitly provided
    String appName = request.appName();
    if (appName == null || appName.isBlank() || "default".equals(appName)) {
        appName = deriveAppNameFromPath(request.rootPath());
    }
    job.setAppName(appName);
    job.setStatus(IndexingJob.Status.RUNNING);
    job.setStartedAt(java.time.Instant.ofEpochMilli(startedAt));
    job = jobRepository.save(job);
        List<String> errors = new ArrayList<>();

        // 1) Discover
        List<Path> files = (request.excludeDirNames() != null || request.excludeGlobPatterns() != null)
                ? fileDiscoveryService.scanDirectory(request.rootPath(),
                    request.excludeDirNames() == null ? List.of() : request.excludeDirNames(),
                    request.excludeGlobPatterns() == null ? List.of() : request.excludeGlobPatterns())
                : fileDiscoveryService.scanDirectory(request.rootPath());
    int discovered = files.size();
    log.info("[Indexing] Discovered {} files under {}", discovered, request.rootPath());
    // publish initial progress
    progressBus.publish(job.getId(), new com.codetalker.firestick.service.dto.IndexingProgress(
        job.getId(), job.getStatus().name(), discovered, 0, 0, 0, 0, 0, 0));

        AtomicInteger parsedCount = new AtomicInteger();
    AtomicInteger chunkCount = new AtomicInteger();
    AtomicInteger skippedCount = new AtomicInteger();
        AtomicInteger indexedDocs = new AtomicInteger();
        AtomicInteger embeddings = new AtomicInteger();

        // 2) Parse -> chunk is handled inside CodeParserService.parseFile()
    for (Path p : files) {
            // cooperative cancellation check before processing each file
            if (jobControl.isCancelled(job.getId())) {
                log.info("[Indexing] Cancellation requested for job {}. Stopping.", job.getId());
                break;
            }
            try {
                // Incremental check by lastModified timestamp
                java.time.Instant fsLastModified = java.nio.file.Files.getLastModifiedTime(p).toInstant();
                var existing = codeFileRepository.findByFilePathAndAppName(p.toString(), request.appName());
                if (existing.isPresent() && fsLastModified.equals(existing.get().getLastModified())) {
                    skippedCount.incrementAndGet();
                    continue;
                }

                CodeFile codeFile = codeParserService.parseFile(p.toString());
                codeFile.setAppName(request.appName());
                parsedCount.incrementAndGet();
                List<CodeChunk> chunks = codeFile.getChunks();
                if (chunks != null) {
                    chunkCount.addAndGet(chunks.size());
                    // 3) Index & 4) Embed (mock)
                    for (CodeChunk c : chunks) {
                        String id = buildDocId(codeFile, c);
                        String content = c.getContent() == null ? "" : c.getContent();
                        // propagate tenant/app name into chunk & index
                        c.setAppName(request.appName());
                        codeSearchService.indexCode(id, request.appName(), content);
                        indexedDocs.incrementAndGet();
                        float[] vec = embeddingService.getEmbedding(content);
                        if (vec != null && vec.length > 0) embeddings.incrementAndGet();
                    }
                    int percent = discovered == 0 ? 0 : (int) Math.min(100, Math.round((parsedCount.get() * 100.0) / discovered));
                    progressBus.publish(job.getId(), new com.codetalker.firestick.service.dto.IndexingProgress(
                            job.getId(), job.getStatus().name(), discovered, parsedCount.get(), skippedCount.get(),
                            chunkCount.get(), indexedDocs.get(), embeddings.get(), percent));

                    // Persist file and replace chunks
                    CodeFile managedFile;
                    if (existing.isPresent()) {
                        managedFile = existing.get();
                        managedFile.setLastModified(codeFile.getLastModified());
                        managedFile.setHash(codeFile.getHash());
                        managedFile.setAppName(request.appName());
                        managedFile = codeFileRepository.save(managedFile);
                        // remove old chunks for this file
                        var oldChunks = codeChunkRepository.findByFile(managedFile);
                        if (oldChunks != null && !oldChunks.isEmpty()) {
                            codeChunkRepository.deleteAll(oldChunks);
                        }
                    } else {
                        // Backward-compat: if legacy unique constraint exists (by filePath), try to reuse that row
                        var legacyExisting = codeFileRepository.findByFilePath(codeFile.getFilePath());
                        if (legacyExisting.isPresent()) {
                            managedFile = legacyExisting.get();
                            managedFile.setAppName(request.appName());
                            managedFile.setLastModified(codeFile.getLastModified());
                            managedFile.setHash(codeFile.getHash());
                            managedFile = codeFileRepository.save(managedFile);
                        } else {
                            managedFile = codeFileRepository.save(new CodeFile(codeFile.getAppName(), codeFile.getFilePath(), codeFile.getLastModified(), codeFile.getHash()));
                        }
                    }
                    for (CodeChunk c : chunks) {
                        c.setFile(managedFile);
                        codeChunkRepository.save(c);
                    }
                }
            } catch (java.io.IOException e) {
                String msg = "IO error indexing file: " + p + " -> " + e.getMessage();
                log.warn("[Indexing] {}", msg, e);
                errors.add(msg);
            } catch (com.codetalker.firestick.exception.CodeParsingException e) {
                String msg = "Parsing error indexing file: " + p + " -> " + e.getMessage();
                log.warn("[Indexing] {}", msg, e);
                errors.add(msg);
            } catch (org.springframework.dao.DataAccessException e) {
                String msg = "Database error while indexing file: " + p + " -> " + e.getMessage();
                log.warn("[Indexing] {}", msg, e);
                errors.add(msg);
            }
        }

        long endedAt = System.currentTimeMillis();

        // Update job status and metrics
        try {
            job.setEndedAt(java.time.Instant.ofEpochMilli(endedAt));
            job.setFilesDiscovered(discovered);
            job.setFilesParsed(parsedCount.get());
            job.setChunksProduced(chunkCount.get());
            job.setDocumentsIndexed(indexedDocs.get());
            job.setEmbeddingsGenerated(embeddings.get());
            job.setErrorCount(errors.size());
            if (!errors.isEmpty()) {
                job.setErrorSummary(String.join("\n", errors));
            }
            if (jobControl.isCancelled(job.getId())) {
                job.setStatus(IndexingJob.Status.CANCELLED);
            } else {
                job.setStatus(IndexingJob.Status.SUCCESS);
            }
        } catch (Exception e) {
            job.setStatus(IndexingJob.Status.FAILED);
        } finally {
            jobRepository.save(job);
            int percent = 100;
            progressBus.publish(job.getId(), new com.codetalker.firestick.service.dto.IndexingProgress(
                    job.getId(), job.getStatus().name(), discovered, parsedCount.get(), skippedCount.get(),
                    chunkCount.get(), indexedDocs.get(), embeddings.get(), percent));
            progressBus.complete(job.getId());
            jobControl.clear(job.getId());
        }

    return new IndexingReport(
        job.getId(),
        request.rootPath(),
        discovered,
        parsedCount.get(),
        skippedCount.get(),
        chunkCount.get(),
        indexedDocs.get(),
        embeddings.get(),
        startedAt,
        endedAt,
        errors
    );
    }

    private String deriveAppNameFromPath(String rootPath) {
        if (rootPath == null || rootPath.isBlank()) {
            return "default";
        }
        
        // Extract the last directory component from the path
        // Handle both Windows (C:\path\to\folder) and Unix (/path/to/folder) paths
        String[] pathParts = rootPath.replaceAll("\\\\", "/").split("/");
        String folderName = "";
        
        // Get the last non-empty component
        for (int i = pathParts.length - 1; i >= 0; i--) {
            if (!pathParts[i].isBlank()) {
                folderName = pathParts[i];
                break;
            }
        }
        
        if (folderName.isBlank()) {
            return "default";
        }
        
        // Sanitize: lowercase, replace non-alphanumeric with underscore, collapse repeats, trim underscores
        String sanitized = folderName.toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_{2,}", "_")
                .replaceAll("^_+|_+$", "");
        
        return sanitized.isBlank() ? "default" : sanitized;
    }

    public List<String> getAvailableApps() {
        // Combine distinct app names observed in indexing jobs and code files
        try {
            List<String> jobs = jobRepository.findDistinctAppNames();
            List<String> files = codeFileRepository.findDistinctAppNames();
            java.util.Set<String> union = new java.util.TreeSet<>(); // TreeSet keeps natural order (alphabetical)
            if (jobs != null) union.addAll(jobs);
            if (files != null) union.addAll(files);
            return new ArrayList<>(union);
        } catch (Exception e) {
            log.warn("Failed to retrieve apps from both jobs and code files, falling back to jobs only", e);
            return jobRepository.findDistinctAppNames();
        }
    }

    private static String buildDocId(CodeFile file, CodeChunk chunk) {
        String path = file.getFilePath();
        String type = chunk.getType() == null ? "chunk" : chunk.getType();
        return path + "#" + type + ":" + chunk.getStartLine() + "-" + chunk.getEndLine();
    }
}
