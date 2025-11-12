import { useCallback, useEffect, useMemo, useState } from 'react'

type CheckResult<T = any> = {
  ok: boolean
  status?: number
  ms?: number
  error?: string
  data?: T
}

export default function Diagnostics() {
  const [health, setHealth] = useState<CheckResult>({ ok: false })
  const [embed, setEmbed] = useState<CheckResult>({ ok: false })
  const [running, setRunning] = useState(false)

  const runChecks = useCallback(async () => {
    setRunning(true)
    try {
      // Health check
      {
        const t0 = performance.now()
        try {
          const controller = new AbortController()
          const id = window.setTimeout(() => controller.abort(), 5000)
          const res = await fetch('/api/health', { signal: controller.signal })
          window.clearTimeout(id)
          const ms = Math.max(0, Math.round(performance.now() - t0))
          if (res.ok) {
            const json = await res.json().catch(() => ({}))
            setHealth({ ok: true, status: res.status, ms, data: json })
          } else {
            setHealth({ ok: false, status: res.status, ms, error: `HTTP ${res.status}` })
          }
        } catch (e: any) {
          const ms = Math.max(0, Math.round(performance.now() - t0))
          setHealth({ ok: false, ms, error: e?.name === 'AbortError' ? 'Timed out' : (e?.message || 'Fetch failed') })
        }
      }

      // Embedding info
      {
        const t0 = performance.now()
        try {
          const controller = new AbortController()
          const id = window.setTimeout(() => controller.abort(), 5000)
          const res = await fetch('/api/embedding/info', { signal: controller.signal })
          window.clearTimeout(id)
          const ms = Math.max(0, Math.round(performance.now() - t0))
          if (res.ok) {
            const json = await res.json().catch(() => ({}))
            setEmbed({ ok: true, status: res.status, ms, data: json })
          } else {
            setEmbed({ ok: false, status: res.status, ms, error: `HTTP ${res.status}` })
          }
        } catch (e: any) {
          const ms = Math.max(0, Math.round(performance.now() - t0))
          setEmbed({ ok: false, ms, error: e?.name === 'AbortError' ? 'Timed out' : (e?.message || 'Fetch failed') })
        }
      }
    } finally {
      setRunning(false)
    }
  }, [])

  useEffect(() => { runChecks() }, [runChecks])

  const mode = useMemo(() => {
    const m = (embed.data?.mode || '').toString().toUpperCase()
    return m === 'ONNX' || m === 'MOCK' ? m : ''
  }, [embed.data])

  const activeProfiles = useMemo<string[]>(() => {
    const arr = Array.isArray(embed.data?.activeProfiles) ? embed.data.activeProfiles : []
    return arr.map((s: any) => String(s))
  }, [embed.data])

  const pill = (text: string, color: string, title?: string) => (
    <span title={title || text} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, padding: '2px 8px', borderRadius: 999, border: '1px solid #eee', background: '#fafafa' }}>
      <span aria-hidden="true" style={{ width: 8, height: 8, borderRadius: '50%', background: color }} />
      <span>{text}</span>
    </span>
  )

  return (
    <div>
      <h2>Diagnostics</h2>
      <p>Quick connectivity and configuration checks.</p>

      <div style={{ display: 'grid', gap: 12 }}>
        <section style={{ border: '1px solid #eee', borderRadius: 8, padding: 12 }}>
          <h3 style={{ marginTop: 0 }}>Backend Health</h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
            {pill(health.ok ? 'Connected' : 'No connection', health.ok ? '#2ecc71' : '#e74c3c', health.error)}
            <span title="Round-trip time">{typeof health.ms === 'number' ? `${health.ms} ms` : ''}</span>
            <button onClick={runChecks} disabled={running} aria-busy={running}>{running ? 'Checking…' : 'Re-run checks'}</button>
          </div>
          <pre style={{ background: '#f9f9f9', padding: 8, marginTop: 8 }}>{JSON.stringify(health.data ?? (health.error ? { error: health.error } : {}), null, 2)}</pre>
        </section>

        <section style={{ border: '1px solid #eee', borderRadius: 8, padding: 12 }}>
          <h3 style={{ marginTop: 0 }}>Embedding</h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
            {pill(`Mode: ${mode || '—'}`, mode === 'ONNX' ? '#8e44ad' : mode === 'MOCK' ? '#34495e' : '#7f8c8d', embed.error)}
            {activeProfiles.length > 0 && pill(`Profiles: ${activeProfiles.join(', ')}`, '#3498db')}
            {embed.data?.modelPath && pill(`Model: ${embed.data.modelPath}`, '#95a5a6')}
            {embed.data?.tokenizerPath && pill(`Tokenizer: ${embed.data.tokenizerPath}`, '#95a5a6')}
            <span title="Round-trip time">{typeof embed.ms === 'number' ? `${embed.ms} ms` : ''}</span>
            <button onClick={runChecks} disabled={running} aria-busy={running}>{running ? 'Checking…' : 'Re-run checks'}</button>
          </div>
          <pre style={{ background: '#f9f9f9', padding: 8, marginTop: 8 }}>{JSON.stringify(embed.data ?? (embed.error ? { error: embed.error } : {}), null, 2)}</pre>
          {!mode && (
            <div style={{ marginTop: 8, color: '#c0392b' }}>
              Tip: If mode is blank, your backend may not include the new <code>/api/embedding/info</code> endpoint. Restart from source after pulling updates, or rebuild the jar, then refresh.
            </div>
          )}
        </section>
      </div>
    </div>
  )
}

