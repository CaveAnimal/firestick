package com.codetalker.firestick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestDataBuilder {

    public static Path createTempJavaFile(String className, String content) throws IOException {
        Path tempFile = Files.createTempFile(className, ".java");
        Files.writeString(tempFile, content);
        return tempFile;
    }

    public static String sampleClassCode(String className) {
        return (
                "package com.example;\n\n" +
                "public class %s {\n" +
                "    private String name;\n\n" +
                "    public %s(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n\n" +
                "    public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "}\n"
        ).formatted(className, className);
    }

    public static String sampleMethodCode() {
        return (
                "public int calculate(int a, int b) {\n" +
                "    int result = 0;\n" +
                "    for (int i = 0; i < a; i++) {\n" +
                "        result += b;\n" +
                "    }\n" +
                "    return result;\n" +
                "}\n"
        );
    }

    public static Path createTempDirectory(String prefix) throws IOException {
        return Files.createTempDirectory(prefix);
    }

    public static String sampleInterfaceCode(String interfaceName) {
        return (
                "package com.example;\n\n" +
                "public interface %s {\n" +
                "    String getId();\n" +
                "}\n"
        ).formatted(interfaceName);
    }
}
