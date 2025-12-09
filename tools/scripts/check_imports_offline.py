#!/usr/bin/env python3
"""
Verify that key packages import successfully with networking disabled.
- Blocks outbound socket connect() calls during the process
- Sets offline-friendly environment flags for Transformers/HF Hub
- Prints OK/FAIL lines and exits non-zero on failure
"""
import os
import sys
import socket
import importlib
from contextlib import contextmanager

# Offline environment flags (no telemetry / hub access)
os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
os.environ.setdefault("HF_HUB_OFFLINE", "1")
os.environ.setdefault("HF_HUB_DISABLE_TELEMETRY", "1")
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")


class NetworkBlockedError(RuntimeError):
    pass


class _NoConnectSocket(socket.socket):
    def connect(self, *args, **kwargs):  # type: ignore[override]
        raise NetworkBlockedError("Outbound network disabled for offline smoke test")


@contextmanager
def block_network():
    original_socket = socket.socket
    try:
        socket.socket = _NoConnectSocket  # type: ignore[assignment]
        yield
    finally:
        socket.socket = original_socket


def main() -> int:
    pkgs = [
        "transformers",
        "torch",
        "joblib",
        "onnx",
        "onnxruntime",
    ]
    failures = 0
    with block_network():
        for name in pkgs:
            try:
                importlib.import_module(name)
                print(f"{name}: OK")
            except Exception as e:  # noqa: BLE001 (explicit for CI printout)
                print(f"{name}: FAIL -> {e}")
                failures += 1
    return failures


if __name__ == "__main__":
    sys.exit(main())
