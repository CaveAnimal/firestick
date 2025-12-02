# Firestick - Dev6 Tasks (GPU Enablement)

**Version:** 1.0
**Focus:** GPU Acceleration for LLM and Embeddings
**Assigned To:** Entry Level Developer

---

## Task Summary

**Total Tasks:** 7
**Completed/Tested:** 0
**In Progress:** 0
**Blocked:** 0

## Task Management System

### Task Status Symbols
- `[ ]` Not Started
- `[-]` In Progress
- `[X]` Completed
- `[V]` Tested & Verified
- `[!]` Blocked
- `[>]` Deferred (include reason on next line)

## Execution Phase

- `[X]` **Task 1: Prepare Python Environment for GPU**
    - Verify NVIDIA drivers and CUDA Toolkit are installed on the machine.
    - Activate the Python virtual environment.
    - Uninstall the current CPU-only library: `pip uninstall llama-cpp-python -y`
    - Install the GPU-enabled version:
      ```powershell
      $env:CMAKE_ARGS = "-DGGML_CUDA=on"
      pip install llama-cpp-python --upgrade --force-reinstall --no-cache-dir
      ```
    - Verify installation: Run `python -c "import llama_cpp; print('GPU Enabled')"` (if no errors, proceed).

- `[X]` **Task 2: Update LLM Worker for GPU Offloading**
    - Open `llm_worker.py`.
    - Locate the `Llama` class instantiation inside the `try/except` block.
    - Update the constructor to include `n_gpu_layers=-1`.
      ```python
      # Before:
      # llm = Llama(model_path=MODEL_PATH, n_threads=2, n_ctx=worker_n_ctx)
      
      # After:
      llm = Llama(model_path=MODEL_PATH, n_threads=2, n_ctx=worker_n_ctx, n_gpu_layers=-1)
      ```

- `[X]` **Task 3: Verify Torch CUDA Support (Optional/Fallback)**
    - Ensure PyTorch is CUDA-ready for `llm_service.py`.
    - Run: `python -c "import torch; print(f'CUDA Available: {torch.cuda.is_available()}')"`
    - If `False`, reinstall PyTorch with CUDA support via the official command from pytorch.org.

- `[ ]` **Task 4: Add ONNX GPU Dependency to Java Project**
    - Open `pom.xml`.
    - Locate the `onnxruntime` dependency.
    - Add or replace with the GPU version:
      ```xml
      <dependency>
          <groupId>com.microsoft.onnxruntime</groupId>
          <artifactId>onnxruntime_gpu</artifactId>
          <version>1.16.3</version> <!-- Use matching version -->
      </dependency>
      ```
    - Run `mvn clean install -DskipTests` to download dependencies.

- `[ ]` **Task 5: Enable CUDA Provider in Java Code**
    - Locate `src/main/java/com/codetalker/firestick/service/EmbeddingService.java`.
    - Find where `OrtSession.SessionOptions` is instantiated.
    - Add the CUDA provider option:
      ```java
      OrtSession.SessionOptions options = new OrtSession.SessionOptions();
      try {
          options.addCUDA(0); // Attempt to use GPU 0
      } catch (Exception e) {
          // Fallback or log warning if GPU unavailable
          System.err.println("CUDA provider not available, falling back to CPU");
      }
      ```

- `[ ]` **Task 6: Verification Run**
    - Start the LLM service (`start_llm_service.bat` or similar).
    - Check logs (`logs/llm/llm-*.log`) for messages indicating "BLAS = 1" or "offloaded 33/33 layers to GPU".
    - Start the Java backend.
    - Check logs (`logs/backend/backend-*.log`) for "Embedding mode=ONNX" and ensure no errors regarding CUDA provider loading.

- `[ ]` **Task 7: Lessons Learned Update**
    - If any command failed during GPU enablement, add an entry to `tools/work/devLESSONS_LEARNED.md`.
    - Include the following details:
      - **Timestamp:** When the issue occurred.
      - **Task/Command:** What you were trying to do.
      - **What:** The plan or goal.
      - **Why:** The reason for the failure.
      - **Work Done:** Steps taken to fix the issue.
      - **Result/Prevent:** Outcome and how to prevent it in the future.
