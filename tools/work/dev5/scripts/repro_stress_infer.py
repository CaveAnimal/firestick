#!/usr/bin/env python3
"""Stress test script that loads the GGUF model and runs many inferences concurrently to try to reproduce ggml assert.
Usage: python repro_stress_infer.py [threads] [iterations]
"""
import sys
import time
from concurrent.futures import ThreadPoolExecutor

threads = int(sys.argv[1]) if len(sys.argv) > 1 else 8
iterations = int(sys.argv[2]) if len(sys.argv) > 2 else 50

print(f"Starting reproducer with {threads} threads x {iterations} iterations")

from llama_cpp import Llama

model_path = 'models/codellama-7b.Q4_K_M.gguf'
print('Loading model...')
llm = Llama(model_path=model_path, n_threads=4, n_ctx=512)
print('Model loaded')

PROMPT = 'Explain in one sentence what recursion is.'

def call_once(i):
    try:
        r = llm(PROMPT, max_tokens=32)
        return True
    except Exception as e:
        print('EXCEPTION', type(e), e)
        raise

start = time.time()
with ThreadPoolExecutor(max_workers=threads) as ex:
    futures = []
    for it in range(iterations):
        for t in range(threads):
            futures.append(ex.submit(call_once, (it * threads) + t))

    # wait
    ok = 0
    total = len(futures)
    for f in futures:
        try:
            res = f.result()
            if res:
                ok += 1
        except Exception as e:
            print('ERROR in future:', e)

elapsed = time.time() - start
print(f"Done: {ok}/{total} success in {elapsed:.1f}s")