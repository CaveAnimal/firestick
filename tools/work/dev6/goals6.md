# Dev6 Goals: Enable GPU Acceleration

**Objective:** Enable GPU acceleration across the entire application stack (LLM inference and Embeddings generation) to utilize the available NVIDIA GeForce RTX 3060.

## 1. Python LLM Service (GGUF / llama-cpp)
The primary LLM service uses `llama-cpp-python` via `llm_worker.py`. Currently, it runs in CPU-only mode.

*   **Location:** `llm_worker.py`
*   **Goal:** Update the `Llama` class instantiation to offload model layers to the GPU.
    *   **Detail:** Set `n_gpu_layers=-1` (all layers) in the constructor.
*   **Location:** Python Environment (`.venv`)
    *   **Goal:** Reinstall `llama-cpp-python` with CUDA support enabled.
    *   **Detail:** Requires uninstalling the CPU version and installing with `CMAKE_ARGS="-DGGML_CUDA=on"`.

## 2. Python LLM Service (Transformers / Fallback)
The `llm_service.py` script uses Hugging Face `transformers` as a fallback or alternative mode.

*   **Location:** `llm_service.py`
*   **Goal:** Ensure the `initialize_model` function correctly detects and uses the GPU.
    *   **Detail:** Verify `device = "cuda"` logic and ensure `torch` is installed with CUDA support in the environment.

## 3. Java Backend (ONNX Embeddings)
The Java backend uses ONNX Runtime for generating code embeddings. Currently, it likely uses the default CPU provider.

*   **Location:** `pom.xml`
    *   **Goal:** Add the ONNX Runtime GPU dependency.
    *   **Detail:** Replace or add `com.microsoft.onnxruntime:onnxruntime_gpu` alongside the existing `onnxruntime` dependency.
*   **Location:** `src/main/java/com/codetalker/firestick/service/EmbeddingService.java` (or similar class handling ONNX)
    *   **Goal:** Configure the ONNX session to use the CUDA execution provider.
    *   **Detail:** When creating `OrtSession.SessionOptions`, add the CUDA provider (e.g., `options.addCUDA(0)`).

## 4. System / Runtime
*   **Location:** OS / Environment Variables
    *   **Goal:** Ensure CUDA Toolkit and cuDNN libraries are accessible to both Python and Java processes.
    *   **Detail:** Verify `PATH` includes CUDA `bin` and `lib` directories.
