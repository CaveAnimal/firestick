package com.codetalker.firestick.service.struct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.model.ClassInfo;
import com.codetalker.firestick.model.FileInfo;
import com.codetalker.firestick.model.MethodInfo;
import com.codetalker.firestick.service.CodeParserService;
import com.codetalker.firestick.service.ConstructorCallVisitor;
import com.codetalker.firestick.service.MethodCallVisitor;
import com.codetalker.firestick.service.QualifiedMethodCallVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Build lightweight File/Class/Method structures (with call/import info) directly from source.
 */
@Service
public class CodeStructureService {
    private static final Logger log = LoggerFactory.getLogger(CodeStructureService.class);

    private final CodeParserService parser;

    public CodeStructureService(CodeParserService parser) {
        this.parser = parser;
    }

    public FileInfo buildFileInfoFromPath(String filePath) throws IOException {
        String source = Files.readString(Path.of(filePath));
        java.util.Optional<CompilationUnit> cuOpt = parser.parseJavaCode(source);
        if (cuOpt.isEmpty()) {
            log.warn("[Struct] JavaParser returned empty CU for {}", filePath);
            return new FileInfo.Builder()
                .filePath(filePath)
                .packageName("")
                .imports(List.of())
                .staticImports(List.of())
                .classes(List.of())
                .build();
        }
        CompilationUnit cu = cuOpt.get();
        String pkg = parser.extractPackage(cu);
        List<String> imports = parser.extractImports(cu);
        List<String> staticImports = parser.extractStaticImports(cu);

        List<ClassInfo> classes = new ArrayList<>();
        List<ClassOrInterfaceDeclaration> classDecls = cu.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration cls : classDecls) {
            List<MethodInfo> methods = new ArrayList<>();
            for (MethodDeclaration md : cls.getMethods()) {
                Set<String> called = new HashSet<>();
                new MethodCallVisitor().visit(md, called);

                Set<String> qcalled = new HashSet<>();
                new QualifiedMethodCallVisitor().visit(md, qcalled);

                Set<String> ctors = new HashSet<>();
                new ConstructorCallVisitor().visit(md, ctors);

                MethodInfo mi = new MethodInfo.Builder()
                    .name(md.getNameAsString())
                    .signature(md.getDeclarationAsString(false, false, false))
                    .modifiers(md.getModifiers().toString())
                    .annotations(md.getAnnotations().toString())
                    .javaDoc(md.getJavadoc().map(jd -> jd.toString()).orElse(""))
                    .startLine(md.getBegin().map(p -> p.line).orElse(1))
                    .endLine(md.getEnd().map(p -> p.line).orElse(1))
                    .calledMethods(new ArrayList<>(called))
                    .calledQualifiedMethods(new ArrayList<>(qcalled))
                    .calledConstructors(new ArrayList<>(ctors))
                    .build();
                methods.add(mi);
            }

            String sig = cls.getFullyQualifiedName().orElse(cls.getNameAsString());
            String superClass = cls.getExtendedTypes().isNonEmpty() ? cls.getExtendedTypes(0).getNameAsString() : null;
            List<String> interfaces = new ArrayList<>();
            cls.getImplementedTypes().forEach(t -> interfaces.add(t.getNameAsString()));

            ClassInfo ci = new ClassInfo.Builder()
                .name(cls.getNameAsString())
                .signature(sig)
                .modifiers(cls.getModifiers().toString())
                .annotations(cls.getAnnotations().toString())
                .javaDoc(cls.getJavadoc().map(jd -> jd.toString()).orElse(""))
                .startLine(cls.getBegin().map(p -> p.line).orElse(1))
                .endLine(cls.getEnd().map(p -> p.line).orElse(1))
                .methods(methods)
                .superClass(superClass)
                .interfaces(interfaces)
                .build();
            classes.add(ci);
        }

        return new FileInfo.Builder()
            .filePath(filePath)
            .packageName(pkg)
            .imports(imports)
            .staticImports(staticImports)
            .classes(classes)
            .build();
    }
}
