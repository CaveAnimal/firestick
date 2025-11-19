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
          <h4 style={{ marginBottom: 8 }}>Open Suggested Files</h4>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {suggestions.map((suggestion, idx) => (
              <article key={`${suggestion.filePath || 'suggestion'}-${idx}`} style={{ border: '1px solid #e1e1e1', borderRadius: 6, padding: 12, background: 'white' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
                  <div>
                    <strong style={{ fontSize: 15 }}>{suggestion.title || `Suggestion ${idx + 1}`}</strong>
                    <p style={{ margin: '6px 0 0', color: '#555' }}>
                      {suggestion.summary || suggestion.filePath}
                      {suggestion.startLine && suggestion.endLine && (
                        <span>{` (lines ${suggestion.startLine}-${suggestion.endLine})`}</span>
                      )}
                    </p>
                    <p style={{ margin: '4px 0 0', fontSize: 12, color: '#666' }}>{suggestion.filePath}</p>
                  </div>
                  {typeof suggestion.score === 'number' && (
                    <span style={{ fontSize: 12, color: '#666' }}>Relevance {(suggestion.score * 100).toFixed(0)}%</span>
                  )}
                </div>
                {suggestion.snippet && (
                  <pre style={{ background: '#f5f5f5', padding: 10, borderRadius: 4, margin: '10px 0 0', fontSize: 13, whiteSpace: 'pre-wrap' }}>{suggestion.snippet}</pre>
                )}
                {suggestion.filePath && onOpen && (
                  <div style={{ marginTop: 10 }}>
                    <button onClick={() => onOpen(suggestion)} style={{ padding: '6px 10px', borderRadius: 4, border: '1px solid #ccc', cursor: 'pointer' }}>
                      Open suggested file
                    </button>
                  </div>
                )}
              </article>
            ))}
          </div>
        </div>
      )}
    </section>
  )
}
