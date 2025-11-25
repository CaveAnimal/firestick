package com.codetalker.firestick.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.dto.IndexingReport;
import com.codetalker.firestick.service.dto.IndexingRequest;

@SpringBootTest
@ActiveProfiles("test")
class IndexingServiceTest {

    @Autowired
    private IndexingService indexingService;

    @Autowired
    private IndexingJobRepository jobRepository;

    @Autowired
    private CodeFileRepository codeFileRepository;

    @Autowired
    private CodeChunkRepository codeChunkRepository;

    @Autowired
    private com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository;

    @Autowired
    private CodeParserService codeParserService;

    @BeforeEach
    void setUp() {
        // Clean up any stale lucene locks that might interfere with indexing during tests
        try { java.nio.file.Files.deleteIfExists(java.nio.file.Path.of("target/test-lucene-indices/default/write.lock")); } catch (Exception ignored) {}
        // Clean up database to ensure fresh state for incremental indexing logic
        codeChunkRepository.deleteAll();
        codeFileRepository.deleteAll();
        jobRepository.deleteAll();
    }

    @Test
    void indexesSampleDirectory() {
        IndexingReport report = indexingService.index(IndexingRequest.of("src/test/resources/test-data/sample-code"));

        assertThat(report).isNotNull();
    assertThat(report.jobId()).isNotNull();
        assertThat(report.filesDiscovered()).isGreaterThanOrEqualTo(1);
        assertThat(report.filesParsed()).isEqualTo(report.filesDiscovered());
        assertThat(report.chunksProduced()).isGreaterThanOrEqualTo(1);
        // Each chunk is indexed and embedded (mock), so these should be >= chunks
        assertThat(report.documentsIndexed()).isGreaterThanOrEqualTo(report.chunksProduced());
        assertThat(report.embeddingsGenerated()).isGreaterThanOrEqualTo(report.chunksProduced());
        // Additional summarization metrics
        assertThat(report.filesSummarized()).isGreaterThanOrEqualTo(0);
        assertThat(report.methodsSummarized()).isGreaterThanOrEqualTo(0);
        assertThat(report.foldersSummarized()).isGreaterThanOrEqualTo(0);
        // Totals for folders & methods should be present
        assertThat(report.totalFolders()).isGreaterThanOrEqualTo(1);
        assertThat(report.totalMethods()).isGreaterThanOrEqualTo(0);
        // Logical relationships: summarized counts should not exceed their respective totals
        assertThat(report.methodsSummarized()).isLessThanOrEqualTo(report.chunksProduced());
        assertThat(report.filesSummarized()).isLessThanOrEqualTo(report.filesParsed());
    assertThat(report.filesSkipped()).isGreaterThanOrEqualTo(0);
        assertThat(report.durationMillis()).isGreaterThanOrEqualTo(0);
        assertThat(report.errors()).isEmpty();

        // Minimal verification that a job record was created and matches the report
    var latest = jobRepository.findTopByOrderByStartedAtDesc();
        assertThat(latest).isPresent();
        IndexingJob job = latest.get();
    assertThat(job.getId()).isEqualTo(report.jobId());
        assertThat(job.getStatus()).isEqualTo(IndexingJob.Status.SUCCESS);
        assertThat(job.getRootPath()).isEqualTo("src/test/resources/test-data/sample-code");
        assertThat(job.getFilesDiscovered()).isEqualTo(report.filesDiscovered());
        assertThat(job.getTotalFolders()).isEqualTo(report.totalFolders());
        assertThat(job.getMethodsSummarized()).isLessThanOrEqualTo(report.methodsSummarized());
        assertThat(job.getFilesParsed()).isEqualTo(report.filesParsed());
        assertThat(job.getChunksProduced()).isEqualTo(report.chunksProduced());
        assertThat(job.getDocumentsIndexed()).isEqualTo(report.documentsIndexed());
        assertThat(job.getEmbeddingsGenerated()).isEqualTo(report.embeddingsGenerated());

        // Persistence smoke-check: CodeFile and its chunks are stored
        String filePath = java.nio.file.Path.of("src/test/resources/test-data/sample-code", "TestA.java").toString();
        var codeFileOpt = codeFileRepository.findByFilePath(filePath);
        assertThat(codeFileOpt).isPresent();
        var chunks = codeChunkRepository.findByFile(codeFileOpt.get());
        // Compute expected chunk count for this specific file using the parser/chunker
        com.codetalker.firestick.model.CodeFile parsed = codeParserService.parseFile(filePath);
        int expectedChunksForFile = parsed.getChunks() != null ? parsed.getChunks().size() : 0;
        assertThat(chunks.size()).isEqualTo(expectedChunksForFile);

        // Verify IndexingObject rows were created for this job (including document objects)
        var objs = indexingObjectRepository.findByJobId(job.getId());
        assertThat(objs).isNotEmpty();
        // we expect at least one DOCUMENT object for indexed chunks
        assertThat(objs.stream().anyMatch(o -> "DOCUMENT".equalsIgnoreCase(o.getObjectType()))).isTrue();
    }
}
