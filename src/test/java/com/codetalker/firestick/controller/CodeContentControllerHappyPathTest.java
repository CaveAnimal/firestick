package com.codetalker.firestick.controller;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;

@WebMvcTest(CodeContentController.class)
@SuppressWarnings({"removal"})
class CodeContentControllerHappyPathTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CodeFileRepository fileRepository;
    @MockBean private CodeChunkRepository chunkRepository;

    @Test
    void file_withExistingPath_ShouldReturn200() throws Exception {
        Path tmp = Files.createTempFile("file-api-test", ".txt");
        Files.writeString(tmp, "hello world");

        mockMvc.perform(get("/api/code/file").param("path", tmp.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.path").value(tmp.toString()))
            .andExpect(jsonPath("$.content").value("hello world"));
    }
}
