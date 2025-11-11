import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import { useEffect, useState } from 'react'

function Dot({ color }: { color: string }) {
  return <span aria-hidden="true" style={{ display: 'inline-block', width: 10, height: 10, borderRadius: '50%', background: color, marginRight: 8 }} />
}

export default function Header() {
  const [status, setStatus] = useState<'unknown' | 'ok' | 'down'>('unknown')
  const [msg, setMsg] = useState<string>('')
  const [embedMode, setEmbedMode] = useState<string>('')
  const [embedMsg, setEmbedMsg] = useState<string>('')
  const [lastEmbedFetch, setLastEmbedFetch] = useState<number>(0)

  useEffect(() => {
    let mounted = true
    let timer: number | null = null

    async function fetchEmbeddingInfo() {
      if (!mounted) return
      // Avoid hammering endpoint: refresh at most every 30s unless mode unknown
      const now = Date.now()
      if (embedMode && now - lastEmbedFetch < 30000) return
      try {
        const controller = new AbortController()
        const abortId = window.setTimeout(() => controller.abort(), 4000)
        const res = await fetch('/api/embedding/info', { signal: controller.signal })
        window.clearTimeout(abortId)
        if (!mounted) return
        if (res.ok) {
          const json = await res.json().catch(() => ({} as any))
          if (json && typeof json.mode === 'string') {
            setEmbedMode(String(json.mode).toUpperCase())
            setEmbedMsg('')
            setLastEmbedFetch(now)
          } else {
            setEmbedMsg('Missing mode field')
          }
        } else {
          setEmbedMsg(`Embedding info HTTP ${res.status}`)
        }
      } catch (e: any) {
        if (!mounted) return
        setEmbedMsg(e?.name === 'AbortError' ? 'Embedding info timeout' : (e?.message || 'Embedding info failed'))
      }
    }

    async function check() {
      try {
        const controller = new AbortController()
        const id = window.setTimeout(() => controller.abort(), 4000)
        const res = await fetch('/api/health', { signal: controller.signal })
        window.clearTimeout(id)
        if (!mounted) return
        if (res.ok) {
          const json = await res.json().catch(() => ({} as any))
          if (json && (json.status === 'OK' || json.status === 'Up' || json.status === 'UP')) {
            setStatus('ok')
            setMsg('')
            // Only attempt embedding mode fetch when backend healthy
            fetchEmbeddingInfo()
          } else {
            setStatus('down')
            setMsg('Unexpected health payload')
          }
        } else {
          setStatus('down')
          setMsg(`HTTP ${res.status}`)
        }
      } catch (e: any) {
        if (!mounted) return
        setStatus('down')
        setMsg(e?.name === 'AbortError' ? 'Timed out' : (e?.message || 'Fetch failed'))
      }
    }

    // initial check + poll
    check()
    timer = window.setInterval(check, 7000) as unknown as number

    return () => {
      mounted = false
      if (timer) window.clearInterval(timer)
    }
  }, [embedMode, lastEmbedFetch])

  const color = status === 'ok' ? '#2ecc71' : status === 'down' ? '#e74c3c' : '#bdc3c7'
  const label = status === 'ok' ? 'Backend: Connected' : status === 'down' ? 'Backend: No connection' : 'Backend: Checking…'

  const modeColor = embedMode === 'ONNX' ? '#8e44ad' : embedMode === 'MOCK' ? '#34495e' : '#7f8c8d'
  const modeLabel = embedMode ? `Mode: ${embedMode}` : 'Mode: —'
  const modeTitle = embedMsg || modeLabel

  return (
    <AppBar position="static" aria-label="Site header">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Firestick
        </Typography>
        <div role="status" aria-live="polite" title={msg || label} style={{ display: 'flex', alignItems: 'center', fontSize: 14, gap: 16 }}>
          <span style={{ display: 'flex', alignItems: 'center' }}>
            <Dot color={color} />
            <span>{label}</span>
          </span>
          <span style={{ display: 'flex', alignItems: 'center' }} title={modeTitle}>
            <Dot color={modeColor} />
            <span>{modeLabel}</span>
          </span>
        </div>
      </Toolbar>
    </AppBar>
  )
}
