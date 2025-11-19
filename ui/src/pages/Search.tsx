import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useSearch, SearchResult } from '../state/SearchContext'
import SearchBar from '../shared/SearchBar'
import Filters from '../shared/Filters'
import ResultsList from '../shared/ResultsList'
import Pagination from '../shared/Pagination'
import Modal from '../shared/Modal'
import CodeViewer from '../shared/CodeViewer'
import { getFileContent } from '../api'
import AppSelector from '../shared/AppSelector'
import LLMResults, { LLMInsight, LLMSuggestedFile } from '../shared/LLMResults'

export default function SearchPage() {
  const { query, setQuery, app, setApp, filters, setFilters, page, setPage, results, total, loading, error, search } = useSearch()
  const [open, setOpen] = useState(false)
  const [viewer, setViewer] = useState<{ path: string, content: string, line?: number } | null>(null)
  const [recent, setRecent] = useState<string[]>([])
  const [searchMode, setSearchMode] = useState<'lucene'|'llm'|'both'>('lucene')
  const [llmInsights, setLlmInsights] = useState<LLMInsight[]>([])
  const [llmSuggestions, setLlmSuggestions] = useState<LLMSuggestedFile[]>([])
  const [llmLoading, setLlmLoading] = useState(false)
  const [llmError, setLlmError] = useState<string | null>(null)
  const [showLLMResults, setShowLLMResults] = useState(false)
  const [indexStatus, setIndexStatus] = useState<{ totalFiles: number, totalChunks: number, hasIndexedData: boolean } | null>(null)
  // App list is provided by AppSelector; Search no longer fetches apps directly
  const [params] = useSearchParams()

  useEffect(() => {
    // fetch index status on load and when app changes
    const fetchIndexStatus = async () => {
      try {
        const res = await fetch('/api/search/index/status')
        if (!res.ok) return
        const data = await res.json()
        setIndexStatus(data)
      } catch (e) { /* ignore */ }
    }
    fetchIndexStatus()
    try {
      const saved = JSON.parse(localStorage.getItem('search:recent') || '[]')
      if (Array.isArray(saved)) setRecent(saved.slice(0, 10))
    } catch { /* ignore */ }
  // AppSelector will populate apps on mount
    // re-run when active app changes so the index status (per-app) is refreshed
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [app])

  // App list fetch moved to shared AppSelector component

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

  const openCodeViewer = async (filePath?: string, line?: number) => {
    if (!filePath) return
    try {
      const file = await getFileContent(filePath)
      setViewer({ path: file.path, content: file.content, line })
      setOpen(true)
    } catch (e) {
      console.error('Failed to open file from search result', e)
    }
  }

  const handleResultOpen = async (item: SearchResult) => {
    await openCodeViewer(item.filePath, item.line)
  }

  const runLuceneSearch = (opts?: { page?: number }) => {
    // Verify that an index exists before running lucene search
    if (indexStatus && !indexStatus.hasIndexedData) {
      // Quick user feedback: don't call the backend search endpoint because it will fail
      alert('No Lucene index found for current data. Open the Indexing page or rebuild the index from database.')
      return
    }
    const p = opts?.page ?? 1
    setPage(p)
    setSearchMode('lucene')
    setShowLLMResults(false)
    setLlmError(null)
    search({ page: p })
    const q = (query || '').trim()
    if (q) {
      setRecent(prev => {
        const next = [q, ...prev.filter(x => x !== q)].slice(0, 25)
        try { localStorage.setItem('search:recent', JSON.stringify(next)) } catch { /* ignore */ }
        return next
      })
    }
  }

  const runLLMSearch = async () => {
    const q = (query || '').trim()
    if (!q) return
    setSearchMode('llm')
    setShowLLMResults(true)
    setLlmLoading(true)
    setLlmError(null)
    // Add quick client-side logs to help debug LLM queries and results
    console.info("LLM: starting search", { query: q, app, timestamp: new Date().toISOString() })
    const start = Date.now()
    try {
      const res = await fetch('/api/llm/search', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query: q, app })
      })
      if (!res.ok) throw new Error('LLM search failed')
      const data = await res.json()
      console.info("LLM: response received", { status: res.status, timeMs: Date.now() - start })
      console.debug("LLM: response body", data)
      setLlmInsights(data.insights || [])
      setLlmSuggestions(data.suggestedFiles || [])
      setPage(1)
    } catch (e) {
      console.error("LLM: search error", e)
      setLlmInsights([])
      setLlmSuggestions([])
      setLlmError(e instanceof Error ? e.message : 'LLM search failed')
    } finally {
      setLlmLoading(false)
    }
  }

  const runBothSearches = async () => {
    setSearchMode('both')
    setShowLLMResults(true)
    runLuceneSearch({ page: 1 })
    await runLLMSearch()
  }

  const runSearch = (opts?: { page?: number }) => {
    switch (searchMode) {
      case 'lucene': return runLuceneSearch(opts)
      case 'llm': return runLLMSearch()
      case 'both': return runBothSearches()
    }
  }

  return (
    <div>
      <h2>Search</h2>
      <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
        <SearchBar value={query} onChange={setQuery} onSubmit={() => runSearch({ page: 1 })} />
        {/* Search mode buttons (always visible) */}
        <div style={{ display: 'flex', gap: 8, marginLeft: 8 }}>
          <button
            onClick={() => runLuceneSearch({ page: 1 })}
            style={{ padding: '8px 10px', background: searchMode === 'lucene' ? '#4CAF50' : '#f0f0f0', color: searchMode === 'lucene' ? 'white' : 'black', border: '1px solid #ccc', borderRadius: 4 }}
          >🔍 Lucene</button>
          <button
            onClick={() => { console.info('LLM button clicked', { query, app, time: new Date().toISOString() }); runLLMSearch() }}
            style={{ padding: '8px 10px', background: searchMode === 'llm' ? '#2196F3' : '#f0f0f0', color: searchMode === 'llm' ? 'white' : 'black', border: '1px solid #ccc', borderRadius: 4 }}
          >🤖 LLM</button>
          <button
            onClick={() => runBothSearches()}
            style={{ padding: '8px 10px', background: searchMode === 'both' ? '#FF9800' : '#f0f0f0', color: searchMode === 'both' ? 'white' : 'black', border: '1px solid #ccc', borderRadius: 4 }}
          >⚡ Both</button>
        </div>
        <div style={{ marginLeft: 8 }}>
          <AppSelector />
        </div>
        
        {/* Index status */}
        <div style={{ marginLeft: 8, fontSize: 13 }}>
          {indexStatus ? (
            indexStatus.hasIndexedData ? (
              <span style={{ color: '#2ecc71' }}>Indexed: {indexStatus.totalFiles} files</span>
            ) : (
              <span style={{ color: '#e74c3c' }}>No index available</span>
            )
          ) : (
            <span style={{ color: '#999' }}>Checking index...</span>
          )}
          {!indexStatus?.hasIndexedData && (
            <button onClick={async () => {
              const res = await fetch('/api/search/index/rebuild', { method: 'POST' })
              if (!res.ok) {
                alert('Rebuild failed. Check server logs.')
                return
              }
              const data = await res.json()
              alert(`Rebuild: ${data.status} - ${data.message}`)
              // Refresh status
              const stat = await fetch('/api/search/index/status')
              if (stat.ok) setIndexStatus(await stat.json())
            }} style={{ marginLeft: 8 }}>
              Rebuild Index
            </button>
          )}
        </div>
      </div>
      {recent.length > 0 && (
        <div aria-label="Recent searches" style={{ display: 'flex', gap: 8, flexWrap: 'wrap', margin: '6px 0 10px' }}>
          {recent.map((r) => (
            <button key={r} onClick={() => { setQuery(r); runSearch({ page: 1 }) }} style={{ border: '1px solid #ddd', borderRadius: 14, padding: '3px 8px', background: '#f9f9f9' }}>{r}</button>
          ))}
          {/* Clear history removed - history is readonly */}
        </div>
      )}
      <Filters filters={filters} onChange={setFilters} />
      {error && <div style={{ color: 'red' }}>Error: {error}</div>}
      {loading ? (
        <div>Loading...</div>
      ) : (
        <ResultsList results={results} onOpen={handleResultOpen} />
      )}
      <Pagination
        page={page}
        pageSize={10}
        total={total}
        onPageChange={(p: number) => runSearch({ page: p })}
      />
      {showLLMResults && (
        <LLMResults
          insights={llmInsights}
          suggestions={llmSuggestions}
          loading={llmLoading}
          error={llmError}
          onOpen={async (suggestion) => {
            if (suggestion.filePath) {
              await openCodeViewer(suggestion.filePath)
            }
          }}
        />
      )}
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
