package com.codetalker.firestick.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doAnswer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.codetalker.firestick.exception.CodeParsingException;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.dto.IndexingReport;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class IndexingSkippedFilesPersistenceTest {

    @Autowired
    private IndexingService indexingService;

    @Autowired
    private IndexingJobRepository jobRepository;

    @SpyBean
    private CodeParserService codeParserService;

    @Test
    void when_parser_throws_for_a_file_it_is_recorded_in_skippedFiles_and_persisted() {
        // Make sure lucene test index dir has no stale locks
        try { java.nio.file.Files.deleteIfExists(java.nio.file.Path.of("target/test-lucene-indices/default/write.lock")); } catch (Exception ignored) {}
        // Make parser throw for TestA.java to force a skipped file
        doThrow(new CodeParsingException("boom", "TestA.java")).when(codeParserService).parseFile(contains("TestA.java"));

        // Run indexing against sample-code folder (contains TestA.java)
        IndexingReport report = indexingService.index(com.codetalker.firestick.service.dto.IndexingRequest.of("src/test/resources/test-data/sample-code"));
        assertThat(report.jobId()).isNotNull();

        // Job persisted should contain skippedFiles JSON with TestA
        IndexingJob job = jobRepository.findById(report.jobId()).orElseThrow();
        assertThat(job.getSkippedFiles()).isNotNull();
        assertThat(job.getSkippedFiles()).contains("TestA.java");

        // Also ensure counters were persisted
        assertThat(job.getFilesSummarized()).isEqualTo(report.filesSummarized());
        assertThat(job.getMethodsSummarized()).isEqualTo(report.methodsSummarized());
        assertThat(job.getFoldersSummarized()).isEqualTo(report.foldersSummarized());
    }

    @Test
    void indexing_test_logs_progress_for_each_file() throws Exception {
        // Make sure lucene test index dir has no stale locks
        try { java.nio.file.Files.deleteIfExists(java.nio.file.Path.of("target/test-lucene-indices/default/write.lock")); } catch (Exception ignored) {}

        // Intercept parseFile to log progress to the console and add a tiny delay so output is visible
        doAnswer(invocation -> {
            String filePath = invocation.getArgument(0, String.class);
            System.out.println("[TEST-PROGRESS] CodeParserService.parseFile called for: " + filePath);
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            return invocation.callRealMethod();
        }).when(codeParserService).parseFile(anyString());

        System.out.println("[TEST-PROGRESS] Starting indexing of sample-code folder");
        IndexingReport report = indexingService.index(com.codetalker.firestick.service.dto.IndexingRequest.of("src/test/resources/test-data/sample-code"));
        System.out.println("[TEST-PROGRESS] indexingService.index returned: jobId=" + report.jobId() + " filesSummarized=" + report.filesSummarized() + " methodsSummarized=" + report.methodsSummarized());

        // Job persisted should exist
        com.codetalker.firestick.model.IndexingJob job = jobRepository.findById(report.jobId()).orElseThrow();
        System.out.println("[TEST-PROGRESS] persisted job filesSummarized=" + job.getFilesSummarized() + " skippedFiles=" + job.getSkippedFiles());

        // Basic sanity checks
        assertThat(job).isNotNull();
        assertThat(job.getFilesSummarized()).isEqualTo(report.filesSummarized());
    }
}
