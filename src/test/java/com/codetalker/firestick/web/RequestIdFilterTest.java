package com.codetalker.firestick.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.controller.HealthController;

@WebMvcTest(controllers = HealthController.class)
@Import(RequestIdFilter.class)
class RequestIdFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addsXRequestIdHeaderWhenMissing() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(RequestIdFilter.REQUEST_ID_HEADER));
    }

    @Test
    void propagatesExistingXRequestIdHeader() throws Exception {
        String id = "test-req-123";
        mockMvc.perform(get("/api/health").header(RequestIdFilter.REQUEST_ID_HEADER, id))
                .andExpect(status().isOk())
                .andExpect(header().string(RequestIdFilter.REQUEST_ID_HEADER, id));
    }
}
