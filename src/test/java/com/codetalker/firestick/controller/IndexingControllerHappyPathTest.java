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

    private static IndexingReport sampleReport() {
        return new IndexingReport(1L, "C:/repo", 3, 3, 0, 7, 7, 7,
                1000L, 2000L, List.of());
    }

    @Test
    void getRun_WithValidParams_ShouldReturn200() throws Exception {
        when(indexingService.index(new IndexingRequest("C:/repo", null, null)))
                .thenReturn(sampleReport());

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
        mockMvc.perform(post("/api/indexing/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rootPath").value("C:/repo"));
    }
}
