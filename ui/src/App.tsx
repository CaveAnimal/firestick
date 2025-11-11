import { Routes, Route, Link } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import Layout from './components/layout/Layout'
import Health from './pages/Health'
const GraphPage = lazy(() => import('./pages/Graph'))
const SearchPage = lazy(() => import('./pages/Search'))
const DashboardPage = lazy(() => import('./pages/Dashboard'))
const IndexingPage = lazy(() => import('./pages/Indexing'))
const DiagnosticsPage = lazy(() => import('./pages/Diagnostics'))

function Home() {
  return (
    <div>
      <h2>Welcome to Firestick UI</h2>
      <p>Use the navigation to explore.</p>
      <p><Link to="/health">Health Check</Link></p>
    </div>
  )
}

export default function App() {
  return (
    <Layout>
      <Suspense fallback={<div style={{ padding: 12 }}>Loading…</div>}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/health" element={<Health />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/graph" element={<GraphPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/indexing" element={<IndexingPage />} />
          <Route path="/diagnostics" element={<DiagnosticsPage />} />
        </Routes>
      </Suspense>
    </Layout>
  )
}
