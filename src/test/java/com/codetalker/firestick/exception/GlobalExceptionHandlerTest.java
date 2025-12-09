package com.codetalker.firestick.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.exception.support.DummyExceptionController;

@WebMvcTest(controllers = DummyExceptionController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Endpoints are provided by DummyExceptionController in test support package

    @Test
    @DisplayName("FileDiscoveryException -> 500 with structured JSON body")
    void fileDiscoveryHandled() throws Exception {
        mockMvc.perform(get("/api/test/file-discovery").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("File discovery failed"))
                .andExpect(jsonPath("$.message").value("boom"))
                .andExpect(jsonPath("$.path").value("/api/test/file-discovery"));
    }

    @Test
    @DisplayName("CodeParsingException -> 422 with file in message")
    void codeParsingHandled() throws Exception {
        mockMvc.perform(get("/api/test/code-parse").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Code parsing failed"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("bad code")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("/tmp/Foo.java")))
                .andExpect(jsonPath("$.path").value("/api/test/code-parse"));
    }

    @Test
    @DisplayName("IndexingException -> 500")
    void indexingHandled() throws Exception {
        mockMvc.perform(get("/api/test/indexing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Indexing failed"))
                .andExpect(jsonPath("$.message").value("index failed"))
                .andExpect(jsonPath("$.path").value("/api/test/indexing"));
    }

    @Test
    @DisplayName("EmbeddingException -> 500")
    void embeddingHandled() throws Exception {
        mockMvc.perform(get("/api/test/embedding").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Embedding failed"))
                .andExpect(jsonPath("$.message").value("embed failed"))
                .andExpect(jsonPath("$.path").value("/api/test/embedding"));
    }

    @Test
    @DisplayName("Generic Exception -> 500 Unexpected error")
    void genericHandled() throws Exception {
        mockMvc.perform(get("/api/test/generic").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Unexpected error"))
                .andExpect(jsonPath("$.message").value("oops"))
                .andExpect(jsonPath("$.path").value("/api/test/generic"))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @Test
    @DisplayName("Missing required param -> 400 VALIDATION_ERROR with details")
    void missingParamHandled() throws Exception {
        mockMvc.perform(get("/api/test/validation-required").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0].field").value("q"))
                .andExpect(jsonPath("$.path").value("/api/test/validation-required"));
    }
}
