package com.codetalker.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.IndexingJobRepository;

@WebMvcTest(IndexingJobController.class)
@SuppressWarnings({"removal", "unused"})
class IndexingJobControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingJobRepository repository;

    @MockBean
    private com.codetalker.firestick.repository.CodeFileRepository codeFileRepository;

    @MockBean
    private com.codetalker.firestick.repository.CodeChunkRepository codeChunkRepository;

    @MockBean
    private com.codetalker.firestick.repository.FolderSummaryRepository folderSummaryRepository;

    @MockBean
    private com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository;

    @Test
    void recent_withLimitZero_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/indexing/jobs").param("limit", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void recent_withLimitTooLarge_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/indexing/jobs").param("limit", "1000"))
            .andExpect(status().isBadRequest());
    }
}
