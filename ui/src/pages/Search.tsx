import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useSearch } from '../state/SearchContext'
import SearchBar from '../shared/SearchBar'
import Filters from '../shared/Filters'
import ResultsList from '../shared/ResultsList'
import Pagination from '../shared/Pagination'
import Modal from '../shared/Modal'
import CodeViewer from '../shared/CodeViewer'
import { getFileContent } from '../api'

export default function SearchPage() {
  const { query, setQuery, app, setApp, filters, setFilters, page, setPage, results, total, loading, error, search } = useSearch()
  const [open, setOpen] = useState(false)
  const [viewer, setViewer] = useState<{ path: string, content: string, line?: number } | null>(null)
  const [recent, setRecent] = useState<string[]>([])
  const [params] = useSearchParams()

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem('search:recent') || '[]')
      if (Array.isArray(saved)) setRecent(saved.slice(0, 10))
    } catch { /* ignore */ }
  }, [])

  // Seed search from querystring if present
  useEffect(() => {
    const qp = (params.get('q') || '').trim()
    if (qp && qp !== query) {
      setQuery(qp)
      // kick off initial search
      setPage(1)
      search({ query: qp, page: 1 })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params])

  const runSearch = (opts?: { page?: number }) => {
    const p = opts?.page ?? 1
    setPage(p)
    search({ page: p })
    const q = (query || '').trim()
    if (q) {
      setRecent(prev => {
        const next = [q, ...prev.filter(x => x !== q)].slice(0, 10)
        try { localStorage.setItem('search:recent', JSON.stringify(next)) } catch { /* ignore */ }
        return next
      })
    }
  }

  return (
    <div>
      <h2>Search</h2>
      <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
        <SearchBar value={query} onChange={setQuery} onSubmit={() => runSearch({ page: 1 })} />
        <label style={{ fontSize: 14 }}>
          App:
          <select value={app} onChange={e => { setApp(e.target.value); runSearch({ page: 1 }) }} style={{ marginLeft: 6 }}>
            {['default','legacy1','legacy2'].map(a => <option key={a} value={a}>{a}</option>)}
          </select>
        </label>
      </div>
      {recent.length > 0 && (
        <div aria-label="Recent searches" style={{ display: 'flex', gap: 8, flexWrap: 'wrap', margin: '6px 0 10px' }}>
          {recent.map((r) => (
            <button key={r} onClick={() => { setQuery(r); runSearch({ page: 1 }) }} style={{ border: '1px solid #ddd', borderRadius: 14, padding: '3px 8px', background: '#f9f9f9' }}>{r}</button>
          ))}
          <button onClick={() => { setRecent([]); try { localStorage.removeItem('search:recent') } catch {} }} style={{ marginLeft: 'auto' }}>Clear</button>
        </div>
      )}
      <Filters filters={filters} onChange={setFilters} />
      {error && <div style={{ color: 'red' }}>Error: {error}</div>}
      {loading ? (
        <div>Loading...</div>
      ) : (
  <ResultsList results={results} onOpen={async (item: any) => {
          try {
            const file = await getFileContent(item.filePath)
            setViewer({ path: file.path, content: file.content, line: item.line })
            setOpen(true)
          } catch (e) {
            // surface minimal error inline
          }
        }} />
      )}
      <Pagination
        page={page}
        pageSize={10}
        total={total}
        onPageChange={(p: number) => runSearch({ page: p })}
      />
      <Modal open={open} onClose={() => setOpen(false)} title={viewer?.path ?? 'Code'}>
        {viewer && (
          <div style={{ height: '100%' }}>
            <CodeViewer path={viewer.path} content={viewer.content} language="java" highlightLine={viewer.line} />
          </div>
        )}
      </Modal>
    </div>
  )
}
