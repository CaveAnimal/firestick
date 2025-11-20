import { Link } from 'react-router-dom'
import AppSelector from '../shared/AppSelector'

export default function HomePage() {
  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 8 }}>
        <AppSelector compact />
      </div>
      <h2>Welcome to Firestick</h2>
      <p>
        Firestick helps engineers quickly search and analyze code. Use the left navigation to get started.
      </p>

      <h3>Quick links</h3>
      <ul>
        <li><Link to="/search">Search Code</Link></li>
        <li><Link to="/graph">Dependency Graph</Link></li>
        <li><Link to="/dashboard">Dashboard & Metrics</Link></li>
      </ul>

      <h3>Welcome Tips</h3>
      <ol>
        <li>Index your code using the <em>Indexing</em> page.</li>
        <li>Use Lucene for fast keyword searches.</li>
        <li>Enable the LLM service for code analysis and insights.</li>
      </ol>
    </div>
  )
}
