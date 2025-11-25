import { useEffect, useState } from 'react'
import { getRecentJobs, getIndexingObjects, IndexingObject, IndexingObjectsPage } from '../api'

export default function AdminIndexingObjectsPage() {
  const [recent, setRecent] = useState<Array<any>>([])
  const [jobId, setJobId] = useState<string | number | ''>('')
  const [objectsPage, setObjectsPage] = useState<IndexingObjectsPage | null>(null)
  const [objects, setObjects] = useState<IndexingObject[]>([])
  const [page, setPage] = useState<number>(0)
  const [limit, setLimit] = useState<number>(50)
  const [typeFilter, setTypeFilter] = useState<string>('')
  const [nameFilter, setNameFilter] = useState<string>('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | undefined>()

  async function refreshRecent() {
    try {
      const list = await getRecentJobs(30)
      setRecent(list)
    } catch (e: any) {
      // ignore non-fatal
    }
  }

  async function loadObjects(id: string | number, p?: number) {
    setLoading(true)
    setError(undefined)
    try {
      const opts: any = {}
      if (typeof p === 'number') opts.page = p
      opts.limit = limit
      if (typeFilter) opts.objectType = typeFilter
      if (nameFilter) opts.q = nameFilter

      const res = await getIndexingObjects(id, opts)
      if (Array.isArray(res)) {
        setObjects(res)
        setObjectsPage(null)
      } else {
        setObjects(res.items || [])
        setObjectsPage(res)
      }
    } catch (e: any) {
      setError(e?.message ?? 'Failed to fetch objects')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { refreshRecent() }, [])
  // Load jobId from query param if present
  useEffect(() => {
    try {
      const qp = new URLSearchParams(window.location.search)
      const q = qp.get('jobId')
      if (q) {
        setJobId(q)
        loadObjects(q)
      }
    } catch { }
  }, [])

  return (
    <div style={{ maxWidth: 1200, width: '92%', margin: '0 auto', padding: 12 }}>
      <h2 style={{ fontSize: 20, fontWeight: 700 }}>Admin: Indexing Objects</h2>
      <div style={{ background: '#fff', borderRadius: 8, boxShadow: '0 2px 6px #eee', padding: 12, marginBottom: 12 }}>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 8 }}>
          <label style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            Job ID:
            <input value={jobId} onChange={e => setJobId(e.target.value)} placeholder="Enter job id or pick recent" style={{ padding: '6px 10px', borderRadius: 6, border: '1px solid #ccc' }} />
          </label>
          <button onClick={() => loadObjects(jobId as string | number)} disabled={!jobId || loading} style={{ padding: '8px 12px', borderRadius: 6, background: '#1976d2', color: 'white', border: 'none' }}>Load</button>
          <button onClick={refreshRecent} style={{ padding: '8px 12px', borderRadius: 6 }}>Refresh jobs</button>
        </div>
        <div style={{ fontSize: 14, color: '#666' }}>Pick a recent job to open it quickly:</div>
        <div style={{ marginTop: 8, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          {recent.map(r => (
            <button key={String(r.id)} onClick={() => { setJobId(r.id); loadObjects(r.id) }} style={{ padding: '6px 10px', borderRadius: 6, border: '1px solid #ddd', background: '#fafafa' }}>{String(r.id)} ({r.appName || '—'})</button>
          ))}
        </div>
      </div>

      {error && <div style={{ color: '#c62828', background: '#ffebee', borderRadius: 6, padding: 10, marginBottom: 16 }}>Error: {error}</div>}

      <div style={{ background: '#fff', borderRadius: 8, boxShadow: '0 2px 6px #eee', padding: 12 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
          <div style={{ fontWeight: 600 }}>Objects ({objects.length})</div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <label style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
              Type:
              <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)} style={{ padding: '6px 8px', borderRadius: 6 }}>
                <option value="">Any</option>
                <option value="FOLDER">Folder</option>
                <option value="FILE">File</option>
                <option value="METHOD">Method</option>
              </select>
            </label>
            <label style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
              Name contains:
              <input value={nameFilter} onChange={e => setNameFilter(e.target.value)} placeholder="substring" style={{ padding: '6px 8px', borderRadius: 6, border: '1px solid #ddd' }} />
            </label>
            <label style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
              Page size:
              <select value={String(limit)} onChange={e => { setLimit(Number(e.target.value)); setPage(0); }} style={{ padding: '6px 8px', borderRadius: 6 }}>
                <option value={10}>10</option>
                <option value={25}>25</option>
                <option value={50}>50</option>
                <option value={100}>100</option>
              </select>
            </label>
            <button onClick={() => { if (jobId) { setPage(0); loadObjects(jobId, 0) } }} style={{ padding: '6px 10px', borderRadius: 6, background: '#1976d2', color: 'white', border: 'none' }}>Apply</button>
          </div>
        </div>
        {loading ? (
          <div style={{ color: '#888' }}>Loading…</div>
        ) : objects.length === 0 ? (
          <div style={{ color: '#999' }}>No objects found for this job</div>
        ) : (
          <div style={{ overflow: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: '#f5f5f5' }}>
                  <th style={{ textAlign: 'left', padding: 8 }}>ID</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Type</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Name</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Started</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Ended</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Elapsed (ms)</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Reason</th>
                </tr>
              </thead>
              <tbody>
                {objects.map(o => (
                  <tr key={String(o.id)} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: 8 }}>{String(o.id)}</td>
                    <td style={{ padding: 8 }}>{o.objectType}</td>
                    <td style={{ padding: 8, fontFamily: 'monospace' }}>{o.objectName}</td>
                    <td style={{ padding: 8 }}>{o.startedAt ? new Date(o.startedAt).toLocaleString() : '—'}</td>
                    <td style={{ padding: 8 }}>{o.endedAt ? new Date(o.endedAt).toLocaleString() : '—'}</td>
                    <td style={{ padding: 8 }}>{typeof o.elapsedMs === 'number' ? o.elapsedMs : '—'}</td>
                    <td style={{ padding: 8 }}>{o.reasonSkipped || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination controls (shown when server returned page envelope) */}
        {objectsPage && (
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 12 }}>
            <div style={{ color: '#666' }}>Showing {objectsPage.items.length} of {objectsPage.total} — page {objectsPage.page + 1} / {Math.max(1, Math.ceil(objectsPage.total / objectsPage.size))}</div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button onClick={() => { if (jobId && page > 0) { setPage(page - 1); loadObjects(jobId, page - 1) } }} disabled={page <= 0} style={{ padding: '6px 10px', borderRadius: 6 }}>Previous</button>
              <button onClick={() => { if (jobId) { setPage(page + 1); loadObjects(jobId, page + 1) } }} disabled={(page + 1) * limit >= (objectsPage.total || 0)} style={{ padding: '6px 10px', borderRadius: 6 }}>Next</button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
