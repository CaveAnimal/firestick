# Code Chunking Strategy for Large Legacy Codebase

## Executive Summary

This document outlines a comprehensive chunking strategy for embedding a 1M+ LOC Java/Python hybrid application to enable semantic code search and question-answering capabilities.

## 1. Chunking Best Practices Research

### 1.1 Core Principles

**Semantic Coherence**: Each chunk should represent a complete, meaningful unit of code that can be understood independently.

**Context Preservation**: Include sufficient surrounding context (imports, class declarations, method signatures) to make chunks interpretable.

**Optimal Size Range**: Target 200-1000 tokens per chunk (roughly 150-750 LOC depending on code density).

**Overlap Strategy**: Use 10-20% overlap between adjacent chunks to prevent information loss at boundaries.

**Metadata Enrichment**: Attach comprehensive metadata to each chunk for hybrid search (keyword + semantic).

### 1.2 Research-Backed Findings

- **Recursive Chunking**: Break down code hierarchically (file → class → method) rather than using fixed-size windows
- **AST-Based Parsing**: Use Abstract Syntax Trees to respect code structure boundaries
- **Relationship Preservation**: Maintain parent-child and sibling relationships between code elements
- **Documentation Integration**: Always include associated docstrings, comments, and inline documentation

## 2. Recommended Multi-Level Chunking Strategy

### 2.1 Hierarchical Approach (RECOMMENDED)

Implement multiple chunk granularities simultaneously:

```
Level 1: Method/Function Level (Primary)
Level 2: Class Level (Secondary)
Level 3: File Level with Summary (Tertiary)
Level 4: Module/Package Level (Context)
```

**Rationale**: Different query types benefit from different granularities. A hierarchical approach provides flexibility and better retrieval accuracy.

### 2.2 Chunk Type Specifications

#### **Method-Level Chunks** (Primary - 70% of chunks)

**Scope**: Individual methods/functions with context

**Structure**:
```
- File path and line numbers
- Class declaration (if applicable)
- Method signature with full type annotations
- Method body
- Associated docstring/javadoc
- Preceding comment block
```

**When to Use**:
- Methods > 5 lines
- Public/protected methods
- Methods with docstrings
- Complex private methods (cyclomatic complexity > 5)

**Advantages**:
- Most granular and precise retrieval
- Perfect for "How does X work?" queries
- Minimal noise in search results
- Faster embedding generation

**Disadvantages**:
- May lose inter-method context
- Small utility methods create overhead

**Example Metadata**:
```json
{
  "chunk_type": "method",
  "language": "java",
  "file_path": "src/main/java/com/app/UserService.java",
  "class_name": "UserService",
  "method_name": "authenticateUser",
  "visibility": "public",
  "parameters": ["String username", "String password"],
  "return_type": "AuthResult",
  "line_start": 45,
  "line_end": 78,
  "complexity": 7,
  "has_docstring": true,
  "calls": ["validatePassword", "createSession"],
  "called_by": ["LoginController.login"]
}
```

#### **Class-Level Chunks** (Secondary - 20% of chunks)

**Scope**: Entire class with all methods, fields, and inner classes

**Structure**:
```
- File path
- Package/module declaration
- Import statements (top 10 most relevant)
- Class declaration with inheritance/interfaces
- All field declarations
- Class-level documentation
- All method signatures (just signatures, not bodies)
- Inner classes
```

**When to Use**:
- Classes < 500 lines
- High cohesion classes (single responsibility)
- Classes with strong method interdependencies
- Data classes, POJOs, DTOs

**Advantages**:
- Captures class-level architecture
- Good for "What does class X do?" queries
- Shows relationships between methods
- Includes field context

**Disadvantages**:
- Can exceed token limits for large classes
- May be too broad for specific queries

**Example Metadata**:
```json
{
  "chunk_type": "class",
  "language": "python",
  "file_path": "src/services/payment_processor.py",
  "class_name": "PaymentProcessor",
  "superclasses": ["BaseProcessor", "EventEmitter"],
  "interfaces": ["IPaymentGateway"],
  "method_count": 12,
  "field_count": 8,
  "line_start": 1,
  "line_end": 345,
  "is_abstract": false,
  "design_patterns": ["Strategy", "Observer"]
}
```

#### **File-Level Chunks with Summary** (Tertiary - 8% of chunks)

