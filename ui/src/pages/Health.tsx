import { useEffect, useState } from 'react'

export default function Health() {
  const [status, setStatus] = useState<string>('Loading...')

  useEffect(() => {
    fetch('/api/health')
      .then(async (res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const json = await res.json()
        setStatus(typeof json?.status === 'string' ? json.status : JSON.stringify(json))
      })
      .catch((err) => setStatus(`Error: ${err.message}`))
  }, [])

  return (
    <div>
      <h2>Backend Health</h2>
      <pre>{status}</pre>
    </div>
  )
}
