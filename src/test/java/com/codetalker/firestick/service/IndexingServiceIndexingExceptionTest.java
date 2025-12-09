package com.codetalker.firestick.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.codetalker.firestick.exception.IndexingException;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.dto.IndexingReport;
import com.codetalker.firestick.service.dto.IndexingRequest;

@SpringBootTest
@ActiveProfiles("test")
class IndexingServiceIndexingExceptionTest {

    @Autowired
    private IndexingService indexingService;

    @Autowired
    private IndexingJobRepository jobRepository;

    @Autowired
    private CodeFileRepository codeFileRepository;

    @Autowired
    private CodeChunkRepository codeChunkRepository;

    @MockBean
    private CodeSearchService codeSearchService;

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
    void indexing_exception_does_not_bubble_up_and_is_recorded_for_files() {
        // Simulate a Lucene/Indexing error for any indexCode invocation
        doThrow(new IndexingException("Lock held by another program")).when(codeSearchService).indexCode(anyString(), anyString(), any(String.class));

        IndexingReport report = indexingService.index(IndexingRequest.of("src/test/resources/test-data/sample-code"));

        assertThat(report).isNotNull();
        assertThat(report.jobId()).isNotNull();
        // We expect the runtime indexing errors to be recorded in report.errors()
        assertThat(report.errors()).isNotEmpty();
        // The service should continue and return a report (not throw)
        var job = jobRepository.findTopByOrderByStartedAtDesc().orElseThrow();
        assertThat(job.getId()).isEqualTo(report.jobId());
    }
}
