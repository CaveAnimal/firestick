package com.codetalker.firestick.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.model.ClassInfo;
import com.codetalker.firestick.model.FileInfo;

/**
 * Service for analyzing code dependencies using JGraphT.
 */
@Service
public class DependencyGraphService {

    /**
     * Create a simple dependency graph.
     *
     * @return A directed graph representing dependencies
     */
    public Graph<String, DefaultEdge> createDependencyGraph() {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // Example: Add vertices
        graph.addVertex("Module A");
        graph.addVertex("Module B");
        graph.addVertex("Module C");

        // Example: Add edges (dependencies)
        graph.addEdge("Module A", "Module B");
        graph.addEdge("Module B", "Module C");

        return graph;
    }

    /**
     * Add a vertex to the graph.
     *
     * @param graph  The graph instance
     * @param vertex The vertex to add
     */
    public void addVertex(Graph<String, DefaultEdge> graph, String vertex) {
        graph.addVertex(vertex);
    }

    /**
     * Add an edge to the graph.
     *
     * @param graph  The graph instance
     * @param source The source vertex
     * @param target The target vertex
     */
    public void addEdge(Graph<String, DefaultEdge> graph, String source, String target) {
        graph.addEdge(source, target);
    }

    /**
     * Find dependencies for a given vertex.
     *
     * @param graph  The graph instance
     * @param vertex The vertex to analyze
     * @return A list of dependent vertices
     */
    public List<String> findDependencies(Graph<String, DefaultEdge> graph, String vertex) {
        return graph.outgoingEdgesOf(vertex).stream()
                .map(graph::getEdgeTarget)
                .collect(Collectors.toList());
    }

    /**
     * Build a basic dependency graph from parsed files.
     * Adds vertices for files (F:), classes (C:), methods (M:), and imports (I:).
     * Adds edges: F -> C, C -> M, F -> I.
     */
    public Graph<String, DefaultEdge> buildFromParsedFiles(List<FileInfo> files, List<com.codetalker.firestick.model.CodeChunk> chunks) {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DependencyGraphService.class);

        // Indexes to help resolve cross-class calls by name
        java.util.Set<String> knownClassFqns = new java.util.HashSet<>();
        java.util.Map<String, java.util.Map<String, String>> methodsByClassAndName = new java.util.HashMap<>();
        // Call stats
        int callsAttempted = 0;
        int callsResolved = 0;
    int callsIntraClassResolved = 0;
    int callsStaticResolved = 0;
    int callsQualifiedResolved = 0;
    int callsConstructorResolved = 0;
    int callsStaticWildcardResolved = 0;
    int callsQualifiedChainedResolved = 0;
    int callsQualifiedFqnResolved = 0;
    int callsQualifiedUniqueSimpleResolved = 0;
    // Unresolved reason buckets
    int callsUnresolvedStaticNoClass = 0;
    int callsUnresolvedQualifiedNoClass = 0;
    int callsUnresolvedQualifiedNoMethod = 0;
    int callsUnresolvedCtorNoClass = 0;
    int callsUnresolvedCtorNoMethod = 0;

        logger.info("[DependencyGraph] Phase 1: Adding all class nodes");
        for (FileInfo fi : files) {
            if (fi.getClasses() != null) {
                for (ClassInfo cls : fi.getClasses()) {
                    String classFqn = classFqn(fi.getPackageName(), cls);
                    String classNode = "C:" + classFqn;
                    graph.addVertex(classNode);
                    knownClassFqns.add(classFqn);
                }
            }
        }

