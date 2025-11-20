"""Shared LLM configuration for Firestick microservices.

This centralizes common limits used across the different LLM microservices
so we maintain consistent behavior and can tune limits using environment vars
without editing code.

Environment variables:
 - LLM_N_CTX: context window token size used by llama-cpp and compatible backends
 - LLM_MAX_TOKENS_DEFAULT: default max tokens for small/unknown endpoints
 - LLM_EXPLAIN_MAX_TOKENS: explain endpoint override
 - LLM_DOCUMENT_MAX_TOKENS: documentation / heavy outputs
 - LLM_PATTERNS_MAX_TOKENS: pattern-detection endpoints
 - LLM_ANALYZE_MAX_TOKENS: relationship analysis

Good defaults were chosen to balance memory and output length.
"""
from __future__ import annotations

import os
from typing import Dict


def _int_env(name: str, default: int) -> int:
    val = os.getenv(name)
    if val is None:
        return default
    try:
        return int(val)
    except Exception:
        return default


# Global context and token settings (override with env vars if needed)
# Increased default context to 32k to support larger inputs (Mistral Nemo supports 128k)
N_CTX = _int_env("LLM_N_CTX", 32768)
MAX_TOKENS_DEFAULT = _int_env("LLM_MAX_TOKENS_DEFAULT", 1024)

# Endpoint-specific token caps
EXPLAIN_MAX_TOKENS = _int_env("LLM_EXPLAIN_MAX_TOKENS", 1024)
DOCUMENT_MAX_TOKENS = _int_env("LLM_DOCUMENT_MAX_TOKENS", 2048)
PATTERNS_MAX_TOKENS = _int_env("LLM_PATTERNS_MAX_TOKENS", 1024)
ANALYZE_MAX_TOKENS = _int_env("LLM_ANALYZE_MAX_TOKENS", 1024)

# Bundle for convenience
ENDPOINT_MAX_TOKENS: Dict[str, int] = {
    "explain": EXPLAIN_MAX_TOKENS,
    "document": DOCUMENT_MAX_TOKENS,
    "patterns": PATTERNS_MAX_TOKENS,
    "analyze": ANALYZE_MAX_TOKENS,
    "default": MAX_TOKENS_DEFAULT,
}


__all__ = [
    "N_CTX",
    "MAX_TOKENS_DEFAULT",
    "EXPLAIN_MAX_TOKENS",
    "DOCUMENT_MAX_TOKENS",
    "PATTERNS_MAX_TOKENS",
    "ANALYZE_MAX_TOKENS",
    "ENDPOINT_MAX_TOKENS",
]
