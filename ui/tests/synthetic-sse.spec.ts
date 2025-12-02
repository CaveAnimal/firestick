import { test, expect } from '@playwright/test';
import { spawn, ChildProcessWithoutNullStreams } from 'child_process';
import path from 'path';

test('synthetic SSE acceptance: server emits object-end and final progress 100', async () => {
  // start synthetic SSE server
  const serverPath = path.resolve(process.cwd(), 'tools/work/dev5/scripts/synthetic_indexing_server.py');
  const py = process.env.PYTHON || 'python';
  const child: ChildProcessWithoutNullStreams = spawn(py, [serverPath, '--port', '9001', '--job-id', '321'], { stdio: 'pipe' });

  let started = false;
  child.stdout.on('data', (d) => {
    const s = d.toString();
    if (s.includes('Synthetic SSE server listening')) started = true;
    // forward logs for CI visibility
    console.log('[synth-server]', s.trim());
  });

  child.stderr.on('data', (d) => console.error('[synth-server-err]', d.toString()));

  // Wait for server start
  await new Promise<void>((resolve, reject) => {
    const t0 = Date.now();
    const check = () => {
      if (started) return resolve();
      if (Date.now() - t0 > 5000) return reject(new Error('server did not start'));
      setTimeout(check, 100);
    };
    check();
  });

  // Open a minimal page that listens for SSE and accumulate events
  const page = await test.newPage();
  await page.goto('about:blank');

  // set up an EventSource in page context and collect events
  await page.evaluate(() => {
    // @ts-ignore
    (window as any).__events = [];
    // @ts-ignore
    const es = new EventSource('http://127.0.0.1:9001/sse?jobId=321');
    es.addEventListener('message', (m) => {
      try {
        const data = JSON.parse((m as any).data);
        // @ts-ignore
        window.__events.push(data);
      } catch (e) {
        // ignore
      }
    });
    // store es to allow later close
    // @ts-ignore
    (window as any).__es = es;
  });

  // wait until events include object-end, object-progress and final progress 100
  const found = await page.waitForFunction(() => {
    // @ts-ignore
    const ev = (window as any).__events || [];
    const hasObjectEnd = ev.some((e: any) => e.event === 'object-end');
    const hasObjectProgress = ev.some((e: any) => e.event === 'object-progress');
    const finalProgress = ev.some((e: any) => e.event === 'progress' && e.percent === 100);
    return hasObjectEnd && hasObjectProgress && finalProgress;
  }, { timeout: 10000 });

  expect(found).toBeTruthy();

  // close eventsource and terminate server
  await page.evaluate(() => { try { (window as any).__es.close(); } catch(e) {} });
  child.kill('SIGTERM');
});
