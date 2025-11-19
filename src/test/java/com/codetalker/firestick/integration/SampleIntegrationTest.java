package com.codetalker.firestick.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class SampleIntegrationTest extends BaseIntegrationTest {
    @Test
    void integrationTestRuns() {
        assertThat(true).isTrue();
    }
}
