#!/usr/bin/env python3
import sys, subprocess, json, time
cmd = [sys.executable, 'llm_worker.py']
proc = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
req = {'id':'test1','prompt':'Define recursion in one sentence.','max_tokens':20}
proc.stdin.write(json.dumps(req) + '\n')
proc.stdin.flush()
# read response
line = proc.stdout.readline().strip()
print('RESP:', line)
proc.kill()
