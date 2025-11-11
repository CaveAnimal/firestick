import { useEffect, useState } from 'react'

type Props = {
  message: string
  type?: 'info' | 'success' | 'error'
  durationMs?: number
}

export default function Toast({ message, type = 'info', durationMs = 3000 }: Props) {
  const [visible, setVisible] = useState(true)
  useEffect(() => {
    const t = setTimeout(() => setVisible(false), durationMs)
    return () => clearTimeout(t)
  }, [durationMs])
  if (!visible) return null
  const bg = type === 'error' ? '#f44336' : type === 'success' ? '#4caf50' : '#333'
  return (
    <div style={{ position: 'fixed', right: 16, bottom: 16, background: bg, color: '#fff', padding: '10px 14px', borderRadius: 6, boxShadow: '0 2px 8px rgba(0,0,0,0.2)' }}>
      {message}
    </div>
  )
}
