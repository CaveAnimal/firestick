package com.codetalker.firestick.service.dto;

import java.util.List;

/**
 * Summary of an indexing run.
 */
public record IndexingReport(
        Long jobId,
        String status,
        String rootPath,
        int filesDiscovered,
        int totalFolders,
        int totalMethods,
        int filesParsed,
        int filesSkipped,
        int chunksProduced,
        int documentsIndexed,
        int embeddingsGenerated,
        long startedAtMillis,
        long endedAtMillis,
        List<String> errors,
        int filesSummarized,
        int foldersSummarized,
        int methodsSummarized,
        List<SkippedFile> skippedFiles
) {
    public long durationMillis() { return endedAtMillis - startedAtMillis; }

    public static record SkippedFile(String fileName, String reason) {}
}
