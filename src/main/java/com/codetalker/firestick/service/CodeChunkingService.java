package com.codetalker.firestick.service;

import java.util.ArrayList;
import java.util.List;

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
    public List<CodeChunk> extractChunks(CodeFile codeFile, CompilationUnit cu) {
        List<CodeChunk> chunks = new ArrayList<>();
        // Extract method-level chunks
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            String content = method.toString();
            int start = method.getBegin().map(p -> p.line).orElse(1);
            int end = method.getEnd().map(p -> p.line).orElse(start);
            chunks.add(new CodeChunk(codeFile, content, start, end, "method"));
        });
        // Optionally, extract class-level chunk
        cu.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(cls -> {
            String content = cls.toString();
            int start = cls.getBegin().map(p -> p.line).orElse(1);
            int end = cls.getEnd().map(p -> p.line).orElse(start);
            chunks.add(new CodeChunk(codeFile, content, start, end, "class"));
        });
        return chunks;
    }
}
