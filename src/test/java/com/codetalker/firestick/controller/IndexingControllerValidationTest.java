package com.codetalker.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.IndexingJobControl;
import com.codetalker.firestick.service.IndexingService;

@WebMvcTest(IndexingController.class)
@SuppressWarnings({"removal", "deprecation", "unused"})
class IndexingControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingService indexingService;

    // New dependencies injected via IndexingController constructor need to be mocked for @WebMvcTest
    @MockBean
    private IndexingJobRepository jobRepository;

    @MockBean
    private IndexingJobControl jobControl;

    @Test
    void missingRootParam_OnGetRun_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/indexing/run").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void blankRootParam_OnGetRun_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/indexing/run").param("root", " ").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void blankRoot_OnPostRun_RequestBody_ShouldReturnBadRequest() throws Exception {
        String body = "{\n  \"rootPath\": \"\"\n}";
        mockMvc.perform(post("/api/indexing/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // success path covered elsewhere; here we focus on validation (400)
}