        logger.info("[DependencyGraph] Phase 2: Adding all method nodes");
        for (FileInfo fi : files) {
            if (fi.getClasses() != null) {
                for (ClassInfo cls : fi.getClasses()) {
                    String classFqn = classFqn(fi.getPackageName(), cls);
                    // prepare per-class method index by simple name
                    methodsByClassAndName.computeIfAbsent(classFqn, k -> new java.util.HashMap<>());
                    if (cls.getMethods() != null) {
                        for (com.codetalker.firestick.model.MethodInfo mi : cls.getMethods()) {
                            String sig = nullToEmpty(mi.getSignature());
                            String methodNode = "M:" + classFqn + "#" + sig;
                            graph.addVertex(methodNode);
                            if (mi.getName() != null) {
                                methodsByClassAndName.get(classFqn).put(mi.getName(), methodNode);
                            }
                        }
                    }
                }
            }
        }

        logger.info("[DependencyGraph] Phase 3: Adding inheritance edges (EXTENDS/IMPLEMENTS)");
        addInheritanceEdges(files, graph);

        logger.info("[DependencyGraph] Phase 4: Adding import edges and file/class/method relationships");
        for (FileInfo fi : files) {
            String fileNode = "F:" + nullToEmpty(fi.getFilePath());
            graph.addVertex(fileNode);

            // Imports (regular)
            if (fi.getImports() != null) {
                for (String imp : fi.getImports()) {
                    String impNorm = normalizeImport(imp);
                    String importNode = "I:" + impNorm;
                    graph.addVertex(importNode);
                    graph.addEdge(fileNode, importNode);
                }
            }

            // Static imports
            if (fi.getStaticImports() != null) {
                for (String imp : fi.getStaticImports()) {
                    String impNorm = normalizeImport(imp);
                    String importNode = "I:" + impNorm;
                    graph.addVertex(importNode);
                    graph.addEdge(fileNode, importNode);
                }
            }

            // Classes and methods
            if (fi.getClasses() != null) {
                for (ClassInfo cls : fi.getClasses()) {
                    String classFqn = classFqn(fi.getPackageName(), cls);
                    String classNode = "C:" + classFqn;
                    graph.addVertex(classNode);
                    // F -> C edge
                    graph.addEdge(fileNode, classNode);

                    // Methods under class
                    java.util.Map<String, String> methodNodeByName = new java.util.HashMap<>();
                    if (cls.getMethods() != null) {
                        for (com.codetalker.firestick.model.MethodInfo mi : cls.getMethods()) {
                            String sig = nullToEmpty(mi.getSignature());
                            String methodNode = "M:" + classFqn + "#" + sig;
                            graph.addVertex(methodNode);
                            // C -> M edge
                            graph.addEdge(classNode, methodNode);
                            // track by simple method name for CALL edges
                            if (mi.getName() != null) {
                                methodNodeByName.put(mi.getName(), methodNode);
                            }
                        }
                        // Add CALLS edges (method -> method) within same class by name
                        for (com.codetalker.firestick.model.MethodInfo mi : cls.getMethods()) {
                            if (mi.getCalledMethods() != null) {
                                String sourceSig = nullToEmpty(mi.getSignature());
                                String sourceNode = "M:" + classFqn + "#" + sourceSig;
                                for (String called : mi.getCalledMethods()) {
                                    callsAttempted++;
                                    String targetNode = methodNodeByName.get(called);
                                    if (targetNode != null) {
                                        graph.addEdge(sourceNode, targetNode);
                                        callsResolved++;
                                        callsIntraClassResolved++;
                                    }
                                }
                                // Heuristic: static-imported unqualified calls across classes
                                if (fi.getStaticImports() != null) {
                                    for (String called : mi.getCalledMethods()) {
                                        callsAttempted++;
                                        java.util.List<String> candidateClasses = resolveClassesFromStaticImports(fi.getStaticImports(), called);
                                        if (candidateClasses.isEmpty()) {
                                            callsUnresolvedStaticNoClass++;
                                        }
                                        for (String targetClassFqn : candidateClasses) {
                                            if (methodsByClassAndName.containsKey(targetClassFqn)) {
                                                String targetNode = methodsByClassAndName.get(targetClassFqn).get(called);
                                                if (targetNode != null) {
                                                    graph.addEdge(sourceNode, targetNode);
                                                    callsResolved++;
                                                    // count wildcard vs exact separately
                                                    boolean wildcard = isWildcardStaticImportFor(fi.getStaticImports(), targetClassFqn, called);
                                                    if (wildcard) callsStaticWildcardResolved++; else callsStaticResolved++;
                                                    break; // resolved once is enough
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Qualified calls like OtherClass.someMethod or com.example.OtherClass.someMethod
                            if (mi.getCalledQualifiedMethods() != null) {
                                String sourceSig = nullToEmpty(mi.getSignature());
                                String sourceNode = "M:" + classFqn + "#" + sourceSig;
                                for (String qcall : mi.getCalledQualifiedMethods()) {
                                    callsAttempted++;
                                    String qualifier;
                                    String methodName;
                                    int idx = qcall.lastIndexOf('.') ;
                                    if (idx > 0) {
                                        qualifier = qcall.substring(0, idx);
                                        methodName = qcall.substring(idx + 1);
                                    } else {
                                        // if somehow malformed, skip
                                        continue;
                                    }
                                    // Simple chained call heuristic: allow qualifiers ending with "()" (treat as class constructor invocation)
                                    if (qualifier.endsWith("()")) {
                                        callsQualifiedChainedResolved++; // track chained qualifier attempts that lead to resolution
                                        qualifier = qualifier.substring(0, qualifier.length() - 2);
                                    }
                                    String targetClassFqn;
                                    if ("this".equals(qualifier)) {
                                        targetClassFqn = classFqn;
                                    } else if ("super".equals(qualifier)) {
                                        targetClassFqn = resolveSuperClassFqn(fi, cls.getSuperClass(), knownClassFqns);
                                    } else {
                                        // If qualifier is a fully-qualified class name and known, resolve directly
                                        if (qualifier.contains(".") && knownClassFqns.contains(qualifier)) {
                                            targetClassFqn = qualifier;
                                            callsQualifiedFqnResolved++;
                                        } else {
                                            targetClassFqn = resolveQualifiedClassFqn(fi, qualifier, knownClassFqns);
                                            // As a fallback, if simple name uniquely matches a known class, use it
                                            if (targetClassFqn == null && !qualifier.contains(".")) {
                                                String unique = resolveByUniqueSimpleName(qualifier, knownClassFqns);
                                                if (unique != null) {
                                                    targetClassFqn = unique;
                                                    callsQualifiedUniqueSimpleResolved++;
                                                }
                                            }
                                        }
                                    }
                                    if (targetClassFqn != null) {
                                        java.util.Map<String, String> targets = methodsByClassAndName.get(targetClassFqn);
                                        if (targets != null) {
                                            String targetNode = targets.get(methodName);
                                            if (targetNode != null) {
                                                graph.addEdge(sourceNode, targetNode);
                                                callsResolved++;
                                                callsQualifiedResolved++;
                                            } else {
                                                callsUnresolvedQualifiedNoMethod++;
                                            }
                                        } else {
                                            callsUnresolvedQualifiedNoMethod++;
                                        }
                                    } else {
                                        callsUnresolvedQualifiedNoClass++;
                                    }
                                }
                            }
                            // Constructor invocations (by simple or fully-qualified class name)
                            if (mi.getCalledConstructors() != null) {
                                String sourceSig = nullToEmpty(mi.getSignature());
                                String sourceNode = "M:" + classFqn + "#" + sourceSig;
                                for (String ctorRef : mi.getCalledConstructors()) {
                                    if (ctorRef == null || ctorRef.isEmpty()) continue;
                                    callsAttempted++;
                                    String targetClassFqn = resolveQualifiedClassFqn(fi, ctorRef, knownClassFqns);
                                    if (targetClassFqn == null) {
                                        // if ctorRef looked fully-qualified but not known, try same-package fallback
                                        if (!ctorRef.contains(".")) {
                                            String candidate = (fi.getPackageName() == null || fi.getPackageName().isEmpty()) ? ctorRef : fi.getPackageName() + "." + ctorRef;
                                            if (knownClassFqns.contains(candidate)) targetClassFqn = candidate;
                                        }
                                    }
                                    if (targetClassFqn != null) {
                                        // Constructor node heuristic: method with simple name equal to class simple name
                                        String simpleClassName = targetClassFqn.substring(targetClassFqn.lastIndexOf('.') + 1);
                                        java.util.Map<String, String> targets = methodsByClassAndName.get(targetClassFqn);
                                        if (targets != null) {
                                            String targetNode = targets.get(simpleClassName);
                                            if (targetNode != null) {
                                                graph.addEdge(sourceNode, targetNode);
                                                callsResolved++;
                                                callsConstructorResolved++;
                                            } else {
                                                callsUnresolvedCtorNoMethod++;
                                            }
                                        } else {
                                            callsUnresolvedCtorNoMethod++;
                                        }
                                    } else {
                                        callsUnresolvedCtorNoClass++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        logger.info("[DependencyGraph] Phase 5: Adding chunk nodes and edges");
        if (chunks != null) {
            for (com.codetalker.firestick.model.CodeChunk chunk : chunks) {
                String chunkNode = "CHUNK:" + nullToEmpty(chunk.getName()) + ":" + nullToEmpty(chunk.getType());
                graph.addVertex(chunkNode);
                if (chunk.getParent() != null) {
                    String parentNode = "CHUNK:" + nullToEmpty(chunk.getParent().getName()) + ":" + nullToEmpty(chunk.getParent().getType());
                    graph.addVertex(parentNode);
                    graph.addEdge(parentNode, chunkNode);
                }
                if (chunk.getChildren() != null) {
                    for (com.codetalker.firestick.model.CodeChunk child : chunk.getChildren()) {
                        String childNode = "CHUNK:" + nullToEmpty(child.getName()) + ":" + nullToEmpty(child.getType());
                        graph.addVertex(childNode);
                        graph.addEdge(chunkNode, childNode);
                    }
                }
            }
        }

        logger.info("[DependencyGraph] Phase 6: Storing graph metadata");
        // Store call stats
        addMetadata("callsAttempted", callsAttempted);
        addMetadata("callsResolved", callsResolved);
        addMetadata("callEdgesAdded", callsResolved);
        addMetadata("callsUnresolved", Math.max(0, callsAttempted - callsResolved));
        addMetadata("callsIntraClassResolved", callsIntraClassResolved);
        addMetadata("callsStaticResolved", callsStaticResolved);
        addMetadata("callsQualifiedResolved", callsQualifiedResolved);
        addMetadata("callsConstructorResolved", callsConstructorResolved);
        addMetadata("callsStaticWildcardResolved", callsStaticWildcardResolved);
        addMetadata("callsQualifiedChainedResolved", callsQualifiedChainedResolved);
        addMetadata("callsQualifiedFqnResolved", callsQualifiedFqnResolved);
        addMetadata("callsQualifiedUniqueSimpleResolved", callsQualifiedUniqueSimpleResolved);
        addMetadata("callsUnresolvedStaticNoClass", callsUnresolvedStaticNoClass);
        addMetadata("callsUnresolvedQualifiedNoClass", callsUnresolvedQualifiedNoClass);
        addMetadata("callsUnresolvedQualifiedNoMethod", callsUnresolvedQualifiedNoMethod);
        addMetadata("callsUnresolvedCtorNoClass", callsUnresolvedCtorNoClass);
        addMetadata("callsUnresolvedCtorNoMethod", callsUnresolvedCtorNoMethod);
        storeBuildMetadata(graph);
        return graph;
    }

    // Adds EXTENDS and IMPLEMENTS edges between class nodes using FileInfo
    public void addInheritanceEdges(List<FileInfo> files, Graph<String, DefaultEdge> graph) {
        for (FileInfo file : files) {
            if (file.getClasses() != null) {
                for (ClassInfo cls : file.getClasses()) {
                    String classNode = "C:" + classFqn(file.getPackageName(), cls);
                    graph.addVertex(classNode);
                    // EXTENDS edge
                    if (cls.getSuperClass() != null) {
                        String superNode = "C:" + nullToEmpty(cls.getSuperClass());
                        graph.addVertex(superNode);
                        graph.addEdge(classNode, superNode);
                    }
                    // IMPLEMENTS edges
                    if (cls.getInterfaces() != null) {
                        for (String interfaceName : cls.getInterfaces()) {
                            String interfaceNode = "C:" + nullToEmpty(interfaceName);
                            graph.addVertex(interfaceNode);
                            graph.addEdge(classNode, interfaceNode);
                        }
                    }
                }
            }
        }
    }

    private String classFqn(String pkg, ClassInfo cls) {
        String name = cls.getSignature() != null && !cls.getSignature().isEmpty() ? cls.getSignature() : cls.getName();
        if (pkg == null || pkg.isEmpty()) return name;
        if (name != null && name.contains(".")) return name; // already FQN
        return pkg + "." + name;
    }

    private String normalizeImport(String imp) {
        if (imp == null) return "";
        String s = imp.trim();
        // If the raw toString() from JavaParser includes semicolon/newlines, strip them
        s = s.replace("import ", "")
             .replace("static ", "")
             .replace(";", "")
             .trim();
        return s;
    }

    // Heuristic: given static imports and a bare method name, return candidate class FQNs
    private java.util.List<String> resolveClassesFromStaticImports(java.util.List<String> staticImports, String methodName) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (staticImports == null || methodName == null || methodName.isEmpty()) return out;
        for (String raw : staticImports) {
            String norm = normalizeImport(raw); // e.g., java.lang.Math.max or com.example.Util.*
            if (norm.endsWith("." + methodName)) {
                int idx = norm.lastIndexOf('.');
                if (idx > 0) {
                    out.add(norm.substring(0, idx));
                    continue;
                }
            }
            if (norm.endsWith(".*")) {
                String clazz = norm.substring(0, norm.length() - 2);
                out.add(clazz);
            }
        }
        return out;
    }

    private boolean isWildcardStaticImportFor(java.util.List<String> staticImports, String classFqn, String methodName) {
        // Touch methodName to avoid unused-parameter warnings in some linters
        if (methodName == null) { /* no-op */ }
        if (staticImports == null) return false;
        String wildcard = classFqn + ".*";
        for (String raw : staticImports) {
            String norm = normalizeImport(raw);
            if (norm.equals(wildcard)) return true;
        }
        return false;
    }

    // Heuristic: resolve a class FQN from a qualifier using file imports and same-package fallback
    private String resolveQualifiedClassFqn(FileInfo fi, String qualifier, java.util.Set<String> knownClassFqns) {
        if (qualifier == null || qualifier.isEmpty()) return null;
        // If already looks like FQN and is known, accept it
        if (qualifier.contains(".")) {
            if (knownClassFqns.contains(qualifier)) return qualifier;
            // If not known, still return null to avoid linking to external libs
        } else {
            // Simple class name: try imports first
            if (fi.getImports() != null) {
                for (String rawImp : fi.getImports()) {
                    String normImp = normalizeImport(rawImp);
                    int idx = normImp.lastIndexOf('.');
                    String simple = idx >= 0 ? normImp.substring(idx + 1) : normImp;
                    if (simple.equals(qualifier)) {
                        if (knownClassFqns.contains(normImp)) return normImp;
                    }
                }
            }
            // Same-package fallback
            String candidate = (fi.getPackageName() == null || fi.getPackageName().isEmpty()) ? qualifier : fi.getPackageName() + "." + qualifier;
            if (knownClassFqns.contains(candidate)) return candidate;
        }
        return null;
    }

    private String resolveSuperClassFqn(FileInfo fi, String superClassValue, java.util.Set<String> knownClassFqns) {
        if (superClassValue == null || superClassValue.isEmpty()) return null;
        // If fully-qualified and known, use it
        if (superClassValue.contains(".")) {
            return knownClassFqns.contains(superClassValue) ? superClassValue : null;
        }
        // Else try same package as current class
        String pkg = fi.getPackageName();
        if (pkg != null && !pkg.isEmpty()) {
            String cand = pkg + "." + superClassValue;
            if (knownClassFqns.contains(cand)) return cand;
        }
        // As a last resort, if a class with that simple name is known uniquely, pick it
        String found = null;
        for (String k : knownClassFqns) {
            if (k.endsWith("." + superClassValue) || k.equals(superClassValue)) {
                if (found != null && !found.equals(k)) {
                    return null; // ambiguous
                }
                found = k;
            }
        }
        return found;
    }

    // If there is exactly one known class whose simple name equals the provided simpleName, return its FQN; otherwise null
    private String resolveByUniqueSimpleName(String simpleName, java.util.Set<String> knownClassFqns) {
        if (simpleName == null || simpleName.isEmpty()) return null;
        String found = null;
        for (String k : knownClassFqns) {
            int i = k.lastIndexOf('.');
            String s = i >= 0 ? k.substring(i + 1) : k;
            if (simpleName.equals(s)) {
                if (found != null && !found.equals(k)) {
                    return null; // ambiguous
                }
                found = k;
            }
        }
        return found;
    }

    // --- Graph Metadata Storage ---
    private final java.util.Map<String, Object> graphMetadata = new java.util.HashMap<>();

    public void addMetadata(String key, Object value) {
        graphMetadata.put(key, value);
    }

    public java.util.Map<String, Object> getMetadata() {
        return new java.util.HashMap<>(graphMetadata);
    }

    // Store basic metadata at end of buildFromParsedFiles
    // (call this at the end of buildFromParsedFiles)
    public void storeBuildMetadata(Graph<?, ?> graph) {
        addMetadata("nodeCount", graph.vertexSet().size());
        addMetadata("edgeCount", graph.edgeSet().size());
        addMetadata("buildTimestamp", System.currentTimeMillis());
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }

    // --- Query helpers ---
    public java.util.List<String> getCallees(Graph<String, DefaultEdge> graph, String methodNode) {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (DefaultEdge e : graph.outgoingEdgesOf(methodNode)) {
            String tgt = graph.getEdgeTarget(e);
            if (tgt != null && tgt.startsWith("M:")) out.add(tgt);
        }
        return out;
    }

    public java.util.List<String> getCallers(Graph<String, DefaultEdge> graph, String methodNode) {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (DefaultEdge e : graph.incomingEdgesOf(methodNode)) {
            String src = graph.getEdgeSource(e);
            if (src != null && src.startsWith("M:")) out.add(src);
        }
        return out;
    }

    public java.util.List<java.util.List<String>> findCircularClassDependencies(Graph<String, DefaultEdge> graph) {
        // Build a class-only subgraph (simple directed, no parallel edges)
        Graph<String, DefaultEdge> classGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        for (String v : graph.vertexSet()) {
            if (v.startsWith("C:")) classGraph.addVertex(v);
        }
        for (DefaultEdge e : graph.edgeSet()) {
            String s = graph.getEdgeSource(e);
            String t = graph.getEdgeTarget(e);
            if (s.startsWith("C:") && t.startsWith("C:")) {
                // Ensure vertices exist
                classGraph.addVertex(s);
                classGraph.addVertex(t);
                // Avoid duplicate edges
                if (!classGraph.containsEdge(s, t)) {
                    classGraph.addEdge(s, t);
                }
            }
        }
        // Use a cycles algorithm that does not reuse edge instances across intermediate graphs
        org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles<String, DefaultEdge> algo =
            new org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles<>(classGraph);
        return algo.findSimpleCycles();
    }
}
