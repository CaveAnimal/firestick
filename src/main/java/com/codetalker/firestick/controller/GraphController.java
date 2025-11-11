package com.codetalker.firestick.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.exception.ErrorResponse;
import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalker.firestick.service.dto.GraphResponse;
import com.codetalker.firestick.service.struct.CodeStructureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Simple graph API exposing File -> Class -> Method containment edges based on persisted chunks.
 * This is an initial endpoint to power the UI graph; future iterations can add call edges and imports.
 */
@RestController
@Validated
@RequestMapping(path = "/api/graph", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Graph")
public class GraphController {
    private static final Logger log = LoggerFactory.getLogger(GraphController.class);

    private final CodeFileRepository codeFileRepository;
    private final CodeChunkRepository codeChunkRepository;
    private final CodeStructureService codeStructureService;
    private final DependencyGraphService dependencyGraphService;

    public GraphController(CodeFileRepository codeFileRepository, CodeChunkRepository codeChunkRepository,
                           CodeStructureService codeStructureService, DependencyGraphService dependencyGraphService) {
        this.codeFileRepository = codeFileRepository;
        this.codeChunkRepository = codeChunkRepository;
        this.codeStructureService = codeStructureService;
        this.dependencyGraphService = dependencyGraphService;
    }

    /**
     * Returns a basic containment graph composed from CodeFile/CodeChunk persistence.
     * Nodes use IDs prefixed with F:, C:, M:. Labels are human-readable names.
     */
    @Operation(summary = "Basic graph", description = "Returns a basic containment graph composed from CodeFile/CodeChunk persistence.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Graph data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GraphResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/basic")
    public GraphResponse getBasicGraph(
        @RequestParam(name = "limit", required = false)
        @jakarta.validation.constraints.Min(1) Integer limit
    ) {
        List<GraphResponse.Node> nodes = new ArrayList<>();
        List<GraphResponse.Edge> edges = new ArrayList<>();

        Map<String, GraphResponse.Node> nodeById = new HashMap<>();
        int filesCount = 0, classesCount = 0, methodsCount = 0;

        // Regex to extract class name from method metadata like "method;class=MyClass"
        Pattern metaClassPattern = Pattern.compile("(?:^|;)class=([^;]+)(?:;|$)");

        List<CodeFile> files = codeFileRepository.findAll();
        if (limit != null && limit > 0 && files.size() > limit) {
            files = files.subList(0, limit);
        }

        for (CodeFile file : files) {
            String fileNodeId = "F:" + file.getId();
            GraphResponse.Node fNode = new GraphResponse.Node(fileNodeId, file.getFilePath(), "F");
            nodeById.putIfAbsent(fileNodeId, fNode);
            filesCount++;

            List<CodeChunk> chunks = codeChunkRepository.findByFile(file);
            if (chunks == null || chunks.isEmpty()) continue;

            // Map class name -> class node id for this file
            Map<String, String> classNodeIdByName = new HashMap<>();

            // First pass: add class nodes and file->class edges
            for (CodeChunk c : chunks) {
                String type = normalize(c.getType());
                if ("class".equalsIgnoreCase(type)) {
                    String clsName = safe(c.getName());
                    String clsSig = safe(c.getSignature());
                    String classId = "C:" + (clsSig.isEmpty() ? (file.getFilePath() + "#" + clsName) : clsSig);
                    GraphResponse.Node cNode = new GraphResponse.Node(classId, clsSig.isEmpty() ? clsName : clsSig, "C");
                    if (!nodeById.containsKey(classId)) {
                        nodeById.put(classId, cNode);
                        classesCount++;
                    }
                    classNodeIdByName.putIfAbsent(clsName, classId);
                    edges.add(new GraphResponse.Edge(fileNodeId, classId, "declares"));
                }
            }

            // Second pass: add method nodes and class->method edges
            for (CodeChunk c : chunks) {
                String type = normalize(c.getType());
                if ("method".equalsIgnoreCase(type)) {
                    String methodName = safe(c.getName());
                    String methodSig = safe(c.getSignature());
                    // Try to resolve parent class from metadata; fallback to first known class in the same file
                    String parentClassId = null;
                    String metadata = safe(c.getMetadata());
                    if (!metadata.isEmpty()) {
                        Matcher m = metaClassPattern.matcher(metadata);
                        if (m.find()) {
                            String className = m.group(1);
                            parentClassId = classNodeIdByName.get(className);
                        }
                    }
                    if (parentClassId == null && !classNodeIdByName.isEmpty()) {
                        parentClassId = classNodeIdByName.values().iterator().next();
                    }

                    String methodId = "M:" + (parentClassId != null ? (parentClassId.substring(2) + "#" + methodSig) : (file.getFilePath() + "#" + methodSig));
                    String label = methodSig.isEmpty() ? methodName : methodSig;
                    GraphResponse.Node mNode = new GraphResponse.Node(methodId, label, "M");
                    if (!nodeById.containsKey(methodId)) {
                        nodeById.put(methodId, mNode);
                        methodsCount++;
                    }
                    if (parentClassId != null) {
                        edges.add(new GraphResponse.Edge(parentClassId, methodId, "contains"));
                    } else {
                        edges.add(new GraphResponse.Edge(fileNodeId, methodId, "contains"));
                    }
                }
            }
        }

        // Finalize nodes list preserving insertion order
        nodes.addAll(nodeById.values());
        Map<String, Object> md = new HashMap<>();
        md.put("files", filesCount);
        md.put("classes", classesCount);
        md.put("methods", methodsCount);
        md.put("edges", edges.size());

        log.info("[Graph] Emitted basic graph: files={}, classes={}, methods={}, edges={} (limit={})",
            filesCount, classesCount, methodsCount, edges.size(), limit);

        return new GraphResponse(nodes, edges, md);
    }

    private static String normalize(String s) { return s == null ? "" : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s; }

    /**
     * Enriched graph including containment (F->C->M), imports (F->I), method calls (M->M), and inheritance (C->C).
     * Query params allow server-side filtering.
     */
    @Operation(summary = "Enriched graph", description = "Enriched graph including containment (F->C->M), imports (F->I), method calls (M->M), and inheritance (C->C).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Graph data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GraphResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/enriched")
    public GraphResponse getEnrichedGraph(
        @RequestParam(name = "limit", required = false)
        @jakarta.validation.constraints.Min(1) Integer limit,
            @RequestParam(name = "include", required = false) String include,
            @RequestParam(name = "nodeTypes", required = false) String nodeTypes
    ) {
        // Parse include flags
        boolean incContainment = true, incImports = true, incCalls = true, incInheritance = true;
        if (include != null && !include.isBlank()) {
            String inc = include.toLowerCase();
            incContainment = inc.contains("containment") || inc.contains("declares") || inc.contains("contains");
            incImports = inc.contains("imports");
            incCalls = inc.contains("calls");
            incInheritance = inc.contains("inheritance") || inc.contains("extends") || inc.contains("implements");
        }

        java.util.Set<String> allowedNodeTypes = new java.util.HashSet<>();
        if (nodeTypes == null || nodeTypes.isBlank()) {
            allowedNodeTypes.add("F");
            allowedNodeTypes.add("C");
            allowedNodeTypes.add("M");
            allowedNodeTypes.add("I");
        } else {
            for (String t : nodeTypes.split(",")) {
                if (t != null && !t.isBlank()) allowedNodeTypes.add(t.trim().toUpperCase());
            }
        }

        // Load files
        List<CodeFile> files = codeFileRepository.findAll();
        if (limit != null && limit > 0 && files.size() > limit) {
            files = files.subList(0, limit);
        }

        // Build FileInfo from source using JavaParser and visitors
        List<com.codetalker.firestick.model.FileInfo> parsedFiles = new ArrayList<>();
        for (CodeFile f : files) {
            try {
                var fi = codeStructureService.buildFileInfoFromPath(f.getFilePath());
                if (fi != null) parsedFiles.add(fi);
            } catch (Exception e) {
                log.warn("[Graph] Failed to parse file for enriched graph: {}", f.getFilePath(), e);
            }
        }

        // Optionally pass chunks for CHUNK relationship rendering (currently unused here)
        List<CodeChunk> allChunks = new ArrayList<>();
        for (CodeFile f : files) {
            var chunks = codeChunkRepository.findByFile(f);
            if (chunks != null) allChunks.addAll(chunks);
        }

        var g = dependencyGraphService.buildFromParsedFiles(parsedFiles, allChunks);

        // Map vertices and edges to GraphResponse with labels inferred by type prefixes
        Map<String, GraphResponse.Node> nodeById = new HashMap<>();
        List<GraphResponse.Edge> edges = new ArrayList<>();

        java.util.function.Function<String, String> nodeTypeOf = (id) -> {
            if (id == null || id.length() < 2 || id.charAt(1) != ':') return "?";
            return id.substring(0, 1);
        };

        java.util.function.Function<String, String> nodeLabelOf = (id) -> {
            if (id == null) return "";
            int idx = id.indexOf(':');
            return idx >= 0 ? id.substring(idx + 1) : id;
        };

        // Add nodes filtered by allowed types
        for (String v : g.vertexSet()) {
            String t = nodeTypeOf.apply(v);
            if (!allowedNodeTypes.contains(t)) continue;
            nodeById.put(v, new GraphResponse.Node(v, nodeLabelOf.apply(v), t));
        }

        // Edge labeling heuristic
        java.util.function.BiFunction<String, String, String> edgeLabel = (s, t) -> {
            String st = nodeTypeOf.apply(s), tt = nodeTypeOf.apply(t);
            if ("F".equals(st) && "C".equals(tt)) return "declares";
            if ("C".equals(st) && "M".equals(tt)) return "contains";
            if ("F".equals(st) && "I".equals(tt)) return "imports";
            if ("M".equals(st) && "M".equals(tt)) return "calls";
            if ("C".equals(st) && "C".equals(tt)) return "inherits";
            return "rel";
        };

        // Add edges with include filters; ensure endpoints exist and types allowed
        for (org.jgrapht.graph.DefaultEdge e : g.edgeSet()) {
            String s = g.getEdgeSource(e);
            String t = g.getEdgeTarget(e);
            String lbl = edgeLabel.apply(s, t);
            boolean keep = (incContainment && ("declares".equals(lbl) || "contains".equals(lbl)))
                        || (incImports && "imports".equals(lbl))
                        || (incCalls && "calls".equals(lbl))
                        || (incInheritance && "inherits".equals(lbl));
            if (!keep) continue;
            String st = nodeTypeOf.apply(s), tt = nodeTypeOf.apply(t);
            if (!allowedNodeTypes.contains(st) || !allowedNodeTypes.contains(tt)) continue;
            // Ensure nodes present
            nodeById.computeIfAbsent(s, k -> new GraphResponse.Node(k, nodeLabelOf.apply(k), st));
            nodeById.computeIfAbsent(t, k -> new GraphResponse.Node(k, nodeLabelOf.apply(k), tt));
            edges.add(new GraphResponse.Edge(s, t, lbl));
        }

        List<GraphResponse.Node> nodes = new ArrayList<>(nodeById.values());
        Map<String, Object> md = new HashMap<>();
        md.putAll(dependencyGraphService.getMetadata());
        md.put("nodes", nodes.size());
        md.put("edges", edges.size());
        md.put("includeCalls", incCalls);
        md.put("includeImports", incImports);
        md.put("includeContainment", incContainment);
        md.put("includeInheritance", incInheritance);

        log.info("[Graph] Emitted enriched graph: nodes={}, edges={}, include=[calls={}, imports={}, contain={}, inherit={}]",
            nodes.size(), edges.size(), incCalls, incImports, incContainment, incInheritance);

        return new GraphResponse(nodes, edges, md);
    }
}
