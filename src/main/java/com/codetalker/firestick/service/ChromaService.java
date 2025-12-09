package com.codetalker.firestick.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.codetalker.firestick.service.dto.ChromaQueryResponse;

@Service
public class ChromaService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String tenant;
    private final String database;

    public ChromaService(RestTemplate restTemplate,
                         @Value("${chroma.base-url:http://localhost:8000}") String baseUrl,
                         @Value("${chroma.tenant:}") String tenant,
                         @Value("${chroma.database:}") String database) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.tenant = tenant == null ? "" : tenant.trim();
        this.database = database == null ? "" : database.trim();
    }

    public String createCollection(String name) {
        String url = collectionsBase();
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        return postJson(url, payload);
    }

    public String addEmbeddings(String collection, List<float[]> embeddings, List<String> documents) {
        String url = collectionItem(collection) + "/add";
        Map<String, Object> payload = new HashMap<>();
        payload.put("embeddings", embeddings);
        payload.put("documents", documents);
        return postJson(url, payload);
    }

    public List<String> query(String collection, float[] queryEmbedding, int topK) {
        String url = collectionItem(collection) + "/query";
        Map<String, Object> payload = new HashMap<>();
        payload.put("query_embeddings", List.of(queryEmbedding));
        payload.put("n_results", topK);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ChromaQueryResponse response = restTemplate.postForObject(url, entity, ChromaQueryResponse.class);
        if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
            return List.of();
        }
        // Return the first query's documents list
        return response.getDocuments().get(0);
    }

    private String collectionsBase() {
        // Prefer tenant/database namespacing when configured, otherwise fall back to non-namespaced v2 routes
        if (!tenant.isEmpty() && !database.isEmpty()) {
            return baseUrl + "/api/v2/tenants/" + tenant + "/databases/" + database + "/collections";
        }
        return baseUrl + "/api/v2/collections";
    }

    private String collectionItem(String name) {
        return collectionsBase() + "/" + name;
    }

    private String postJson(String url, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        return restTemplate.postForObject(url, entity, String.class);
    }

    // -------- Convenience helpers for app-scoped collection naming --------

    /** Compute the collection name for a given app using a stable scheme. */
    public String collectionForApp(String appName) {
        return ChromaUtil.collectionForApp(appName);
    }

    /** Ensure a collection exists for the given app (idempotent). */
    public String ensureAppCollection(String appName) {
        String name = collectionForApp(appName);
        return createCollection(name);
    }

    /** Add embeddings to the app-scoped collection. */
    public String addEmbeddingsForApp(String appName, List<float[]> embeddings, List<String> documents) {
        return addEmbeddings(collectionForApp(appName), embeddings, documents);
    }

    /** Query the app-scoped collection. */
    public List<String> queryByApp(String appName, float[] queryEmbedding, int topK) {
        return query(collectionForApp(appName), queryEmbedding, topK);
    }
}
