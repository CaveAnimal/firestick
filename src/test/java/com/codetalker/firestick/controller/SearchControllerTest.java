package com.codetalker.firestick.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.CodeSearchService;

@WebMvcTest(SearchController.class)
@SuppressWarnings({"removal", "deprecation"})
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodeSearchService codeSearchService;

    @MockBean
    private CodeFileRepository fileRepository;

    @MockBean
    private CodeChunkRepository chunkRepository;

    @Test
    void missingQueryParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/search").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void filterByPath_ShouldExcludeNonMatching() throws Exception {
        // Search returns one id (multi-tenant aware default app)
        when(codeSearchService.searchCode("term", "default")).thenReturn(List.of("src/A.java#M:1-5"));
        // Repo returns the file and chunk for the id
        CodeFile file = new CodeFile();
        file.setFilePath("src/A.java");
        when(fileRepository.findByFilePathAndAppName("src/A.java", "default")).thenReturn(Optional.of(file));
        CodeChunk chunk = new CodeChunk();
        chunk.setContent("class A {}");
        when(chunkRepository.findByFileAndStartLineAndEndLine(Mockito.eq(file), Mockito.eq(1), Mockito.eq(5)))
            .thenReturn(Optional.of(chunk));

        mockMvc.perform(get("/api/search")
                .param("q", "term")
                .param("path", "does-not-match")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void happyPath_ShouldReturnResults() throws Exception {
        when(codeSearchService.searchCode("hello", "default")).thenReturn(List.of("src/A.java#M:1-5"));
        CodeFile file = new CodeFile();
        file.setFilePath("src/A.java");
        when(fileRepository.findByFilePathAndAppName("src/A.java", "default")).thenReturn(Optional.of(file));
        CodeChunk chunk = new CodeChunk();
        chunk.setContent("class A {}\nvoid m(){}");
        when(chunkRepository.findByFileAndStartLineAndEndLine(Mockito.eq(file), Mockito.eq(1), Mockito.eq(5)))
            .thenReturn(Optional.of(chunk));

        mockMvc.perform(get("/api/search")
                .param("q", "hello")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(1))
            .andExpect(jsonPath("$.results[0].filePath").value("src/A.java"))
            .andExpect(jsonPath("$.results[0].line").value(1))
            .andExpect(jsonPath("$.results[0].snippet").isString());
    }

    @Test
    void invalidPage_ShouldReturnBadRequestValidationError() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("q", "hello")
                .param("page", "0") // invalid, must be >=1
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.details[0].field").value(org.hamcrest.Matchers.containsString("page")));
    }
}
