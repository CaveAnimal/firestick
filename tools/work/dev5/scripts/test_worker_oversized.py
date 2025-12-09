#!/usr/bin/env python3
import sys, subprocess, json, time

cmd = [sys.executable, 'llm_worker.py']
proc = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, bufsize=1)
req = {'id':'big1','prompt':'Short prompt','max_tokens':99999}
proc.stdin.write(json.dumps(req) + '\n')
proc.stdin.flush()
# read response
line = proc.stdout.readline().strip()
print('RESP:', line)
# also print any stderr lines
time.sleep(0.1)
while True:
    err = proc.stderr.readline()
    if not err:
        break
    print('[stderr]', err.strip())
proc.kill()
