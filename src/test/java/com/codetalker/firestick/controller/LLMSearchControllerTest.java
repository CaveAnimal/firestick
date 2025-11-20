package com.codetalker.firestick.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.codetalker.firestick.llm.LLMServiceClient;
import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.CodeSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LLMSearchController.class)
public class LLMSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CodeSearchService codeSearchService;

    @MockBean
    private CodeFileRepository codeFileRepository;

    @MockBean
    private CodeChunkRepository codeChunkRepository;

    @MockBean
    private LLMServiceClient llmServiceClient;

    @Test
    public void testLlmSearch() throws Exception {
        // Mock LLM expansion
        when(llmServiceClient.isHealthy()).thenReturn(true);
        when(llmServiceClient.expandQuery(anyString())).thenReturn(java.util.Arrays.asList("expanded"));

        // Mock Search
        when(codeSearchService.searchCode(anyString(), anyString())).thenReturn(java.util.Arrays.asList("file1.java#chunk:10-20"));

        // Mock DB lookups
        CodeFile mockFile = new CodeFile();
        mockFile.setFilePath("file1.java");
        when(codeFileRepository.findByFilePathAndAppName(anyString(), anyString())).thenReturn(Optional.of(mockFile));

        CodeChunk mockChunk = new CodeChunk();
        mockChunk.setContent("public class Auth {}");
        when(codeChunkRepository.findByFileAndStartLineAndEndLine(mockFile, 10, 20)).thenReturn(Optional.of(mockChunk));

        LLMSearchController.LLMSearchRequest request = new LLMSearchController.LLMSearchRequest();
        request.setQuery("auth");
        request.setApp("default");

        mockMvc.perform(post("/api/llm/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.insights").isArray())
                .andExpect(jsonPath("$.suggestedFiles").isArray())
                .andExpect(jsonPath("$.suggestedFiles[0].filePath").value("file1.java"));
    }
}
