import { Link } from 'react-router-dom'

export default function Sidebar() {
  return (
    <aside role="navigation" aria-label="Primary navigation" style={{ padding: '1rem', borderRight: '1px solid #eee' }}>
      <div style={{ fontWeight: 600, marginBottom: '0.5rem' }}>Navigation</div>
      <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 8 }}>
        <li><Link to="/search">Search</Link></li>
        <li><Link to="/graph">Graph</Link></li>
        <li><Link to="/dashboard">Dashboard</Link></li>
        <li><Link to="/indexing">Indexing</Link></li>
        <li><Link to="/diagnostics">Diagnostics</Link></li>
      </ul>
    </aside>
  )
}
