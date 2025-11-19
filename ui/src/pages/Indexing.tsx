import { useEffect, useRef, useState, type ChangeEvent } from 'react'
import { getLatestJob, startIndexing, IndexingJob, getRecentJobs, browseDirectory, type DirectoryListing } from '../api'
import ProgressBar from '../shared/ProgressBar'
import Toast from '../shared/Toast'

export default function IndexingPage() {
  const [job, setJob] = useState<IndexingJob | null>(null)
  const [recent, setRecent] = useState<IndexingJob[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | undefined>()
  const [toast, setToast] = useState<string | undefined>()
  const [sseConnected, setSseConnected] = useState(false)
  const [lastEventAt, setLastEventAt] = useState<number | null>(null)
  // Configuration options
  const [root, setRoot] = useState<string>(localStorage.getItem('idx.root') || '')
  const [appName, setAppName] = useState<string>(localStorage.getItem('idx.appName') || '')
  const [excludeDirs, setExcludeDirs] = useState<string>(localStorage.getItem('idx.excludeDirs') || '')
  const [excludeGlobs, setExcludeGlobs] = useState<string>(localStorage.getItem('idx.excludeGlobs') || '')
  // Directory browser state
  const [showBrowser, setShowBrowser] = useState(false)
  const [browserLoading, setBrowserLoading] = useState(false)
  const [browserError, setBrowserError] = useState<string | undefined>()
  const [currentBrowsePath, setCurrentBrowsePath] = useState<string | undefined>()
  const [browserListing, setBrowserListing] = useState<DirectoryListing | undefined>()
  const pollRef = useRef<number | null>(null)

  async function refreshLatest() {
    try {
      const latest = await getLatestJob()
      setJob(latest)
    } catch (e: any) {
      setError(e?.message ?? 'Failed to load latest job')
    }
  }

  async function refreshRecent() {
    try {
      const list = await getRecentJobs(10)
      setRecent(list)
    } catch (e: any) {
      // non-fatal
    }
  }

  const sseRef = useRef<EventSource | null>(null)
  const sseTriedRef = useRef(false)
  const reconnectRef = useRef<number | null>(null)
  const backoffMsRef = useRef(1000)
  const maxBackoffMs = 15000

  function persistConfig() {
    localStorage.setItem('idx.root', root)
    localStorage.setItem('idx.appName', appName)
    localStorage.setItem('idx.excludeDirs', excludeDirs)
    localStorage.setItem('idx.excludeGlobs', excludeGlobs)
  }

  // Derive app name from folder name (matches backend sanitization)
  function deriveAppNameFromPath(path: string): string {
    if (!path || !path.trim()) return 'default'
    const parts = path.replace(/\\/g, '/').split('/')
    let folderName = ''
    for (let i = parts.length - 1; i >= 0; i--) {
      if (parts[i].trim()) {
        folderName = parts[i]
        break
      }
    }
    if (!folderName.trim()) return 'default'
    const sanitized = folderName
      .toLowerCase()
      .replace(/[^a-z0-9_]/g, '_')
      .replace(/_+/g, '_')
      .replace(/^_+|_+$/g, '')
    return sanitized || 'default'
  }

  async function openBrowser() {
    setShowBrowser(true)
    setBrowserError(undefined)
    setBrowserLoading(true)
    try {
      const listing = await browseDirectory(currentBrowsePath || root || undefined)
      setBrowserListing(listing)
      setCurrentBrowsePath(listing.currentPath)
    } catch (e: any) {
      setBrowserError(e?.message ?? 'Failed to browse directory')
    } finally {
      setBrowserLoading(false)
    }
  }

  async function browsePath(path: string) {
    setBrowserLoading(true)
    setBrowserError(undefined)
    try {
      const listing = await browseDirectory(path)
      setBrowserListing(listing)
      setCurrentBrowsePath(listing.currentPath)
    } catch (e: any) {
      setBrowserError(e?.message ?? 'Failed to browse directory')
    } finally {
      setBrowserLoading(false)
    }
  }

  function selectPath(path: string) {
    setRoot(path)
    setShowBrowser(false)
  }

  async function triggerIndexing() {
    setLoading(true)
    setError(undefined)
    try {
      // Parse CSV inputs into arrays
  const dirs = excludeDirs.split(',').map((s: string) => s.trim()).filter(Boolean)
  const globs = excludeGlobs.split(',').map((s: string) => s.trim()).filter(Boolean)
  const finalAppName = appName.trim() || deriveAppNameFromPath(root)
      const res = await startIndexing({
        root: root.trim() || undefined,
        appName: finalAppName,
        excludeDirs: dirs.length ? dirs : undefined,
        excludeGlobs: globs.length ? globs : undefined,
      })
      persistConfig()
      setToast(`Indexing started${res?.jobId ? ` (job ${res.jobId})` : ''}`)
      // start polling
      if (pollRef.current) window.clearInterval(pollRef.current)
      // prefer SSE if available
      try {
        const job = await getLatestJob()
        if (job?.id) {
          startSse(job.id)
        } else {
          pollRef.current = window.setInterval(refreshLatest, 1500)
        }
      } catch {
        pollRef.current = window.setInterval(refreshLatest, 1500)
      }
      await refreshLatest()
    } catch (e: any) {
      setError(e?.message ?? 'Failed to start indexing')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    refreshLatest()
    refreshRecent()
    return () => {
      if (pollRef.current) window.clearInterval(pollRef.current)
      if (sseRef.current) { sseRef.current.close(); sseRef.current = null }
    }
  }, [])

  // Attach SSE automatically when we detect an active job without 100% progress
  useEffect(() => {
    if (!job?.id) return
    if (typeof job.progress === 'number' && job.progress >= 100) {
      if (sseRef.current) { sseRef.current.close(); sseRef.current = null }
      return
    }
    if (!sseRef.current && !sseTriedRef.current) {
      startSse(job.id)
    }
  }, [job?.id, job?.progress])

  function startSse(jobId: string | number) {
    sseTriedRef.current = true
    try {
      if (sseRef.current) { sseRef.current.close(); sseRef.current = null }
      if (reconnectRef.current) { window.clearTimeout(reconnectRef.current); reconnectRef.current = null }
      const es = new EventSource(`/api/indexing/stream?jobId=${jobId}`)
      sseRef.current = es
      es.onopen = () => { setSseConnected(true) }
      es.onmessage = (ev) => {
        try {
          const p = JSON.parse(ev.data)
          setLastEventAt(Date.now())
          setJob((prev: IndexingJob | null) => ({
            ...(prev || {} as any),
            id: jobId,
            status: p?.status ?? prev?.status,
            progress: typeof p?.percent === 'number' ? p.percent : prev?.progress,
            stats: {
              filesParsed: p?.filesParsed,
              filesDiscovered: p?.filesDiscovered,
              chunksProduced: p?.chunksProduced,
              documentsIndexed: p?.documentsIndexed,
              embeddingsGenerated: p?.embeddingsGenerated,
              filesSkipped: p?.filesSkipped,
            }
          } as IndexingJob))
          if (p?.percent >= 100) {
            es.close(); sseRef.current = null; setSseConnected(false)
          }
        } catch { /* ignore */ }
      }
      es.onerror = () => {
        es.close(); sseRef.current = null; setSseConnected(false)
        // Attempt reconnect with backoff first; fallback to polling after several attempts
        const delay = backoffMsRef.current
        backoffMsRef.current = Math.min(maxBackoffMs, Math.floor(backoffMsRef.current * 1.8))
        if (reconnectRef.current) window.clearTimeout(reconnectRef.current)
        reconnectRef.current = window.setTimeout(() => {
          if (jobId) {
            startSse(jobId)
          }
        }, delay)
      }
    } catch {
      if (pollRef.current) window.clearInterval(pollRef.current)
      pollRef.current = window.setInterval(refreshLatest, 2000)
    }
  }

  const progress = typeof job?.progress === 'number' ? job!.progress : (job?.status === 'COMPLETED' ? 100 : 0)

  return (
    <div>
      <h2>Indexing Console</h2>
      <fieldset style={{ border: '1px solid #eee', borderRadius: 6, padding: 12, marginBottom: 12 }}>
        <legend style={{ padding: '0 6px', color: '#555' }}>Configuration</legend>
        <div style={{ display: 'grid', gap: 8 }}>
          <label style={{ display: 'grid', gap: 4 }}>
            <span>Root Path (optional)</span>
            <div style={{ display: 'flex', gap: 6 }}>
              <input 
                value={root} 
                onChange={(e: ChangeEvent<HTMLInputElement>) => setRoot(e.target.value)} 
                placeholder="e.g. E:\\code\\my-project"
                style={{ flex: 1 }}
              />
              <button onClick={openBrowser} style={{ padding: '6px 12px' }}>Browse</button>
            </div>
          </label>
          <label style={{ display: 'grid', gap: 4 }}>
            <span>Application Name {appName.trim() || deriveAppNameFromPath(root) ? `(auto: "${appName.trim() || deriveAppNameFromPath(root)}")` : ''}</span>
            <input 
              value={appName} 
              onChange={(e: ChangeEvent<HTMLInputElement>) => setAppName(e.target.value)} 
              placeholder={`e.g. ${deriveAppNameFromPath(root) || 'myapp'}`}
            />
          </label>
          <label style={{ display: 'grid', gap: 4 }}>
            <span>Exclude Directories (CSV)</span>
            <input value={excludeDirs} onChange={(e: ChangeEvent<HTMLInputElement>) => setExcludeDirs(e.target.value)} placeholder="e.g. node_modules, .git, target" />
          </label>
          <label style={{ display: 'grid', gap: 4 }}>
            <span>Exclude Globs (CSV)</span>
            <input value={excludeGlobs} onChange={(e: ChangeEvent<HTMLInputElement>) => setExcludeGlobs(e.target.value)} placeholder="e.g. **/*.min.js, **/*.map" />
          </label>
        </div>
      </fieldset>
      <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
        <button onClick={triggerIndexing} disabled={loading}>Trigger Indexing</button>
        <button onClick={refreshLatest} disabled={loading}>Refresh</button>
        <button onClick={() => { if (job?.id) { backoffMsRef.current = 1000; startSse(job.id) } }} disabled={!job?.id}>Reconnect</button>
      </div>
      {error && <div style={{ color: 'red', marginBottom: 8 }}>Error: {error}</div>}
      <div style={{ border: '1px solid #eee', borderRadius: 6, padding: 12 }}>
        <div style={{ display: 'grid', gap: 6 }}>
          <div><strong>Job ID:</strong> {job?.id ?? '—'}</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <strong>Status:</strong>
            <span style={{
              padding: '2px 8px',
              borderRadius: 999,
              background: job?.status === 'COMPLETED' ? '#e8f5e9' : job?.status === 'FAILED' ? '#ffebee' : '#e3f2fd',
              color: job?.status === 'COMPLETED' ? '#2e7d32' : job?.status === 'FAILED' ? '#c62828' : '#1565c0',
              border: '1px solid #eee'
            }}>
              {job?.status ?? '—'}
            </span>
            <span title={sseConnected ? 'Live (SSE connected)' : 'Not live'} style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
              <span style={{ width: 8, height: 8, borderRadius: '50%', background: sseConnected ? '#2ecc71' : '#bdc3c7' }} />
              <span style={{ fontSize: 12, color: '#666' }}>live</span>
            </span>
            {lastEventAt && <span style={{ marginLeft: 'auto', fontSize: 12, color: '#666' }}>Last update: {new Date(lastEventAt).toLocaleTimeString()}</span>}
          </div>
          <div><strong>Progress:</strong> <ProgressBar value={progress} /></div>
          <div><strong>Started:</strong> {job?.startTime ?? '—'}</div>
          <div><strong>Finished:</strong> {job?.endTime ?? '—'}</div>
          {job?.stats && <pre style={{ background: '#fafafa', padding: 8 }}>{JSON.stringify(job.stats, null, 2)}</pre>}
        </div>
      </div>
      <h3 style={{ marginTop: 16 }}>Recent Jobs</h3>
      <div style={{ border: '1px solid #eee', borderRadius: 6, padding: 8 }}>
        {recent.length === 0 ? (
          <div>No jobs yet</div>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th style={{ textAlign: 'left', padding: 6, borderBottom: '1px solid #eee' }}>ID</th>
                <th style={{ textAlign: 'left', padding: 6, borderBottom: '1px solid #eee' }}>Status</th>
                <th style={{ textAlign: 'left', padding: 6, borderBottom: '1px solid #eee' }}>Started</th>
                <th style={{ textAlign: 'left', padding: 6, borderBottom: '1px solid #eee' }}>Ended</th>
                <th style={{ textAlign: 'left', padding: 6, borderBottom: '1px solid #eee' }}>Files</th>
                <th style={{ textAlign: 'left', padding: 6, borderBottom: '1px solid #eee' }}>Chunks</th>
              </tr>
            </thead>
            <tbody>
              {recent.map(r => (
                <tr key={String(r.id)} style={{ cursor: 'pointer' }} onClick={() => { setJob(r); if (r.id) startSse(r.id) }} title="Open job">
                  <td style={{ padding: 6, borderBottom: '1px solid #f5f5f5' }}>{String(r.id)}</td>
                  <td style={{ padding: 6, borderBottom: '1px solid #f5f5f5' }}>{r.status}</td>
                  <td style={{ padding: 6, borderBottom: '1px solid #f5f5f5' }}>{r.startTime ?? ''}</td>
                  <td style={{ padding: 6, borderBottom: '1px solid #f5f5f5' }}>{r.endTime ?? ''}</td>
                  <td style={{ padding: 6, borderBottom: '1px solid #f5f5f5' }}>{r.stats?.filesParsed ?? 0}</td>
                  <td style={{ padding: 6, borderBottom: '1px solid #f5f5f5' }}>{r.stats?.chunksProduced ?? 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      {toast && <Toast message={toast} type="success" />}

      {/* Directory Browser Modal */}
      {showBrowser && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
        }}>
          <div style={{
            background: 'white',
            borderRadius: 8,
            padding: 20,
            maxWidth: '700px',
            width: '95%',
            maxHeight: '80vh',
            display: 'flex',
            flexDirection: 'column',
            boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          }}>
            <h3 style={{ marginTop: 0, marginBottom: 16 }}>Select Root Path</h3>
            
            {browserError && (
              <div style={{ 
                background: '#ffebee', 
                color: '#c62828', 
                padding: 8, 
                borderRadius: 4, 
                marginBottom: 12,
                fontSize: 14
              }}>
                {browserError}
              </div>
            )}

            <div style={{ marginBottom: 12 }}>
              <label style={{ display: 'block', marginBottom: 6, fontSize: 12, fontWeight: 500, color: '#666' }}>
                Current Path:
              </label>
              <div style={{
                display: 'flex',
                gap: 6,
                alignItems: 'center',
              }}>
                <input
                  type="text"
                  value={browserListing?.currentPath || ''}
                  onChange={(e) => {
                    const newPath = e.target.value.trim()
                    if (newPath) {
                      setCurrentBrowsePath(newPath)
                    }
                  }}
                  placeholder="Enter path..."
                  style={{
                    flex: 1,
                    padding: '8px 12px',
                    border: '1px solid #ddd',
                    borderRadius: 4,
                    fontSize: 13,
                  }}
                  disabled={browserLoading}
                />
                <button
                  onClick={() => browsePath(browserListing?.currentPath || '')}
                  disabled={browserLoading}
                  style={{
                    padding: '6px 12px',
                    background: '#2196f3',
                    color: 'white',
                    border: 'none',
                    borderRadius: 4,
                    cursor: browserLoading ? 'not-allowed' : 'pointer',
                    opacity: browserLoading ? 0.6 : 1,
                  }}
                >
                  Go
                </button>
              </div>
            </div>

            <div style={{
              flex: 1,
              border: '1px solid #eee',
              borderRadius: 4,
              overflow: 'auto',
              marginBottom: 12,
              minHeight: '300px',
              background: '#fafafa',
            }}>
              {browserLoading ? (
                <div style={{ padding: 16, textAlign: 'center', color: '#999' }}>Loading...</div>
              ) : !browserListing?.entries || browserListing.entries.length === 0 ? (
                <div style={{ padding: 16, textAlign: 'center', color: '#999' }}>No items</div>
              ) : (
                <ul style={{ listStyle: 'none', margin: 0, padding: 0 }}>
                  {browserListing.entries.map((entry, idx) => (
                    <li
                      key={idx}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        padding: '10px 12px',
                        borderBottom: '1px solid #f0f0f0',
                        background: idx % 2 === 0 ? '#fff' : '#fafafa',
                        cursor: 'pointer',
                        transition: 'background 0.15s',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.background = '#f0f0f0'
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.background = idx % 2 === 0 ? '#fff' : '#fafafa'
                      }}
                    >
                      <span style={{ marginRight: 10, width: 20, fontSize: 16 }}>
                        {entry.name === '..' ? '📤' : entry.isDirectory ? '📁' : '📄'}
                      </span>
                      <span style={{ flex: 1, fontSize: 13, fontFamily: 'monospace' }}>
                        {entry.name}
                      </span>
                      <div style={{ display: 'flex', gap: 6 }}>
                        {entry.isDirectory && (
                          <button
                            onClick={() => browsePath(entry.path)}
                            disabled={browserLoading}
                            style={{
                              padding: '4px 10px',
                              fontSize: 11,
                              background: '#e3f2fd',
                              border: '1px solid #90caf9',
                              borderRadius: 3,
                              cursor: browserLoading ? 'not-allowed' : 'pointer',
                              color: '#1565c0',
                              opacity: browserLoading ? 0.6 : 1,
                            }}
                          >
                            Open
                          </button>
                        )}
                        <button
                          onClick={() => selectPath(entry.path)}
                          disabled={browserLoading || entry.isDirectory === false}
                          style={{
                            padding: '4px 10px',
                            fontSize: 11,
                            background: entry.isDirectory ? '#e8f5e9' : '#f5f5f5',
                            border: entry.isDirectory ? '1px solid #81c784' : '1px solid #ddd',
                            borderRadius: 3,
                            cursor: entry.isDirectory ? 'pointer' : 'not-allowed',
                            color: entry.isDirectory ? '#2e7d32' : '#999',
                            opacity: entry.isDirectory && !browserLoading ? 1 : 0.6,
                          }}
                        >
                          Select
                        </button>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </div>

            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button
                onClick={() => {
                  setShowBrowser(false)
                  setBrowserListing(undefined)
                  setBrowserError(undefined)
                }}
                disabled={browserLoading}
                style={{
                  padding: '8px 16px',
                  background: '#f0f0f0',
                  border: '1px solid #ddd',
                  borderRadius: 4,
                  cursor: browserLoading ? 'not-allowed' : 'pointer',
                  opacity: browserLoading ? 0.6 : 1,
                }}
              >
                Close
              </button>
              {currentBrowsePath && (
                <button
                  onClick={() => selectPath(currentBrowsePath)}
                  disabled={browserLoading}
                  style={{
                    padding: '8px 16px',
                    background: '#4caf50',
                    color: 'white',
                    border: 'none',
                    borderRadius: 4,
                    cursor: browserLoading ? 'not-allowed' : 'pointer',
                    opacity: browserLoading ? 0.6 : 1,
                  }}
                >
                  Select Current Path
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
