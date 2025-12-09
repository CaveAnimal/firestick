// Minimal app.js to handle search, indexing, and logs without frameworks

function doSearch(q) {
  const out = document.getElementById('search-results');
  out.innerHTML = 'Searching...';
  fetch(`/api/search?q=${encodeURIComponent(q)}`)
    .then(r => r.json())
    .then(data => {
      out.innerHTML = '';
      if (!data || data.length === 0) {
        out.innerHTML = '<div class="muted">No results.</div>';
        return;
      }
      for (const item of data) {
        const div = document.createElement('div');
        div.className = 'card';
        div.style.margin = '8px 0';
        div.innerHTML = `<strong>${item.title || item.id || 'result'}</strong><div class="muted">${item.snippet || ''}</div>`;
        out.appendChild(div);
      }
    })
    .catch(err => {
      out.innerHTML = `<div class="muted">Search error: ${err.message}</div>`;
    });
}

// minimal helper to post and update status
function postJson(url, body) {
  return fetch(url, {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body)})
    .then(r => r.json());
}

function fetchLogs(path, elId) {
  fetch(path).then(r => r.text()).then(t => document.getElementById(elId).textContent = t);
}

// Indexing helpers
async function fetchApps() {
  try {
    const r = await fetch('/api/indexing/apps');
    if (!r.ok) return [];
    const d = await r.json();
    return d?.apps || [];
  } catch(e) { return []; }
}

async function startIndexing(app) {
  const url = `/api/indexing/run${app ? `?app=${encodeURIComponent(app)}` : ''}`;
  return fetch(url).then(r => r.json());
}

async function fetchLatestJob(app) {
  const url = app ? `/api/indexing/jobs/latest?app=${encodeURIComponent(app)}` : '/api/indexing/jobs/latest';
  try {
    const r = await fetch(url);
    if (!r.ok) return null;
    return await r.json();
  } catch(e) { return null; }
}

// wire-up for indexing page (safe if elements not present)
function initIndexingPage() {
  const appSelect = document.getElementById('app-select');
  const startBtn = document.getElementById('start-btn');
  const stopBtn = document.getElementById('stop-btn');
  const statusEl = document.getElementById('index-status');
  const logEl = document.getElementById('index-log');
  if (!appSelect) return;

  fetchApps().then(apps => {
    for (const a of apps) {
      const opt = document.createElement('option'); opt.value = a; opt.textContent = a; appSelect.appendChild(opt);
    }
  });

  startBtn.addEventListener('click', async () => {
    const app = appSelect.value || null;
    statusEl.textContent = 'starting...';
    const res = await startIndexing(app);
    statusEl.textContent = res?.status || JSON.stringify(res);
  });

  // Poll status periodically
  setInterval(async () => {
    const app = appSelect.value || null;
    const job = await fetchLatestJob(app);
    if (job) {
      statusEl.textContent = job?.status || 'unknown';
      logEl.textContent = JSON.stringify(job, null, 2);
    }
  }, 2000);
}

// Run init on pages
try { window.addEventListener('DOMContentLoaded', initIndexingPage); } catch(e) {}

// no more than this for now - add features later
