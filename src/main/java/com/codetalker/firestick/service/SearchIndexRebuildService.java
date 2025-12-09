package com.codetalker.firestick.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.repository.CodeChunkRepository;

/**
 * Service to rebuild the Lucene search index from database chunks.
 * Used when the index is lost but data exists in the database (e.g., after restart).
 */
@Service
public class SearchIndexRebuildService {

    private static final Logger log = LoggerFactory.getLogger(SearchIndexRebuildService.class);

    private final CodeChunkRepository chunkRepository;
    private final CodeSearchService codeSearchService;

    public SearchIndexRebuildService(
            CodeChunkRepository chunkRepository,
            CodeSearchService codeSearchService) {
        this.chunkRepository = chunkRepository;
        this.codeSearchService = codeSearchService;
    }

    /**
     * Rebuild the Lucene search index from all chunks in the database.
     * This is useful after application restart when the in-memory index is lost.
     *
     * @return Number of chunks indexed
     * @throws Exception if rebuild fails
     */
    @Transactional(readOnly = true)
    public long rebuildIndexFromDatabase() throws Exception {
        log.info("Starting search index rebuild from database...");
        
        // Fetch all chunks from database
        List<CodeChunk> chunks = chunkRepository.findAll();
        
        if (chunks.isEmpty()) {
            log.warn("No chunks found in database to rebuild index");
            return 0;
        }
        
        log.info("Found {} chunks in database, rebuilding Lucene index...", chunks.size());
        
        long indexedCount = 0;
        long startTime = System.currentTimeMillis();
        
        for (CodeChunk chunk : chunks) {
            try {
                // Build a unique ID for the chunk
                // Format: filePath:startLine-endLine (same as IndexingService#buildDocId)
                String docId = buildDocId(
                    chunk.getFile().getFilePath(),
                    chunk.getStartLine(),
                    chunk.getEndLine()
                );
                
                // Index the chunk content
                String appName = chunk.getAppName() != null ? chunk.getAppName() : "default";
                codeSearchService.indexCode(docId, appName, chunk.getContent());
                
                indexedCount++;
                
                if (indexedCount % 100 == 0) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    log.info("Progress: {} chunks indexed in {} ms", indexedCount, elapsed);
                }
            } catch (Exception e) {
                log.error("Failed to index chunk {}", chunk.getId(), e);
                // Continue with next chunk instead of failing completely
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Search index rebuild completed: {} chunks indexed in {} ms", indexedCount, totalTime);
        
        return indexedCount;
    }

    /**
     * Build a document ID for a code chunk.
     * Format: filePath:startLine-endLine
     * This matches the format used by IndexingService#buildDocId
     */
    private String buildDocId(String filePath, int startLine, int endLine) {
        return filePath + ":" + startLine + "-" + endLine;
    }
}
