package com.codetalker.firestick.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.IndexingService;
import com.codetalker.firestick.service.dto.IndexingReport;
import com.codetalker.firestick.service.dto.IndexingRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IndexingJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IndexingService indexingService;

    @Autowired
    private IndexingJobRepository jobRepository;

    @Test
    void latest_and_byId_endpoints_return_last_run() throws Exception {
        // Ensure a clean slate for deterministic "latest"
        jobRepository.deleteAll();

        // Ensure Lucene test index directory is clean to avoid stale locks when running tests locally
        try {
            java.nio.file.Path lock = java.nio.file.Path.of("target/test-lucene-indices/default/write.lock");
            if (java.nio.file.Files.exists(lock)) java.nio.file.Files.deleteIfExists(lock);
        } catch (Exception ignored) {}

        // Trigger an indexing run to create a job
        IndexingReport report = indexingService.index(IndexingRequest.of("src/test/resources/test-data/sample-code"));
        assertThat(report.jobId()).isNotNull();

        // latest should match the job we just created
        mockMvc.perform(get("/api/indexing/jobs/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(report.jobId()))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.rootPath").value("src/test/resources/test-data/sample-code"))
                .andExpect(jsonPath("$.filesDiscovered").value(report.filesDiscovered()))
                .andExpect(jsonPath("$.filesParsed").value(report.filesParsed()))
                .andExpect(jsonPath("$.chunksProduced").value(report.chunksProduced()))
                .andExpect(jsonPath("$.documentsIndexed").value(report.documentsIndexed()))
                .andExpect(jsonPath("$.embeddingsGenerated").value(report.embeddingsGenerated()))
                .andExpect(jsonPath("$.filesSummarized").value(report.filesSummarized()))
                .andExpect(jsonPath("$.foldersSummarized").value(report.foldersSummarized()))
                .andExpect(jsonPath("$.methodsSummarized").value(report.methodsSummarized()));
                
            // skippedFiles should be present (may be empty array)
            mockMvc.perform(get("/api/indexing/jobs/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skippedFiles").isArray());

        // byId should return the same data
        mockMvc.perform(get("/api/indexing/jobs/{id}", report.jobId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(report.jobId()))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.rootPath").value("src/test/resources/test-data/sample-code"))
                .andExpect(jsonPath("$.filesDiscovered").value(report.filesDiscovered()))
                .andExpect(jsonPath("$.filesParsed").value(report.filesParsed()))
                .andExpect(jsonPath("$.chunksProduced").value(report.chunksProduced()))
                .andExpect(jsonPath("$.documentsIndexed").value(report.documentsIndexed()))
                .andExpect(jsonPath("$.embeddingsGenerated").value(report.embeddingsGenerated()))
                .andExpect(jsonPath("$.filesSummarized").value(report.filesSummarized()))
                .andExpect(jsonPath("$.foldersSummarized").value(report.foldersSummarized()))
                .andExpect(jsonPath("$.methodsSummarized").value(report.methodsSummarized()));
            // byId skippedFiles should be present as array
            mockMvc.perform(get("/api/indexing/jobs/{id}", report.jobId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skippedFiles").isArray());
    }

        @Test
        void latest_handles_null_appName_gracefully() throws Exception {
        // create a job with null appName (older data scenario)
        jobRepository.deleteAll();
        com.codetalker.firestick.model.IndexingJob job = new com.codetalker.firestick.model.IndexingJob();
        job.setRootPath("src/test/resources/test-data/sample-code");
        job.setAppName(null);
        job.setStatus(com.codetalker.firestick.model.IndexingJob.Status.SUCCESS);
        job.setStartedAt(java.time.Instant.now());
        job.setFilesDiscovered(1);
        job.setFilesParsed(1);
        job.setChunksProduced(0);
        job.setDocumentsIndexed(0);
        job.setEmbeddingsGenerated(0);
        job = jobRepository.save(job);

        mockMvc.perform(get("/api/indexing/jobs/latest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobId").value(job.getId()))
            .andExpect(jsonPath("$.rootPath").value("src/test/resources/test-data/sample-code"))
            .andExpect(jsonPath("$.skippedFiles").isArray());

        mockMvc.perform(get("/api/indexing/jobs/{id}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobId").value(job.getId()))
            .andExpect(jsonPath("$.skippedFiles").isArray());
        }
}
