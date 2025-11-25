#!/usr/bin/env bash
PORT=${1:-9001}
JOB_ID=${2:-123}
echo "Starting synthetic SSE server on port $PORT (jobId=$JOB_ID)"
python3 tools/work/dev5/scripts/synthetic_indexing_server.py --port $PORT --job-id $JOB_ID
