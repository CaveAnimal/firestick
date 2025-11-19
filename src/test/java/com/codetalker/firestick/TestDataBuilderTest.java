package com.codetalker.firestick;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.*;

class TestDataBuilderTest extends BaseTest {
    @Test
    void createsTempJavaFileWithContent() throws Exception {
        String code = TestDataBuilder.sampleClassCode("Sample");
        Path file = TestDataBuilder.createTempJavaFile("Sample", code);
        assertThat(file).exists();
        assertThat(java.nio.file.Files.readString(file)).contains("public class Sample");
    }

    @Test
    void sampleMethodCodeReturnsValidJava() {
        String methodCode = TestDataBuilder.sampleMethodCode();
        assertThat(methodCode).contains("public int calculate");
    }
}
