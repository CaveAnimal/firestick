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

    public ChromaService(RestTemplate restTemplate,
                         @Value("${chroma.base-url:http://localhost:8000}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public String createCollection(String name) {
        String url = baseUrl + "/api/v1/collections";
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        return postJson(url, payload);
    }

    public String addEmbeddings(String collection, List<float[]> embeddings, List<String> documents) {
        String url = baseUrl + "/api/v1/collections/" + collection + "/add";
        Map<String, Object> payload = new HashMap<>();
        payload.put("embeddings", embeddings);
        payload.put("documents", documents);
        return postJson(url, payload);
    }

    public List<String> query(String collection, float[] queryEmbedding, int topK) {
        String url = baseUrl + "/api/v1/collections/" + collection + "/query";
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

    private String postJson(String url, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        return restTemplate.postForObject(url, entity, String.class);
    }
}
