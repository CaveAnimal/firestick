package com.codetalker.firestick.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.exception.CodeParsingException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Service for parsing Java source code using JavaParser.
 */
@Service
public class CodeParserService {

    private final JavaParser javaParser;
    private static final Logger log = LoggerFactory.getLogger(CodeParserService.class);

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
         * Extract package declaration from CompilationUnit.
         * @param cu CompilationUnit
         * @return package name or empty string
         */
        public String extractPackage(CompilationUnit cu) {
            return cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
        }

        /**
         * Extract import statements from CompilationUnit.
         * @param cu CompilationUnit
         * @return List of import strings
         */
        public java.util.List<String> extractImports(CompilationUnit cu) {
            java.util.List<String> imports = new java.util.ArrayList<>();
            cu.getImports().stream()
                .filter(id -> !id.isStatic())
                .forEach(id -> imports.add(id.getNameAsString()));
            return imports;
        }

        /**
         * Extract static import statements from CompilationUnit.
         * @param cu CompilationUnit
         * @return List of static import strings (fully qualified, may include member name)
         */
        public java.util.List<String> extractStaticImports(CompilationUnit cu) {
            java.util.List<String> imports = new java.util.ArrayList<>();
            cu.getImports().stream()
                .filter(com.github.javaparser.ast.ImportDeclaration::isStatic)
                .forEach(id -> imports.add(id.getNameAsString()));
            return imports;
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
                    log.debug("Parsed file '{}' into {} chunks", filePath, chunks.size());
                } else {
                    log.warn("JavaParser returned empty result for file '{}'", filePath);
                }
                return codeFile;
            } catch (Exception e) {
                log.error("Failed to parse file: {}", filePath, e);
                throw new CodeParsingException("Failed to parse file", filePath, e);
            }
        }
}
