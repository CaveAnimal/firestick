package com.codetalker.firestick.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.exception.support.DummyExceptionController;
import com.codetalker.firestick.web.RequestIdFilter;

@WebMvcTest(controllers = DummyExceptionController.class)
@Import({ GlobalExceptionHandler.class, RequestIdFilter.class })
class ErrorResponseRequestIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Error responses include X-Request-Id header and requestId field")
    void errorResponseCarriesRequestId() throws Exception {
        // When no header present, filter generates a UUID; just assert existence and non-empty
        mockMvc.perform(get("/api/test/generic").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists(RequestIdFilter.REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.requestId").isNotEmpty());
    }

    @Test
    @DisplayName("Error responses propagate provided X-Request-Id into header and body")
    void errorResponsePropagatesProvidedRequestId() throws Exception {
        String id = "err-req-123";
        mockMvc.perform(get("/api/test/generic").header(RequestIdFilter.REQUEST_ID_HEADER, id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(RequestIdFilter.REQUEST_ID_HEADER, id))
                .andExpect(jsonPath("$.requestId").value(id));
    }
}
