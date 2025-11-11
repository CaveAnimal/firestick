package com.codetalker.firestick.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class ErrorSchemaTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc buildMockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void errorResponseSchema_containsRequestId() throws Exception {
        MockMvc mockMvc = buildMockMvc();
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/v3/api-docs").accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        String generatedJson = result.getResponse().getContentAsString();
        JsonNode root = mapper.readTree(generatedJson);

        JsonNode requestIdNode = root.path("components")
                .path("schemas")
                .path("ErrorResponse")
                .path("properties")
                .path("requestId");

        assertThat(requestIdNode.isMissingNode())
                .as("ErrorResponse schema must include 'requestId' property")
                .isFalse();

        assertThat(requestIdNode.path("type").asText())
                .as("'requestId' property should be a string")
                .isEqualTo("string");
    }
}
