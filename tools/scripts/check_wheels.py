#!/usr/bin/env python3
import importlib
import sys
import traceback

pkgs = [
    'chromadb',
    'onnxruntime',
    'hnswlib',
    'faiss',
    'torch',
    'sentence_transformers'
]

failed = False
for p in pkgs:
    try:
        importlib.import_module(p)
        print(f"{p}: OK")
    except Exception as e:
        print(f"{p}: FAIL -> {e}")
        traceback.print_exc()
        failed = True

if failed:
    print('\nOne or more imports failed')
    sys.exit(2)
else:
    print('\nAll imports OK')
    sys.exit(0)

