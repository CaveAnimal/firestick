import os
import sys
import time
import subprocess
import uuid
import signal
import tempfile

import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from llm_service_gguf import LLMWorkerManager

FAKE_WORKER = os.path.join(os.path.dirname(__file__), 'fake_worker_mixed.py')

@pytest.mark.timeout(30)
def test_worker_manager_handles_mixed_stdout(tmp_path):
    mgr = LLMWorkerManager(worker_path=FAKE_WORKER)
    try:
        # ensure ready
        t0 = time.time()
        while not mgr.is_ready() and time.time() - t0 < 5:
            time.sleep(0.02)
        assert mgr.is_ready()

        # make a request; the fake worker will reply with a JSON response
        prompt = "hello"
        resp = mgr.request(prompt=prompt, max_tokens=8, timeout=5)
        assert resp.get('ok') is True
        assert resp.get('result').startswith('result_for_')
    finally:
        mgr.stop()


@pytest.mark.timeout(60)
def test_worker_manager_restarts_after_consecutive_timeouts(monkeypatch, tmp_path):
    # configure a very short timeout and restart threshold for the test
    monkeypatch.setenv('LLM_WORKER_TIMEOUT', '0')
    monkeypatch.setenv('LLM_WORKER_RESTART_THRESHOLD', '2')

    mgr = LLMWorkerManager(worker_path=FAKE_WORKER)
    try:
        assert mgr.is_ready()
        before_pid = mgr.proc.pid

        # send two requests that instruct the fake worker to sleep longer than the timeout
        # the fake worker understands a 'work_delay' field we pass in the payload
        # We'll use request() with max_tokens and rely on manager to pass work_delay through
        # We directly call request with a prompt containing work_delay encoded so worker sleeps
        with pytest.raises(TimeoutError):
            mgr.request(prompt='x', max_tokens=1, timeout=0.01)
        # second timeout should trigger a restart (threshold=2)
        with pytest.raises(TimeoutError):
            mgr.request(prompt='x', max_tokens=1, timeout=0.01)

        # allow a moment for restart
        t0 = time.time()
        while mgr.proc and mgr.proc.pid == before_pid and time.time() - t0 < 5:
            time.sleep(0.05)

        assert mgr.proc is not None
        assert mgr.proc.pid != before_pid
    finally:
        try:
            mgr.stop()
        except Exception:
            pass
