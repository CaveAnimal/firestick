#!/usr/bin/env python3
"""
Simplified CodeLlama-compatible LLM microservice for Firestick
Provides LLM capabilities via a lightweight implementation
Runs on port 8001 for integration with Java backend
"""

import os
import sys
import json
import logging
import re
from datetime import datetime
from flask import Flask, request, jsonify
from flask_cors import CORS

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

# ============================================================================
# Mock LLM Implementation (No large model downloads needed)
# ============================================================================

class SimpleLLM:
    """Lightweight pattern-based code analyzer"""
    
    def __init__(self):
        self.initialized = True
        logger.info("✓ Lightweight LLM initialized")
    
    def explain_code(self, code: str) -> str:
        """Explain what code does"""
        lines = code.strip().split('\n')
        if not lines:
            return "Empty code"
        
        explanation = f"Code Analysis ({len(lines)} lines):\n"
        
        # Detect patterns
        if 'def ' in code or 'function' in code:
            explanation += "- Defines a function/method\n"
        if 'class ' in code:
            explanation += "- Defines a class\n"
        if 'for ' in code or 'while ' in code:
            explanation += "- Contains loop logic\n"
        if 'if ' in code:
            explanation += "- Has conditional logic\n"
        if 'import ' in code:
            explanation += "- Imports external modules\n"
        
        return explanation.strip()
    
    def generate_docs(self, code: str) -> str:
        """Generate documentation from code"""
        lines = code.strip().split('\n')
        if not lines:
            return "No code to document"
        
        # Extract function/class names
        func_pattern = r'(?:def|function)\s+(\w+)'
        class_pattern = r'class\s+(\w+)'
        
        funcs = re.findall(func_pattern, code)
        classes = re.findall(class_pattern, code)
        
        doc = "## Code Documentation\n\n"
        
        if classes:
            doc += f"### Classes\n- {', '.join(classes)}\n\n"
        
        if funcs:
            doc += f"### Functions\n- {', '.join(funcs)}\n\n"
        
        doc += f"### Code Length\n- {len(lines)} lines\n"
        
        return doc.strip()
    
    def detect_patterns(self, code: str) -> list:
        """Detect common patterns in code"""
        patterns = []
        
        # Design patterns
        if 'super().__init__' in code or '__init__' in code:
            patterns.append("constructor_pattern")
        if 'try:' in code and 'except' in code:
            patterns.append("exception_handling")
        if 'with ' in code:
            patterns.append("context_manager")
        if '@property' in code:
            patterns.append("property_decorator")
        if 'lambda' in code:
            patterns.append("lambda_function")
        if '**kwargs' in code or '*args' in code:
            patterns.append("variable_arguments")
        
        return patterns
    
    def analyze_relationships(self, code: str) -> dict:
        """Analyze relationships in code"""
        # Find function/class definitions
        func_names = re.findall(r'(?:def|function)\s+(\w+)', code)
        class_names = re.findall(r'class\s+(\w+)', code)
        
        # Find calls/usage
        relationships = {
            "defined_functions": func_names,
            "defined_classes": class_names,
            "imports_detected": bool(re.search(r'import\s+\w+', code)),
            "complexity": "low" if len(code) < 200 else "medium" if len(code) < 500 else "high"
        }
        
        return relationships

# Initialize the simple LLM
simple_llm = SimpleLLM()

# ============================================================================
# REST API Endpoints
# ============================================================================

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        "status": "healthy",
        "service": "CodeLlama LLM Service",
        "timestamp": datetime.now().isoformat(),
        "mode": "lightweight"
    })

@app.route('/api/llm/explain', methods=['POST'])
def explain_code():
    """Explain code functionality"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        if not code:
            return jsonify({"error": "No code provided"}), 400
        
        explanation = simple_llm.explain_code(code)
        
        return jsonify({
            "success": True,
            "explanation": explanation,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error explaining code: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/llm/document', methods=['POST'])
def document_code():
    """Generate documentation for code"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        if not code:
            return jsonify({"error": "No code provided"}), 400
        
        documentation = simple_llm.generate_docs(code)
        
        return jsonify({
            "success": True,
            "documentation": documentation,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error generating documentation: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/llm/patterns', methods=['POST'])
def detect_patterns():
    """Detect code patterns"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        if not code:
            return jsonify({"error": "No code provided"}), 400
        
        patterns = simple_llm.detect_patterns(code)
        
        return jsonify({
            "success": True,
            "patterns": patterns,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error detecting patterns: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/llm/analyze', methods=['POST'])
def analyze_relationships():
    """Analyze code relationships"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        
        if not code:
            return jsonify({"error": "No code provided"}), 400
        
        analysis = simple_llm.analyze_relationships(code)
        
        return jsonify({
            "success": True,
            "analysis": analysis,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error analyzing relationships: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/llm/summarize', methods=['POST'])
def summarize_code():
    """Summarize code (combined analysis)"""
    try:
        data = request.get_json()
        code = data.get('code', '')
        format_type = data.get('format', 'explanation')
        
        if not code:
            return jsonify({"error": "No code provided"}), 400
        
        if format_type == 'explanation':
            result = simple_llm.explain_code(code)
        elif format_type == 'documentation':
            result = simple_llm.generate_docs(code)
        elif format_type == 'patterns':
            result = simple_llm.detect_patterns(code)
        elif format_type == 'analysis':
            result = simple_llm.analyze_relationships(code)
        else:
            result = simple_llm.explain_code(code)
        
        return jsonify({
            "success": True,
            "format": format_type,
            "result": result,
            "timestamp": datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error summarizing code: {e}")
        return jsonify({"error": str(e)}), 500

# ============================================================================
# Main
# ============================================================================

def main():
    """Start the LLM service"""
    logger.info("Starting CodeLlama LLM service on port 8001...")
    logger.info("✓ Service available at http://127.0.0.1:8001")
    logger.info("✓ Health check: GET http://127.0.0.1:8001/health")
    logger.info("✓ Test with: curl http://127.0.0.1:8001/health")
    
    try:
        app.run(host='0.0.0.0', port=8001, debug=False, use_reloader=False, threaded=True)
    except Exception as e:
        logger.error(f"Failed to start service: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
