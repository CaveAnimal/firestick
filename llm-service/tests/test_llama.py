"""
Unit tests for CodeLlama LLM processor
"""

import pytest
import os
import sys

# Ensure repo root is on sys.path during tests so llm_config can be imported
ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
if ROOT not in sys.path:
    sys.path.insert(0, ROOT)

# Also add the llm-service folder so 'llama' module can be imported when tests
# are run from the repository root.
LLM_SERVICE_PATH = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if LLM_SERVICE_PATH not in sys.path:
    sys.path.insert(0, LLM_SERVICE_PATH)

from llama import CodeLlamaProcessor
from llm_config import N_CTX, MAX_TOKENS_DEFAULT


class TestCodeLlamaProcessor:
    """Tests for CodeLlamaProcessor class"""
    
    @pytest.fixture
    def processor(self):
        """Create processor instance for testing"""
        p = CodeLlamaProcessor(
            model_name="meta-llama/Llama-2-7b-chat-hf",
            cache_dir="./models",
            device="cpu"
        )
        return p
    
    def test_init(self, processor):
        """Test processor initialization"""
        assert processor.model_name == "meta-llama/Llama-2-7b-chat-hf"
        assert processor.device == "cpu"
        assert processor.context_window == N_CTX
        assert processor.max_new_tokens == MAX_TOKENS_DEFAULT
    
    def test_truncate_code_within_limit(self, processor):
        """Test code truncation when within limit"""
        code = "public void test() { }"
        result = processor._truncate_code(code, 100)
        assert result == code
    
    def test_truncate_code_exceeds_limit(self, processor):
        """Test code truncation when exceeding limit"""
        code = "a" * 1000
        result = processor._truncate_code(code, 100)
        assert len(result) == 100 + len("\n// ... [truncated]")
        assert "[truncated]" in result
    
    def test_build_explanation_prompt(self, processor):
        """Test explanation prompt building"""
        code = "public void test() { }"
        prompt = processor._build_explanation_prompt(code)
        assert "Explain" in prompt
        assert code in prompt
        assert "java" in prompt.lower()
    
    def test_build_relationship_prompt(self, processor):
        """Test relationship prompt building"""
        prompt = processor._build_relationship_prompt(
            "ClassA", "ClassB", "ClassA calls ClassB"
        )
        assert "ClassA" in prompt
        assert "ClassB" in prompt
        assert "depends on" in prompt
    
    def test_build_docs_prompt(self, processor):
        """Test documentation prompt building"""
        code = "public void test() { }"
        prompt = processor._build_docs_prompt(code)
        assert "Javadoc" in prompt
        assert code in prompt
    
    def test_build_pattern_prompt(self, processor):
        """Test pattern detection prompt building"""
        code = "public void test() { }"
        prompt = processor._build_pattern_prompt(code)
        assert "design patterns" in prompt or "patterns" in prompt
        assert code in prompt


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
