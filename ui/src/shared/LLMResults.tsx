import { useMemo } from 'react'

export type LLMInsight = {
  title?: string
  description?: string
  reasoning?: string
  score?: number
}

export type LLMSuggestedFile = {
  title?: string
  summary?: string
  snippet?: string
  filePath?: string
  startLine?: number
  endLine?: number
  score?: number
}

type Props = {
  insights: LLMInsight[]
  suggestions: LLMSuggestedFile[]
  loading: boolean
  error?: string | null
  onOpen?: (result: LLMSuggestedFile) => void
}

export default function LLMResults({ insights, suggestions, loading, error, onOpen }: Props) {
  const hasInsights = insights && insights.length > 0
  const hasSuggestions = suggestions && suggestions.length > 0

  const groupedSuggestions = useMemo(() => {
    const groups: Record<string, LLMSuggestedFile[]> = {}
    if (suggestions) {
      suggestions.forEach(s => {
        const key = s.filePath || 'Unknown File'
        if (!groups[key]) groups[key] = []
        groups[key].push(s)
      })
    }
    return groups
  }, [suggestions])

  return (
    <section style={{ marginTop: 24, padding: 16, border: '1px solid #ddd', borderRadius: 8, background: '#fafafa' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
        <h3 style={{ margin: 0 }}>LLM Insights</h3>
        {loading && <span style={{ fontSize: 13, color: '#555' }}>LLM is analyzing...</span>}
      </div>
      {error && <div style={{ color: '#d63031', marginBottom: 12 }}>LLM search failed: {error}</div>}
      {loading ? (
        <div>Gathering semantic insights from the local model...</div>
      ) : hasInsights ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {insights.map((result, idx) => (
            <article key={`${result.title || 'insight'}-${idx}`} style={{ border: '1px solid #e1e1e1', borderRadius: 6, padding: 12, background: 'white' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
                <div>
                  <strong style={{ fontSize: 15 }}>{result.title || `Insight ${idx + 1}`}</strong>
                  {result.description && <p style={{ margin: '6px 0 0', color: '#555' }}>{result.description}</p>}
                </div>
                {typeof result.score === 'number' && (
                  <span style={{ fontSize: 12, color: '#666' }}>Confidence {(result.score * 100).toFixed(0)}%</span>
                )}
              </div>
              {result.reasoning && (
                <p style={{ margin: '10px 0 0', whiteSpace: 'pre-wrap', fontFamily: 'inherit' }}>{result.reasoning}</p>
              )}
            </article>
          ))}
        </div>
      ) : (
        <div>No LLM insights yet. Submit a query with the LLM button to see semantic analysis.</div>
      )}

      {hasSuggestions && (
        <div style={{ marginTop: 24 }}>
          <h4 style={{ marginBottom: 8 }}>Suggested Files</h4>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {Object.entries(groupedSuggestions).map(([filePath, items]) => (
              <article key={filePath} style={{ border: '1px solid #e1e1e1', borderRadius: 6, padding: 12, background: 'white' }}>
                <div style={{ marginBottom: 12, paddingBottom: 8, borderBottom: '1px solid #eee' }}>
                  <strong style={{ fontSize: 15, color: '#2c3e50' }}>{filePath}</strong>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                  {items.map((suggestion, idx) => (
                    <div key={`${filePath}-${idx}`} style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
                        <div>
                          <strong style={{ fontSize: 14, color: '#444' }}>{suggestion.title || `Reference ${idx + 1}`}</strong>
                          <p style={{ margin: '4px 0 0', color: '#555', fontSize: 13 }}>
                            {suggestion.summary}
                            {suggestion.startLine && suggestion.endLine && (
                              <span style={{ fontFamily: 'monospace', marginLeft: 6, background: '#f5f5f5', padding: '2px 4px', borderRadius: 3 }}>
                                lines {suggestion.startLine}-{suggestion.endLine}
                              </span>
                            )}
                          </p>
                        </div>
                        {typeof suggestion.score === 'number' && (
                          <span style={{ fontSize: 12, color: '#666', whiteSpace: 'nowrap' }}>Relevance {(suggestion.score * 100).toFixed(0)}%</span>
                        )}
                      </div>
                      {suggestion.snippet && (
                        <details>
                          <summary style={{ cursor: 'pointer', color: '#0066cc', fontSize: '12px', userSelect: 'none' }}>View code snippet</summary>
                          <pre style={{ background: '#f8f9fa', padding: 10, borderRadius: 4, marginTop: 6, fontSize: 12, whiteSpace: 'pre-wrap', border: '1px solid #eee' }}>{suggestion.snippet}</pre>
                        </details>
                      )}
                    </div>
                  ))}
                </div>
              </article>
            ))}
          </div>
        </div>
      )}
    </section>
  )
}
