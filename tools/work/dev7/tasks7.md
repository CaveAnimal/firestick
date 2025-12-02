# Firestick - Dev7 Tasks (Performance Optimization)

**Version:** 1.0
**Focus:** GPU Debugging and Performance Tuning
**Assigned To:** Entry Level Developer

---

## Task Summary

**Total Tasks:** 5
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
    - Open `llm_service_gguf.py`.
    - Locate the `WORKER_TIMEOUT` constant or the `request` method call.
    - Increase the timeout (currently causing fallbacks) to `120` seconds or more to accommodate initial model loading and large context processing.

    - The logs indicate layers falling back to CPU (`CPU_REPACK`).
    - Uninstall current version: `pip uninstall llama-cpp-python -y`
    - Reinstall with verbose output to confirm CUDA compilation:
      ```powershell
      $env:CMAKE_ARGS = "-DGGML_CUDA=on -DCMAKE_BUILD_TYPE=Release"
      pip install llama-cpp-python --upgrade --force-reinstall --no-cache-dir --verbose
      ```

 [X] **Task 1: Increase Worker Timeout**
        - Open `llm_service_gguf.py`.
        - Locate the `WORKER_TIMEOUT` constant or the `request` method call.
        - Increase the timeout (currently causing fallbacks) to `120` seconds or more to accommodate initial model loading and large context processing.

 [X] **Task 2: Debug and Reinstall llama-cpp-python**
        - The logs indicate layers falling back to CPU (`CPU_REPACK`).
        - Uninstall current version: `pip uninstall llama-cpp-python -y`
        - Reinstall with verbose output to confirm CUDA compilation:
            ```powershell
            $env:CMAKE_ARGS = "-DGGML_CUDA=on -DCMAKE_BUILD_TYPE=Release"
            pip install llama-cpp-python --upgrade --force-reinstall --no-cache-dir --verbose
            ```
    - Verify that the installation log explicitly mentions compiling CUDA kernels.
    - Open `llm_worker.py`.
    - Check `n_ctx` (Context Window). The logs show `n_ctx=16384` but `n_ctx_train=32768`. Ensure this fits in VRAM (approx 12GB for 3060).
    - If VRAM is tight, reduce `worker_n_ctx` to `8192` or `4096`.
    - Verify `n_gpu_layers=-1` is behaving correctly. If not, try setting a fixed number (e.g., `35`).

 [X] **Task 3: Optimize Worker Configuration**
     - Open `llm_worker.py`.
     - Check `n_ctx` (Context Window). The logs show `n_ctx=16384` but `n_ctx_train=32768`. Ensure this fits in VRAM (approx 12GB for 3060).
     - If VRAM is tight, reduce `worker_n_ctx` to `8192` or `4096`.
     - Verify `n_gpu_layers=-1` is behaving correctly. If not, try setting a fixed number (e.g., `35`).

- `[ ]` **Task 4: Verify Java ONNX GPU Usage**
    - Review `logs/backend/backend-*.log` (start the backend if not running).
    - Confirm "CUDA provider added successfully" appears in the logs.
    - If errors appear, verify `onnxruntime_gpu` version compatibility with the installed CUDA Toolkit (13.0) and cuDNN.

- `[ ]` **Task 5: Performance Benchmark**
    - Start the LLM service: `start_llm_service.bat`.
    - Trigger a summarization request (e.g., via the UI or a curl command).
    - Check logs for "eval time" and calculate tokens per second.
    - **Goal:** > 20 tokens/second.
