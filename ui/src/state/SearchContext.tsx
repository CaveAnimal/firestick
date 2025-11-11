import { createContext, useContext, useMemo, useState, PropsWithChildren } from 'react'

export type SearchResult = {
  id: string
  filePath: string
  line: number
  snippet: string
}

export type SearchFilters = {
  pathIncludes?: string
  language?: string
}

export type SearchState = {
  query: string
  app: string
  filters: SearchFilters
  page: number
  pageSize: number
  total: number
  results: SearchResult[]
  loading: boolean
  error?: string
  setQuery: (q: string) => void
  setApp: (a: string) => void
  setFilters: (f: Partial<SearchFilters>) => void
  setPage: (p: number) => void
  search: (opts?: { query?: string; page?: number }) => Promise<void>
}

const SearchContext = createContext<SearchState | undefined>(undefined)

export function SearchProvider({ children }: PropsWithChildren) {
  const [query, setQuery] = useState('')
  const [app, setApp] = useState('default')
  const [filters, setFiltersState] = useState<SearchFilters>({})
  const [page, setPage] = useState(1)
  const [pageSize] = useState(10)
  const [total, setTotal] = useState(0)
  const [results, setResults] = useState<SearchResult[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | undefined>(undefined)

  const setFilters = (f: Partial<SearchFilters>) => setFiltersState(prev => ({ ...prev, ...f }))

  async function search(opts?: { query?: string; page?: number }) {
    const q = opts?.query ?? query
    const p = opts?.page ?? page
    if (!q || q.trim().length === 0) {
      setResults([])
      setTotal(0)
      return
    }
    setLoading(true)
    setError(undefined)
    try {
      const url = new URL('/api/search', window.location.origin)
      url.searchParams.set('q', q)
      if (app) url.searchParams.set('app', app)
      url.searchParams.set('page', String(p))
      url.searchParams.set('pageSize', String(pageSize))
      if (filters.pathIncludes) url.searchParams.set('path', filters.pathIncludes)
      if (filters.language) url.searchParams.set('lang', filters.language)

      const res = await fetch(url.pathname + url.search)
      if (!res.ok) {
        // fallback mocked results for dev
        const mock: SearchResult[] = Array.from({ length: 3 }).map((_, i) => ({
          id: `${i + 1}`,
          filePath: `src/example/Example${i + 1}.java`,
          line: 10 + i * 3,
          snippet: `public class Example${i + 1} {\n  // TODO: replace with real data\n}`,
        }))
        setResults(mock)
        setTotal(mock.length)
        return
      }
      const data = await res.json() as { total: number; results: SearchResult[] }
      setResults(data.results || [])
      setTotal(data.total || 0)
    } catch (e: any) {
      setError(e?.message ?? 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  const value = useMemo<SearchState>(() => ({
    query,
    app,
    filters,
    page,
    pageSize,
    total,
    results,
    loading,
    error,
    setQuery,
    setApp,
    setFilters,
    setPage,
    search,
  }), [query, app, filters, page, pageSize, total, results, loading, error])

  return <SearchContext.Provider value={value}>{children}</SearchContext.Provider>
}

export function useSearch() {
  const ctx = useContext(SearchContext)
  if (!ctx) throw new Error('useSearch must be used within SearchProvider')
  return ctx
}
