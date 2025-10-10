package com.caveanimal.firestick.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
