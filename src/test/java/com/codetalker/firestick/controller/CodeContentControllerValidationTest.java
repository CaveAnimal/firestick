package com.codetalker.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;

@WebMvcTest(CodeContentController.class)
@SuppressWarnings({"removal", "deprecation", "unused"})
class CodeContentControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CodeFileRepository fileRepository;
    @MockBean private CodeChunkRepository chunkRepository;

    @Test
    void file_withBlankPath_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/code/file").param("path", " "))
            .andExpect(status().isBadRequest());
    }

    @Test
    void chunk_withBlankDocId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/code/chunk").param("docId", ""))
            .andExpect(status().isBadRequest());
    }
}
