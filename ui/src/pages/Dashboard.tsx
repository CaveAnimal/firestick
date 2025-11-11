import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts'
import { getDashboardSummary, type DashboardSummary } from '../api'

export default function DashboardPage() {
  const navigate = useNavigate()
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [error, setError] = useState<string>('')
  const [loading, setLoading] = useState<boolean>(true)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        setLoading(true)
        const s = await getDashboardSummary()
        if (!cancelled) setSummary(s)
      } catch (e: any) {
        if (!cancelled) setError(e?.message || 'Failed to load dashboard')
      } finally {
        if (!cancelled) setLoading(false)
      }
    })()
    return () => { cancelled = true }
  }, [])

  const data = useMemo(() => (
    summary?.chart || []
  ), [summary])

  return (
    <div>
      <h2>Analysis Dashboard</h2>
      {loading && <div role="status" aria-live="polite" style={{ marginBottom: 8 }}>Loading dashboard…</div>}
      {error && <div role="alert" style={{ color: '#b00020', marginBottom: 8 }}>{error}</div>}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: 12 }}>
        {loading && !summary && (
          <>
            {[1,2,3].map(i => (
              <div key={i} aria-hidden="true" style={{ border: '1px solid #eee', borderRadius: 6, padding: 12 }}>
                <div style={{ height: 14, width: 140, background: 'linear-gradient(90deg,#f0f0f0,#e8e8e8,#f0f0f0)', backgroundSize: '200% 100%', animation: 'sh 1.2s ease-in-out infinite' }} />
                <div style={{ marginTop: 12, height: 120, background: 'linear-gradient(90deg,#f9f9f9,#f1f1f1,#f9f9f9)', backgroundSize: '200% 100%', animation: 'sh 1.2s ease-in-out infinite' }} />
              </div>
            ))}
          </>
        )}
        <div style={{ border: '1px solid #eee', borderRadius: 6, padding: 12 }}>
          <h3 style={{ margin: '0 0 8px 0' }}>Project Stats</h3>
          <ul style={{ margin: 0 }}>
            <li>Total Files: {summary?.stats.totalFiles ?? '—'}</li>
            <li>Total Classes: {summary?.stats.totalClasses ?? '—'}</li>
            <li>Total Methods: {summary?.stats.totalMethods ?? '—'}</li>
            <li>Hotspots: {summary?.stats.hotspotCount ?? '—'}</li>
          </ul>
        </div>
        <div style={{ border: '1px solid #eee', borderRadius: 6, padding: 12 }}>
          <h3 style={{ margin: '0 0 8px 0' }}>Complexity</h3>
          <div style={{ width: '100%', height: 220 }}>
            <ResponsiveContainer>
              <BarChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="value" fill="#1976d2" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div style={{ border: '1px solid #eee', borderRadius: 6, padding: 12 }}>
          <h3 style={{ margin: '0 0 8px 0' }}>Hotspots</h3>
          <ol>
            {(summary?.hotspots || []).map((h, i) => (
              <li key={i}>
                <button
                  onClick={() => navigate(`/search?q=${encodeURIComponent(h.name)}`)}
                  title="Search for this hotspot"
                  style={{
                    border: 'none',
                    background: 'transparent',
                    color: '#1976d2',
                    textDecoration: 'underline',
                    cursor: 'pointer',
                    padding: 0,
                    font: 'inherit',
                  }}
                >
                  {h.name}
                </button>{' '}
                ({h.count})
              </li>
            ))}
          </ol>
        </div>
      </div>
      <style>{`@keyframes sh{0%{background-position:0 0}100%{background-position:200% 0}}`}</style>
    </div>
  )
}
