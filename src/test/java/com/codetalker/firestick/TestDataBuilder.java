package com.codetalker.firestick;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

public class TestDataBuilder {
    public static Path createTempJavaFile(String className, String content) throws IOException {
        Path tempFile = Files.createTempFile(className, ".java");
        Files.writeString(tempFile, content);
        return tempFile;
    }

    public static String sampleClassCode(String className) {
        return """
            package com.example;
            public class %s {
                private String name;
                public %s(String name) { this.name = name; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
            }
        """.formatted(className, className);
    }

    public static String sampleMethodCode() {
        return """
            public int calculate(int a, int b) {
                int result = 0;
                for (int i = 0; i < a; i++) {
                    result += b;
                }
                return result;
            }
        """;
    }
}
