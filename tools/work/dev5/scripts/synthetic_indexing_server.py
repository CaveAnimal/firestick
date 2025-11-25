#!/usr/bin/env python3
"""Small synthetic SSE server to emit indexing events for local dev and CI smoke tests.

Usage:
  python tools/work/dev5/scripts/synthetic_indexing_server.py --port 9001 --job-id 123

This server publishes a deterministic sequence of SSE events (object-start, object-progress, progress, object-end)
so clients can validate UI handling and end-to-end flows.
"""
import argparse
import http.server
import json
import time
from threading import Thread


class SSEHandler(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith("/sse"):
            self.send_response(200)
            self.send_header('Content-Type', 'text/event-stream')
            self.send_header('Cache-Control', 'no-cache')
            self.send_header('Connection', 'keep-alive')
            self.end_headers()

            job_id = int(self.get_query_param('jobId') or 999)

            events = self.build_event_sequence(job_id)

            for e in events:
                self.wfile.write(f"data: {json.dumps(e)}\n\n".encode('utf-8'))
                self.wfile.flush()
                time.sleep(e.get('delay', 0.2))

            # after done, keep connection open for a short time
            time.sleep(0.5)
        else:
            self.send_response(404)
            self.end_headers()

    def get_query_param(self, name):
        # very small parser
        from urllib.parse import urlparse, parse_qs
        q = urlparse(self.path).query
        params = parse_qs(q)
        return params.get(name, [None])[0]

    def build_event_sequence(self, job_id):
        # deterministic sample: 3 objects
        seq = []
        total_files = 3

        # aggregated progress helper
        def progress(percent, filesParsed):
            return {
                'event': 'progress',
                'jobId': job_id,
                'percent': percent,
                'filesDiscovered': total_files,
                'filesParsed': filesParsed,
                'chunksProduced': filesParsed * 2,
                'documentsIndexed': filesParsed,
                'embeddingsGenerated': filesParsed,
                'filesSkipped': 0
            }

        objects = [
            {'objectId': 'o-1', 'path': 'src/A.java'},
            {'objectId': 'o-2', 'path': 'src/B.java'},
            {'objectId': 'o-3', 'path': 'src/C.java'},
        ]

        filesParsed = 0
        for idx, o in enumerate(objects, start=1):
            # object-start
            seq.append({'event': 'object-start', 'jobId': job_id, 'objectId': o['objectId'], 'objectType': 'file', 'path': o['path'], 'ts': int(time.time() * 1000), 'delay': 0.5})

            # simulated progress dots
            for pct in (25, 50, 75):
                seq.append({'event': 'object-progress', 'jobId': job_id, 'objectId': o['objectId'], 'objectType': 'file', 'path': o['path'], 'objectWorkDone': pct, 'objectTotalWork': 100, 'ts': int(time.time() * 1000), 'delay': 0.2})

            # object-end
            filesParsed += 1
            seq.append({'event': 'object-end', 'jobId': job_id, 'objectId': o['objectId'], 'objectType': 'file', 'path': o['path'], 'ts': int(time.time() * 1000), 'elapsedMs': 120 + idx * 3, 'delay': 0.2})

            # aggregated progress after each end
            pct_total = int(filesParsed / total_files * 100)
            seq.append(progress(pct_total, filesParsed))

        # final progress
        seq.append(progress(100, total_files))
        return seq


def serve(host, port):
    server = http.server.ThreadingHTTPServer((host, port), SSEHandler)
    print(f"Synthetic SSE server listening on http://{host}:{port}/sse?jobId=<id>")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        server.server_close()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--host', default='127.0.0.1')
    parser.add_argument('--port', type=int, default=9001)
    parser.add_argument('--job-id', type=int, default=123)
    args = parser.parse_args()

    t = Thread(target=serve, args=(args.host, args.port), daemon=True)
    t.start()
    print('Press Ctrl+C to stop')
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print('\nShutting down')


if __name__ == '__main__':
    main()
