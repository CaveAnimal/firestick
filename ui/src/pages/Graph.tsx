import GraphView from '../visual/GraphView'
import AppSelector from '../shared/AppSelector'

export default function GraphPage() {
  return (
    <div>
      <h2>Dependency Graph</h2>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 8 }}>
        <AppSelector compact />
      </div>
      <GraphView />
    </div>
  )
}
