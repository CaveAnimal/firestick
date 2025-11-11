package com.codetalker.firestick.service.dto;

import java.util.List;

/**
 * Summary of an indexing run.
 */
public record IndexingReport(
        Long jobId,
        String rootPath,
        int filesDiscovered,
        int filesParsed,
        int filesSkipped,
        int chunksProduced,
        int documentsIndexed,
        int embeddingsGenerated,
        long startedAtMillis,
        long endedAtMillis,
        List<String> errors
) {
    public long durationMillis() { return endedAtMillis - startedAtMillis; }
}
