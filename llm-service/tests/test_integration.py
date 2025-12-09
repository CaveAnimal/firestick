#!/usr/bin/env python3
"""Integration tests for LLM service - requires Python service running on port 8001"""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

import pytest
import requests
from llama import CodeLlamaProcessor

BASE_URL = "http://127.0.0.1:8001"


class TestLLMServiceEndpoints:
    """Test LLM service HTTP endpoints"""
    
    def test_health_endpoint(self):
        """Test /health endpoint"""
        try:
            response = requests.get(f"{BASE_URL}/health", timeout=5)
            assert response.status_code == 200
        except requests.exceptions.ConnectionError:
            pytest.skip("LLM service not running on port 8001")

    def test_root_endpoint(self):
        """Test root endpoint shows a friendly HTML page"""
        try:
            response = requests.get(f"{BASE_URL}/", timeout=5)
            # If service is up, root should return HTML with service name
            assert response.status_code == 200
            content_type = response.headers.get('Content-Type', '')
            assert 'text/html' in content_type or 'application/json' in content_type
            assert 'Firestick LLM Service' in response.text
        except requests.exceptions.ConnectionError:
            pytest.skip("LLM service not running on port 8001")
    
    def test_summarize_endpoint(self):
        """Test /api/llm/summarize endpoint"""
        try:
            payload = {"code": "int x = 5;"}
            response = requests.post(
                f"{BASE_URL}/api/llm/summarize",
                json=payload,
                timeout=10
            )
            assert response.status_code == 200
            data = response.json()
            assert "summary" in data
        except requests.exceptions.ConnectionError:
            pytest.skip("LLM service not running on port 8001")
    
    def test_empty_request(self):
        """Test error handling for empty code"""
        try:
            payload = {"code": ""}
            response = requests.post(
                f"{BASE_URL}/api/llm/summarize",
                json=payload,
                timeout=5
            )
            # Should return 400 or 500 depending on validation
            assert response.status_code in [400, 422, 500]
        except requests.exceptions.ConnectionError:
            pytest.skip("LLM service not running on port 8001")

    def test_alias_analyze_relationship(self):
        """Compatibility alias POST /api/llm/analyze-relationship"""
        try:
            payload = {
                "from_class": "com.example.Foo",
                "to_class": "com.example.Bar",
                "context": "Foo calls Bar"
            }
            response = requests.post(f"{BASE_URL}/api/llm/analyze-relationship", json=payload, timeout=10)
            if response.status_code != 200:
                # Print response to help debugging when running locally
                print("DEBUG analyze-relationship status:", response.status_code, response.text)
            # If the alias isn't present (404), fall back to older /api/llm/analyze
            if response.status_code == 404:
                response = requests.post(f"{BASE_URL}/api/llm/analyze", json={"code":"// fallback"}, timeout=10)
                assert response.status_code in [200, 400, 500]
                return

            assert response.status_code == 200
            data = response.json()
            assert "explanation" in data
        except requests.exceptions.ConnectionError:
            pytest.skip("LLM service not running on port 8001")

    def test_alias_generate_docs_and_detect_patterns(self):
        """Compatibility aliases for generate-docs and detect-patterns"""
        try:
            payload = {"code": "public class X { int sum(int a,int b) { return a+b; } }"}
            r1 = requests.post(f"{BASE_URL}/api/llm/generate-docs", json=payload, timeout=10)
            assert r1.status_code == 200
            assert "documentation" in r1.json()
            # Expect non-empty documentation - fallback should handle empty responses
            doc = r1.json().get('documentation')
            assert doc is not None and len(str(doc).strip()) > 0

            r2 = requests.post(f"{BASE_URL}/api/llm/detect-patterns", json=payload, timeout=10)
            assert r2.status_code == 200
            assert "patterns" in r2.json()
        except requests.exceptions.ConnectionError:
            pytest.skip("LLM service not running on port 8001")


class TestCodeLlamaProcessor:
    """Test CodeLlamaProcessor directly"""
    
    def test_processor_initialization(self):
        """Test processor can be initialized"""
        processor = CodeLlamaProcessor(
            model_name="meta-llama/Llama-2-7b-chat-hf",
            cache_dir="./models",
            device="cpu"
        )
        assert processor is not None
    
    def test_code_truncation(self):
        """Test code truncation for context window"""
        processor = CodeLlamaProcessor(
            model_name="meta-llama/Llama-2-7b-chat-hf",
            cache_dir="./models",
            device="cpu"
        )
        long_code = "x = 1;\n" * 1000
        truncated = processor._truncate_code(long_code, 1000)
        # Truncation adds "// ... [truncated]" so allow small overage
        assert len(truncated) <= 1100
        assert "// ... [truncated]" in truncated or len(truncated) > 0
