# PyPI Artifact Allowlist / Mirror Request

Project: Firestick
Environment: Windows workstation behind Zscaler + internal Nexus PyPI proxy
Date: 2025-10-29

## Summary
We need access to additional Python packages (torch, transformers, joblib, and onnx) not currently mirrored on the internal Nexus proxy. These are foundational for ML embedding, model inference, and code intelligence features. Other required packages have been successfully installed from the proxy.

## Current Working Set (Installed via Nexus Proxy)
- annotated-types 0.7.0
- typing-extensions 4.12.2
- pydantic 2.9.2
- numpy 2.1.2
- faiss-cpu 1.12.0
- onnxruntime 1.23.2
- chromadb 1.2.1 (and its dependencies)

## Missing Packages
- transformers (needed >=4.41.0, ideally 4.44.2)
- torch (needed 2.4.0 CPU build acceptable; GPU not required initially)
- joblib==1.5.2 (required by scikit-learn pin)
- onnx==1.19.1 (required for ONNX export parity with runtime)

The absence of the above mirrors prevents us from installing these project pins that are already present in `requirements.txt`:
- optimum==2.0.0 and optimum-onnx==0.0.3 (depend on transformers>=4.29)
- scikit-learn==1.7.2 (depends on joblib>=1.2.0)
- sentence-transformers==5.1.1 (depends on transformers>=4.41.0)

Sentence-transformers also cannot install because it depends on transformers.

## Business Justification
These libraries enable:
1. Embedding generation for semantic code search.
2. Model inference pipelines for code analysis (PyTorch backend).
3. Integration with ONNX models for performance (already installed: onnxruntime; need onnx to export and align models).
4. Vector similarity and advanced retrieval (faiss-cpu, chromadb already working).
5. Scikit-learn job management and persisted models (joblib is the pinned dependency).

Without torch/transformers the ML feature set is blocked and development is limited to stubbed interfaces.

## Security Considerations
- We will pin exact versions in `requirements.txt`.
- Pip enforces SHA256 hash validation from index metadata.
- These are widely adopted, reducing supply-chain novelty risk.
- We can optionally provide wheel hashes (download externally, record SHA256) before internal deployment.

## Request Options
1. Mirror the packages into the existing Nexus proxy (preferred):
   - torch==2.4.0 (CPU)
   - transformers==4.44.2
   - joblib==1.5.2
   - onnx==1.19.1
   - sentence-transformers==5.1.2 (optional convenience once transformers mirrored)
2. Temporarily allow outbound GET to `https://files.pythonhosted.org` for the specific wheel paths (if mirroring delay expected).

## Validation Plan After Approval
1. Run `pip install torch==2.4.0 transformers==4.44.2 joblib==1.5.2 onnx==1.19.1` inside the venv.
2. Execute minimal import test:
   ```powershell
   python -c "import torch, transformers, joblib, onnx; print('torch', torch.__version__, 'transformers', transformers.__version__, 'joblib', joblib.__version__, 'onnx', onnx.__version__)"
   ```
3. Add versions to `requirements.txt` and freeze lock file.
4. Run existing code search prototype against embeddings to confirm functionality.

## Contact
Developer: [Your Name]
Email: [your.email@domain]
Reference Ticket: (to be assigned)

Please advise on expected timeline or any additional security review steps required.

Thank you.
