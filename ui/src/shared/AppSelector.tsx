import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { useSearch } from '../state/SearchContext'

export default function AppSelector({ compact = false }: { compact?: boolean }) {
  const { app, setApp, search } = useSearch()
  const [apps, setApps] = useState<string[] | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const loc = useLocation()

  useEffect(() => {
    let active = true
    let retryTimer: number | undefined
    const MAX_RETRIES = 3
    const INITIAL_RETRY_DELAY_MS = 1000
    let attempt = 0
    ;(async () => {
      try {
        setLoading(true)
        const res = await fetch('/api/indexing/apps')
        if (!res.ok) throw new Error('Failed to fetch apps')
        const data = await res.json()
        const list: string[] = data?.apps ?? []
        if (!active) return
        setApps(list)
        if (list.length > 0 && !list.includes(app)) {
          setApp(list[0])
          // If on Search page, also trigger search
          if (loc.pathname.startsWith('/search')) {
            try { await search({ page: 1 }) } catch { /* ignore */ }
          }
        }
      } catch (e: any) {
        if (!active) return
        setError(e?.message ?? 'Unknown error')
        // Automatic retry with exponential backoff
        if (attempt < MAX_RETRIES) {
          attempt += 1
          const delay = INITIAL_RETRY_DELAY_MS * Math.pow(2, attempt - 1)
          retryTimer = window.setTimeout(() => {
            // Only retry if component still mounted
            if (!active) return
            setError(`Retrying (${attempt}/${MAX_RETRIES})...`)
            ;(async () => {
              try { setLoading(true); const r2 = await fetch('/api/indexing/apps'); if (!r2.ok) throw new Error('Failed to fetch apps'); const d2 = await r2.json(); if (!active) return; setApps(d2?.apps ?? []); setError(null); } catch (err:any) { setError(err?.message ?? 'Unknown error'); }
              finally { if (active) setLoading(false) }
            })()
          }, delay)
        }
        setApps([])
      } finally {
        if (!active) return
        setLoading(false)
      }
    })()
    return () => { active = false }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const onChange = (v: string) => {
    setApp(v)
    // Notify current page when app changes
    try { window.dispatchEvent(new CustomEvent('appChanged', { detail: { app: v } })) } catch { /* ignore */ }
    if (loc.pathname.startsWith('/search')) {
      search({ page: 1 })
    }
  }

  const manualRetry = () => {
    setError(null)
    setApps(null)
    setLoading(true)
    // Re-run the effect logic by calling fetch directly
    ;(async () => {
      try {
        const res = await fetch('/api/indexing/apps')
        if (!res.ok) throw new Error('Failed to fetch apps')
        const data = await res.json()
        const list: string[] = data?.apps ?? []
        setApps(list)
        if (list.length > 0 && !list.includes(app)) {
          setApp(list[0])
        }
        setError(null)
      } catch (e:any) {
        setError(e?.message ?? 'Unknown error')
        setApps([])
      } finally {
        setLoading(false)
      }
    })()
  }

  return (
    <label style={{ fontSize: 14, display: 'inline-flex', alignItems: 'center' }}>
      {!compact && 'App:'}
      <select value={app} onChange={e => onChange(e.target.value)} disabled={loading} style={{ marginLeft: 8 }}>
        {loading && <option>Loading apps…</option>}
        {!loading && apps && apps.length === 0 && <option value="default">default</option>}
        {!loading && apps && apps.map(a => <option key={a} value={a}>{a}</option>)}
      </select>
      {error && (
        <span style={{ color: '#e74c3c', marginLeft: 8 }}>
          {error}
          <button onClick={manualRetry} style={{ marginLeft: 8 }}>Try again</button>
        </span>
      )}
    </label>
  )
}
