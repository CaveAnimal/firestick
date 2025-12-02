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
    private final com.codetalker.firestick.llm.LLMServiceClient llmServiceClient;
    private final SummaryAggregationService summaryAggregationService;
    private final ProgressBus progressBus;
        private final com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final IndexingJobControl jobControl;

    public IndexingService(
            FileDiscoveryService fileDiscoveryService,
            CodeParserService codeParserService,
            CodeSearchService codeSearchService,
            EmbeddingService embeddingService,
            IndexingJobRepository jobRepository,
            CodeFileRepository codeFileRepository,
            CodeChunkRepository codeChunkRepository,
            com.codetalker.firestick.llm.LLMServiceClient llmServiceClient,
            SummaryAggregationService summaryAggregationService,
            ProgressBus progressBus,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper,
            IndexingJobControl jobControl,
            com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository) {
        this.fileDiscoveryService = fileDiscoveryService;
        this.codeParserService = codeParserService;
        this.codeSearchService = codeSearchService;
        this.embeddingService = embeddingService;
        this.jobRepository = jobRepository;
        this.codeFileRepository = codeFileRepository;
        this.codeChunkRepository = codeChunkRepository;
        this.llmServiceClient = llmServiceClient;
        this.summaryAggregationService = summaryAggregationService;
        this.progressBus = progressBus;
            this.indexingObjectRepository = indexingObjectRepository;
        this.objectMapper = objectMapper;
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
    // Compute folder & method totals before heavy processing so the UI can display totals
    java.util.Set<String> folders = new java.util.HashSet<>();
    java.util.Map<java.nio.file.Path, com.codetalker.firestick.model.CodeFile> parsedFiles = new java.util.HashMap<>();
    int totalMethods = 0;
    for (Path p : files) {
        Path parent = p.getParent();
        if (parent != null) folders.add(parent.toString());
        try {
            // pre-parse to count methods (lightweight use of parser)
            com.codetalker.firestick.model.CodeFile cf = codeParserService.parseFile(p.toString());
            parsedFiles.put(p, cf);
            if (cf.getChunks() != null) {
                for (com.codetalker.firestick.model.CodeChunk ck : cf.getChunks()) {
                    if ("method".equalsIgnoreCase(ck.getType())) totalMethods++;
                }
            }
        } catch (Exception ex) {
            // parsing may fail for some non-Java or malformed files — ignore for totals
        }
    }
    int totalFolders = folders.size();
    job.setFilesDiscovered(discovered);
    job.setTotalFolders(totalFolders);
    job.setTotalMethods(totalMethods);
    job = jobRepository.save(job);

    // Persist per-object rows (folders / files / methods)
    try {
        java.util.List<com.codetalker.firestick.model.IndexingObject> objs = new java.util.ArrayList<>();
        for (String folder : folders) {
            com.codetalker.firestick.model.IndexingObject o = new com.codetalker.firestick.model.IndexingObject();
            o.setJobId(job.getId()); o.setObjectType("FOLDER"); o.setObjectName(folder);
            objs.add(o);
        }
        for (Path p : files) {
            com.codetalker.firestick.model.IndexingObject o = new com.codetalker.firestick.model.IndexingObject();
            o.setJobId(job.getId()); o.setObjectType("FILE"); o.setObjectName(p.toString());
            objs.add(o);
            // add methods discovered for this file
            com.codetalker.firestick.model.CodeFile cf = parsedFiles.get(p);
            if (cf != null && cf.getChunks() != null) {
                for (com.codetalker.firestick.model.CodeChunk ck : cf.getChunks()) {
                    if ("method".equalsIgnoreCase(ck.getType())) {
                        com.codetalker.firestick.model.IndexingObject mo = new com.codetalker.firestick.model.IndexingObject();
                        mo.setJobId(job.getId());
                        mo.setObjectType("METHOD");
                        mo.setObjectName(p.toString() + "#" + (ck.getName() == null ? "method" : ck.getName()));
                        objs.add(mo);
                    }
                }
            }
        }
        if (!objs.isEmpty()) indexingObjectRepository.saveAll(objs);
    } catch (Exception ex) {
        // non-fatal; log and continue
        log.warn("Failed to persist indexing objects for job {}: {}", job.getId(), ex.getMessage());
    }

    // publish initial progress with totals
    progressBus.publish(job.getId(), new com.codetalker.firestick.service.dto.IndexingProgress(
        job.getId(), job.getStatus().name(), discovered, totalFolders, totalMethods, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, java.util.List.of()));

        AtomicInteger parsedCount = new AtomicInteger();
    AtomicInteger chunkCount = new AtomicInteger();
    AtomicInteger skippedCount = new AtomicInteger();
        AtomicInteger indexedDocs = new AtomicInteger();
        AtomicInteger embeddings = new AtomicInteger();
        AtomicInteger filesSummarized = new AtomicInteger();
        AtomicInteger methodsSummarized = new AtomicInteger();
        java.util.List<com.codetalker.firestick.service.dto.IndexingReport.SkippedFile> skippedFiles = new java.util.ArrayList<>();

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
                
                // Skip ONLY if file hasn't changed AND we already have a summary
                // This ensures previously indexed files get re-processed to generate summaries
                if (existing.isPresent() && 
                    fsLastModified.equals(existing.get().getLastModified()) && 
                    existing.get().getSummary() != null && !existing.get().getSummary().isBlank()) {
                    skippedCount.incrementAndGet();
                    // mark per-file object as skipped with reason
                    try {
                        var fo = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString());
                        if (fo.isPresent()) {
                            var o = fo.get();
                            o.setReasonSkipped("unchanged");
                            o.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                            if (o.getStartedAt() != null) o.setElapsedMs(java.time.Duration.between(o.getStartedAt(), o.getEndedAt()).toMillis());
                            indexingObjectRepository.save(o);
                            progressBus.publish(job.getId(), java.util.Map.of("event","object-skipped","type","FILE","name", p.toString(), "reason","unchanged"));
                        }
                    } catch (Exception ex) {
                        log.debug("Failed to mark file object skipped for {}: {}", p, ex.getMessage());
                    }
                    continue;
                }

                // mark file object started (best-effort)
                try {
                    var fo = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString());
                    if (fo.isPresent()) {
                        var o = fo.get();
                        o.setStartedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                        indexingObjectRepository.save(o);
                        progressBus.publish(job.getId(), java.util.Map.of("event","object-start","type","FILE","name", p.toString(), "ts", System.currentTimeMillis()));
                    }
                } catch (Exception ex) {
                    log.debug("Failed to mark file object started for {}: {}", p, ex.getMessage());
                }

                // Use pre-parsed result when available
                CodeFile codeFile = parsedFiles.containsKey(p) ? parsedFiles.get(p) : codeParserService.parseFile(p.toString());
                
                // Generate Summary using LLM
                try {
                    String content = java.nio.file.Files.readString(p);
                    // Truncate if too large for LLM context (simple safeguard)
                    if (content.length() > 32000) {
                        content = content.substring(0, 32000) + "\n... (truncated)";
                    }
                    String summary = llmServiceClient.summarize(content);
                    codeFile.setSummary(summary);
                    if (summary != null && !summary.isBlank()) {
                        filesSummarized.incrementAndGet();
                    }
                } catch (Exception e) {
                    log.warn("Failed to generate summary for file: {}", p, e);
                    // Continue without summary
                }

                codeFile.setAppName(request.appName());
                parsedCount.incrementAndGet();
                List<CodeChunk> chunks = codeFile.getChunks();
                if (chunks != null) {
                    chunkCount.addAndGet(chunks.size());
                    // 3) Index & 4) Embed (mock)
                    // per-file progress tracking
                    int totalChunksForFile = (chunks == null) ? 0 : chunks.size();
                    int processedForFile = 0;
                    long lastProgressEmitMs = 0L;
                    for (CodeChunk c : chunks) {
                        // method-level per-object timing
                        com.codetalker.firestick.model.IndexingObject methodObj = null;
                        // Method-Level Summarization
                        if ("method".equals(c.getType()) && c.getContent().length() > 500) { // Only summarize methods > ~500 chars
                            try {
                                // mark method object started (best-effort)
                                try {
                                    String mName = c.getName() == null ? "method" : c.getName();
                                    var opt = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString() + "#" + mName);
                                    if (opt.isPresent()) {
                                        methodObj = opt.get();
                                        methodObj.setStartedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                                        indexingObjectRepository.save(methodObj);
                                        progressBus.publish(job.getId(), java.util.Map.of("event","object-start","type","METHOD","name", methodObj.getObjectName(), "ts", System.currentTimeMillis()));
                                    }
                                } catch (Exception ignore) {}
                                String methodSummary = llmServiceClient.summarize(c.getContent());
                                c.setSummary(methodSummary);
                                if (methodSummary != null && !methodSummary.isBlank()) {
                                    methodsSummarized.incrementAndGet();
                                }
                            } catch (Exception e) {
                                log.warn("Failed to summarize method chunk: {}", c.getName(), e);
                            }
                        }

                        String id = buildDocId(codeFile, c);
                        // Ensure we persist a DOCUMENT object for this chunk so the UI/db can show document-level progress
                        com.codetalker.firestick.model.IndexingObject docObj = null;
                        try {
                            var opt = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), id);
                            if (opt.isPresent()) {
                                docObj = opt.get();
                            } else {
                                docObj = new com.codetalker.firestick.model.IndexingObject();
                                docObj.setJobId(job.getId());
                                docObj.setObjectType("DOCUMENT");
                                docObj.setObjectName(id);
                                indexingObjectRepository.save(docObj);
                            }
                            // mark doc started
                            try {
                                docObj.setStartedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                                docObj.setEndedAt(null);
                                docObj.setElapsedMs(null);
                                indexingObjectRepository.save(docObj);
                                progressBus.publish(job.getId(), java.util.Map.of("event","object-start","type","DOCUMENT","name", id, "ts", System.currentTimeMillis()));
                            } catch (Exception ignore) {}
                        } catch (Exception ex) {
                            // best-effort - keep going even if doc object persistence fails
                            log.debug("Failed to create/mark document object {} for job {}: {}", id, job.getId(), ex.getMessage());
                        }
                        String content = c.getContent() == null ? "" : c.getContent();
                        // propagate tenant/app name into chunk & index
                        c.setAppName(request.appName());
                        try {
                            codeSearchService.indexCode(id, request.appName(), content);
                        } catch (com.codetalker.firestick.exception.IndexingException ie) {
                            // Lucene indexing problems (locks, IO) should be handled per-file rather than
                            // bubbling up and failing the entire indexing run. Record the error and continue.
                            String msg = "Indexing error for file " + p + " -> " + ie.getMessage();
                            log.warn("[Indexing] {}", msg, ie);
                            errors.add(msg);
                            skippedFiles.add(new com.codetalker.firestick.service.dto.IndexingReport.SkippedFile(p.toString(), ie.getMessage()));
                            // continue to next chunk/file
                            continue;
                        }
                        
                        // Index method summary if available
                        if (c.getSummary() != null && !c.getSummary().isBlank()) {
                            codeSearchService.indexSummary(id + "_summary", request.appName(), c.getSummary(), "method_summary");
                        }

                        // mark method object ended (best-effort)
                        if (methodObj != null) {
                            try {
                                methodObj.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                                if (methodObj.getStartedAt() != null) methodObj.setElapsedMs(java.time.Duration.between(methodObj.getStartedAt(), methodObj.getEndedAt()).toMillis());
                                indexingObjectRepository.save(methodObj);
                                progressBus.publish(job.getId(), java.util.Map.of("event","object-end","type","METHOD","name", methodObj.getObjectName(), "ts", System.currentTimeMillis(), "elapsedMs", methodObj.getElapsedMs()));
                            } catch (Exception ignore) {}
                        }

                        // mark document object ended (best-effort)
                        if (docObj != null) {
                            try {
                                docObj.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                                if (docObj.getStartedAt() != null) docObj.setElapsedMs(java.time.Duration.between(docObj.getStartedAt(), docObj.getEndedAt()).toMillis());
                                indexingObjectRepository.save(docObj);
                                progressBus.publish(job.getId(), java.util.Map.of("event","object-end","type","DOCUMENT","name", docObj.getObjectName(), "ts", System.currentTimeMillis(), "elapsedMs", docObj.getElapsedMs()));
                            } catch (Exception ignore) {}
                        }

                        indexedDocs.incrementAndGet();
                        // increment per-file processed counter and optionally emit object-progress
                        processedForFile++;
                        try {
                            // throttle to roughly 1/sec per-file to avoid SSE flooding
                            long nowMs = System.currentTimeMillis();
                            if (nowMs - lastProgressEmitMs >= 900 || processedForFile == totalChunksForFile) {
                                lastProgressEmitMs = nowMs;
                                progressBus.publish(job.getId(), java.util.Map.of(
                                    "event", "object-progress",
                                    "type", "FILE",
                                    "name", p.toString(),
                                    "objectWorkDone", processedForFile,
                                    "objectTotalWork", totalChunksForFile,
                                    "ts", nowMs
                                ));
                            }
                        } catch (Exception ignore) {}
                        float[] vec = embeddingService.getEmbedding(content);
                        if (vec != null && vec.length > 0) embeddings.incrementAndGet();
                    }
                        int percent = discovered == 0 ? 0 : (int) Math.min(100, Math.round((parsedCount.get() * 100.0) / discovered));
                            progressBus.publish(job.getId(), new com.codetalker.firestick.service.dto.IndexingProgress(
                                job.getId(), job.getStatus().name(), discovered, job.getTotalFolders(), job.getTotalMethods(), parsedCount.get(), skippedCount.get(),
                                chunkCount.get(), indexedDocs.get(), embeddings.get(), percent, p.toString(), filesSummarized.get(), 0, methodsSummarized.get(), new java.util.ArrayList<>(skippedFiles)));

                    // Persist file and replace chunks
                    CodeFile managedFile;
                    if (existing.isPresent()) {
                        managedFile = existing.get();
                        managedFile.setLastModified(codeFile.getLastModified());
                        managedFile.setHash(codeFile.getHash());
                        managedFile.setAppName(request.appName());
                        managedFile.setSummary(codeFile.getSummary());
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
                            managedFile.setSummary(codeFile.getSummary());
                            managedFile = codeFileRepository.save(managedFile);
                        } else {
                            managedFile = new CodeFile(codeFile.getAppName(), codeFile.getFilePath(), codeFile.getLastModified(), codeFile.getHash());
                            managedFile.setSummary(codeFile.getSummary());
                            managedFile = codeFileRepository.save(managedFile);
                        }
                    }
                    
                    // Index Summary
                    if (managedFile.getSummary() != null && !managedFile.getSummary().isBlank()) {
                        codeSearchService.indexSummary("summary_" + managedFile.getId(), request.appName(), managedFile.getSummary(), "file_summary");
                    }

                    for (CodeChunk c : chunks) {
                        c.setFile(managedFile);
                        codeChunkRepository.save(c);
                    }
                    // mark file object ended and set elapsed
                    try {
                        var fo = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString());
                        if (fo.isPresent()) {
                            var o = fo.get();
                            o.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                            if (o.getStartedAt() != null) o.setElapsedMs(java.time.Duration.between(o.getStartedAt(), o.getEndedAt()).toMillis());
                            indexingObjectRepository.save(o);
                            progressBus.publish(job.getId(), java.util.Map.of("event","object-end","type","FILE","name", o.getObjectName(), "ts", System.currentTimeMillis(), "elapsedMs", o.getElapsedMs()));
                        }
                    } catch (Exception ex) {
                        log.debug("Failed to mark file object ended for {}: {}", p, ex.getMessage());
                    }
                }
            } catch (java.io.IOException e) {
                String msg = "IO error indexing file: " + p + " -> " + e.getMessage();
                log.warn("[Indexing] {}", msg, e);
                errors.add(msg);
                skippedFiles.add(new com.codetalker.firestick.service.dto.IndexingReport.SkippedFile(p.toString(), e.getMessage()));
                // mark file object ended with failure reason
                try {
                    var fo = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString());
                    if (fo.isPresent()) {
                        var o = fo.get();
                        o.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                        if (o.getStartedAt() != null) o.setElapsedMs(java.time.Duration.between(o.getStartedAt(), o.getEndedAt()).toMillis());
                        o.setReasonSkipped(e.getMessage());
                        indexingObjectRepository.save(o);
                    }
                } catch (Exception ex) {
                    // ignore
                }
            } catch (com.codetalker.firestick.exception.CodeParsingException e) {
                String msg = "Parsing error indexing file: " + p + " -> " + e.getMessage();
                log.warn("[Indexing] {}", msg, e);
                errors.add(msg);
                skippedFiles.add(new com.codetalker.firestick.service.dto.IndexingReport.SkippedFile(p.toString(), e.getMessage()));
                try {
                    var fo = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString());
                    if (fo.isPresent()) {
                        var o = fo.get();
                        o.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                        if (o.getStartedAt() != null) o.setElapsedMs(java.time.Duration.between(o.getStartedAt(), o.getEndedAt()).toMillis());
                        o.setReasonSkipped(e.getMessage());
                        indexingObjectRepository.save(o);
                    }
                } catch (Exception ex) {}
            } catch (org.springframework.dao.DataAccessException e) {
                String msg = "Database error while indexing file: " + p + " -> " + e.getMessage();
                log.warn("[Indexing] {}", msg, e);
                errors.add(msg);
                skippedFiles.add(new com.codetalker.firestick.service.dto.IndexingReport.SkippedFile(p.toString(), e.getMessage()));
                try {
                    var fo = indexingObjectRepository.findFirstByJobIdAndObjectName(job.getId(), p.toString());
                    if (fo.isPresent()) {
                        var o = fo.get();
                        o.setEndedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis()));
                        if (o.getStartedAt() != null) o.setElapsedMs(java.time.Duration.between(o.getStartedAt(), o.getEndedAt()).toMillis());
                        o.setReasonSkipped(e.getMessage());
                        indexingObjectRepository.save(o);
                    }
                } catch (Exception ex) {}
            }
        }

        long endedAt = System.currentTimeMillis();

        // Trigger "Reduce" phase: Aggregate summaries by folder
        int foldersSummarizedCount = 0;
        if (errors.isEmpty() && !jobControl.isCancelled(job.getId())) {
            try {
                log.info("[Indexing] Starting Map-Reduce aggregation for app: {}", job.getAppName());
                foldersSummarizedCount = summaryAggregationService.aggregateSummaries(job.getAppName());
            } catch (Exception e) {
                log.error("[Indexing] Failed to aggregate summaries", e);
                errors.add("Summary aggregation failed: " + e.getMessage());
            }
        }

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
            // persist summarization counters and skipped files payload
            try {
                job.setFilesSummarized(filesSummarized.get());
                job.setFoldersSummarized(foldersSummarizedCount);
                job.setMethodsSummarized(methodsSummarized.get());
                if (skippedFiles != null && !skippedFiles.isEmpty()) {
                    job.setSkippedFiles(objectMapper.writeValueAsString(skippedFiles));
                } else {
                    job.setSkippedFiles(null);
                }
            } catch (Exception ignored) {
                // if JSON serialization fails, store nothing — non-fatal
            }
        } catch (Exception e) {
            job.setStatus(IndexingJob.Status.FAILED);
        } finally {
            jobRepository.save(job);
                int percent = 100;
                progressBus.publish(job.getId(), new com.codetalker.firestick.service.dto.IndexingProgress(
                    job.getId(), job.getStatus().name(), discovered, job.getTotalFolders(), job.getTotalMethods(), parsedCount.get(), skippedCount.get(),
                    chunkCount.get(), indexedDocs.get(), embeddings.get(), percent, null, filesSummarized.get(), foldersSummarizedCount, methodsSummarized.get(), new java.util.ArrayList<>(skippedFiles)));
            progressBus.complete(job.getId());
            jobControl.clear(job.getId());
        }

    return new IndexingReport(
        job.getId(),
        job.getStatus() == null ? "UNKNOWN" : job.getStatus().name(),
        request.rootPath(),
        discovered,
        job.getTotalFolders(),
        job.getTotalMethods(),
        parsedCount.get(),
        skippedCount.get(),
        chunkCount.get(),
        indexedDocs.get(),
        embeddings.get(),
        startedAt,
        endedAt,
        errors,
        filesSummarized.get(), // filesSummarized
        foldersSummarizedCount, // foldersSummarized
        methodsSummarized.get(), // methodsSummarized
        new java.util.ArrayList<>(skippedFiles) // skippedFiles
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
        // Combine distinct app names observed in indexing jobs, code files, and Lucene indices
        try {
            List<String> jobs = jobRepository.findDistinctAppNames();
            List<String> files = codeFileRepository.findDistinctAppNames();
            List<String> indices = codeSearchService.getAvailableApps();
            
            java.util.Set<String> union = new java.util.TreeSet<>(); // TreeSet keeps natural order (alphabetical)
            if (jobs != null) union.addAll(jobs);
            if (files != null) union.addAll(files);
            if (indices != null) union.addAll(indices);
            
            return new ArrayList<>(union);
        } catch (Exception e) {
            log.warn("Failed to retrieve apps from jobs, code files, and indices", e);
            // Fallback: try to get at least something
            try {
                return jobRepository.findDistinctAppNames();
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
    }

    private static String buildDocId(CodeFile file, CodeChunk chunk) {
        String path = file.getFilePath();
        String type = chunk.getType() == null ? "chunk" : chunk.getType();
        return path + "#" + type + ":" + chunk.getStartLine() + "-" + chunk.getEndLine();
    }
}
