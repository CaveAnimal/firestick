// Simple in-flight deduping and abort support for GET requests
type GetOptions = {
  dedupe?: 'share' | 'cancel-previous'
  signal?: AbortSignal
}

const inflight = new Map<string, { controller: AbortController; promise: Promise<unknown> }>()

export async function apiGet<T>(path: string, opts: GetOptions = {}): Promise<T> {
  const key = `GET ${path}`
  const mode = opts.dedupe || 'share'

  if (mode === 'share' && inflight.has(key)) {
    return inflight.get(key)!.promise as Promise<T>
  }

  if (mode === 'cancel-previous' && inflight.has(key)) {
    try { inflight.get(key)!.controller.abort() } catch { /* ignore */ }
    inflight.delete(key)
  }

  const controller = new AbortController()
  const signal = opts.signal || controller.signal
  const p = (async () => {
    try {
      const res = await fetch(path, { signal })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      return res.json() as Promise<T>
    } finally {
      inflight.delete(key)
    }
  })()
  inflight.set(key, { controller, promise: p })
  return p as Promise<T>
}

export type IndexingRunResponse = {
  jobId?: string | number
  status?: string
}

export type IndexingOptions = {
  root?: string
  excludeDirs?: string[]
  excludeGlobs?: string[]
}

export async function startIndexing(opts: IndexingOptions = {}, app?: string): Promise<IndexingRunResponse> {
  // backend uses GET /api/indexing/run with optional query params
  const params = new URLSearchParams()
  if (opts.root && opts.root.trim()) params.set('root', opts.root.trim())
  if (opts.excludeDirs && opts.excludeDirs.length > 0) params.set('excludeDirs', opts.excludeDirs.join(','))
  if (opts.excludeGlobs && opts.excludeGlobs.length > 0) params.set('excludeGlobs', opts.excludeGlobs.join(','))
  if (app && app.trim()) params.set('app', app.trim())
  const qs = params.toString()
  const url = `/api/indexing/run${qs ? `?${qs}` : ''}`
  return apiGet<IndexingRunResponse>(url, { dedupe: 'cancel-previous' })
}

export type IndexingJob = {
  id?: string | number
  status?: string
  startTime?: string
  endTime?: string
  progress?: number
  stats?: Record<string, number>
}

export async function getLatestJob(app?: string): Promise<IndexingJob> {
  const url = app ? `/api/indexing/jobs/latest?app=${encodeURIComponent(app)}` : '/api/indexing/jobs/latest'
  return apiGet<IndexingJob>(url, { dedupe: 'share' })
}

export async function getJobById(id: string | number, app?: string): Promise<IndexingJob> {
  const url = app ? `/api/indexing/jobs/${id}?app=${encodeURIComponent(app)}` : `/api/indexing/jobs/${id}`
  return apiGet<IndexingJob>(url, { dedupe: 'share' })
}

export async function getRecentJobs(limit = 10, app?: string): Promise<IndexingJob[]> {
  const base = `/api/indexing/jobs?limit=${limit}`
  const url = app ? `${base}&app=${encodeURIComponent(app)}` : base
  return apiGet<IndexingJob[]>(url, { dedupe: 'share' })
}

export type FileContent = { path: string; content: string }
export async function getFileContent(path: string): Promise<FileContent> {
  const q = new URLSearchParams({ path }).toString()
  return apiGet<FileContent>(`/api/code/file?${q}`, { dedupe: 'cancel-previous' })
}

// Graph API types
export type GraphNode = { id: string; label: string; type: 'F' | 'C' | 'M' | 'I' }
export type GraphEdge = { source: string; target: string; type?: string }
export type GraphResponse = { nodes: GraphNode[]; edges: GraphEdge[]; metadata?: Record<string, unknown> }

export async function getBasicGraph(limit?: number): Promise<GraphResponse> {
  const url = `/api/graph/basic${typeof limit === 'number' ? `?limit=${limit}` : ''}`
  return apiGet<GraphResponse>(url, { dedupe: 'share' })
}

export type EnrichedGraphOptions = {
  limit?: number
  include?: Array<'containment' | 'imports' | 'calls' | 'inheritance'>
  nodeTypes?: Array<'F' | 'C' | 'M' | 'I'>
}
export async function getEnrichedGraph(opts: EnrichedGraphOptions = {}): Promise<GraphResponse> {
  const params = new URLSearchParams()
  if (typeof opts.limit === 'number') params.set('limit', String(opts.limit))
  if (opts.include && opts.include.length > 0) params.set('include', opts.include.join(','))
  if (opts.nodeTypes && opts.nodeTypes.length > 0) params.set('nodeTypes', opts.nodeTypes.join(','))
  const url = `/api/graph/enriched${params.toString() ? `?${params.toString()}` : ''}`
  return apiGet<GraphResponse>(url, { dedupe: 'share' })
}

// Dashboard API types
export type DashboardSummary = {
  stats: { totalFiles: number; totalClasses: number; totalMethods: number; hotspotCount: number }
  chart: Array<{ name: string; value: number }>
  hotspots: Array<{ name: string; count: number }>
  recentJobs: Array<{ id: number | string; status?: string; startedAt?: string; endedAt?: string }>
}
export async function getDashboardSummary(app?: string): Promise<DashboardSummary> {
  const url = app ? `/api/dashboard/summary?app=${encodeURIComponent(app)}` : '/api/dashboard/summary'
  return apiGet<DashboardSummary>(url, { dedupe: 'share' })
}
