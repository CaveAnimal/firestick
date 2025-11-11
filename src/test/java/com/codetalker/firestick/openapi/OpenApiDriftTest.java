package com.codetalker.firestick.openapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

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
public class OpenApiDriftTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc buildMockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void documentedPathsArePresentInGeneratedSpec() throws Exception {
        MockMvc mockMvc = buildMockMvc();
        ObjectMapper mapper = new ObjectMapper();

        // Load generated spec via springdoc endpoint; now enforce status to be OK
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/v3/api-docs").accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        int status = result.getResponse().getStatus();
        assertThat(status)
            .as("/v3/api-docs must be available (status 200) to enforce drift checks")
            .isEqualTo(200);

        String generatedJson = result.getResponse().getContentAsString();
        JsonNode generated = mapper.readTree(generatedJson);

        // Load baseline
        JsonNode baseline = readBaseline(mapper);

        // Compare paths: baseline must be a subset of generated
        JsonNode baselinePaths = baseline.path("paths");
        JsonNode generatedPaths = generated.path("paths");
        assertThat(baselinePaths.isObject()).as("baseline paths should be an object").isTrue();
        assertThat(generatedPaths.isObject()).as("generated paths should be an object").isTrue();

        Iterator<String> pathNames = baselinePaths.fieldNames();
        while (pathNames.hasNext()) {
            String path = pathNames.next();
            JsonNode baselinePathItem = baselinePaths.get(path);

            assertThat(generatedPaths.has(path))
                    .as("generated spec missing documented path '%s'", path)
                    .isTrue();

            JsonNode generatedPathItem = generatedPaths.get(path);
            // For each HTTP method documented, ensure it exists in generated
            Iterator<String> methodNames = baselinePathItem.fieldNames();
            while (methodNames.hasNext()) {
                String method = methodNames.next();
                JsonNode baselineOp = baselinePathItem.get(method);

                assertThat(generatedPathItem.has(method))
                        .as("generated spec missing method '%s' for path '%s'", method, path)
                        .isTrue();

                // Compare response codes: baseline response codes must exist in generated
                JsonNode baselineResponses = baselineOp.path("responses");
                if (baselineResponses.isObject()) {
                    JsonNode generatedResponses = generatedPathItem.path(method).path("responses");
                    Iterator<String> responseCodes = baselineResponses.fieldNames();
                    while (responseCodes.hasNext()) {
                        String code = responseCodes.next();
                        assertThat(generatedResponses.has(code))
                                .as("generated spec missing response '%s' for %s %s", code, method.toUpperCase(), path)
                                .isTrue();
                    }
                }
            }
        }
    }

    private JsonNode readBaseline(ObjectMapper mapper) throws IOException {
        // Use project-relative path; surefire runs from module root
        Path path = Path.of("docs", "openapi", "openapi.json");
        if (!Files.exists(path)) {
            throw new IOException("Baseline OpenAPI not found at " + path.toAbsolutePath());
        }
        String json = Files.readString(path);
        return mapper.readTree(json);
    }
}
