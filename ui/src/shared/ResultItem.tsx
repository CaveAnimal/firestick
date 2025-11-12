import CodePreview from './CodePreview'
import { useState } from 'react'
import { SearchResult } from '../state/SearchContext'

type Props = {
  item: SearchResult
  onOpen?: (item: SearchResult) => void
}

export default function ResultItem({ item, onOpen }: Props) {
  const [copied, setCopied] = useState(false)
  const copyPath = async () => {
    try {
      await navigator.clipboard?.writeText(item.filePath)
      setCopied(true)
      setTimeout(() => setCopied(false), 1200)
    } catch {
      // ignore
    }
  }
  return (
    <div style={{ border: '1px solid #eee', borderRadius: 6, marginBottom: 8 }}>
      <div style={{ padding: '6px 8px', background: '#fafafa', fontSize: 12, color: '#555', display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 8 }}>
        <span title={item.filePath}>{item.filePath}:{item.line}</span>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <button onClick={copyPath} style={{ fontSize: 12 }} aria-label={`Copy path ${item.filePath}`}>{copied ? 'Copied' : 'Copy Path'}</button>
          {onOpen && (
            <button onClick={() => onOpen(item)} style={{ fontSize: 12 }}>Open</button>
          )}
        </span>
      </div>
      <div style={{ padding: '8px' }}>
        <CodePreview code={item.snippet} language="java" />
      </div>
    </div>
  )
}