**Scope**: Entire file with AI-generated summary

**Structure**:
```
- File path and metadata
- AI-generated summary (2-4 sentences)
- Package/module structure
- All import statements
- All class/function signatures
- File-level comments
- Key constants and configurations
```

**When to Use**:
- Files with multiple loosely-coupled classes
- Utility files
- Configuration files
- Script files

**Advantages**:
- Good for high-level navigation
- Helps with "Where is functionality X?" queries
- Captures file-level organization

**Disadvantages**:
- Too coarse for specific implementation questions
- Summary generation adds processing overhead

#### **Module/Package-Level Chunks** (Context - 2% of chunks)

**Scope**: README-style summaries of entire modules

**Structure**:
```
- Module path
- Purpose and responsibility summary
- Key classes and their roles
- Entry points and public API
- Dependencies (internal and external)
- Architecture patterns used
```

**When to Use**:
- Major packages/modules
- Service boundaries
- API layers

**Advantages**:
- Provides architectural context
- Good for "How does subsystem X work?" queries

## 3. Implementation Strategy for Java/Python Hybrid

### 3.1 Parsing Tools

**Java**:
- Primary: JavaParser (https://javaparser.org/)
- Alternative: Eclipse JDT
- Extract AST, resolve symbols, get full type information

**Python**:
- Primary: `ast` module (built-in)
- Alternative: `libcst` for more detailed parsing
- Use type hints from annotations

### 3.2 Chunking Pipeline

```
Step 1: Parse codebase with AST parsers
  ↓
Step 2: Extract all code elements (methods, classes, files)
  ↓
Step 3: Generate chunks at all levels
  ↓
Step 4: Enrich with metadata and relationships
  ↓
Step 5: Generate embeddings
  ↓
Step 6: Store in vector database with metadata
```

### 3.3 Metadata Schema

```json
{
  "id": "unique_chunk_id",
  "chunk_type": "method|class|file|module",
  "language": "java|python",
  "content": "actual code content",
  "content_summary": "AI-generated summary",
  
  "location": {
    "file_path": "relative/path/to/file",
    "line_start": 100,
    "line_end": 150,
    "git_commit": "abc123...",
    "git_branch": "main"
  },
  
  "hierarchy": {
    "package": "com.app.services",
    "class": "UserService",
    "method": "authenticateUser",
    "parent_chunk_id": "class_chunk_id"
  },
  
  "code_attributes": {
    "visibility": "public|private|protected",
    "is_static": false,
    "is_abstract": false,
    "is_deprecated": false,
    "complexity": 7,
    "loc": 35
  },
  
  "relationships": {
    "calls": ["method_chunk_id_1", "method_chunk_id_2"],
    "called_by": ["method_chunk_id_3"],
    "imports": ["java.util.List", "com.app.User"],
    "implements": ["IAuthService"],
    "extends": ["BaseService"]
  },
  
  "documentation": {
    "has_docstring": true,
    "docstring_summary": "Authenticates user credentials",
    "parameters_doc": {...},
    "return_doc": "Authentication result with token",
    "tags": ["authentication", "security"]
  },
  
  "quality_metrics": {
    "has_tests": true,
    "test_coverage": 85.5,
    "last_modified": "2024-10-15",
    "author": "john.doe",
    "modification_count": 12
  }
}
```

## 4. Embedding Strategy (Open Source / Self-Hosted)

### 4.1 Recommended Open Source Models

**PRIMARY RECOMMENDATION - Code-Specific Models**:

1. **microsoft/codebert-base** (768 dimensions)
   - Trained specifically on code (6 programming languages)
   - Best for code similarity and search
   - Model size: ~500MB
   ```bash
   pip install transformers torch
   ```

2. **Salesforce/codet5-base** (768 dimensions)
   - Code understanding and generation
   - Excellent for code summarization
   - Model size: ~850MB

3. **sentence-transformers/all-mpnet-base-v2** (768 dimensions)
   - General purpose but works well with code
   - Fastest inference time
   - Model size: ~420MB
   - **RECOMMENDED FOR STARTING**: Best balance of quality/speed

**ALTERNATIVE - Larger Models for Better Quality**:

4. **BAAI/bge-large-en-v1.5** (1024 dimensions)
   - State-of-the-art retrieval quality
   - Slower but very accurate
   - Model size: ~1.3GB

5. **intfloat/e5-large-v2** (1024 dimensions)
   - Excellent for technical documentation
   - Good code understanding
   - Model size: ~1.3GB

**LOCAL LLM Option for Summaries/Documentation**:
- **TheBloke/Mistral-7B-Instruct-v0.2-GGUF** (quantized)
  - Generate synthetic docstrings for undocumented code
  - Run locally via llama.cpp or Ollama
  - 4-bit quantized: ~4GB RAM
  ```bash
  # Using Ollama (easiest)
  curl -fsSL https://ollama.com/install.sh | sh
  ollama pull mistral
  ```

### 4.2 Complete Self-Hosted Stack

```
Embedding Model: sentence-transformers/all-mpnet-base-v2 (local)
Vector Database: ChromaDB (local) or Qdrant (self-hosted)
LLM for Summaries: Ollama + Mistral-7B (local)
Code Parsing: javalang + ast (Python built-in)
```

**Hardware Requirements**:
- Minimum: 16GB RAM, 50GB disk space
- Recommended: 32GB RAM, 100GB disk space, GPU optional (speeds up embedding)

### 4.2 Code Preprocessing for Embeddings

Before embedding, enhance code chunks:

```python
def prepare_chunk_for_embedding(chunk):
    """
    Enhance code chunk with natural language context
    """
    components = [
        f"File: {chunk['location']['file_path']}",
        f"Language: {chunk['language']}",
    ]
    
    if chunk['chunk_type'] == 'method':
        components.append(
            f"Method {chunk['hierarchy']['method']} "
            f"in class {chunk['hierarchy']['class']}"
        )
        if chunk['documentation']['has_docstring']:
            components.append(
                f"Description: {chunk['documentation']['docstring_summary']}"
            )
    
    components.append(f"Code:\n{chunk['content']}")
    
    return "\n\n".join(components)
```

### 4.3 Implementation with Open Source Models

```python
from sentence_transformers import SentenceTransformer
import chromadb
from chromadb.config import Settings

class OpenSourceCodeEmbedder:
    def __init__(self):
        # Load embedding model locally (downloads on first run)
        print("Loading embedding model...")
        self.model = SentenceTransformer('sentence-transformers/all-mpnet-base-v2')
        
        # Initialize local vector database
        self.client = chromadb.Client(Settings(
            chroma_db_impl="duckdb+parquet",
            persist_directory="./chroma_db"
        ))
        
        # Create collection for code chunks
        self.collection = self.client.get_or_create_collection(
            name="code_chunks",
            metadata={"description": "Code embeddings for 1M LOC app"}
        )
    
    def embed_chunks(self, chunks):
        """Generate embeddings for code chunks"""
        texts = [self.prepare_chunk_for_embedding(c) for c in chunks]
        
        # Batch embedding (much faster than one-by-one)
        embeddings = self.model.encode(
            texts, 
            batch_size=32,
            show_progress_bar=True,
            convert_to_numpy=True
        )
        
        return embeddings
    
    def store_chunks(self, chunks, embeddings):
        """Store in ChromaDB"""
        self.collection.add(
            embeddings=embeddings.tolist(),
            documents=[c['content'] for c in chunks],
            metadatas=[c['metadata'] for c in chunks],
            ids=[c['id'] for c in chunks]
        )
    
    def search(self, query, top_k=10, filters=None):
        """Search code using natural language query"""
        # Embed the query
        query_embedding = self.model.encode([query])[0]
        
        # Search in ChromaDB
        results = self.collection.query(
            query_embeddings=[query_embedding.tolist()],
            n_results=top_k,
            where=filters  # e.g., {"language": "java"}
        )
        
        return results
    
    def prepare_chunk_for_embedding(self, chunk):
        """Enhance code chunk with natural language context"""
        components = [
            f"File: {chunk['location']['file_path']}",
            f"Language: {chunk['language']}",
        ]
        
        if chunk['chunk_type'] == 'method':
            components.append(
                f"Method {chunk['hierarchy']['method']} "
                f"in class {chunk['hierarchy']['class']}"
            )
            if chunk['documentation']['has_docstring']:
                components.append(
                    f"Description: {chunk['documentation']['docstring_summary']}"
                )
        
        components.append(f"Code:\n{chunk['content']}")
        
        return "\n\n".join(components)

# Usage example
embedder = OpenSourceCodeEmbedder()

# Process your codebase
chunks = chunk_codebase("./src")  # Your chunking function
embeddings = embedder.embed_chunks(chunks)
embedder.store_chunks(chunks, embeddings)

# Query example
results = embedder.search(
    "How is user authentication implemented?",
    filters={"chunk_type": "method"}
)
```

### 4.4 Optional: Generate Synthetic Documentation with Local LLM

```python
import subprocess
import json

class LocalDocGenerator:
    def __init__(self):
        # Assumes Ollama is installed
        self.model = "mistral"
    
    def generate_docstring(self, code_chunk):
        """Generate docstring for undocumented code"""
        prompt = f"""Analyze this code and provide a brief 1-2 sentence summary of what it does:

{code_chunk}

Summary:"""
        
        # Call Ollama via subprocess
        result = subprocess.run(
            ["ollama", "run", self.model, prompt],
            capture_output=True,
            text=True,
            timeout=30
        )
        
        return result.stdout.strip()
    
    def enrich_chunks_with_docs(self, chunks):
        """Add generated docs to chunks missing them"""
        for chunk in chunks:
            if not chunk['documentation']['has_docstring']:
                print(f"Generating doc for {chunk['hierarchy']['method']}...")
                summary = self.generate_docstring(chunk['content'])
                chunk['documentation']['docstring_summary'] = summary
                chunk['documentation']['has_docstring'] = True
        
        return chunks

# Usage
doc_gen = LocalDocGenerator()
enriched_chunks = doc_gen.enrich_chunks_with_docs(chunks)
```

## 5. Special Considerations for Legacy Code

### 5.1 Handling Large Classes/Files

For classes > 1000 lines or files > 2000 lines:

1. **Split by logical sections**: Use comment headers, nested classes
2. **Create multiple overlapping chunks**: 200-line sliding window
3. **Generate connecting summaries**: Link chunks with "This is part X of Y"

### 5.2 Undocumented Code

For code without documentation:

1. **Generate synthetic docstrings**: Use Claude/GPT-4 to create descriptions
2. **Extract intent from names**: Use variable/method names as semantic signals
3. **Analyze call patterns**: Infer purpose from how code is used

### 5.3 Dead Code Detection

Track and potentially exclude:
- Methods with no callers
- Classes not instantiated
- Files not imported anywhere

## 6. Recommended Chunk Distribution

For your 1M LOC codebase, target distribution:

```
Method-level chunks:     ~40,000-60,000 (avg 20-25 LOC per chunk)
Class-level chunks:      ~8,000-12,000
File-level summaries:    ~3,000-5,000
Module-level summaries:  ~100-200
---
Total chunks:           ~51,000-77,000
```

## 7. Implementation Pseudocode

```python
class CodeChunker:
    def __init__(self, language):
        self.language = language
        self.parser = self._get_parser(language)
    
    def chunk_file(self, file_path):
        """Generate all chunk levels for a file"""
        chunks = []
        
        # Parse file
        ast = self.parser.parse(file_path)
        
        # Level 1: Method chunks
        for method in ast.get_methods():
            if self._should_chunk_method(method):
                chunks.append(self._create_method_chunk(method))
        
        # Level 2: Class chunks
        for cls in ast.get_classes():
            if self._should_chunk_class(cls):
                chunks.append(self._create_class_chunk(cls))
        
        # Level 3: File summary
        chunks.append(self._create_file_chunk(ast))
        
        return chunks
    
    def _create_method_chunk(self, method):
        return {
            'chunk_type': 'method',
            'content': self._get_method_with_context(method),
            'metadata': self._extract_method_metadata(method),
            'relationships': self._analyze_relationships(method)
        }
    
    def _get_method_with_context(self, method):
        """Include class context and docstring"""
        parts = []
        
        # Add class declaration
        parts.append(f"// From class {method.class_name}")
        parts.append(method.class_declaration)
        parts.append("")
        
        # Add method docstring
        if method.docstring:
            parts.append(method.docstring)
        
        # Add method code
        parts.append(method.signature)
        parts.append(method.body)
        
        return "\n".join(parts)
```

## 8. Next Steps

1. **Week 1**: Implement AST parsers for Java and Python
2. **Week 2**: Build chunking pipeline with method-level focus
3. **Week 3**: Add class and file-level chunking
4. **Week 4**: Generate embeddings and test retrieval quality
5. **Week 5**: Iterate based on query performance metrics

## 9. Success Metrics

- **Retrieval Accuracy**: % of queries that return relevant code in top-5 results
- **Coverage**: % of codebase successfully chunked and embedded
- **Query Latency**: < 500ms for semantic search
- **Chunk Quality**: Manual review of 100 random chunks for coherence

## 10. Complete Open Source Tools & Libraries Stack

### 10.1 Installation Script

```bash
#!/bin/bash

# Core dependencies
pip install sentence-transformers==2.2.2  # Embedding models
pip install chromadb==0.4.22              # Local vector database
pip install torch torchvision             # Required for transformers

# Code parsing
pip install javalang==0.13.0              # Java AST parsing
# Python ast is built-in, no install needed
pip install libcst==1.1.0                 # Advanced Python parsing

# Code analysis
pip install radon==6.0.1                  # Complexity metrics
pip install pygments==2.17.2              # Syntax highlighting

# Optional: Local LLM for documentation generation
# Install Ollama from https://ollama.com
curl -fsSL https://ollama.com/install.sh | sh
ollama pull mistral  # 7B parameter model, ~4GB

# Alternative vector databases (choose one)
# pip install qdrant-client  # Self-hosted option
# pip install faiss-cpu      # Facebook's vector search
```

### 10.2 Open Source Stack Summary

| Component | Tool | Size | Purpose |
|-----------|------|------|---------|
| **Embedding Model** | all-mpnet-base-v2 | 420MB | Convert code to vectors |
| **Vector Database** | ChromaDB | ~100MB | Store & search embeddings |
| **Java Parser** | javalang | ~5MB | Parse Java AST |
| **Python Parser** | ast (built-in) | 0MB | Parse Python AST |
| **LLM (optional)** | Mistral 7B via Ollama | 4GB | Generate documentation |
| **Code Metrics** | radon | ~2MB | Calculate complexity |

**Total Storage**: ~5-10GB (including models and databases)
**Total Cost**: $0 (completely free and open source)

### 10.3 Alternative Embedding Models Comparison

```python
# Performance comparison on code search tasks (relative):
models = {
    'all-mpnet-base-v2': {
        'dimensions': 768,
        'speed': 'Fast (100 chunks/sec)',
        'quality': 'Good (85%)',
        'model_size': '420MB',
        'best_for': 'General purpose, fastest start'
    },
    'microsoft/codebert-base': {
        'dimensions': 768,
        'speed': 'Fast (90 chunks/sec)',
        'quality': 'Very Good (88%)',
        'model_size': '500MB',
        'best_for': 'Code-specific tasks'
    },
    'BAAI/bge-large-en-v1.5': {
        'dimensions': 1024,
        'speed': 'Medium (40 chunks/sec)',
        'quality': 'Excellent (92%)',
        'model_size': '1.3GB',
        'best_for': 'Best accuracy, slower'
    }
}
```

### 10.4 Quick Start Commands

```bash
# 1. Install dependencies
pip install -r requirements.txt

# 2. Download embedding model (first run only)
python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('sentence-transformers/all-mpnet-base-v2')"

# 3. Optional: Setup local LLM for doc generation
ollama pull mistral

# 4. Run chunking pipeline
python chunk_codebase.py --input ./src --output ./chunks

# 5. Generate embeddings
python generate_embeddings.py --chunks ./chunks --output ./embeddings

# 6. Start search API
python search_api.py --embeddings ./embeddings --port 8000
```

### 10.5 Hardware Requirements (All Local)

**Minimum Configuration**:
- CPU: 4 cores
- RAM: 16GB
- Disk: 50GB free space
- GPU: Not required (but speeds up embedding by 3-5x)

**Recommended Configuration**:
- CPU: 8+ cores  
- RAM: 32GB
- Disk: 100GB SSD
- GPU: 6GB+ VRAM (optional, for faster processing)

**Expected Processing Time** (1M LOC):
- Chunking: 2-4 hours
- Embedding generation: 4-8 hours (CPU) or 1-2 hours (GPU)
- Total setup: ~1 day