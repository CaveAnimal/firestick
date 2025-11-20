#!/usr/bin/env python3
"""
CodeLlama 7B Flask microservice for Firestick
Provides LLM capabilities: code explanation, documentation, pattern detection, relationship analysis
Runs on port 8001 for integration with Java backend
"""

import os
import sys
import json
import logging
from typing import List, Dict, Optional
from flask import Flask, request, jsonify
from flask_cors import CORS
import torch
from llm_config import (
    MAX_TOKENS_DEFAULT,
    EXPLAIN_MAX_TOKENS,
    DOCUMENT_MAX_TOKENS,
    PATTERNS_MAX_TOKENS,
    ANALYZE_MAX_TOKENS,
)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

# Global model variables
model = None
tokenizer = None
device = None

def initialize_model():
    """Initialize CodeLlama 7B model on first run"""
    global model, tokenizer, device
    
    if model is not None:
        return True
    
    try:
        logger.info("Initializing CodeLlama 7B model...")
        
        # Use CPU-friendly settings for 7B model
        device = "cpu"
        if torch.cuda.is_available():
            device = "cuda"
            logger.info(f"Using GPU: {torch.cuda.get_device_name(0)}")
        else:
            logger.info("GPU not available, using CPU")
        
        # Import transformers (will download if needed)
        from transformers import AutoTokenizer, AutoModelForCausalLM
        
        # Allow overriding model path via environment variable
        # Default to a freely available model if not specified
        model_name = os.getenv("MODEL_PATH", "mistralai/Mistral-7B-Instruct-v0.1")
        
        logger.info(f"Loading tokenizer from {model_name}...")
        tokenizer = AutoTokenizer.from_pretrained(model_name)
        
        logger.info(f"Loading model from {model_name}...")
        model = AutoModelForCausalLM.from_pretrained(
            model_name,
            torch_dtype=torch.float16 if device == "cuda" else torch.float32,
            device_map="auto" if device == "cuda" else None,
            load_in_8bit=False
        )
        
        if device == "cpu":
            model = model.to(device)
        
        model.eval()
        logger.info("Model initialized successfully")
        return True
        
    except ImportError as e:
        logger.error(f"Missing required package: {e}")
        logger.info("Install with: pip install torch transformers flask flask-cors")
        return False
    except Exception as e:
        logger.error(f"Failed to initialize model: {e}")
        return False

def generate_response(prompt: str, max_tokens: int = MAX_TOKENS_DEFAULT) -> str:
    """Generate response from CodeLlama"""
    if model is None or tokenizer is None:
        return ""
    
    try:
        inputs = tokenizer(prompt, return_tensors="pt")
        if device == "cuda":
            inputs = {k: v.to(device) for k, v in inputs.items()}
        
        with torch.no_grad():
            outputs = model.generate(
                **inputs,
                max_new_tokens=max_tokens,
                temperature=0.7,
                top_p=0.9,
                do_sample=True
            )
        
        response = tokenizer.decode(outputs[0], skip_special_tokens=True)
        # Remove the prompt from response
        if prompt in response:
            response = response[len(prompt):].strip()
        return response
    except Exception as e:
        logger.error(f"Error generating response: {e}")
        return ""

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'model_loaded': model is not None,
        'device': device
    }), 200

