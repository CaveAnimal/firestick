package com.codetalker.firestick.service.dto;

public record IndexingProgress(
        Long jobId,
        String status,
        int filesDiscovered,
        int totalFolders,
        int totalMethods,
        int filesParsed,
        int filesSkipped,
        int chunksProduced,
        int documentsIndexed,
        int embeddingsGenerated,
        int percent,
        String currentFile,
        int filesSummarized,
        int foldersSummarized,
        int methodsSummarized,
        java.util.List<com.codetalker.firestick.service.dto.IndexingReport.SkippedFile> skippedFiles
) {}
