# Firestick CodeLlama 7B LLM Service

A lightweight FastAPI microservice providing code explanation and analysis using CodeLlama 7B.

## Features

- **Code Summarization:** Generate 2-3 sentence explanations of Java code
- **Relationship Analysis:** Explain dependencies between classes
- **Documentation Generation:** Auto-generate Javadoc-style comments
- **Pattern Detection:** Identify design patterns and anti-patterns
- **Offline Operation:** No external API calls required
- **Graceful Degradation:** Java backend continues working if LLM service is down

## Quick Start

### Prerequisites
- Python 3.12+
- 6GB RAM minimum
- 4-5GB disk space for CodeLlama 7B model

### Installation

1. **Create virtual environment:**
   ```bash
   cd llm-service
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\Activate.ps1
   ```

2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Configure environment:**
   ```bash
   cp .env.example .env
   # Edit .env if needed (defaults work for most setups)
   ```

4. **Start the service:**
   ```bash
   python main.py
   ```

Service will start on `http://127.0.0.1:8001`

## API Endpoints

### GET /health
Health check endpoint
```json
{
  "status": "healthy",
  "model_loaded": true,
  "version": "1.0.0"
}
```

### POST /api/llm/summarize
Generate code explanation

**Request:**
```json
{
  "code": "public void processPayment(Order order, String cardToken) { ... }",
  "language": "java"
}
```

**Response:**
```json
{
  "summary": "This method processes a payment for an order by charging the customer's card.",
  "confidence": 0.95,
  "tokens_used": 47
}
```

### POST /api/llm/analyze-relationship
Explain class dependency

**Request:**
```json
{
  "from_class": "PaymentService",
  "to_class": "PaymentProcessor",
  "context": "PaymentService calls PaymentProcessor.charge()"
}
```

**Response:**
```json
{
  "explanation": "PaymentService depends on PaymentProcessor to handle card transactions.",
  "relationship_type": "dependency"
}
```

### POST /api/llm/generate-docs
Generate documentation

**Request:**
```json
{
  "code": "public void processPayment(Order order, String cardToken) { ... }",
  "format": "javadoc"
}
```

**Response:**
```json
{
  "documentation": "/**\n * Processes a payment for the given order using the provided card token.\n * @param order The order to process\n * @param cardToken The payment card token\n */",
  "format": "javadoc"
}
```

### POST /api/llm/detect-patterns
Identify code patterns

**Request:**
```json
{
  "code": "public class Singleton { private static Singleton instance; ... }"
}
```

**Response:**
```json
{
  "patterns": ["singleton", "lazy_initialization"],
  "issues": ["missing_null_check"]
}
```

## Configuration

Edit `.env` to customize:

| Variable | Default | Description |
|----------|---------|-------------|
| MODEL_NAME | meta-llama/Llama-2-7b-chat-hf | HuggingFace model identifier |
| MODEL_CACHE_DIR | ./models | Where to cache downloaded model |
| LLM_SERVICE_PORT | 8001 | Service port |
| DEVICE | cpu | Compute device (cpu or cuda) |
| MAX_TOKENS | 512 | Max tokens to generate |
| TIMEOUT_SECONDS | 30 | Request timeout |

## Performance

Typical latencies (CPU, CodeLlama 7B 4-bit):
- Summarization: 2-4 seconds
- Relationship analysis: 2-4 seconds
- Documentation: 3-5 seconds
- Pattern detection: 2-4 seconds

Memory usage:
- Model: 4-5GB (quantized)
- Runtime: 1-2GB
- Total: ~6GB

## Troubleshooting

### Model download hangs
The first run downloads CodeLlama 7B (~3.5GB). Use HF_HOME environment variable to point to fast storage.

### Out of memory
Ensure 6GB RAM available. Try reducing batch size or using quantized version.

### Service not responding from Java backend
Check CORS configuration in main.py; verify Java backend URL is in allow_origins.

## Testing

```bash
# Test health endpoint
curl http://127.0.0.1:8001/health

# Test summarization
curl -X POST http://127.0.0.1:8001/api/llm/summarize \
  -H "Content-Type: application/json" \
  -d '{"code":"public void test() { }", "language":"java"}'
```

## Integration with Java Backend

The Java Spring Boot backend calls this service via RestTemplate. Configuration in `application.properties`:

```properties
llm.service.url=http://127.0.0.1:8001
llm.service.timeout.seconds=30
llm.service.enabled=true
```

See `LLMServiceClient` in Java code for integration details.

## License

MIT License - See LICENSE file

## References

- [CodeLlama Documentation](https://github.com/meta-llama/codellama)
- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [Transformers Library](https://huggingface.co/docs/transformers/)
