# Firestick - Dev7 Goals (Performance & Stability)

**Version:** 1.0
**Focus:** Resolving GPU offloading issues and optimizing application performance.

## Primary Goals
1. **Resolve Python LLM Service CPU Fallback:** Ensure the `llama-cpp-python` library correctly offloads layers to the GPU to prevent slow inference speeds (~1.43 t/s) and high CPU usage.
2. **Optimize Worker Stability:** Prevent worker timeouts by adjusting timeout thresholds and resource allocation (threads/context) to match hardware capabilities.
3. **Validate Full Stack GPU Acceleration:** Confirm that both the Python LLM service and the Java Embedding service are utilizing the GPU concurrently without resource conflicts.
4. **Achieve Efficient Inference Speed:** Target an inference speed of > 20 tokens per second for the 7B model to ensure a responsive user experience.
