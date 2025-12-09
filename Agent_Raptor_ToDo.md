# Agent Raptor Todo List

This file contains a copy of the current managed todo list for the Agent Raptor session.

- Search for torch imports — completed
  - Search repository for 'import torch' and 'from torch import' usage.

- Search for pytorch requirements — completed
  - Search requirements.txt and other manifests for 'torch' or 'pytorch' dependency.

- Search for 'torch' strings in code — completed
  - Search repository for occurrences of 'torch' anywhere.

- Search for max_tokens settings — completed
  - Search repository for 'max_tokens', 'max_new_tokens', 'max_length' usages.

- Update settings in `llm_service_gguf.py` — completed
  - Set `N_CTX = 2048` and `MAX_TOKENS_DEFAULT = 512` and update endpoint-specific `max_tokens`.

- Add shared llm_config and update services — completed
  - Added `llm_config.py` (shared constants), updated `llm_service_gguf.py`, `llm_service.py`, `llm-service/llama.py`, and tests.

- App running - DON'T start tests or services — completed
  - The application is already running. Do not start tests or services during this session.
  - This todo was created per the user's request and saved to this file.  
  - If you need the earlier work (tests or service restarts), please explicitly request them.

---

Notes:
- You can change LLM tuning in `llm_config.py` using environment variables or edit the defaults.
- LLM tokens and context settings were centralized to make microservices consistent.
