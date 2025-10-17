package com.codetalker.firestick.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Service for parsing Java source code using JavaParser.
 */
@Service
public class CodeParserService {

    private final JavaParser javaParser;

    public CodeParserService() {
        this.javaParser = new JavaParser();
    }

    /**
     * Parse Java source code and return the compilation unit.
     *
     * @param sourceCode Java source code as String
     * @return Optional containing the CompilationUnit if parsing succeeds
     */
    public Optional<CompilationUnit> parseJavaCode(String sourceCode) {
        ParseResult<CompilationUnit> parseResult = javaParser.parse(sourceCode);
        return parseResult.getResult();
    }

        /**
         * Parse a Java file and return a CodeFile with extracted chunks.
         * @param filePath Path to Java file
         * @return CodeFile with chunks
         */
        public com.codetalker.firestick.model.CodeFile parseFile(String filePath) {
            try {
                java.nio.file.Path path = java.nio.file.Path.of(filePath);
                String source = java.nio.file.Files.readString(path);
                java.time.Instant lastModified = java.nio.file.Files.getLastModifiedTime(path).toInstant();
                String hash = Integer.toHexString(source.hashCode());
                com.codetalker.firestick.model.CodeFile codeFile = new com.codetalker.firestick.model.CodeFile(filePath, lastModified, hash);
                Optional<CompilationUnit> cuOpt = parseJavaCode(source);
                if (cuOpt.isPresent()) {
                    com.codetalker.firestick.service.CodeChunkingService chunker = new com.codetalker.firestick.service.CodeChunkingService();
                    java.util.List<com.codetalker.firestick.model.CodeChunk> chunks = chunker.extractChunks(codeFile, cuOpt.get());
                    codeFile.setChunks(chunks);
                }
                return codeFile;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse file: " + filePath, e);
            }
        }
}
