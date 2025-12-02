#!/usr/bin/env python3
"""Simple llm worker for process-isolated inference.
Receives JSON lines on stdin, each is a request: {"id": <unique>, "prompt": "...", "max_tokens": 128}
Writes JSON lines on stdout with the result: {"id": <id>, "ok": true, "result": "..."} or {"id": <id>, "ok": false, "error": "..."}
"""
import json, sys, os, logging
import torch

logging.basicConfig(level=logging.INFO, format='[llm_worker] %(asctime)s %(levelname)s %(message)s')
logger = logging.getLogger('llm_worker')

MODEL_PATH = os.getenv('MODEL_PATH', 'models/codellama-7b.Q4_K_M.gguf')

try:
    from llama_cpp import Llama
    from llm_config import N_CTX, MAX_TOKENS_DEFAULT
except Exception as e:
    logger.exception('llama_cpp import failed: %s', e)
    sys.exit(2)

try:
    # Optimize worker context for VRAM (RTX 3060 ~12GB)
    # If VRAM is tight, use 8192; otherwise, use up to 16384
    worker_n_ctx = max(128, min(N_CTX, 8192))
    # Set n_gpu_layers to 35 for better GPU offloading (instead of -1)
    llm = Llama(model_path=MODEL_PATH, n_threads=2, n_ctx=worker_n_ctx, n_gpu_layers=35)
    logger.info('Worker context configured: n_ctx=%s, n_gpu_layers=%s', worker_n_ctx, 35)
    logger.info('Model loaded in worker: %s', MODEL_PATH)
    if torch.cuda.is_available():
        logger.info(f"GPU is available: {torch.cuda.get_device_name(0)}")
    else:
        logger.info("GPU is not available, using CPU.")
    # Heartbeat thread so long-running tests show activity on stdout/stderr
    import threading as _threading
    def _heartbeat():
        try:
            while True:
                # keep stdout JSON-only for protocol; emit heartbeat to stderr
                print(f"[llm_worker] heartbeat: model={os.path.basename(MODEL_PATH)} n_ctx={worker_n_ctx}", file=sys.stderr, flush=True)
                logger.info('heartbeat')
                _threading.Event().wait(60)
        except Exception:
            pass
    _threading.Thread(target=_heartbeat, daemon=True).start()
except Exception:
    logger.exception('Failed to load model in worker')
    sys.exit(3)

# Line-buffer stdout
sys.stdout.reconfigure(line_buffering=True)

for line in sys.stdin:
    line = line.strip()
    if not line:
        continue
    try:
        req = json.loads(line)
        req_id = req.get('id')
        prompt = req.get('prompt', '')
        max_tokens = int(req.get('max_tokens', MAX_TOKENS_DEFAULT))
        # enforce safe upper bound relative to worker's context window
        if max_tokens >= worker_n_ctx:
            # prefer to return structured error than allow llama-cpp to raise
            out = {'id': req_id, 'ok': False, 'error': f'requested max_tokens ({max_tokens}) >= worker context ({worker_n_ctx})'}
            sys.stdout.write(json.dumps(out) + '\n')
            sys.stdout.flush()
            continue
        # simple inference
        try:
            resp = llm(prompt, max_tokens=max_tokens, temperature=req.get('temperature', 0.7), stop=req.get('stop', None))
            text = ''
            if isinstance(resp, dict) and 'choices' in resp and isinstance(resp['choices'], list) and len(resp['choices'])>0:
                text = resp['choices'][0].get('text', '')
            else:
                text = str(resp)
            out = {'id': req_id, 'ok': True, 'result': text}
        except Exception as e:
            logger.exception('inference error')
            out = {'id': req_id, 'ok': False, 'error': str(e)}
    except Exception as e:
        out = {'id': None, 'ok': False, 'error': f'bad_request: {e}'}
    # write JSON line
    sys.stdout.write(json.dumps(out) + '\n')
    sys.stdout.flush()
