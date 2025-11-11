package com.codetalker.firestick.service;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codetalker.firestick.model.ClassInfo;
import com.codetalker.firestick.model.FileInfo;
import com.codetalker.firestick.model.MethodInfo;

@SpringBootTest
class DependencyGraphServiceTest {

    @Autowired
    private DependencyGraphService dependencyGraphService;

    @Test
    void testCreateDependencyGraph() {
        Graph<String, DefaultEdge> graph = dependencyGraphService.createDependencyGraph();

        assertThat(graph.vertexSet()).hasSize(3);
        assertThat(graph.edgeSet()).hasSize(2);
        assertThat(graph.containsVertex("Module A")).isTrue();
        assertThat(graph.containsVertex("Module B")).isTrue();
        assertThat(graph.containsVertex("Module C")).isTrue();
    }

    @Test
    void testBuildFromParsedFiles_instanceAndConstructorCalls() {
        // Target class with constructor and instance method
        MethodInfo utilCtor = new MethodInfo.Builder()
            .name("Util")
            .signature("Util()")
            .build();
        MethodInfo utilInst = new MethodInfo.Builder()
            .name("inst")
            .signature("inst()")
            .build();
        ClassInfo utilClass = new ClassInfo.Builder()
            .name("Util")
            .methods(java.util.Arrays.asList(utilCtor, utilInst))
            .build();

        // Service method that constructs Util and calls an instance method.
        MethodInfo doWork = new MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledConstructors(java.util.Arrays.asList("Util"))
            // simple chained call heuristic: qualifier ending with ()
            .calledQualifiedMethods(java.util.Arrays.asList("Util().inst"))
            .build();
        ClassInfo serviceClass = new ClassInfo.Builder()
            .name("MyService")
            .methods(java.util.Arrays.asList(doWork))
            .build();

        FileInfo fService = new FileInfo.Builder()
            .filePath("src/main/java/com/example/MyService.java")
            .packageName("com.example")
            .imports(java.util.Arrays.asList("com.example.Util"))
            .classes(java.util.Arrays.asList(serviceClass))
            .build();

        FileInfo fUtil = new FileInfo.Builder()
            .filePath("src/main/java/com/example/Util.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(utilClass))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fService, fUtil), java.util.Collections.emptyList());

        String doWorkNode = "M:com.example.MyService#doWork()";
        String utilCtorNode = "M:com.example.Util#Util()";
        String utilInstNode = "M:com.example.Util#inst()";

        // Expect edges from doWork to constructor and then to instance method
        assertThat(graph.containsEdge(doWorkNode, utilCtorNode)).isTrue();
        assertThat(graph.containsEdge(doWorkNode, utilInstNode)).isTrue();

        // Verify richer call metrics present
        java.util.Map<String, Object> md = dependencyGraphService.getMetadata();
        assertThat(md).containsKeys(
            "callsAttempted",
            "callsResolved",
            "callEdgesAdded",
            "callsUnresolved",
            "callsConstructorResolved",
            "callsQualifiedResolved",
            "callsStaticResolved",
            "callsIntraClassResolved",
            "callsStaticWildcardResolved",
            "callsQualifiedChainedResolved"
        );
        // For this scenario, at least one constructor resolution should be counted
        assertThat(((Number)md.get("callsConstructorResolved")).intValue()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testAddVertexAndEdge() {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        dependencyGraphService.addVertex(graph, "Module X");
        dependencyGraphService.addVertex(graph, "Module Y");
        dependencyGraphService.addEdge(graph, "Module X", "Module Y");

        assertThat(graph.vertexSet()).contains("Module X", "Module Y");
        assertThat(graph.edgeSet()).hasSize(1);
    }

    @Test
    void testFindDependencies() {
        Graph<String, DefaultEdge> graph = dependencyGraphService.createDependencyGraph();

        List<String> dependencies = dependencyGraphService.findDependencies(graph, "Module A");

        assertThat(dependencies).containsExactly("Module B");
    }

    @Test
    void testBuildFromParsedFiles_basic() {
        // Build a simple FileInfo -> ClassInfo -> MethodInfo structure
        MethodInfo m1 = new MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledMethods(Arrays.asList("helperMethod"))
            .build();

        MethodInfo m2 = new MethodInfo.Builder()
            .name("helperMethod")
            .signature("helperMethod()")
            .build();

        ClassInfo c1 = new ClassInfo.Builder()
            .name("MyService")
            .methods(Arrays.asList(m1, m2))
            .superClass("com.example.BaseService")
            .interfaces(Arrays.asList("com.example.Serviceable", "java.io.Serializable"))
            .build();

    FileInfo f1 = new FileInfo.Builder()
                .filePath("src/main/java/com/example/MyService.java")
                .packageName("com.example")
        .imports(Arrays.asList("import java.util.List;", "import com.other.Lib;"))
        .staticImports(Arrays.asList("import static java.lang.Math.max;"))
                .classes(Arrays.asList(c1))
                .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(Arrays.asList(f1), java.util.Collections.emptyList());

        // Expected nodes
        String fileNode = "F:src/main/java/com/example/MyService.java";
        String classNode = "C:com.example.MyService";
        String methodNode = "M:com.example.MyService#doWork()";
        String helperNode = "M:com.example.MyService#helperMethod()";
        String importNode1 = "I:java.util.List";
    String importNode2 = "I:com.other.Lib";
    String importStaticNode = "I:java.lang.Math.max";
        String superNode = "C:com.example.BaseService";
        String ifaceNode1 = "C:com.example.Serviceable";
        String ifaceNode2 = "C:java.io.Serializable";

    assertThat(graph.vertexSet()).contains(fileNode, classNode, methodNode, helperNode, importNode1, importNode2, importStaticNode, superNode, ifaceNode1, ifaceNode2);

        // Expected edges
        assertThat(graph.containsEdge(fileNode, classNode)).isTrue();
        assertThat(graph.containsEdge(classNode, methodNode)).isTrue();
        assertThat(graph.containsEdge(classNode, helperNode)).isTrue();
        assertThat(graph.containsEdge(fileNode, importNode1)).isTrue();
    assertThat(graph.containsEdge(fileNode, importNode2)).isTrue();
    assertThat(graph.containsEdge(fileNode, importStaticNode)).isTrue();
        assertThat(graph.containsEdge(classNode, superNode)).isTrue();
        assertThat(graph.containsEdge(classNode, ifaceNode1)).isTrue();
        assertThat(graph.containsEdge(classNode, ifaceNode2)).isTrue();
        // CALLS edge
        assertThat(graph.containsEdge(methodNode, helperNode)).isTrue();

        // Verify metadata
        java.util.Map<String, Object> metadata = dependencyGraphService.getMetadata();
        assertThat(metadata.get("nodeCount")).isEqualTo(graph.vertexSet().size());
        assertThat(metadata.get("edgeCount")).isEqualTo(graph.edgeSet().size());
        assertThat(metadata.get("buildTimestamp")).isInstanceOf(Long.class);
    }

    @Test
    void testBuildFromParsedFiles_crossClassCalls_minimalHeuristics() {
        // Class Util with utilMethod
        com.codetalker.firestick.model.MethodInfo utilMethod = new com.codetalker.firestick.model.MethodInfo.Builder()
            .name("utilMethod")
            .signature("utilMethod()")
            .build();
        ClassInfo utilClass = new ClassInfo.Builder()
            .name("Util")
            .methods(java.util.Arrays.asList(utilMethod))
            .build();

        // Class OtherUtil with staticThing
        com.codetalker.firestick.model.MethodInfo staticThing = new com.codetalker.firestick.model.MethodInfo.Builder()
            .name("staticThing")
            .signature("staticThing()")
            .build();
        ClassInfo otherUtilClass = new ClassInfo.Builder()
            .name("OtherUtil")
            .methods(java.util.Arrays.asList(staticThing))
            .build();

        // MyService.doWork calls Util.utilMethod via qualified name,
        // and calls staticThing() via static import
        com.codetalker.firestick.model.MethodInfo doWork = new com.codetalker.firestick.model.MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledMethods(java.util.Arrays.asList("staticThing"))
            .calledQualifiedMethods(java.util.Arrays.asList("Util.utilMethod"))
            .build();
        ClassInfo serviceClass = new ClassInfo.Builder()
            .name("MyService")
            .methods(java.util.Arrays.asList(doWork))
            .build();

        FileInfo fService = new FileInfo.Builder()
            .filePath("src/main/java/com/example/MyService.java")
            .packageName("com.example")
            .imports(java.util.Arrays.asList("com.example.Util"))
            .staticImports(java.util.Arrays.asList("com.example.OtherUtil.staticThing"))
            .classes(java.util.Arrays.asList(serviceClass))
            .build();

        FileInfo fUtil = new FileInfo.Builder()
            .filePath("src/main/java/com/example/Util.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(utilClass))
            .build();

        FileInfo fOtherUtil = new FileInfo.Builder()
            .filePath("src/main/java/com/example/OtherUtil.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(otherUtilClass))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fService, fUtil, fOtherUtil), java.util.Collections.emptyList());

        String doWorkNode = "M:com.example.MyService#doWork()";
        String utilMethodNode = "M:com.example.Util#utilMethod()";
        String staticThingNode = "M:com.example.OtherUtil#staticThing()";

        assertThat(graph.containsEdge(doWorkNode, utilMethodNode)).isTrue();
        assertThat(graph.containsEdge(doWorkNode, staticThingNode)).isTrue();
    }

    @Test
    void testBuildFromParsedFiles_thisAndSuperQualifiedCalls() {
        // Base class with helper
        MethodInfo baseHelper = new MethodInfo.Builder()
            .name("helper")
            .signature("helper()")
            .build();
        ClassInfo base = new ClassInfo.Builder()
            .name("Base")
            .methods(java.util.Arrays.asList(baseHelper))
            .build();

        // Subclass with its own helper and a doWork method that calls this.helper and super.helper
        MethodInfo subHelper = new MethodInfo.Builder()
            .name("helper")
            .signature("helper()")
            .build();
        MethodInfo doWork = new MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledQualifiedMethods(java.util.Arrays.asList("this.helper", "super.helper"))
            .build();
        ClassInfo sub = new ClassInfo.Builder()
            .name("Sub")
            .superClass("com.example.Base")
            .methods(java.util.Arrays.asList(doWork, subHelper))
            .build();

        FileInfo fBase = new FileInfo.Builder()
            .filePath("src/main/java/com/example/Base.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(base))
            .build();
        FileInfo fSub = new FileInfo.Builder()
            .filePath("src/main/java/com/example/Sub.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(sub))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fBase, fSub), java.util.Collections.emptyList());

        String doWorkNode = "M:com.example.Sub#doWork()";
        String subHelperNode = "M:com.example.Sub#helper()";
        String baseHelperNode = "M:com.example.Base#helper()";

        assertThat(graph.containsEdge(doWorkNode, subHelperNode)).isTrue();
        assertThat(graph.containsEdge(doWorkNode, baseHelperNode)).isTrue();

        // Ensure unresolved metrics keys present for diagnostics (no strict count assertions here)
        java.util.Map<String, Object> md = dependencyGraphService.getMetadata();
        assertThat(md).containsKeys(
            "callsUnresolvedQualifiedNoClass",
            "callsUnresolvedQualifiedNoMethod",
            "callsUnresolvedCtorNoClass",
            "callsUnresolvedCtorNoMethod",
            "callsUnresolvedStaticNoClass"
        );
    }

    @Test
    void testQueryHelpers_callersAndCallees() {
        // Build a simple class with two methods where one calls the other
        MethodInfo caller = new MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledMethods(java.util.Arrays.asList("helper"))
            .build();
        MethodInfo callee = new MethodInfo.Builder()
            .name("helper")
            .signature("helper()")
            .build();
        ClassInfo cls = new ClassInfo.Builder()
            .name("Sample")
            .methods(java.util.Arrays.asList(caller, callee))
            .build();
        FileInfo file = new FileInfo.Builder()
            .filePath("src/main/java/com/example/Sample.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(cls))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(file), java.util.Collections.emptyList());

        String callerNode = "M:com.example.Sample#doWork()";
        String calleeNode = "M:com.example.Sample#helper()";

        java.util.List<String> callees = dependencyGraphService.getCallees(graph, callerNode);
        java.util.List<String> callers = dependencyGraphService.getCallers(graph, calleeNode);

        assertThat(callees).contains(calleeNode);
        assertThat(callers).contains(callerNode);
    }

    @Test
    void testFindCircularClassDependencies_basic() {
        // Create two classes with an EXTENDS cycle (A extends B, B extends A)
        ClassInfo a = new ClassInfo.Builder()
            .name("A")
            .superClass("com.example.B")
            .methods(java.util.Collections.emptyList())
            .build();
        FileInfo fa = new FileInfo.Builder()
            .filePath("src/main/java/com/example/A.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(a))
            .build();

        ClassInfo b = new ClassInfo.Builder()
            .name("B")
            .superClass("com.example.A")
            .methods(java.util.Collections.emptyList())
            .build();
        FileInfo fb = new FileInfo.Builder()
            .filePath("src/main/java/com/example/B.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(b))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fa, fb), java.util.Collections.emptyList());

        java.util.List<java.util.List<String>> cycles = dependencyGraphService.findCircularClassDependencies(graph);
        // Expect at least one cycle containing both class nodes
        String an = "C:com.example.A";
        String bn = "C:com.example.B";
        boolean found = cycles.stream().anyMatch(c -> c.contains(an) && c.contains(bn));
        assertThat(found).isTrue();
    }

    @Test
    void testBuildFromParsedFiles_staticWildcardImportResolves() {
        // OtherUtil with staticThing
        MethodInfo staticThing = new MethodInfo.Builder()
            .name("staticThing")
            .signature("staticThing()")
            .build();
        ClassInfo otherUtil = new ClassInfo.Builder()
            .name("OtherUtil")
            .methods(java.util.Arrays.asList(staticThing))
            .build();

        // Service calling staticThing() without qualifier, relying on wildcard static import
        MethodInfo doWork = new MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledMethods(java.util.Arrays.asList("staticThing"))
            .build();
        ClassInfo service = new ClassInfo.Builder()
            .name("MyService")
            .methods(java.util.Arrays.asList(doWork))
            .build();

        FileInfo fService = new FileInfo.Builder()
            .filePath("src/main/java/com/example/MyService.java")
            .packageName("com.example")
            .staticImports(java.util.Arrays.asList("com.example.OtherUtil.*"))
            .classes(java.util.Arrays.asList(service))
            .build();
        FileInfo fOther = new FileInfo.Builder()
            .filePath("src/main/java/com/example/OtherUtil.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(otherUtil))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fService, fOther), java.util.Collections.emptyList());

        String doWorkNode = "M:com.example.MyService#doWork()";
        String staticThingNode = "M:com.example.OtherUtil#staticThing()";
        assertThat(graph.containsEdge(doWorkNode, staticThingNode)).isTrue();

        java.util.Map<String, Object> md = dependencyGraphService.getMetadata();
        assertThat(((Number)md.get("callsStaticWildcardResolved")).intValue()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testBuildFromParsedFiles_fqnQualifiedCallResolves() {
        // Define a utility class in a different package
        MethodInfo utilMethod = new MethodInfo.Builder()
            .name("utilMethod")
            .signature("utilMethod()")
            .build();
        ClassInfo util = new ClassInfo.Builder()
            .name("Util")
            .methods(java.util.Arrays.asList(utilMethod))
            .build();

        // Service calls fully-qualified com.tools.Util.utilMethod() without import
        MethodInfo doWork = new MethodInfo.Builder()
            .name("doWork")
            .signature("doWork()")
            .calledQualifiedMethods(java.util.Arrays.asList("com.tools.Util.utilMethod"))
            .build();
        ClassInfo service = new ClassInfo.Builder()
            .name("MyService")
            .methods(java.util.Arrays.asList(doWork))
            .build();

        FileInfo fService = new FileInfo.Builder()
            .filePath("src/main/java/com/example/MyService.java")
            .packageName("com.example")
            .classes(java.util.Arrays.asList(service))
            .build();
        FileInfo fUtil = new FileInfo.Builder()
            .filePath("src/main/java/com/tools/Util.java")
            .packageName("com.tools")
            .classes(java.util.Arrays.asList(util))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fService, fUtil), java.util.Collections.emptyList());

        String doWorkNode = "M:com.example.MyService#doWork()";
        String utilMethodNode = "M:com.tools.Util#utilMethod()";
        assertThat(graph.containsEdge(doWorkNode, utilMethodNode)).isTrue();

        java.util.Map<String, Object> md = dependencyGraphService.getMetadata();
        assertThat(((Number)md.get("callsQualifiedFqnResolved")).intValue()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testBuildFromParsedFiles_uniqueSimpleNameFallbackResolves() {
        // Only one Helper class exists across known classes (unique simple name)
        MethodInfo help = new MethodInfo.Builder()
            .name("help")
            .signature("help()")
            .build();
        ClassInfo helper = new ClassInfo.Builder()
            .name("Helper")
            .methods(java.util.Arrays.asList(help))
            .build();

        // Service references Helper.help() without import and different package
        MethodInfo run = new MethodInfo.Builder()
            .name("run")
            .signature("run()")
            .calledQualifiedMethods(java.util.Arrays.asList("Helper.help"))
            .build();
        ClassInfo service = new ClassInfo.Builder()
            .name("Runner")
            .methods(java.util.Arrays.asList(run))
            .build();

        FileInfo fService = new FileInfo.Builder()
            .filePath("src/main/java/app/Runner.java")
            .packageName("app")
            .classes(java.util.Arrays.asList(service))
            .build();
        FileInfo fHelper = new FileInfo.Builder()
            .filePath("src/main/java/tools/Helper.java")
            .packageName("tools")
            .classes(java.util.Arrays.asList(helper))
            .build();

        Graph<String, DefaultEdge> graph = dependencyGraphService.buildFromParsedFiles(
            java.util.Arrays.asList(fService, fHelper), java.util.Collections.emptyList());

        String runNode = "M:app.Runner#run()";
        String helpNode = "M:tools.Helper#help()";
        assertThat(graph.containsEdge(runNode, helpNode)).isTrue();

        java.util.Map<String, Object> md = dependencyGraphService.getMetadata();
        assertThat(((Number)md.get("callsQualifiedUniqueSimpleResolved")).intValue()).isGreaterThanOrEqualTo(1);
    }
}
