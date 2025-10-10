package com.caveanimal.firestick.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for health check and basic information.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "firestick");
        response.put("features", new String[]{
            "JavaParser - Code parsing & AST analysis",
            "JGraphT - Dependency graph analysis",
            "Apache Lucene - Code search",
            "H2 Database - Embedded SQL",
            "ONNX Runtime - Embeddings",
            "DJL - Deep Java Library"
        });
        return response;
    }
}
