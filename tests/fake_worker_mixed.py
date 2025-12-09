#!/usr/bin/env python3
import sys
import json
import time

# Simple fake worker that reads JSON lines from stdin and responds
# Demonstrates mixed stdout output (some non-JSON) and proper JSON responses

for line in sys.stdin:
    line=line.strip()
    if not line:
        continue
    try:
        req = json.loads(line)
    except Exception:
        # echo a stray line to stdout to simulate noisy output
        print("NOT_JSON_LINE: " + line)
        sys.stdout.flush()
        continue
    req_id = req.get('id')
    prompt = req.get('prompt','')
    # Write some non-JSON debug on stdout (simulating a buggy tool) -- manager should ignore
    print(f"DEBUG: processing id={req_id} prompt_len={len(prompt)}")
    sys.stdout.flush()
    # Also print a diagnostic to stderr (heartbeat-like; allowed)
    print(f"[fake_worker] heartbeat id={req_id}", file=sys.stderr, flush=True)
    # Simulate processing time
    work = req.get('work_delay', 0)
    try:
        work = float(work)
    except Exception:
        work = 0
    if work:
        time.sleep(work)
    # generate JSON result
    out = {'id': req_id, 'ok': True, 'result': f'result_for_{req_id}'}
    print(json.dumps(out), flush=True)

# exit when stdin closed
sys.exit(0)
