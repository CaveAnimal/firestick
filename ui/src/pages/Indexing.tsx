import { useEffect, useRef, useState, type ChangeEvent } from 'react'
import { getLatestJob, startIndexing, IndexingJob, getRecentJobs, browseDirectory, type DirectoryListing } from '../api'
import ProgressBar from '../shared/ProgressBar'
import AppSelector from '../shared/AppSelector'
import { useSearch } from '../state/SearchContext'
import Toast from '../shared/Toast'

export default function IndexingPage() {
  const { app } = useSearch()
  // Zero out all progress values when app changes
  useEffect(() => {
    setJob({
      id: '',
      appName: app,
      status: '',
      progress: 0,
      stats: {
        filesDiscovered: 0,
        filesParsed: 0,
        chunksProduced: 0,
        documentsIndexed: 0,
        embeddingsGenerated: 0,
        filesSkipped: 0,
        filesSummarized: 0,
        foldersSummarized: 0,
        methodsSummarized: 0,
        skippedFiles: [],
      },
      currentFile: '',
      startedAt: '',
      endedAt: '',
    } as IndexingJob)
  }, [app])
  const [job, setJob] = useState<IndexingJob | null>(null)
  const [selectedApp, setSelectedApp] = useState<string>('')
  const [recent, setRecent] = useState<IndexingJob[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | undefined>()
  const [toast, setToast] = useState<string | undefined>()
  const [sseConnected, setSseConnected] = useState(false)
  // LLM progress state
  const [llmProgress, setLlmProgress] = useState<{status: string, type: string, timestamp: string, error?: string} | null>(null)
  const [lastEventAt, setLastEventAt] = useState<number | null>(null)
  const [objectEvents, setObjectEvents] = useState<Array<{event: string, type: string, name: string, ts: number, elapsedMs?: number}>>([])
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
      // Patch: ensure stats object is populated for UI compatibility
      setJob(latest ? {
        ...latest,
        stats: {
          filesDiscovered: latest.stats?.filesDiscovered ?? latest['filesDiscovered'] ?? 0,
          filesParsed: latest.stats?.filesParsed ?? latest['filesParsed'] ?? 0,
          chunksProduced: latest.stats?.chunksProduced ?? latest['chunksProduced'] ?? 0,
          documentsIndexed: latest.stats?.documentsIndexed ?? latest['documentsIndexed'] ?? 0,
          embeddingsGenerated: latest.stats?.embeddingsGenerated ?? latest['embeddingsGenerated'] ?? 0,
          filesSkipped: latest.stats?.filesSkipped ?? latest['filesSkipped'] ?? 0,
        }
      } : null)
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
    if (!root || !root.trim()) {
      setError('Root path is required')
      setLoading(false)
      return
    }
    // Always subscribe to SSE before triggering indexing to avoid missing events
    let jobIdBefore: string | number | null = null;
    try {
      const job = await getLatestJob();
      if (job?.id) {
        jobIdBefore = job.id;
        startSse(job.id);
      }
    } catch {
      // ignore
    }
    try {
      // Parse CSV inputs into arrays
      const dirs = excludeDirs.split(',').map((s: string) => s.trim()).filter(Boolean);
      const globs = excludeGlobs.split(',').map((s: string) => s.trim()).filter(Boolean);
      const finalAppName = appName.trim() || deriveAppNameFromPath(root);
      const res = await startIndexing({
        root: root.trim() || undefined,
        appName: finalAppName,
        excludeDirs: dirs.length ? dirs : undefined,
        excludeGlobs: globs.length ? globs : undefined,
      });
      persistConfig();
      setToast(`Indexing started${res?.jobId ? ` (job ${res.jobId})` : ''}`);
      if (pollRef.current) window.clearInterval(pollRef.current);
      // If startIndexing returned a jobId, subscribe immediately to avoid any gap
      if (res?.jobId) {
        startSse(res.jobId as string | number)
      }
      // After triggering, double-check latest job and subscribe to new job SSE if jobId changed
      let jobIdAfter: string | number | null = null;
      try {
        const job = await getLatestJob();
        if (job?.id && job?.id !== jobIdBefore) {
          startSse(job.id);
        }
        jobIdAfter = job?.id || null;
      } catch {
        pollRef.current = window.setInterval(refreshLatest, 1500);
      }
      await refreshLatest();
    } catch (e: any) {
      setError(e?.message ?? 'Failed to start indexing');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refreshLatest()
    refreshRecent()
    // LLM SSE subscription
    const llmSse = new EventSource('http://localhost:8001/api/llm/progress-stream')
    llmSse.onmessage = (ev) => {
      try {
        const data = JSON.parse(ev.data)
        setLlmProgress(data)
      } catch {}
    }
    llmSse.onerror = () => {
      llmSse.close()
    }
    return () => {
      if (pollRef.current) window.clearInterval(pollRef.current)
      if (sseRef.current) { sseRef.current.close(); sseRef.current = null }
      llmSse.close()
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
          // handle object start/end events
          if (p?.event && (p.event === 'object-start' || p.event === 'object-end' || p.event === 'object-skipped')) {
            try {
              const newEvt = { event: p.event, type: p.type, name: p.name, ts: p.ts || Date.now(), elapsedMs: p.elapsedMs }
              setObjectEvents(prev => [newEvt, ...prev].slice(0, 30))
            } catch { /* ignore */ }
          }
          setJob((prev: IndexingJob | null) => {
            // Merge previous stats with new event data for live updates
            const mergedStats = {
              ...((prev && prev.stats) || {}),
              filesParsed: p?.filesParsed ?? prev?.stats?.filesParsed,
              filesDiscovered: p?.filesDiscovered ?? prev?.stats?.filesDiscovered,
              chunksProduced: p?.chunksProduced ?? prev?.stats?.chunksProduced,
              documentsIndexed: p?.documentsIndexed ?? prev?.stats?.documentsIndexed,
              embeddingsGenerated: p?.embeddingsGenerated ?? prev?.stats?.embeddingsGenerated,
              filesSkipped: p?.filesSkipped ?? prev?.stats?.filesSkipped,
              filesSummarized: p?.filesSummarized ?? prev?.stats?.filesSummarized,
              foldersSummarized: p?.foldersSummarized ?? prev?.stats?.foldersSummarized,
              methodsSummarized: p?.methodsSummarized ?? prev?.stats?.methodsSummarized,
              skippedFiles: p?.skippedFiles ?? prev?.stats?.skippedFiles,
            }
            return {
              ...(prev || {} as any),
              id: jobId,
              status: p?.status ?? prev?.status,
              progress: typeof p?.percent === 'number' ? p.percent : prev?.progress,
              totalFolders: p?.totalFolders ?? prev?.totalFolders,
              totalMethods: p?.totalMethods ?? prev?.totalMethods,
              stats: mergedStats,
              currentFile: p?.currentFile ?? prev?.currentFile
            } as IndexingJob
          })
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

    let progress = 0;
    if (
      job &&
      job.stats &&
      typeof job.stats.filesParsed === 'number' &&
      typeof job.stats.filesDiscovered === 'number' &&
      job.stats.filesDiscovered > 0
    ) {
      progress = Math.round((job.stats.filesParsed / job.stats.filesDiscovered) * 100);
    } else if (typeof job?.progress === 'number') {
      progress = job.progress;
    } else if (job?.status === 'COMPLETED') {
      progress = 100;
    }

  function formatTime(iso?: string) {
    if (!iso) return '—'
    return new Date(iso).toLocaleString(undefined, {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  }

  function getDuration(start?: string, end?: string) {
    if (!start) return '—'
    const s = new Date(start).getTime()
    const e = end ? new Date(end).getTime() : Date.now()
    const diff = Math.max(0, e - s)
    const sec = Math.floor(diff / 1000)
    if (sec < 60) return `${sec}s`
    const min = Math.floor(sec / 60)
    return `${min}m ${sec % 60}s`
  }

  return (
    <div style={{ maxWidth: '1200px', width: '92%', margin: '0 auto', padding: 12 }}>
      <h2 style={{ fontWeight: 700, fontSize: 22, marginBottom: 12, letterSpacing: 0.5 }}>
        Indexing Console
      </h2>
      {/* LLM Progress Section */}
      {llmProgress && (
        <div style={{ background: '#e3f2fd', borderRadius: 6, padding: 8, marginBottom: 12, fontSize: 14, color: '#1565c0', fontWeight: 500 }}>
          <span style={{ marginRight: 8 }}>🤖</span>
          LLM Service Progress: <span style={{ fontWeight: 600 }}>{llmProgress.type}</span> - <span>{llmProgress.status}</span>
          {llmProgress.error && <span style={{ color: '#c62828', marginLeft: 12 }}>Error: {llmProgress.error}</span>}
          <span style={{ marginLeft: 12, fontSize: 13, color: '#888' }}>{new Date(llmProgress.timestamp).toLocaleTimeString()}</span>
        </div>
      )}
      {/* Configuration Card */}
      <div style={{ background: '#fff', borderRadius: 8, boxShadow: '0 2px 6px #eee', padding: 12, marginBottom: 12 }}>
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 8 }}>
          <AppSelector compact={true} />
        </div>
        <div style={{ fontWeight: 600, fontSize: 16, marginBottom: 8, color: '#1976d2', display: 'flex', alignItems: 'center' }}>
          Configuration
        </div>
        <div style={{ display: 'grid', gap: 8 }}>
          <label style={{ display: 'grid', gap: 4 }} title="Select the root directory for indexing. Optional, but recommended.">
            <span style={{ fontWeight: 500 }}>Root Path <span style={{ color: '#888', fontWeight: 400 }}>(optional)</span></span>
            <div style={{ display: 'flex', gap: 8 }}>
              <input
                value={root}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setRoot(e.target.value)} 
                placeholder="e.g. E:\\code\\my-project"
                style={{ flex: 1, padding: '6px 10px', borderRadius: 6, border: '1px solid #ccc', fontSize: 14 }}
              />
              <button onClick={openBrowser} style={{ padding: '6px 14px', borderRadius: 6, background: '#1976d2', color: '#fff', border: 'none', fontWeight: 500, cursor: 'pointer' }}>Browse</button>
            </div>
          </label>
          <div style={{ display: 'flex', gap: 12, marginTop: 8 }}>
            <label style={{ flex: 1, display: 'grid', gap: 4 }} title="Application name is auto-derived from folder name, but you can override it.">
              <span style={{ fontWeight: 500 }}>Application Name <span style={{ color: '#888', fontWeight: 400 }}>(auto: "{appName.trim() || deriveAppNameFromPath(root)}")</span></span>
                  <input
                    value={appName}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setAppName(e.target.value)}
                    placeholder={`e.g. ${deriveAppNameFromPath(root) || 'charliebrown'}`}
                    style={{ padding: '6px 10px', borderRadius: 6, border: '1px solid #ccc', fontSize: 14 }}
              />
            </label>
            <label style={{ flex: 1, display: 'grid', gap: 4 }} title="Comma-separated directories to exclude from indexing.">
              <span style={{ fontWeight: 500 }}>Exclude Directories <span style={{ color: '#888', fontWeight: 400 }}>(CSV)</span></span>
                  <input value={excludeDirs} onChange={(e: ChangeEvent<HTMLInputElement>) => setExcludeDirs(e.target.value)} placeholder="e.g. node_modules, .git, target" style={{ padding: '6px 10px', borderRadius: 6, border: '1px solid #ccc', fontSize: 14 }} />
            </label>
            <label style={{ flex: 1, display: 'grid', gap: 4 }} title="Comma-separated glob patterns to exclude from indexing.">
              <span style={{ fontWeight: 500 }}>Exclude Globs <span style={{ color: '#888', fontWeight: 400 }}>(CSV)</span></span>
                  <input value={excludeGlobs} onChange={(e: ChangeEvent<HTMLInputElement>) => setExcludeGlobs(e.target.value)} placeholder="e.g. **/*.min.js, **/*.map" style={{ padding: '6px 10px', borderRadius: 6, border: '1px solid #ccc', fontSize: 14 }} />
            </label>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 10, marginTop: 12 }}>
          <button onClick={triggerIndexing} disabled={loading} style={{ fontWeight: 600, border: '1px solid #ccc', borderRadius: 6, padding: '8px 14px', fontSize: 14, cursor: loading ? 'not-allowed' : 'pointer', background: 'transparent', color: '#222', boxShadow: 'none' }}>Trigger Indexing</button>
          <button onClick={refreshLatest} disabled={loading} style={{ fontWeight: 500, border: '1px solid #ccc', borderRadius: 6, padding: '8px 14px', fontSize: 14, cursor: loading ? 'not-allowed' : 'pointer', background: 'transparent', color: '#222' }}>Refresh</button>
          <button onClick={() => { if (job?.id) { backoffMsRef.current = 1000; startSse(job.id) } }} disabled={!job?.id} style={{ fontWeight: 500, border: '1px solid #ccc', borderRadius: 6, padding: '8px 14px', fontSize: 14, cursor: !job?.id ? 'not-allowed' : 'pointer', background: 'transparent', color: '#222' }}>Reconnect</button>
        </div>
      </div>
      {error && <div style={{ color: '#c62828', background: '#ffebee', borderRadius: 6, padding: 10, marginBottom: 16, fontWeight: 500 }}>❌ Error: {error}</div>}
      {/* Job Status Card */}
      <div style={{ background: '#fff', borderRadius: 8, boxShadow: '0 2px 6px #eee', padding: 12, marginBottom: 12 }}>
        <div style={{ fontWeight: 600, fontSize: 16, marginBottom: 8, color: '#43a047', display: 'flex', alignItems: 'center' }}>
          Indexing Progress
        </div>
        <div style={{ display: 'grid', gap: 10 }}>
          <div style={{ fontSize: 16, fontWeight: 500 }}>
            <span style={{ color: '#1976d2', fontWeight: 700 }}>Job ID:</span> {job?.id ?? '—'} {job?.appName ? <span style={{ color: '#666', fontWeight: 400 }}>({job.appName})</span> : ''}
            {job?.id && <a href={`/admin/indexing-objects?jobId=${job.id}`} style={{ marginLeft: 10, fontSize: 12, color: '#1976d2' }}>View objects</a>}
            {/* Reordered to match the Summary block: Folders, Files, Methods */}
            <span style={{ marginLeft: 14, color: '#000', fontWeight: 700 }}>
              <span style={{ color: '#6a1b9a' }}>Folders:</span> {job?.totalFolders ?? job?.stats?.foldersSummarized ?? 0}
            </span>
            <span style={{ marginLeft: 18, color: '#000', fontWeight: 700 }}>
              <span style={{ color: '#43a047' }}>Files:</span> {job?.filesDiscovered ?? job?.stats?.filesDiscovered ?? 0}
            </span>
            <span style={{ marginLeft: 18, color: '#000', fontWeight: 700 }}>
              <span style={{ color: '#ef6c00' }}>Methods:</span> {job?.totalMethods ?? job?.stats?.methodsSummarized ?? 0}
            </span>
          </div>
          {job?.currentFile && (
             <div style={{ fontSize: 13, color: '#555', fontFamily: 'monospace', background: '#f5f5f5', padding: '6px 12px', borderRadius: 6, marginBottom: 4 }}>
               <span style={{ marginRight: 8 }}>🔄</span> Indexing: {job.currentFile}
             </div>
          )}
          {objectEvents.length > 0 && (
            <div style={{ fontSize: 13, color: '#444', marginTop: 8 }}>
              <strong style={{ fontWeight: 600 }}>Recent events:</strong>
              <div style={{ marginTop: 6, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                {objectEvents.slice(0,5).map((e, idx) => (
                  <div key={String(e.name)+idx} style={{ background: '#f5f5f5', padding: '6px 10px', borderRadius: 6, fontFamily: 'monospace', fontSize: 13 }}>
                    <div style={{ color: '#666' }}>{new Date(e.ts).toLocaleTimeString()}</div>
                    <div><strong>{e.event}</strong> <span style={{ color: '#1976d2' }}>{e.type}</span> <span style={{ color: '#d84315' }}>{e.name.length > 40 ? e.name.substring(0, 40) + '…' : e.name}</span></div>
                    {typeof e.elapsedMs === 'number' && <div style={{ fontSize: 12, color: '#777' }}>took {e.elapsedMs}ms</div>}
                  </div>
                ))}
              </div>
            </div>
          )}
          <div>
            <strong style={{ fontWeight: 600 }}>Progress:</strong>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 6 }}>
              <div style={{ flex: 1 }}><ProgressBar value={progress} color="#2ecc40" /></div>
              <span style={{ fontSize: 14, minWidth: 90, textAlign: 'right', fontWeight: 500, color: '#1976d2' }}>
                {job?.stats?.filesParsed ?? 0} / {job?.stats?.filesDiscovered ?? '?'} files
              </span>
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, marginTop: 8 }}>
            <div><strong>Started:</strong><br/><span style={{ fontSize: 14 }}>{job?.startedAt ? formatTime(job.startedAt) : '—'}</span></div>
            <div><strong>Finished:</strong><br/><span style={{ fontSize: 14 }}>{job?.endedAt ? formatTime(job.endedAt) : '—'}</span></div>
            <div><strong>Elapsed:</strong><br/><span style={{ fontSize: 14 }}>{job?.startedAt ? getDuration(job.startedAt, job.endedAt) : '—'}</span></div>
          </div>
          {/* Summary Section for Methods, Files, Folders */}
          <div style={{ background: '#e3f2fd', borderRadius: 6, padding: 8, marginTop: 12, fontSize: 14, color: '#1565c0', fontWeight: 500 }}>
            <div style={{ marginBottom: 6 }}><strong>Summary:</strong></div>
            <div style={{ display: 'flex', gap: '32px', flexWrap: 'wrap', fontFamily: 'monospace', fontSize: 15 }}>
              <div><span style={{ fontWeight: 600 }}>Folders summarized:</span> {job?.totalFolders ?? job?.stats?.foldersSummarized ?? '—'}</div>
              <div><span style={{ fontWeight: 600 }}>Files summarized:</span> {job?.filesDiscovered ?? job?.stats?.filesSummarized ?? '—'}</div>
              <div><span style={{ fontWeight: 600 }}>Methods summarized:</span> {job?.totalMethods ?? job?.stats?.methodsSummarized ?? '—'}</div>
            </div>
          </div>
          {job?.stats && <div style={{ background: '#f9fbe7', borderRadius: 6, padding: 8, marginTop: 8, fontSize: 14, fontFamily: 'monospace', color: '#333', boxShadow: '0 1px 4px #e0e0e0' }}>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '32px', marginTop: 8, fontFamily: 'monospace', fontSize: 15, background: 'transparent', boxShadow: 'none', borderRadius: 0, padding: 0 }}>
              <div><span style={{ fontWeight: 600 }}>Files Discovered:</span> {job.stats.filesDiscovered}</div>
              <div><span style={{ fontWeight: 600 }}>Files Parsed:</span> {job.stats.filesParsed}</div>
              <div><span style={{ fontWeight: 600 }}>Files Skipped:</span> {(job.stats.filesSkipped && job.stats.filesSkipped > 0) ? job.stats.filesSkipped : (job.stats.filesDiscovered - job.stats.filesParsed)}</div>
            </div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '32px', marginTop: 8, fontFamily: 'monospace', fontSize: 15 }}>
              <div><span style={{ fontWeight: 600 }}>Chunks Produced:</span> {job.stats.chunksProduced}</div>
              <div><span style={{ fontWeight: 600 }}>Documents Indexed:</span> {job.stats.documentsIndexed}</div>
              <div><span style={{ fontWeight: 600 }}>Embeddings Generated:</span> {job.stats.embeddingsGenerated}</div>
            </div>
          </div>}
        </div>
      </div>
      {/* Skipped Files Area */}
      {job?.stats?.skippedFiles && Array.isArray(job.stats.skippedFiles) && job.stats.skippedFiles.length > 0 && (
        <div style={{ background: '#fff3e0', borderRadius: 8, boxShadow: '0 2px 6px #ffe0b2', padding: 12, marginBottom: 12 }}>
          <div style={{ fontWeight: 600, fontSize: 15, marginBottom: 8, color: '#ef6c00', display: 'flex', alignItems: 'center' }}>
            <span style={{ marginRight: 8 }}>🚫</span> Skipped Files
          </div>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 15 }}>
            <thead>
              <tr style={{ background: '#ffe0b2' }}>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #ffd54f', fontWeight: 600 }}>File Name</th>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #ffd54f', fontWeight: 600 }}>Reason</th>
              </tr>
            </thead>
            <tbody>
              {job.stats.skippedFiles.map((f: any, idx: number) => (
                <tr key={f.fileName + idx} style={{ background: idx % 2 === 0 ? '#fff8e1' : '#fff3e0' }}>
                  <td style={{ padding: 8, borderBottom: '1px solid #ffe0b2', fontFamily: 'monospace', color: '#d84315' }}>{f.fileName}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #ffe0b2', color: '#6d4c41' }}>{f.reason}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <h3 style={{ marginTop: 16, fontWeight: 700, fontSize: 18, color: '#1976d2' }}><span style={{ marginRight: 8 }}>🕑</span> Recent Jobs</h3>
      <div style={{ background: '#fff', borderRadius: 8, boxShadow: '0 2px 6px #eee', padding: 12, marginBottom: 12 }}>
        {recent.length === 0 ? (
          <div style={{ color: '#888', fontSize: 15, padding: 10 }}>No jobs yet</div>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 15 }}>
            <thead>
              <tr style={{ background: '#f5f5f5' }}>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #eee', fontWeight: 600 }}>ID</th>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #eee', fontWeight: 600 }}>App Name</th>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #eee', fontWeight: 600 }}>Started</th>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #eee', fontWeight: 600 }}>Finished</th>
                <th style={{ textAlign: 'left', padding: 8, borderBottom: '2px solid #eee', fontWeight: 600 }}>Duration</th>
              </tr>
            </thead>
            <tbody>
              {recent.map(r => (
                <tr key={String(r.id)} style={{ cursor: 'pointer', transition: 'background 0.2s' }} onClick={() => { setJob(r); if (r.id) startSse(r.id) }} title="Open job" onMouseEnter={e => e.currentTarget.style.background = '#e3f2fd'} onMouseLeave={e => e.currentTarget.style.background = ''}>
                  <td style={{ padding: 8, borderBottom: '1px solid #f5f5f5' }}>{String(r.id)}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #f5f5f5' }}>{r.appName || '—'}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #f5f5f5' }}>{formatTime(r.startedAt)}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #f5f5f5' }}>{formatTime(r.endedAt)}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #f5f5f5' }}>{getDuration(r.startedAt, r.endedAt)}</td>
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
