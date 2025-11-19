"""
CodeLlama Model Wrapper and Processing Engine
Provides text generation and code explanation capabilities
"""

import logging
from typing import Optional, List
from transformers import AutoTokenizer, AutoModelForCausalLM
import os
import sys

# Add repository root to path so we can import `llm_config` from top-level
ROOT_PATH = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if ROOT_PATH not in sys.path:
    sys.path.insert(0, ROOT_PATH)

from llm_config import N_CTX, MAX_TOKENS_DEFAULT
import torch

logger = logging.getLogger(__name__)


class CodeLlamaProcessor:
    """Wrapper for CodeLlama 7B model with explanation generation"""
    
    def __init__(self, model_name: str, cache_dir: str, device: str = "cpu"):
        self.model_name = model_name
        self.cache_dir = cache_dir
        self.device = device
        self.model = None
        self.tokenizer = None
        # Use shared config so microservices share the same limits
        self.context_window = N_CTX
        self.max_new_tokens = MAX_TOKENS_DEFAULT
        
    def load(self) -> bool:
        """Load model and tokenizer"""
        try:
            logger.info(f"Loading model: {self.model_name}")
            self.tokenizer = AutoTokenizer.from_pretrained(
                self.model_name,
                cache_dir=self.cache_dir,
                trust_remote_code=True
            )
            self.model = AutoModelForCausalLM.from_pretrained(
                self.model_name,
                cache_dir=self.cache_dir,
                torch_dtype=torch.float16 if self.device == "cuda" else torch.float32,
                device_map=self.device,
                trust_remote_code=True,
                low_cpu_mem_usage=True
            )
            logger.info("Model loaded successfully")
            return True
        except Exception as e:
            logger.error(f"Failed to load model: {e}")
            return False
    
    def unload(self):
        """Release model from memory"""
        if self.model:
            del self.model
            del self.tokenizer
            torch.cuda.empty_cache()
            logger.info("Model unloaded")
    
    def generate_explanation(self, code_snippet: str, max_tokens: int = 256) -> str:
        """Generate brief explanation of code (2-3 sentences)"""
        if not self.model or not self.tokenizer:
            return ""
        
        try:
            prompt = self._build_explanation_prompt(code_snippet)
            tokens = self.tokenizer.encode(prompt, return_tensors="pt").to(self.device)
            
            output = self.model.generate(
                tokens,
                max_new_tokens=min(max_tokens, self.max_new_tokens),
                temperature=0.7,
                top_p=0.9,
                do_sample=True,
                pad_token_id=self.tokenizer.eos_token_id
            )
            
            response = self.tokenizer.decode(output[0], skip_special_tokens=True)
            # Extract only the new tokens (after prompt)
            return response[len(prompt):].strip()
        except Exception as e:
            logger.error(f"Error generating explanation: {e}")
            return ""
    
    def analyze_relationship(self, from_class: str, to_class: str, context: str, max_tokens: int | None = None) -> str:
        """Explain why two classes are related"""
        if not self.model or not self.tokenizer:
            return ""
        
        try:
            prompt = self._build_relationship_prompt(from_class, to_class, context)
            tokens = self.tokenizer.encode(prompt, return_tensors="pt").to(self.device)
            
            output = self.model.generate(
                tokens,
                max_new_tokens=self.max_new_tokens if max_tokens is None else min(max_tokens, self.max_new_tokens),
                temperature=0.7,
                top_p=0.9,
                do_sample=True,
                pad_token_id=self.tokenizer.eos_token_id
            )
            
            response = self.tokenizer.decode(output[0], skip_special_tokens=True)
            return response[len(prompt):].strip()
        except Exception as e:
            logger.error(f"Error analyzing relationship: {e}")
            return ""
    
    def generate_documentation(self, code_snippet: str, max_tokens: int | None = None) -> str:
        """Generate Javadoc-style documentation"""
        if not self.model or not self.tokenizer:
            return ""
        
        try:
            prompt = self._build_docs_prompt(code_snippet)
            tokens = self.tokenizer.encode(prompt, return_tensors="pt").to(self.device)
            
            output = self.model.generate(
                tokens,
                max_new_tokens=self.max_new_tokens if max_tokens is None else min(max_tokens, self.max_new_tokens),
                temperature=0.7,
                top_p=0.9,
                do_sample=True,
                pad_token_id=self.tokenizer.eos_token_id
            )
            
            response = self.tokenizer.decode(output[0], skip_special_tokens=True)
            return response[len(prompt):].strip()
        except Exception as e:
            logger.error(f"Error generating documentation: {e}")
            return ""
    
    def detect_patterns(self, code_snippet: str) -> List[str]:
        """Identify code patterns and anti-patterns"""
        if not self.model or not self.tokenizer:
            return []
        
        try:
            prompt = self._build_pattern_prompt(code_snippet)
            tokens = self.tokenizer.encode(prompt, return_tensors="pt").to(self.device)
            
            output = self.model.generate(
                tokens,
                max_new_tokens=256,
                temperature=0.7,
                top_p=0.9,
                do_sample=True,
                pad_token_id=self.tokenizer.eos_token_id
            )
            
            response = self.tokenizer.decode(output[0], skip_special_tokens=True)
            response = response[len(prompt):].strip()
            
            # Parse comma-separated patterns
            patterns = [p.strip() for p in response.split(",")]
            return [p for p in patterns if p]
        except Exception as e:
            logger.error(f"Error detecting patterns: {e}")
            return []
    
    def _build_explanation_prompt(self, code: str) -> str:
        """Build prompt for code explanation"""
        code = self._truncate_code(code, 1000)
        return f"""Explain this Java method in 2-3 sentences:

```java
{code}
```

Explanation:"""
    
    def _build_relationship_prompt(self, from_class: str, to_class: str, context: str) -> str:
        """Build prompt for dependency analysis"""
        context = self._truncate_code(context, 500)
        return f"""Explain why {from_class} depends on {to_class}:

Context:
```java
{context}
```

Explanation:"""
    
    def _build_docs_prompt(self, code: str) -> str:
        """Build prompt for documentation generation"""
        code = self._truncate_code(code, 1000)
        return f"""Generate Javadoc documentation for this Java method:

```java
{code}
```

Documentation:"""
    
    def _build_pattern_prompt(self, code: str) -> str:
        """Build prompt for pattern detection"""
        code = self._truncate_code(code, 1000)
        return f"""Identify design patterns and anti-patterns in this code (list as comma-separated values):

```java
{code}
```

Patterns: """
    
    def _truncate_code(self, code: str, max_chars: int) -> str:
        """Truncate code to fit in context window"""
        if len(code) > max_chars:
            return code[:max_chars] + "\n// ... [truncated]"
        return code
