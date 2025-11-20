#!/usr/bin/env python3
"""
CodeLlama 7B GGUF Flask microservice for Firestick
Uses local GGUF model (no downloads needed)
Provides LLM capabilities: code explanation, documentation, pattern detection, relationship analysis
Runs on port 8001 for integration with Java backend
"""

import os
import sys
import json
import logging
from datetime import datetime
from typing import Dict, List, Optional
from flask import Flask, request, jsonify
from flask_cors import CORS

# Fix Windows console encoding for Unicode characters
if sys.platform == "win32":
    # Enable UTF-8 output on Windows
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

# Create LLM logs directory with date
logs_dir = "logs/LLMlogs"
os.makedirs(logs_dir, exist_ok=True)

# Create date-stamped log filenames
date_str = datetime.now().strftime("%Y-%m-%d")
log_file = os.path.join(logs_dir, f"llm_service_{date_str}.log")
requests_log_file = os.path.join(logs_dir, f"llm_requests_{date_str}.log")

# Configure main logger with UTF-8 encoding
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(log_file, encoding='utf-8'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

# Create separate logger for requests
requests_logger = logging.getLogger("llm_requests")
requests_handler = logging.FileHandler(requests_log_file, encoding='utf-8')
requests_handler.setFormatter(logging.Formatter('%(asctime)s - %(message)s'))
requests_logger.addHandler(requests_handler)
requests_logger.setLevel(logging.INFO)

app = Flask(__name__)
CORS(app)

# ============================================================================
# Load Local GGUF Model with llama-cpp-python
# ============================================================================

model = None
# Allow overriding model path via environment variable
model_path = os.getenv("MODEL_PATH", "models/codellama-7b.Q4_K_M.gguf")

# Shared config for context and token caps
from llm_config import (
    N_CTX,
    MAX_TOKENS_DEFAULT,
    EXPLAIN_MAX_TOKENS,
    DOCUMENT_MAX_TOKENS,
    PATTERNS_MAX_TOKENS,
    ANALYZE_MAX_TOKENS,
)

def initialize_model():
    """Initialize CodeLlama from local GGUF file"""
    global model
    
    if model is not None:
        return True
    
    try:
        # Check if GGUF file exists
        if not os.path.exists(model_path):
            logger.error(f"❌ Model file not found: {model_path}")
            logger.error(f"Current directory: {os.getcwd()}")
            logger.error(f"Listing models directory:")
            if os.path.exists("models"):
                for f in os.listdir("models"):
                    logger.error(f"  - {f}")
            return False
        
        logger.info(f"✓ Found model file: {model_path}")
        logger.info(f"✓ File size: {os.path.getsize(model_path) / (1024**3):.2f} GB")
        
        # Try to import llama-cpp-python
        try:
            from llama_cpp import Llama
            logger.info("✓ llama-cpp-python imported successfully")
        except ImportError:
            logger.error("❌ llama-cpp-python not installed")
            logger.info("Install with: pip install llama-cpp-python")
            return False
        
        logger.info("Loading CodeLlama 7B model (this may take a minute)...")
        
        # Load the model
        model = Llama(
            model_path=model_path,
            n_gpu_layers=-1,  # Use GPU if available, CPU otherwise
            n_threads=4,
            n_ctx=N_CTX,  # Context window (configurable variable)
            verbose=False
        )
        logger.info("✓ CodeLlama 7B model loaded successfully")
        logger.info(f"✓ Model context: {N_CTX} tokens")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to initialize model: {e}")
        import traceback
        traceback.print_exc()
        return False

def generate_response(prompt: str, max_tokens: int = MAX_TOKENS_DEFAULT) -> str:
    """Generate a response using the loaded model"""
    try:
        if model is None:
            error_msg = "Model not initialized"
            logger.error(error_msg)
            return f"Error: {error_msg}"
        logger.debug(f"Generating response with max_tokens={max_tokens}")
        response = model(
            prompt,
            max_tokens=max_tokens,
            temperature=0.7,
            top_p=0.9,
            stop=["```", "---"]
        )
        
        result = response["choices"][0]["text"].strip()
        logger.debug(f"Generation complete: {len(result)} chars")
        return result
    except Exception as e:
        logger.error(f"Generation error: {e}", exc_info=True)
        return f"Error generating response: {e}"

# ============================================================================
# REST API Endpoints
# ============================================================================

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    health_data = {
        "status": "healthy",
        "service": "CodeLlama LLM Service (GGUF)",
        "timestamp": datetime.now().isoformat(),
        "model": "codellama-7b.Q4_K_M.gguf",
        "model_loaded": model is not None
    }
    requests_logger.info(f"HEALTH CHECK - Status: {health_data['status']}, Model Loaded: {health_data['model_loaded']}")
    return jsonify(health_data)


@app.route('/', methods=['GET'])
def root():
        """Friendly root page so browsing to the service shows something useful"""
        html = """
        <!doctype html>
        <html lang="en">
            <head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Firestick LLM (GGUF)</title></head>
            <body style="font-family:system-ui,Segoe UI,Roboto,Helvetica,Arial;max-width:800px;margin:24px auto;">
                <h1>Firestick LLM (GGUF)</h1>
                <p>GGUF-based LLM microservice for local inference.</p>
                <ul>
                    <li><a href="/health">/health</a> - health check</li>
                    <li>/api/llm/summarize - POST - summarize code</li>
                        <li>/api/llm/explain - POST - explain a code snippet</li>
                        <li>/api/llm/analyze-relationship - POST - analyze relation (compat alias)</li>
                        <li>/api/llm/generate-docs - POST - generate docs (compat alias)</li>
                        <li>/api/llm/detect-patterns - POST - detect patterns (compat alias)</li>
                </ul>
                <p>See logs for model loading status in <code>logs/LLMlogs</code>.</p>
            </body>
        </html>
        """
        return html, 200, {'Content-Type': 'text/html; charset=utf-8'}

@app.route('/api/llm/explain', methods=['POST'])
def explain_code():
    """Explain code functionality"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        requests_logger.info(f"EXPLAIN REQUEST - Code length: {len(code)} chars")
        logger.debug(f"Received code: {code[:200]}...")
        
        if not code:
            requests_logger.warning("EXPLAIN FAILED - No code provided")
            return jsonify({"error": "No code provided"}), 400
        
        prompt = f"""[INST] Explain what this code does in 2-3 sentences:

```
{code}
```
[/INST]"""
        
        logger.debug(f"Generated prompt: {prompt[:300]}...")
        explanation = generate_response(prompt, max_tokens=EXPLAIN_MAX_TOKENS)
        
        requests_logger.info(f"EXPLAIN SUCCESS - Response length: {len(explanation)} chars, Response: {explanation[:100]}...")
        logger.debug(f"Full response: {explanation}")
        
        return jsonify({
            "success": True,
            "explanation": explanation,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error explaining code: {e}", exc_info=True)
        requests_logger.error(f"EXPLAIN ERROR - {str(e)}")
        return jsonify({"error": str(e)}), 500


def _generate_documentation_response(data):
    """Shared implementation for documentation generation"""
    code = data.get('code', '')
    style = data.get('style', data.get('format', 'javadoc'))
    
    requests_logger.info(f"DOCUMENT REQUEST - Code length: {len(code)} chars")
    logger.debug(f"Received code: {code[:200]}...")
    
    if not code:
        requests_logger.warning("DOCUMENT FAILED - No code provided")
        return jsonify({"error": "No code provided"}), 400
    
    if style and style.lower().startswith('mark'):
        prompt = f"""[INST] Generate comprehensive Markdown documentation for this code:

```
{code}
```
[/INST]"""
    else:
        # Default: JavaDoc
        prompt = f"""[INST] Write comprehensive JavaDoc for this code:

```
{code}
```
[/INST]"""

    logger.debug(f"Generated prompt: {prompt[:300]}...")
    documentation = generate_response(prompt, max_tokens=DOCUMENT_MAX_TOKENS)
    
    # If the model returned an empty string, attempt a fallback prompt.
    if not documentation or len(documentation.strip()) == 0:
        requests_logger.warning("DOCUMENT EMPTY - trying fallback prompt")
        fallback_prompt = f"""Generate detailed JavaDoc-style documentation for the following code. Include descriptions about parameters and return values if present:

```
{code}
```

Please produce the JavaDoc comment only:"""
        documentation = generate_response(fallback_prompt, max_tokens=DOCUMENT_MAX_TOKENS)
        # If the fallback still didn't work, use a lightweight fallback
        if not documentation or len(documentation.strip()) == 0:
            requests_logger.warning("DOCUMENT FALLBACK EMPTY - invoking lightweight fallback")
            try:
                # import the SimpleLLM-based generator from the simple service
                from llm_service_simple import simple_llm
                documentation = simple_llm.generate_docs(code)
                requests_logger.info("DOCUMENT FALLBACK - SimpleLLM produced documentation")
            except Exception:
                requests_logger.exception("DOCUMENT FALLBACK ERROR - simple generator failed")

    requests_logger.info(f"DOCUMENT SUCCESS - Response length: {len(documentation)} chars, Response: {documentation[:100]}...")
    logger.debug(f"Full response: {documentation}")
    
    return jsonify({
        "success": True,
        "documentation": documentation,
        "timestamp": datetime.now().isoformat()
    })


@app.route('/api/llm/document', methods=['POST'])
def document_code():
    """Generate documentation for code"""
    try:
        data = request.get_json()
        # Use helper that accepts a payload dict so both the normal
        # endpoint and compatibility alias call the same implementation.
        return _generate_documentation_response(data)
        
        requests_logger.info(f"DOCUMENT REQUEST - Code length: {len(code)} chars")
        logger.debug(f"Received code: {code[:200]}...")
        
        if not code:
            requests_logger.warning("DOCUMENT FAILED - No code provided")
            return jsonify({"error": "No code provided"}), 400
        
        if style and style.lower().startswith('mark'):
            prompt = f"""Generate comprehensive Markdown documentation for this code:

```
{code}
```

Documentation:"""
        else:
            # Default: JavaDoc
            prompt = f"""Write comprehensive JavaDoc for this code:

```
{code}
```

JavaDoc:"""

        logger.debug(f"Generated prompt: {prompt[:300]}...")
        documentation = generate_response(prompt, max_tokens=DOCUMENT_MAX_TOKENS)
        
        # If the model returned an empty string, attempt a fallback prompt.
        if not documentation or len(documentation.strip()) == 0:
            requests_logger.warning("DOCUMENT EMPTY - trying fallback prompt")
            fallback_prompt = f"""Generate detailed JavaDoc-style documentation for the following code. Include descriptions about parameters and return values if present:

```
{code}
```

Please produce the JavaDoc comment only:"""
            documentation = generate_response(fallback_prompt, max_tokens=DOCUMENT_MAX_TOKENS)
            # If the fallback still didn't work, use a lightweight fallback
            if not documentation or len(documentation.strip()) == 0:
                requests_logger.warning("DOCUMENT FALLBACK EMPTY - invoking lightweight fallback")
                try:
                    # import the SimpleLLM-based generator from the simple service
                    from llm_service_simple import simple_llm
                    documentation = simple_llm.generate_docs(code)
                    requests_logger.info("DOCUMENT FALLBACK - SimpleLLM produced documentation")
                except Exception:
                    requests_logger.exception("DOCUMENT FALLBACK ERROR - simple generator failed")

        requests_logger.info(f"DOCUMENT SUCCESS - Response length: {len(documentation)} chars, Response: {documentation[:100]}...")
        logger.debug(f"Full response: {documentation}")
        
        return jsonify({
            "success": True,
            "documentation": documentation,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error generating documentation: {e}", exc_info=True)
        requests_logger.error(f"DOCUMENT ERROR - {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/api/llm/expand-query', methods=['POST'])
def expand_query():
    """Expand user query with technical synonyms"""
    try:
        data = request.get_json()
        query = data.get('query', '')
        
        requests_logger.info(f"EXPAND REQUEST - Query: {query}")
        
        if not query:
            return jsonify({"error": "No query provided"}), 400
            
        prompt = f"""[INST] You are an expert Java developer. Provide 5-10 technical keywords, class names, or concepts related to the following query for a legacy Java application. Do not explain, just list the terms separated by commas.

Query: {query}
[/INST]"""

        logger.debug(f"Generated prompt: {prompt}")
        response_text = generate_response(prompt, max_tokens=128)
        
        # Parse response: split by commas or newlines, strip whitespace
        terms = []
        if response_text:
            # Handle comma-separated or newline-separated lists
            raw_terms = response_text.replace('\n', ',').split(',')
            for term in raw_terms:
                clean_term = term.strip().strip('- ').strip()
                if clean_term and len(clean_term) > 2:
                    terms.append(clean_term)
        
        # Deduplicate
        terms = list(set(terms))
        
        requests_logger.info(f"EXPAND SUCCESS - Terms: {terms}")
        
        return jsonify({
            "success": True,
            "expanded_terms": terms,
            "original_query": query,
            "timestamp": datetime.now().isoformat()
        })

    except Exception as e:
        logger.error(f"Error expanding query: {e}", exc_info=True)
        requests_logger.error(f"EXPAND ERROR - {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/api/llm/answer-question', methods=['POST'])
def answer_question():
    """Answer question using provided code context (RAG)"""
    try:
        data = request.get_json()
        query = data.get('query', '')
        context_chunks = data.get('context_chunks', [])
        
        requests_logger.info(f"ANSWER REQUEST - Query: {query}, Chunks: {len(context_chunks)}")
        
        if not query:
            return jsonify({"error": "No query provided"}), 400
            
        # Format context
        formatted_context = ""
        for i, chunk in enumerate(context_chunks):
            formatted_context += f"\n--- Chunk {i+1} ---\n{chunk}\n"
            
        prompt = f"""[INST] Using the following code snippets, answer the user's original question. Cite specific classes or methods. If the answer is not in the context, state that.

Question: {query}

Context:
{formatted_context}
[/INST]"""

        logger.debug(f"Generated prompt length: {len(prompt)}")
        
        # Use a larger token limit for the answer
        answer = generate_response(prompt, max_tokens=512)
        
        requests_logger.info(f"ANSWER SUCCESS - Response length: {len(answer)}")
        
        return jsonify({
            "success": True,
            "answer": answer,
            "timestamp": datetime.now().isoformat()
        })

    except Exception as e:
        logger.error(f"Error answering question: {e}", exc_info=True)
        requests_logger.error(f"ANSWER ERROR - {str(e)}")
        return jsonify({"error": str(e)}), 500


# ---------------------------------------------------------------------------
# Compatibility aliases (Java client expects 'analyze-relationship',
# 'generate-docs', and 'detect-patterns' endpoints). These forward to
# the existing implementations so both Flask and FastAPI microservices
# accept the same routes used by the Java test harness.
# ---------------------------------------------------------------------------


@app.route('/api/llm/analyze-relationship', methods=['POST'])
def analyze_relationships_compat():
    """Alias for backwards-compatibility with Java client endpoints.

    Java client posts {from_class, to_class, context}; the gguf microservice
    expects a `code` field. For compatibility we accept either shape and
    synthesize a prompt when needed. We return JSON with an `explanation`
    field (the Java client expects this) and a `relationship_type` key.
    """
    try:
        payload = request.get_json() or {}

        # If Java-style payload is present, synthesize a prompt
        if payload.get('from_class') or payload.get('to_class'):
            from_class = payload.get('from_class', '<unknown>')
            to_class = payload.get('to_class', '<unknown>')
            context = payload.get('context', '')
            prompt = (
                f"[INST] Explain the relationship between {from_class} and {to_class}.\n"
                f"Context:\n{context}\n"
                "Describe the dependency and interaction in 2-4 sentences. [/INST]"
            )
            explanation = generate_response(prompt, max_tokens=EXPLAIN_MAX_TOKENS)
            return jsonify({
                "explanation": explanation,
                "relationship_type": "dependency"
            }), 200

        # Otherwise fallback to the default code-based analyze which returns `analysis`.
        # We will forward to the existing analysis pipeline but convert key name.
        # Reuse the code path: call generate_response with the code field.
        code = payload.get('code', '')
        if not code:
            return jsonify({"error": "Missing code or relationship fields"}), 400

        analysis = generate_response(f"[INST] Analyze relationships in this code:\n{code} [/INST]", max_tokens=ANALYZE_MAX_TOKENS)
        # Map 'analysis' to 'explanation' to match Java client
        return jsonify({
            "explanation": analysis,
            "relationship_type": "dependency"
        }), 200
    except Exception as e:
        logger.error(f"ANALYZE_COMPAT_ERROR - {e}", exc_info=True)
        return jsonify({"error": str(e)}), 500


@app.route('/api/llm/patterns', methods=['POST'])
@app.route('/api/llm/detect-patterns', methods=['POST'])
def detect_patterns():
    """Detect design patterns and anti-patterns"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        requests_logger.info(f"PATTERNS REQUEST - Code length: {len(code)} chars")
        
        if not code:
            return jsonify({"error": "No code provided"}), 400
            
        prompt = f"""[INST] Identify design patterns and anti-patterns in this code (list as comma-separated values):

```
{code}
```
[/INST]"""

        logger.debug(f"Generated prompt: {prompt[:300]}...")
        response_text = generate_response(prompt, max_tokens=PATTERNS_MAX_TOKENS)
        
        # Parse response
        patterns = []
        issues = []
        
        if response_text:
            raw_items = response_text.replace('\n', ',').split(',')
            for item in raw_items:
                clean_item = item.strip().strip('- ').strip()
                if clean_item:
                    if "issue" in clean_item.lower() or "anti" in clean_item.lower() or "smell" in clean_item.lower():
                        issues.append(clean_item)
                    else:
                        patterns.append(clean_item)
        
        requests_logger.info(f"PATTERNS SUCCESS - Found {len(patterns)} patterns, {len(issues)} issues")
        
        return jsonify({
            "success": True,
            "patterns": patterns,
            "issues": issues,
            "timestamp": datetime.now().isoformat()
        })

    except Exception as e:
        logger.error(f"Error detecting patterns: {e}", exc_info=True)
        requests_logger.error(f"PATTERNS ERROR - {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/api/llm/generate-docs', methods=['POST'])
def generate_docs_compat():
    """Alias for generate docs - maps to /api/llm/document"""
    # Compatibility with Java client: it may send either `format` or `style`.
    # Accept both and forward to the document pipeline.
    payload = request.get_json() or {}
    # Accept 'format' as an alias for 'style' and forward the payload to
    # the internal implementation.
    if 'format' in payload and 'style' not in payload:
        payload['style'] = payload.get('format')

    # Forward to the new shared implementation
    try:
        return _generate_documentation_response(payload)
    except Exception as e:
        # If something unexpected happens return a structured error
        logger.exception("GENERATE_DOCS_COMPAT_ERROR")
        return jsonify({"error": str(e)}), 500


# ============================================================================
# Main
# ============================================================================

def main():
    """Start the LLM service"""
    logger.info("=" * 70)
    logger.info("CodeLlama 7B GGUF LLM Microservice")
    logger.info("=" * 70)
    logger.info(f"Log files created in: {logs_dir}")
    logger.info(f"Main log: {log_file}")
    logger.info(f"Requests log: {requests_log_file}")
    requests_logger.info("=" * 70)
    requests_logger.info("LLM Service Started")
    requests_logger.info("=" * 70)
    
    if not initialize_model():
        logger.error("Failed to initialize model. Exiting.")
        requests_logger.error("FAILED TO INITIALIZE MODEL - Service not starting")
        sys.exit(1)
    
    logger.info("=" * 70)
    logger.info("✓ Service Ready")
    logger.info("✓ Listening on http://127.0.0.1:8001")
    logger.info("✓ Health check: GET http://127.0.0.1:8001/health")
    logger.info("=" * 70)
    requests_logger.info("SERVICE STARTED - Ready to receive requests")
    
    app.run(host='0.0.0.0', port=8001, debug=False, use_reloader=False, threaded=True)

if __name__ == '__main__':
    main()
