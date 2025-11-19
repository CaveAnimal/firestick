package com.codetalker.firestick;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseTestSample extends BaseTest {
    @Test
    void sampleAssertionWorks() {
        String value = "firestick";
        assertThat(value).isEqualTo("firestick");
    }

    @Test
    void sampleMockitoWorks() {
        Runnable mockRunnable = mock(Runnable.class);
        mockRunnable.run();
        verify(mockRunnable).run();
    }
}
