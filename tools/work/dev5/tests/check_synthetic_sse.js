/*
  Lightweight Node script to connect to the synthetic SSE server and assert we receive expected events.

  Usage: node tools/work/dev5/tests/check_synthetic_sse.js http://127.0.0.1:9001/sse?jobId=123

  Exits 0 on success, non-zero on failure.
*/
const http = require('http');

const url = process.argv[2] || 'http://127.0.0.1:9001/sse?jobId=123';
const EVENT_TIMEOUT_MS = 10000; // overall timeout

function parseSSEChunk(chunk) {
  const text = chunk.toString('utf8');
  // SSE 'data: <json>\n\n' sequences; split by double newline
  return text.split('\n\n').map(s => s.trim()).filter(Boolean).map(s => s.replace(/^data:\s*/,''));
}

function main() {
  const start = Date.now();
  const parsedEvents = [];

  const req = http.get(url, (res) => {
    res.on('data', (chunk) => {
      const parts = parseSSEChunk(chunk);
      for(const p of parts){
        try {
          const evt = JSON.parse(p);
          parsedEvents.push(evt);
          // Quick success condition: received at least one object-end and final progress percent 100
          const hasObjectEnd = parsedEvents.some(e => e.event === 'object-end');
          const finalProgress100 = parsedEvents.some(e => e.event === 'progress' && e.percent === 100);
          if (hasObjectEnd && finalProgress100) {
            console.log('Received required events: OK');
            process.exit(0);
          }
        } catch (err) {
          // ignore parse errors while streaming
        }
      }
    });

    res.on('end', () => {
      console.error('Connection ended before expected events. Events received:', parsedEvents.length);
      process.exit(2);
    });
  });

  req.on('error', (err) => {
    console.error('Connection error', err.message);
    process.exit(3);
  });

  // global timeout
  setTimeout(() => {
    console.error('Timeout waiting for events. Received', parsedEvents.length);
    process.exit(4);
  }, EVENT_TIMEOUT_MS);
}

main();
