package com.codetalker.firestick.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Service to extract code chunks (methods) from parsed Java files.
 */
@Service
public class CodeChunkingService {
    private static final Logger log = LoggerFactory.getLogger(CodeChunkingService.class);
    public List<CodeChunk> extractChunks(CodeFile codeFile, CompilationUnit cu) {
        List<CodeChunk> chunks = new ArrayList<>();

        // Class-level chunks (build first to allow method -> class parenting)
        List<CodeChunk> classChunks = new ArrayList<>();
    List<ClassOrInterfaceDeclaration> allClasses = cu.findAll(ClassOrInterfaceDeclaration.class);
    log.debug("[Chunking] Found {} classes in {}", allClasses.size(), codeFile != null ? codeFile.getFilePath() : "<unknown>");
    allClasses.forEach(cls -> {
            StringBuilder classContent = new StringBuilder();
            classContent.append(cls.toString());
            // Add method signatures for quick scanning context
            cls.getMethods().forEach(m -> {
                classContent.append("\n");
                classContent.append(m.getDeclarationAsString(false, false, false));
            });
            int start = cls.getBegin().map(p -> p.line).orElse(1);
            int end = cls.getEnd().map(p -> p.line).orElse(start);
            String name = cls.getNameAsString();
            String signature = cls.getFullyQualifiedName().orElse(name);
            String modifiers = cls.getModifiers().toString();
            String annotations = cls.getAnnotations().toString();
            String javaDoc = cls.getJavadoc().map(jd -> jd.toString()).orElse("");
            CodeChunk chunk = new CodeChunk.Builder()
                .file(codeFile)
                .content(classContent.toString())
                .startLine(start)
                .endLine(end)
                .type("class")
                .metadata("class")
                .name(name)
                .signature(signature)
                .modifiers(modifiers)
                .annotations(annotations)
                .javaDoc(javaDoc)
                .children(new ArrayList<>())
                .build();
            classChunks.add(chunk);
        });

        // File-level chunk (tertiary)
    String fileContent = cu.toString();
        int fileStart = cu.getBegin().map(p -> p.line).orElse(1);
        int fileEnd = cu.getEnd().map(p -> p.line).orElse(fileStart);
    String fileName = (codeFile != null ? codeFile.getFilePath() : "<unknown>");
        String fileSignature = fileName;
        String fileModifiers = "";
        String fileAnnotations = "";
        String fileJavaDoc = "";
        CodeChunk fileChunk = new CodeChunk.Builder()
            .file(codeFile)
            .content(fileContent)
            .startLine(fileStart)
            .endLine(fileEnd)
            .type("file")
            .metadata("file")
            .name(fileName)
            .signature(fileSignature)
            .modifiers(fileModifiers)
            .annotations(fileAnnotations)
            .javaDoc(fileJavaDoc)
            .children(new ArrayList<>())
            .build();
        // Defensive: ensure type is set as expected
        fileChunk.setType("file");

        // Attach classes under file
        for (CodeChunk classChunk : classChunks) {
            classChunk.setParent(fileChunk);
            fileChunk.getChildren().add(classChunk);
        }

    // Method-level chunks (now that classes and file are ready)
        List<CodeChunk> methodChunks = new ArrayList<>();
    List<MethodDeclaration> allMethods = cu.findAll(MethodDeclaration.class);
    log.debug("[Chunking] Found {} methods in {} (file lines {}-{})", allMethods.size(), fileName, fileStart, fileEnd);
    allMethods.forEach(method -> {
            String content = method.toString();
            int start = method.getBegin().map(p -> p.line).orElse(1);
            int end = method.getEnd().map(p -> p.line).orElse(start);
            String name = method.getNameAsString();
            String signature = method.getDeclarationAsString(false, false, false);
            String modifiers = method.getModifiers().toString();
            String annotations = method.getAnnotations().toString();
            String javaDoc = method.getJavadoc().map(jd -> jd.toString()).orElse("");
            @SuppressWarnings("unchecked")
            String className = method.findAncestor(ClassOrInterfaceDeclaration.class)
                .map(ClassOrInterfaceDeclaration::getNameAsString).orElse("");
            @SuppressWarnings("unchecked")
            String classSignature = method.findAncestor(ClassOrInterfaceDeclaration.class)
                .map(cls -> cls.getFullyQualifiedName().orElse(cls.getNameAsString())).orElse("");
            String context = "Class: " + className + "\nSignature: " + classSignature;
            CodeChunk chunk = new CodeChunk.Builder()
                .file(codeFile)
                .content(content + "\n" + context)
                .startLine(start)
                .endLine(end)
                .type("method")
                // include parent class name in metadata to aid debugging while preserving "method" substring for tests
                .metadata(className.isEmpty() ? "method" : ("method;class=" + className))
                .name(name)
                .signature(signature)
                .modifiers(modifiers)
                .annotations(annotations)
                .javaDoc(javaDoc)
                .build();

            // Link method under its class when possible; otherwise attach to file
            CodeChunk parentClass = classChunks.stream()
                .filter(c -> c.getName() != null && !c.getName().isEmpty() && c.getName().equals(className))
                .findFirst().orElse(null);
            if (parentClass != null) {
                chunk.setParent(parentClass);
                parentClass.getChildren().add(chunk);
            } else {
                chunk.setParent(fileChunk);
                fileChunk.getChildren().add(chunk);
            }

            methodChunks.add(chunk);
        });

        // Return a flat list including file, classes, and methods for easier consumption in tests
        chunks.add(fileChunk);
        chunks.addAll(classChunks);
        chunks.addAll(methodChunks);

        log.debug("[Chunking] Emitted {} chunks for {} (file + classes + methods)", chunks.size(), fileName);

        return chunks;
    }
}
