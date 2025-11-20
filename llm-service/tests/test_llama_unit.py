#!/usr/bin/env python3
"""Unit tests for CodeLlamaProcessor - tests individual methods"""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

import pytest
from unittest.mock import patch, MagicMock
from llama import CodeLlamaProcessor


class TestCodeLlamaProcessorUnit:
    """Unit tests for CodeLlamaProcessor methods"""
    
    @pytest.fixture
    def processor(self):
        """Create processor instance for testing (CPU to avoid CUDA issues)"""
        with patch('llama.AutoTokenizer.from_pretrained'):
            with patch('llama.AutoModelForCausalLM.from_pretrained'):
                return CodeLlamaProcessor(
                    model_name="test-model",
                    cache_dir="./models",
                    device="cpu"
                )
    
    def test_truncate_code_under_limit(self, processor):
        """Test truncate_code when code is under limit"""
        code = "int x = 5;"
        result = processor._truncate_code(code, 100)
        assert result == code
        assert len(result) <= 100
    
    def test_truncate_code_over_limit(self, processor):
        """Test truncate_code when code exceeds limit"""
        code = "x = 1;\n" * 500
        result = processor._truncate_code(code, 100)
        assert "// ... [truncated]" in result or len(result) > 0
        assert len(result) < len(code)
    
    def test_truncate_code_empty_string(self, processor):
        """Test truncate_code with empty string"""
        result = processor._truncate_code("", 100)
        assert result == ""
    
    def test_truncate_code_respects_max_chars(self, processor):
        """Test that truncate_code respects max_chars parameter"""
        code = "a" * 200
        for max_chars in [50, 100, 150]:
            result = processor._truncate_code(code, max_chars)
            # Allow small overage for truncation message
            assert len(result) <= max_chars + 50
    
    def test_build_explanation_prompt_returns_string(self, processor):
        """Test _build_explanation_prompt returns valid string"""
        code = "int x = 5;"
        prompt = processor._build_explanation_prompt(code)
        assert isinstance(prompt, str)
        assert len(prompt) > 0
        assert code in prompt or "code" in prompt.lower()
    
    def test_build_relationship_prompt_returns_string(self, processor):
        """Test _build_relationship_prompt returns valid string"""
        prompt = processor._build_relationship_prompt("class A {}", "class B {}", "java")
        assert isinstance(prompt, str)
        assert len(prompt) > 0
    
    def test_build_docs_prompt_returns_string(self, processor):
        """Test _build_docs_prompt returns valid string"""
        code = "public void doSomething() {}"
        prompt = processor._build_docs_prompt(code)
        assert isinstance(prompt, str)
        assert len(prompt) > 0
    
    def test_build_pattern_prompt_returns_string(self, processor):
        """Test _build_pattern_prompt returns valid string"""
        code = "for (int i = 0; i < n; i++) {}"
        prompt = processor._build_pattern_prompt(code)
        assert isinstance(prompt, str)
        assert len(prompt) > 0
    
    def test_processor_initializes_with_device(self):
        """Test processor initialization with device parameter"""
        for device in ["cpu"]:
            with patch('llama.AutoTokenizer.from_pretrained'):
                with patch('llama.AutoModelForCausalLM.from_pretrained'):
                    processor = CodeLlamaProcessor(
                        model_name="test-model",
                        device=device,
                        cache_dir="./models"
                    )
                    assert processor.device == device
    
    def test_processor_initializes_with_model_name(self):
        """Test processor initialization with model name"""
        model_name = "test-model-123"
        with patch('llama.AutoTokenizer.from_pretrained'):
            with patch('llama.AutoModelForCausalLM.from_pretrained'):
                processor = CodeLlamaProcessor(
                    model_name=model_name,
                    device="cpu",
                    cache_dir="./models"
                )
                assert processor.model_name == model_name
    
    def test_processor_initializes_with_cache_dir(self):
        """Test processor initialization with cache directory"""
        cache_dir = "./test_models"
        with patch('llama.AutoTokenizer.from_pretrained'):
            with patch('llama.AutoModelForCausalLM.from_pretrained'):
                processor = CodeLlamaProcessor(
                    model_name="test",
                    device="cpu",
                    cache_dir=cache_dir
                )
                assert processor.cache_dir == cache_dir
    
    def test_build_prompts_with_special_characters(self, processor):
        """Test prompt building with special characters in code"""
        special_code = 'print("Hello\\nWorld\\t!");'
        
        # All prompt builders should handle special chars gracefully
        prompt1 = processor._build_explanation_prompt(special_code)
        prompt2 = processor._build_docs_prompt(special_code)
        prompt3 = processor._build_pattern_prompt(special_code)
        
        assert all(isinstance(p, str) and len(p) > 0 for p in [prompt1, prompt2, prompt3])
    
    def test_truncate_code_with_unicode(self, processor):
        """Test truncate_code handles unicode characters"""
        code = "# Comment with unicode: é à ü\nx = 1"
        result = processor._truncate_code(code, 100)
        assert isinstance(result, str)
        assert len(result) > 0


class TestCodeLlamaProcessorIntegration:
    """Integration tests that test interaction between methods"""
    
    @pytest.fixture
    def processor(self):
        """Create processor instance for testing"""
        with patch('llama.AutoTokenizer.from_pretrained'):
            with patch('llama.AutoModelForCausalLM.from_pretrained'):
                return CodeLlamaProcessor(
                    model_name="test-model",
                    cache_dir="./models",
                    device="cpu"
                )
    
    def test_truncate_then_build_explanation_prompt(self, processor):
        """Test truncation followed by prompt building"""
        long_code = "x = 1;\n" * 500
        truncated = processor._truncate_code(long_code, 100)
        prompt = processor._build_explanation_prompt(truncated)
        assert isinstance(prompt, str)
        assert len(prompt) > 0
    
    def test_all_prompts_with_same_code(self, processor):
        """Test all prompt builders with same code"""
        code = "public class Example { public int getValue() { return 42; } }"
        
        prompts = [
            processor._build_explanation_prompt(code),
            processor._build_relationship_prompt(code, code, "java"),
            processor._build_docs_prompt(code),
            processor._build_pattern_prompt(code)
        ]
        
        # All should return non-empty strings
        assert all(isinstance(p, str) and len(p) > 0 for p in prompts)
    
    def test_processor_state_consistency(self, processor):
        """Test that processor maintains consistent state across calls"""
        code1 = "code1"
        code2 = "code2"
        
        prompt1a = processor._build_explanation_prompt(code1)
        prompt2 = processor._build_explanation_prompt(code2)
        prompt1b = processor._build_explanation_prompt(code1)
        
        # Same code should produce same prompt
        assert prompt1a == prompt1b
        assert prompt1a != prompt2

