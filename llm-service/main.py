"""
FastAPI Application for CodeLlama LLM Service
Provides REST endpoints for code explanation, pattern detection, and documentation
"""

import logging
import os
from contextlib import asynccontextmanager
from typing import Optional

from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.responses import HTMLResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import uvicorn

from llama import CodeLlamaProcessor

# Configure logging
logging.basicConfig(
    level=os.getenv("LOG_LEVEL", "INFO"),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Global processor instance
processor: Optional[CodeLlamaProcessor] = None


# ============================================================================
# Request/Response Models
# ============================================================================

class SummarizeRequest(BaseModel):
    code: str = Field(..., description="Code snippet to explain")
    language: str = Field(default="java", description="Programming language")


class SummarizeResponse(BaseModel):
    summary: str = Field(..., description="Generated explanation")
    confidence: float = Field(default=0.95, description="Confidence score")
    tokens_used: int = Field(default=0, description="Tokens used in generation")


class RelationshipRequest(BaseModel):
    from_class: str = Field(..., description="Source class name")
    to_class: str = Field(..., description="Target class name")
    context: str = Field(..., description="Code context showing relationship")


class RelationshipResponse(BaseModel):
    explanation: str = Field(..., description="Relationship explanation")
    relationship_type: str = Field(default="dependency", description="Type of relationship")


class DocsRequest(BaseModel):
    code: str = Field(..., description="Code snippet to document")
    format: str = Field(default="javadoc", description="Documentation format")


class DocsResponse(BaseModel):
    documentation: str = Field(..., description="Generated documentation")
    format: str = Field(default="javadoc", description="Documentation format")


class PatternsRequest(BaseModel):
    code: str = Field(..., description="Code snippet to analyze")


class PatternsResponse(BaseModel):
    patterns: list[str] = Field(default=[], description="Detected patterns")
    issues: list[str] = Field(default=[], description="Detected anti-patterns or issues")


class HealthResponse(BaseModel):
    status: str = Field(..., description="Service status")
    model_loaded: bool = Field(default=False, description="Whether model is loaded")
    version: str = Field(default="1.0.0", description="Service version")


# ============================================================================
# Lifecycle Events
# ============================================================================

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Startup and shutdown event handlers"""
    # Startup
    logger.info("Starting CodeLlama LLM Service")
    global processor
    processor = CodeLlamaProcessor(
        model_name=os.getenv("MODEL_NAME", "meta-llama/Llama-2-7b-chat-hf"),
        cache_dir=os.getenv("MODEL_CACHE_DIR", "./models"),
        device=os.getenv("DEVICE", "cpu")
    )
    if not processor.load():
        logger.error("Failed to load model - service may not function properly")
    
    yield
    
    # Shutdown
    logger.info("Shutting down CodeLlama LLM Service")
    if processor:
        processor.unload()


# ============================================================================
# FastAPI Application
# ============================================================================

app = FastAPI(
    title="Firestick LLM Service",
    description="CodeLlama 7B for code explanation and analysis",
    version="1.0.0",
    lifespan=lifespan
)

# CORS Configuration - Allow Java backend on port 8080
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080", "http://127.0.0.1:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ============================================================================
# Health & Status Endpoints
# ============================================================================

@app.get("/health", response_model=HealthResponse)
async def health() -> HealthResponse:
    """Health check endpoint"""
    return HealthResponse(
        status="healthy" if processor and processor.model else "degraded",
        model_loaded=processor is not None and processor.model is not None,
        version="1.0.0"
    )


@app.get("/status", response_model=dict)
async def status() -> dict:
    """Detailed status endpoint"""
    return {
        "service": "CodeLlama LLM",
        "status": "running",
        "model_loaded": processor is not None and processor.model is not None,
        "device": processor.device if processor else "unknown",
        "version": "1.0.0"
    }


# ============================================================================
# Code Explanation Endpoints
# ============================================================================

@app.post("/api/llm/summarize", response_model=SummarizeResponse)
async def summarize_code(request: SummarizeRequest) -> SummarizeResponse:
    """Generate brief explanation of code snippet"""
    if not processor or not processor.model:
        raise HTTPException(status_code=503, detail="LLM service not ready")
    
    if not request.code or len(request.code.strip()) == 0:
        raise HTTPException(status_code=400, detail="Code snippet cannot be empty")
    
    if len(request.code) > 10000:
        raise HTTPException(status_code=400, detail="Code snippet too large (max 10000 chars)")
    
    try:
        summary = processor.generate_explanation(request.code, max_tokens=256)
        return SummarizeResponse(
            summary=summary or "Unable to generate explanation",
            confidence=0.95 if summary else 0.0,
            tokens_used=len(summary.split()) if summary else 0
        )
    except Exception as e:
        logger.error(f"Error in summarize: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/llm/analyze-relationship", response_model=RelationshipResponse)
async def analyze_relationship(request: RelationshipRequest) -> RelationshipResponse:
    """Explain relationship between two classes"""
    if not processor or not processor.model:
        raise HTTPException(status_code=503, detail="LLM service not ready")
    
    if not request.from_class or not request.to_class or not request.context:
        raise HTTPException(status_code=400, detail="Missing required fields")
    
    try:
        explanation = processor.analyze_relationship(
            request.from_class, request.to_class, request.context
        )
        return RelationshipResponse(
            explanation=explanation or "Unable to analyze relationship",
            relationship_type="dependency"
        )
    except Exception as e:
        logger.error(f"Error in analyze_relationship: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/llm/generate-docs", response_model=DocsResponse)
async def generate_docs(request: DocsRequest) -> DocsResponse:
    """Generate documentation for code"""
    if not processor or not processor.model:
        raise HTTPException(status_code=503, detail="LLM service not ready")
    
    if not request.code or len(request.code.strip()) == 0:
        raise HTTPException(status_code=400, detail="Code snippet cannot be empty")
    
    try:
        docs = processor.generate_documentation(request.code)
        return DocsResponse(
            documentation=docs or "Unable to generate documentation",
            format=request.format
        )
    except Exception as e:
        logger.error(f"Error in generate_docs: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/llm/detect-patterns", response_model=PatternsResponse)
async def detect_patterns(request: PatternsRequest) -> PatternsResponse:
    """Detect code patterns and anti-patterns"""
    if not processor or not processor.model:
        raise HTTPException(status_code=503, detail="LLM service not ready")
    
    if not request.code or len(request.code.strip()) == 0:
        raise HTTPException(status_code=400, detail="Code snippet cannot be empty")
    
    try:
        patterns = processor.detect_patterns(request.code)
        return PatternsResponse(
            patterns=[p for p in patterns if p and not p.startswith("issue")],
            issues=[p for p in patterns if p.startswith("issue")]
        )
    except Exception as e:
        logger.error(f"Error in detect_patterns: {e}")
        raise HTTPException(status_code=500, detail=str(e))


# ============================================================================
# Root Endpoint
# ============================================================================

@app.get("/", response_class=HTMLResponse)
async def root() -> HTMLResponse:
        """Root endpoint with small human-friendly HTML page linking to docs and health"""
        content = """
        <!doctype html>
        <html lang="en">
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Firestick LLM Service</title>
                <style>body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial;max-width:900px;margin:32px auto;padding:8px;color:#222}a{color:#2b6cb0;text-decoration:none}</style>
            </head>
            <body>
                <h1>Firestick LLM Service</h1>
                <p>CodeLlama microservice used for explanations, pattern detection and docs generation.</p>
                <ul>
                    <li><a href="/docs">OpenAPI docs</a> (interactive)</li>
                    <li><a href="/health">Health endpoint</a></li>
                    <li><a href="/api/llm/summarize">Summarize (POST /api/llm/summarize)</a></li>
                </ul>
                <h2>Quick cURL examples</h2>
                <pre><code>curl -X GET http://127.0.0.1:8001/health
curl -X POST http://127.0.0.1:8001/api/llm/summarize -H 'Content-Type: application/json' -d '{"code":"int x=5;"}'
</code></pre>
                <hr/>
                <footer><small>Version: 1.0.0</small></footer>
            </body>
        </html>
        """
        return HTMLResponse(content=content, status_code=200)


# ============================================================================
# Main Entry Point
# ============================================================================

if __name__ == "__main__":
    port = int(os.getenv("LLM_SERVICE_PORT", "8001"))
    host = os.getenv("LLM_SERVICE_HOST", "127.0.0.1")
    
    logger.info(f"Starting server on {host}:{port}")
    uvicorn.run(app, host=host, port=port)
