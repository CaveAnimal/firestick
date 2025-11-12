package com.codetalker.firestick.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

        // Trigger an indexing run to create a job
        IndexingReport report = indexingService.index(IndexingRequest.of("src/test/resources/test-data/sample-code"));
        assertThat(report.jobId()).isNotNull();

        // latest should match the job we just created
        mockMvc.perform(get("/api/indexing/jobs/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(report.jobId()))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.rootPath").value("src/test/resources/test-data/sample-code"))
                .andExpect(jsonPath("$.filesDiscovered").value(report.filesDiscovered()))
                .andExpect(jsonPath("$.filesParsed").value(report.filesParsed()))
                .andExpect(jsonPath("$.chunksProduced").value(report.chunksProduced()))
                .andExpect(jsonPath("$.documentsIndexed").value(report.documentsIndexed()))
                .andExpect(jsonPath("$.embeddingsGenerated").value(report.embeddingsGenerated()));

        // byId should return the same data
        mockMvc.perform(get("/api/indexing/jobs/{id}", report.jobId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(report.jobId()))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.rootPath").value("src/test/resources/test-data/sample-code"))
                .andExpect(jsonPath("$.filesDiscovered").value(report.filesDiscovered()))
                .andExpect(jsonPath("$.filesParsed").value(report.filesParsed()))
                .andExpect(jsonPath("$.chunksProduced").value(report.chunksProduced()))
                .andExpect(jsonPath("$.documentsIndexed").value(report.documentsIndexed()))
                .andExpect(jsonPath("$.embeddingsGenerated").value(report.embeddingsGenerated()));
    }
}
