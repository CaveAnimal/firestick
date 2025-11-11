import { useCallback, useEffect, useRef, useState } from 'react'
import Editor, { OnMount } from '@monaco-editor/react'

type Props = {
  path: string
  content: string
  language?: 'java' | 'javascript' | 'typescript' | 'json'
  highlightLine?: number
}

export default function CodeViewer({ path, content, language = 'java', highlightLine }: Props) {
  const decorationsRef = useRef<string[]>([])
  const [copied, setCopied] = useState(false)

  const onMount: OnMount = useCallback((editor: Parameters<OnMount>[0], monaco: Parameters<OnMount>[1]) => {
    // Basic read-only editor config
  ;(editor as any).updateOptions({ readOnly: true, minimap: { enabled: false } })
    if (highlightLine && highlightLine > 0) {
      decorationsRef.current = (editor as any).deltaDecorations([], [
        {
          range: new (monaco as any).Range(highlightLine, 1, highlightLine, 1),
          options: {
            isWholeLine: true,
            className: 'codeviewer-line-highlight',
            linesDecorationsClassName: 'codeviewer-line-deco'
          }
        }
      ])
      // Scroll into view
      ;(editor as any).revealLineInCenter(highlightLine)
    }
  }, [highlightLine])

  useEffect(() => {
    // styles for line highlight
    const style = document.createElement('style')
    style.innerHTML = `
      .codeviewer-line-highlight { background: rgba(255, 235, 59, 0.25) !important; }
      .codeviewer-line-deco { border-left: 3px solid #fbc02d; }
      .codeviewer-toolbar { position: absolute; top: 8px; right: 8px; display: flex; gap: 6px; z-index: 10; }
      .codeviewer-btn { font-size: 12px; padding: 4px 8px; border-radius: 4px; border: 1px solid #ddd; background: #fff; cursor: pointer; }
      .codeviewer-btn:hover { background: #f6f6f6; }
    `
    document.head.appendChild(style)
    return () => { document.head.removeChild(style) }
  }, [])

  const doCopy = async () => {
    try {
      await navigator.clipboard?.writeText(content)
      setCopied(true)
      setTimeout(() => setCopied(false), 1500)
    } catch {
      // no-op
    }
  }

  const doDownload = () => {
    try {
      const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      const filename = (path?.split('/')?.pop() || 'code.txt') as string
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      a.remove()
      URL.revokeObjectURL(url)
    } catch {
      // ignore
    }
  }

  const doCopyPath = async () => {
    try {
      await navigator.clipboard?.writeText(path)
      setCopied(true)
      setTimeout(() => setCopied(false), 1200)
    } catch {
      // ignore
    }
  }

  return (
    <div style={{ position: 'relative', height: '100%' }}>
      <div className="codeviewer-toolbar">
        <button className="codeviewer-btn" onClick={doCopy} title="Copy code to clipboard" aria-label="Copy code">{copied ? 'Copied' : 'Copy'}</button>
        <button className="codeviewer-btn" onClick={doCopyPath} title={`Copy path: ${path}`} aria-label="Copy file path">Copy Path</button>
        <button className="codeviewer-btn" onClick={doDownload} title="Download file" aria-label="Download file">Download</button>
      </div>
      <Editor
        height="100%"
        defaultLanguage={language}
        defaultValue={content}
        path={path}
        onMount={onMount}
        options={{ readOnly: true, scrollBeyondLastLine: false }}
      />
    </div>
  )
}
