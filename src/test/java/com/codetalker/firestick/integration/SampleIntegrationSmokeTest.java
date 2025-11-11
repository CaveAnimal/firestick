package com.codetalker.firestick.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

class SampleIntegrationSmokeTest extends BaseIntegrationTest {

    @Autowired
    private Environment env;

    @Test
    void testTestProfileUsesInMemoryH2() {
        String url = env.getProperty("spring.datasource.url");
        assertThat(url)
                .as("spring.datasource.url should use in-memory H2 for tests")
                .isNotNull()
                .contains("jdbc:h2:mem");
    }
}