@app.route('/api/llm/summarize', methods=['POST'])
def summarize():
    """
    Summarize/Explain code
    Expected JSON: {"code": "...code...", "format": "explanation|summary|docs"}
    """
    try:
        data = request.get_json()
        if not data or 'code' not in data:
            return jsonify({'error': 'Missing "code" field'}), 400
        
        code = data.get('code', '')
        format_type = data.get('format', 'explanation')
        
        if format_type == 'docs':
            prompt = f"""Generate JSDoc documentation for this code:
```
{code}
```

Documentation:"""
        else:  # explanation or summary
            prompt = f"""Explain what this code does in 2-3 sentences:
```
{code}
```

Explanation:"""
        
        explanation = generate_response(prompt, max_tokens=EXPLAIN_MAX_TOKENS)
        
        return jsonify({
            'code': code,
            'explanation': explanation,
            'format': format_type
        }), 200
    
    except Exception as e:
        logger.error(f"Error in summarize: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/llm/analyze-relationship', methods=['POST'])
def analyze_relationship():
    """
    Analyze relationship between two classes
    Expected JSON: {"from_class": "...", "to_class": "...", "context": "..."}
    """
    try:
        data = request.get_json()
        from_class = data.get('from_class', '')
        to_class = data.get('to_class', '')
        context = data.get('context', '')
        
        prompt = f"""Analyze the relationship between {from_class} and {to_class}.
Context: {context}

What is the relationship and why would {from_class} call {to_class}?
Relationship:"""
        
        analysis = generate_response(prompt, max_tokens=ANALYZE_MAX_TOKENS)
        
        return jsonify({
            'from_class': from_class,
            'to_class': to_class,
            'analysis': analysis
        }), 200
    
    except Exception as e:
        logger.error(f"Error in analyze_relationship: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/llm/detect-patterns', methods=['POST'])
def detect_patterns():
    """
    Detect design patterns in code
    Expected JSON: {"code": "...code..."}
    """
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        prompt = f"""Identify design patterns in this code:
```
{code}
```

List each pattern found (e.g., Singleton, Observer, Factory, etc):
Patterns:"""
        
        patterns_text = generate_response(prompt, max_tokens=PATTERNS_MAX_TOKENS)
        
        # Parse patterns from response
        patterns = [p.strip() for p in patterns_text.split('\n') if p.strip() and not p.startswith('#')]
        
        return jsonify({
            'code': code,
            'patterns': patterns,
            'raw_analysis': patterns_text
        }), 200
    
    except Exception as e:
        logger.error(f"Error in detect_patterns: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/llm/generate-docs', methods=['POST'])
def generate_docs():
    """
    Generate documentation
    Expected JSON: {"code": "...code...", "style": "javadoc|markdown"}
    """
    try:
        data = request.get_json()
        code = data.get('code', '')
        # Accept either 'style' or 'format' - both are used in the wild
        style = data.get('style', data.get('format', 'javadoc'))
        
        if style == 'markdown':
            prompt = f"""Generate comprehensive Markdown documentation for:
```
{code}
```

Documentation (Markdown format):
"""
        else:  # javadoc
            prompt = f"""Generate comprehensive JavaDoc for:
```
{code}
```

JavaDoc:"""
        
        docs = generate_response(prompt, max_tokens=DOCUMENT_MAX_TOKENS)
        if not docs or len(docs.strip()) == 0:
            logger.warning("Empty documentation returned from model, retrying with fallback prompt")
            fallback = f"""Generate JavaDoc-style documentation for the following code. Include @param and @return descriptions if applicable:

```
{code}
```

JavaDoc:"""
            docs = generate_response(fallback, max_tokens=DOCUMENT_MAX_TOKENS)
            # If we still didn't get anything, use the simple LLM fallback to avoid empty responses
            if not docs or len(docs.strip()) == 0:
                logger.warning("DOCUMENT FALLBACK EMPTY - invoking lightweight fallback")
                try:
                    from llm_service_simple import simple_llm
                    docs = simple_llm.generate_docs(code)
                    logger.info("SimpleLLM generated fallback docs")
                except Exception as e:
                    logger.error(f"SimpleLLM fallback failed: {e}")
        
        return jsonify({
            'code': code,
            'documentation': docs,
            'style': style
        }), 200
    
    except Exception as e:
        logger.error(f"Error in generate_docs: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/llm/expand-query', methods=['POST'])
def expand_query():
    """Expand user query with technical synonyms"""
    try:
        data = request.get_json()
        query = data.get('query', '')
        
        if not query:
            return jsonify({"error": "No query provided"}), 400
            
        prompt = f"""You are an expert Java developer. Provide 5-10 technical keywords, class names, or concepts related to the following query for a legacy Java application. Do not explain, just list the terms separated by commas.

Query: {query}
Terms:"""

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
        
        return jsonify({
            "success": True,
            "expanded_terms": terms,
            "original_query": query
        })

    except Exception as e:
        logger.error(f"Error expanding query: {e}")
        return jsonify({"error": str(e)}), 500


@app.route('/api/llm/answer-question', methods=['POST'])
def answer_question():
    """Answer question using provided code context (RAG)"""
    try:
        data = request.get_json()
        query = data.get('query', '')
        context_chunks = data.get('context_chunks', [])
        
        if not query:
            return jsonify({"error": "No query provided"}), 400
            
        # Format context
        formatted_context = ""
        for i, chunk in enumerate(context_chunks):
            formatted_context += f"\n--- Chunk {i+1} ---\n{chunk}\n"
            
        prompt = f"""Using the following code snippets, answer the user's original question. Cite specific classes or methods. If the answer is not in the context, state that.

Question: {query}

Context:
{formatted_context}

Answer:"""

        # Use a larger token limit for the answer
        answer = generate_response(prompt, max_tokens=512)
        
        return jsonify({
            "success": True,
            "answer": answer
        })

    except Exception as e:
        logger.error(f"Error answering question: {e}")
        return jsonify({"error": str(e)}), 500

def main():
    """Main entry point"""
    port = int(os.getenv('LLM_PORT', 8001))
    debug = os.getenv('LLM_DEBUG', 'false').lower() == 'true'
    
    logger.info(f"Starting CodeLlama LLM service on port {port}...")
    
    if not initialize_model():
        logger.error("Failed to initialize model. Exiting.")
        sys.exit(1)
    
    logger.info("✓ Model loaded successfully")
    logger.info(f"✓ Service available at http://127.0.0.1:{port}")
    logger.info("✓ Endpoints:")
    logger.info("  POST /api/llm/summarize - Explain/summarize code")
    logger.info("  POST /api/llm/analyze-relationship - Analyze class relationships")
    logger.info("  POST /api/llm/detect-patterns - Detect design patterns")
    logger.info("  POST /api/llm/generate-docs - Generate documentation")
    logger.info("  GET /health - Health check")
    
    app.run(host='127.0.0.1', port=port, debug=debug, threaded=True)

if __name__ == '__main__':
    main()
