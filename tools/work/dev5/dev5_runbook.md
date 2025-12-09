# dev5 Runbook — troubleshooting & quick checks

This runbook gives short, deterministic steps to triage issues with the Indexing Console SSE flow, the dev5 synthetic harness and CI smoke checks.

Common checks
-------------
1. Verify the synthetic SSE server starts locally:

```powershell
& .\.venv\Scripts\Activate.ps1
.
python tools/work/dev5/scripts/synthetic_indexing_server.py --port 9001 --job-id 123
```

Then open: http://127.0.0.1:9001/sse?jobId=123 and confirm streaming events appear.

2. Run the local Node validation (requires node installed):

```powershell
node tools/work/dev5/tests/check_synthetic_sse.js http://127.0.0.1:9001/sse?jobId=123
```

3. Run the CI smoke local script (PowerShell):

```powershell
.\tools\work\dev5\scripts\ci-smoke.ps1
```

If this fails, check: Python & Node on PATH, ports available (9001 default), and examine the server logs printed by the harness.

Debugging tips
--------------
- If the SSE client disconnects early you'll see a ConnectionResetError in Python logs. That's OK for local tests — ensure the clients received expected events before disconnecting.
- Use `curl -N http://127.0.0.1:9001/sse?jobId=123` to see streaming events raw.
- If CI fails, run the smoke script locally and attach stdout to the CI task logs for easier triage.

When to escalate
----------------
- If the server never starts: check python runtime (3.8+), file permissions, and port conflicts. Try `Get-Process -Id (Get-NetTCPConnection -LocalPort 9001).OwningProcess` to locate conflicts.
- If events are malformed: check `tools/work/dev5/tests/API_CONTRACTS.md` and compare actual payloads.
