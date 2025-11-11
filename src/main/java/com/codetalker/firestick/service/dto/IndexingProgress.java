package com.codetalker.firestick.service.dto;

public record IndexingProgress(
        Long jobId,
        String status,
        int filesDiscovered,
        int filesParsed,
        int filesSkipped,
        int chunksProduced,
        int documentsIndexed,
        int embeddingsGenerated,
        int percent
) {}
