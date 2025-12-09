package com.codetalker.firestick.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.IndexingJobControl;
import com.codetalker.firestick.service.IndexingService;
import com.codetalker.firestick.service.dto.IndexingReport;
import com.codetalker.firestick.service.dto.IndexingRequest;

@WebMvcTest(IndexingController.class)
@SuppressWarnings({"removal"})
class IndexingControllerHappyPathTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingService indexingService;

    // New dependencies injected via IndexingController constructor need to be mocked for @WebMvcTest
    @MockBean
    private IndexingJobRepository jobRepository;

    @MockBean
    private IndexingJobControl jobControl;

    @MockBean
    private com.codetalker.firestick.service.AppRenameService appRenameService;

    private static IndexingReport sampleReport() {
        return new IndexingReport(
            1L, // jobId
            "RUNNING",
            "C:/repo", // rootPath
            3, // filesDiscovered
            1, // totalFolders
            5, // totalMethods
            3, // filesParsed
            0, // filesSkipped
            7, // chunksProduced
            7, // documentsIndexed
            7, // embeddingsGenerated
            1000L, // startedAtMillis
            2000L, // endedAtMillis
            List.of(), // errors
            2, // filesSummarized
            1, // foldersSummarized
            5, // methodsSummarized
            List.of() // skippedFiles
        );
    }

    @Test
    void getRun_WithValidParams_ShouldReturn200() throws Exception {
        when(indexingService.index(new IndexingRequest("C:/repo", null, null)))
                .thenReturn(sampleReport());

        // Mock job repository returning a recently created job so controller can return job id immediately
        var j = new com.codetalker.firestick.model.IndexingJob();
        j.setId(1L);
        j.setAppName("default");
        j.setRootPath("C:/repo");
        j.setStatus(com.codetalker.firestick.model.IndexingJob.Status.RUNNING);
        j.setStartedAt(java.time.Instant.ofEpochMilli(1000L));
        when(jobRepository.findTopByOrderByStartedAtDesc()).thenReturn(java.util.Optional.of(j));

        mockMvc.perform(get("/api/indexing/run")
                .param("root", "C:/repo")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobId").value(1));
    }

    @Test
    void postRun_WithValidBody_ShouldReturn200() throws Exception {
        IndexingRequest req = new IndexingRequest("C:/repo", null, null);
        when(indexingService.index(req)).thenReturn(sampleReport());

        String body = "{\n  \"rootPath\": \"C:/repo\"\n}";
        // Mock job repository
        var j2 = new com.codetalker.firestick.model.IndexingJob();
        j2.setId(1L);
        j2.setAppName("default");
        j2.setRootPath("C:/repo");
        j2.setStatus(com.codetalker.firestick.model.IndexingJob.Status.RUNNING);
        j2.setStartedAt(java.time.Instant.ofEpochMilli(1000L));
        when(jobRepository.findTopByOrderByStartedAtDesc()).thenReturn(java.util.Optional.of(j2));
        mockMvc.perform(post("/api/indexing/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rootPath").value("C:/repo"));
    }
}
