import cytoscape, { Core } from 'cytoscape'
// Minimap (Navigator) plugin
// Types are ambient via a local d.ts to avoid TS errors
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import navigator from 'cytoscape-navigator'
cytoscape.use(navigator)
import type { EventObject, NodeSingular, EdgeSingular } from 'cytoscape'
import { useEffect, useRef, useState, type ChangeEvent } from 'react'
import { getEnrichedGraph, type GraphNode, type GraphEdge } from '../api'

export default function GraphView() {
  const containerRef = useRef<HTMLDivElement | null>(null)
  const minimapRef = useRef<HTMLDivElement | null>(null)
  const navigatorRef = useRef<any | null>(null)
  const [cy, setCy] = useState<Core | null>(null)
  const [edgeInfo, setEdgeInfo] = useState<string>('')
  const [filter, setFilter] = useState<string>('')
  const [showFiles, setShowFiles] = useState<boolean>(true)
  const [showClasses, setShowClasses] = useState<boolean>(true)
  const [showMethods, setShowMethods] = useState<boolean>(true)
  const [showImports, setShowImports] = useState<boolean>(false)
  const [includeCalls, setIncludeCalls] = useState<boolean>(true)
  const [includeImports, setIncludeImports] = useState<boolean>(true)
  // Edge-type visibility toggles (client-side)
  const [showEdgeContains, setShowEdgeContains] = useState<boolean>(true)
  const [showEdgeDeclares, setShowEdgeDeclares] = useState<boolean>(true)
  const [showEdgeImports, setShowEdgeImports] = useState<boolean>(true)
  const [showEdgeCalls, setShowEdgeCalls] = useState<boolean>(true)
  const [showEdgeInherits, setShowEdgeInherits] = useState<boolean>(true)
  const lastHoverTsRef = useRef<number>(0)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string>('')
  // Layout selection
  const [layoutName, setLayoutName] = useState<'breadthfirst' | 'cose' | 'circle' | 'concentric' | 'grid'>('breadthfirst')
  // Help overlay
  const [showHelp, setShowHelp] = useState<boolean>(false)
  // Minimap
  const [showMinimap, setShowMinimap] = useState<boolean>(true)
  // Shortest path tool
  const [pathMode, setPathMode] = useState<boolean>(false)
  const [pathStart, setPathStart] = useState<string>('')
  const [pathEnd, setPathEnd] = useState<string>('')
  const [pathInfo, setPathInfo] = useState<string>('')

  useEffect(() => {
    if (!containerRef.current) return
    // Load saved preferences on first mount
    try {
      const saved = JSON.parse(localStorage.getItem('graph:prefs') || 'null')
      if (saved) {
        if (typeof saved.showFiles === 'boolean') setShowFiles(saved.showFiles)
        if (typeof saved.showClasses === 'boolean') setShowClasses(saved.showClasses)
        if (typeof saved.showMethods === 'boolean') setShowMethods(saved.showMethods)
        if (typeof saved.showImports === 'boolean') setShowImports(saved.showImports)
        if (typeof saved.includeCalls === 'boolean') setIncludeCalls(saved.includeCalls)
        if (typeof saved.includeImports === 'boolean') setIncludeImports(saved.includeImports)
        if (typeof saved.showEdgeContains === 'boolean') setShowEdgeContains(saved.showEdgeContains)
        if (typeof saved.showEdgeDeclares === 'boolean') setShowEdgeDeclares(saved.showEdgeDeclares)
        if (typeof saved.showEdgeImports === 'boolean') setShowEdgeImports(saved.showEdgeImports)
        if (typeof saved.showEdgeCalls === 'boolean') setShowEdgeCalls(saved.showEdgeCalls)
        if (typeof saved.showEdgeInherits === 'boolean') setShowEdgeInherits(saved.showEdgeInherits)
        if (typeof saved.layoutName === 'string') setLayoutName(saved.layoutName)
        if (typeof saved.showMinimap === 'boolean') setShowMinimap(saved.showMinimap)
      }
    } catch { /* ignore */ }
    const cyInstance = cytoscape({
      container: containerRef.current,
      layout: { name: 'breadthfirst', directed: true, padding: 10 },
      style: [
        // Base styles
        { selector: 'node', style: { 'background-color': '#1976d2', label: 'data(label)', color: '#fff', 'text-valign': 'center', 'text-halign': 'center', 'font-size': 10, shape: 'round-rectangle' } },
        { selector: 'edge', style: { width: 2, 'line-color': '#bdbdbd', 'target-arrow-color': '#bdbdbd', 'target-arrow-shape': 'triangle', 'curve-style': 'bezier' } },
        { selector: ':selected', style: { 'background-color': '#ff9800', 'line-color': '#ff9800', 'target-arrow-color': '#ff9800' } },

        // Node type styling
        { selector: 'node[_t = "F"]', style: { 'background-color': '#546e7a', shape: 'round-rectangle' } }, // File
        { selector: 'node[_t = "C"]', style: { 'background-color': '#1976d2', shape: 'round-rectangle' } }, // Class
        { selector: 'node[_t = "M"]', style: { 'background-color': '#0288d1', shape: 'diamond' } }, // Method
        { selector: 'node[_t = "I"]', style: { 'background-color': '#009688', shape: 'ellipse' } }, // Import/Package

        // Edge type styling
        { selector: 'edge[_t = "contains"], edge[_t = "containment"]', style: { 'line-color': '#bdbdbd', 'target-arrow-color': '#bdbdbd', width: 1.5 } },
        { selector: 'edge[_t = "declares"]', style: { 'line-color': '#90a4ae', 'target-arrow-color': '#90a4ae', 'line-style': 'dashed', width: 1.5 } },
        { selector: 'edge[_t = "imports"]', style: { 'line-color': '#43a047', 'target-arrow-color': '#43a047', 'line-style': 'dashed' } },
        { selector: 'edge[_t = "calls"]', style: { 'line-color': '#ef6c00', 'target-arrow-color': '#ef6c00', width: 3 } },
        { selector: 'edge[_t = "inherits"], edge[_t = "inheritance"]', style: { 'line-color': '#7e57c2', 'target-arrow-color': '#7e57c2', width: 2 } },
      ],
      elements: [],
    })
    const load = async () => {
      try {
        setLoading(true)
        const graph = await getEnrichedGraph({
          limit: 500,
          include: [
            'containment',
            includeImports ? 'imports' : undefined,
            includeCalls ? 'calls' : undefined,
            'inheritance'
          ].filter(Boolean) as any,
          nodeTypes: [
            showFiles ? 'F' : undefined,
            showClasses ? 'C' : undefined,
            showMethods ? 'M' : undefined,
            showImports ? 'I' : undefined,
          ].filter(Boolean) as any
        })
        const elems = [] as Array<{ data: any }>
        // de-dup ids while assembling
        const seen = new Set<string>()
        graph.nodes.forEach((n: GraphNode) => {
          if (!seen.has(n.id)) {
            elems.push({ data: { id: n.id, label: n.label, _t: n.type } })
            seen.add(n.id)
          }
        })
        graph.edges.forEach((e: GraphEdge) => {
          // Cytoscape edges implicitly get an id if not provided
          elems.push({ data: { source: e.source, target: e.target, _t: e.type || 'rel' } })
        })
        cyInstance.add(elems)
        cyInstance.layout({ name: layoutName as any, directed: true, padding: 10 }).run()
        cyInstance.fit()
      } catch (err) {
        setError((err as any)?.message || 'Failed to load graph')
        // Fallback sample
        cyInstance.add([
          { data: { id: 'A', label: 'com.example.A' } },
          { data: { id: 'B', label: 'com.example.B' } },
          { data: { id: 'C', label: 'com.example.C' } },
          { data: { source: 'A', target: 'B' } },
          { data: { source: 'B', target: 'C' } },
          { data: { source: 'A', target: 'C' } },
        ])
        cyInstance.layout({ name: layoutName as any, directed: true, padding: 10 }).run()
        cyInstance.fit()
      } finally { setLoading(false) }
    }
    // initial load
    load()
    // initialize minimap if enabled
    if (showMinimap && minimapRef.current) {
      try {
        navigatorRef.current = (cyInstance as any).navigator({
          container: minimapRef.current,
          // Keep it light to avoid perf issues
          viewLiveFramerate: 0, // render on demand
          thumbnailEventFramerate: 30,
          dblClickDelay: 200,
          removeCustomContainer: false,
          rerenderDelay: 100,
        })
      } catch { /* ignore runtime plugin errors */ }
    }
    // Edge hover details (throttled for perf)
    cyInstance.on('mouseover', 'edge', (evt: EventObject) => {
      const now = performance.now()
      if (now - (lastHoverTsRef.current || 0) < 50) return
      lastHoverTsRef.current = now
      const e = evt.target
      const src = e.source().data('label') || e.source().id()
      const tgt = e.target().data('label') || e.target().id()
      const t = (e.data('_t') || '').toString()
      const tPretty = t ? ({ calls: 'calls', imports: 'imports', inherits: 'inherits', inheritance: 'inherits', contains: 'contains', containment: 'contains', declares: 'declares' } as Record<string, string>)[t] || t : ''
      setEdgeInfo(tPretty ? `${src} —[${tPretty}]→ ${tgt}` : `${src} → ${tgt}`)
    })
    cyInstance.on('mouseout', 'edge', () => setEdgeInfo(''))

    // Interactive node selection (highlight + focus neighborhood)
    cyInstance.on('tap', 'node', (evt: EventObject) => {
      const node = evt.target
      if (pathMode) {
        // Shortest path selection mode
        const nid = node.id()
        if (!pathStart) {
          setPathStart(nid)
          setPathInfo(`Start: ${node.data('label') || nid}`)
        } else if (!pathEnd || nid !== pathStart) {
          setPathEnd(nid)
          setPathInfo(prev => `${prev ? prev + ' \u2192 ' : ''}End: ${node.data('label') || nid}`)
          // Compute path when both are chosen
          try {
            cyInstance.batch(() => {
              cyInstance.elements().removeClass('path')
              const startNode = cyInstance.$id(pathStart)
              const endNode = cyInstance.$id(nid)
              if (startNode.nonempty() && endNode.nonempty()) {
                const dijkstra = cyInstance.elements().dijkstra({ root: startNode, weight: () => 1, directed: true })
                const path = dijkstra.pathTo(endNode)
                if (path && path.nonempty()) {
                  path.addClass('path')
                  setPathInfo(`Path length: ${path.length} (nodes+edges)`)
                  cyInstance.fit(path, 50)
                } else {
                  setPathInfo('No path found')
                }
              }
            })
          } catch { /* ignore compute errors */ }
        }
        return
      }
      // Default behavior: focus neighborhood
      cyInstance.batch(() => {
        cyInstance.elements().removeClass('faded selected')
        const neighborhood = node.closedNeighborhood()
        cyInstance.elements().not(neighborhood).addClass('faded')
        node.addClass('selected')
      })
      const nb = node.closedNeighborhood()
      if (nb && nb.length > 0) cyInstance.fit(nb, 40)
    })
    cyInstance.on('tap', (evt: EventObject) => {
      if (evt.target === cyInstance) {
        cyInstance.elements().removeClass('faded selected')
      }
    })

    setCy(cyInstance)
    return () => {
      try { if (navigatorRef.current && navigatorRef.current.destroy) navigatorRef.current.destroy() } catch { /* ignore */ }
      cyInstance.destroy()
    }
  }, [])

  const zoom = (delta: number) => {
    if (!cy) return
    const z = cy.zoom() + delta
    cy.zoom(Math.max(0.1, Math.min(3, z)))
  }
  const reset = () => {
    if (!cy) return
    cy.fit()
  }

  const reload = async () => {
    if (!cy) return
    setError('')
    setLoading(true)
    cy.batch(() => { cy.elements().remove() })
    try {
      const graph = await getEnrichedGraph({
        limit: 500,
        include: [
          'containment',
          includeImports ? 'imports' : undefined,
          includeCalls ? 'calls' : undefined,
          'inheritance'
        ].filter(Boolean) as any,
        nodeTypes: [
          showFiles ? 'F' : undefined,
          showClasses ? 'C' : undefined,
          showMethods ? 'M' : undefined,
          showImports ? 'I' : undefined,
        ].filter(Boolean) as any
      })
      const elems = [] as Array<{ data: any }>
      const seen = new Set<string>()
      graph.nodes.forEach((n: GraphNode) => {
        if (!seen.has(n.id)) {
          elems.push({ data: { id: n.id, label: n.label, _t: n.type } })
          seen.add(n.id)
        }
      })
      graph.edges.forEach((e: GraphEdge) => {
        elems.push({ data: { source: e.source, target: e.target, _t: e.type || 'rel' } })
      })
      cy.add(elems)
      cy.layout({ name: layoutName as any, directed: true, padding: 10 }).run()
      cy.fit()
      applyFilter()
      // Rebuild minimap when reloading
      try {
        if (navigatorRef.current && navigatorRef.current.destroy) navigatorRef.current.destroy()
        if (showMinimap && minimapRef.current) {
          navigatorRef.current = (cy as any).navigator({
            container: minimapRef.current,
            viewLiveFramerate: 0,
            thumbnailEventFramerate: 30,
            removeCustomContainer: false,
            rerenderDelay: 100,
          })
        }
      } catch { /* ignore */ }
    } catch (err) {
      setError((err as any)?.message || 'Failed to load graph')
    } finally {
      setLoading(false)
    }
  }

  const exportPng = () => {
    if (!cy) return
    const png = cy.png({ full: true, scale: 2, bg: '#ffffff' })
    const a = document.createElement('a')
    a.href = png
    a.download = 'graph.png'
    a.click()
  }

  const clearPath = () => {
    if (!cy) return
    setPathStart('')
    setPathEnd('')
    setPathInfo('')
    cy.batch(() => { cy.elements().removeClass('path') })
  }

  const exportSvg = () => {
    if (!cy) return
    // cytoscape-svg extension augments the core with svg(); cast to any to avoid type errors when extension types are absent
    const svg = (cy as any).svg({ full: true, scale: 1, bg: '#ffffff' })
    const blob = new Blob([svg], { type: 'image/svg+xml;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'graph.svg'
    a.click()
    URL.revokeObjectURL(url)
  }

  const applyFilter = () => {
    if (!cy) return
    const q = filter.trim().toLowerCase()
    const isEdgeTypeVisible = (tRaw: unknown) => {
      const t = (tRaw || '').toString()
      if (t === 'calls') return showEdgeCalls
      if (t === 'imports') return showEdgeImports
      if (t === 'inherits' || t === 'inheritance') return showEdgeInherits
      if (t === 'declares') return showEdgeDeclares
      if (t === 'contains' || t === 'containment' || t === '' || t === 'rel') return showEdgeContains
      return true
    }
    cy.batch(() => {
      if (!q) {
        cy.elements().removeClass('hidden')
      } else {
        cy.nodes().forEach((n: NodeSingular) => {
          const label = `${n.data('label') || n.id()}`.toLowerCase()
          if (label.includes(q)) n.removeClass('hidden')
          else n.addClass('hidden')
        })
        // Hide edges connected to hidden nodes
        cy.edges().forEach((e: EdgeSingular) => {
          const visible = !e.source().hasClass('hidden') && !e.target().hasClass('hidden') && isEdgeTypeVisible(e.data('_t'))
          e[visible ? 'removeClass' : 'addClass']('hidden')
        })
      }

      // Type-based visibility toggles
      cy.nodes().forEach((n: NodeSingular) => {
        const t = (n.data('_t') || '').toString()
        const visibleByType = (t === 'F' && showFiles) || (t === 'C' && showClasses) || (t === 'M' && showMethods) || (t === 'I' && showImports) || (!t)
        if (!visibleByType) n.addClass('hidden')
      })
      // Then recalc edge visibility
      cy.edges().forEach((e: EdgeSingular) => {
        const visible = !e.source().hasClass('hidden') && !e.target().hasClass('hidden') && isEdgeTypeVisible(e.data('_t'))
        e[visible ? 'removeClass' : 'addClass']('hidden')
      })
    })
  }

  // Hide labels at very low zoom to improve performance
  useEffect(() => {
    if (!cy) return
    const handler = () => {
      const z = cy.zoom()
      const showLabels = z >= 0.4
      cy.batch(() => {
        cy.nodes().forEach((n: NodeSingular) => {
          n.style('label', showLabels ? (n.data('label') || n.id()) : '')
        })
      })
    }
    handler()
    cy.on('zoom', handler)
    return () => { cy.off('zoom', handler) }
  }, [cy])

  // Persist preferences when relevant states change
  useEffect(() => {
    const prefs = {
      showFiles, showClasses, showMethods, showImports,
      includeCalls, includeImports,
      showEdgeContains, showEdgeDeclares, showEdgeImports, showEdgeCalls, showEdgeInherits,
      layoutName,
      showMinimap,
    }
    try { localStorage.setItem('graph:prefs', JSON.stringify(prefs)) } catch { /* ignore */ }
  }, [showFiles, showClasses, showMethods, showImports, includeCalls, includeImports, showEdgeContains, showEdgeDeclares, showEdgeImports, showEdgeCalls, showEdgeInherits, layoutName, showMinimap])

  // Toggle minimap on state change
  useEffect(() => {
    if (!cy) return
    try {
      if (navigatorRef.current && navigatorRef.current.destroy) {
        navigatorRef.current.destroy()
        navigatorRef.current = null
      }
      if (showMinimap && minimapRef.current) {
        navigatorRef.current = (cy as any).navigator({
          container: minimapRef.current,
          viewLiveFramerate: 0,
          thumbnailEventFramerate: 30,
          removeCustomContainer: false,
          rerenderDelay: 100,
        })
      }
    } catch { /* ignore */ }
  }, [showMinimap, cy])

  // Keyboard shortcuts: +/- zoom, R reset, F focus selected, / focus filter input
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.target && (e.target as HTMLElement).tagName === 'INPUT') return
      if (e.key === '+' || e.key === '=') { zoom(0.1); e.preventDefault() }
      else if (e.key === '-') { zoom(-0.1); e.preventDefault() }
      else if (e.key.toLowerCase() === 'r') { reset(); e.preventDefault() }
      else if (e.key.toLowerCase() === 'f') {
        if (cy) {
          const sel = cy.$(':selected')
          if (sel && sel.length > 0) cy.fit(sel, 40)
        }
        e.preventDefault()
      } else if (e.key === '/') {
        const input = document.getElementById('graph-filter') as HTMLInputElement | null
        if (input) { input.focus(); e.preventDefault() }
      }
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [cy])

  return (
    <div>
      {loading && <div role="status" aria-live="polite" style={{ marginBottom: 8 }}>Loading graph…</div>}
      {error && <div role="alert" style={{ color: '#b00020', marginBottom: 8 }}>{error}</div>}
      <div style={{ marginBottom: 8, display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
        <button onClick={() => zoom(0.1)}>Zoom In</button>
        <button onClick={() => zoom(-0.1)}>Zoom Out</button>
        <button onClick={reset}>Reset</button>
        <button onClick={reload}>Reload</button>
        <button onClick={exportPng}>Export PNG</button>
        <button onClick={exportSvg}>Export SVG</button>
        <div style={{ width: 1, height: 18, background: '#ddd' }} />
        <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <input type="checkbox" checked={pathMode} onChange={e => { setPathMode(e.target.checked); if (!e.target.checked) clearPath() }} />
          Shortest Path
        </label>
        <button disabled={!pathStart && !pathEnd} onClick={clearPath}>Clear Path</button>
        <button onClick={() => {
          if (!cy) return
          const payload = {
            nodes: cy.nodes().map(n => ({ id: n.id(), label: n.data('label'), type: n.data('_t') })),
            edges: cy.edges().map(e => ({ source: e.source().id(), target: e.target().id(), type: e.data('_t') })),
          }
          const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' })
          const url = URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = 'graph.json'
          a.click()
          URL.revokeObjectURL(url)
        }}>Export JSON</button>
        <button onClick={() => setShowHelp(true)}>Shortcuts</button>
        <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
          <input type="checkbox" checked={showMinimap} onChange={e => setShowMinimap(e.target.checked)} /> Minimap
        </label>
        <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <span>Layout</span>
          <select value={layoutName} onChange={e => setLayoutName(e.target.value as any)}>
            <option value="breadthfirst">breadthfirst</option>
            <option value="cose">cose</option>
            <option value="circle">circle</option>
            <option value="concentric">concentric</option>
            <option value="grid">grid</option>
          </select>
        </label>
        <div style={{ marginLeft: 'auto', display: 'flex', gap: 8, alignItems: 'center' }}>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showFiles} onChange={e => { setShowFiles(e.target.checked); applyFilter() }} /> Files
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showClasses} onChange={e => { setShowClasses(e.target.checked); applyFilter() }} /> Classes
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showMethods} onChange={e => { setShowMethods(e.target.checked); applyFilter() }} /> Methods
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showImports} onChange={e => { setShowImports(e.target.checked); applyFilter() }} /> Imports
          </label>
        </div>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <label htmlFor="graph-filter">Filter</label>
          <input id="graph-filter" value={filter} onChange={(e: ChangeEvent<HTMLInputElement>) => setFilter(e.target.value)} placeholder="Type to filter..." />
          <button onClick={applyFilter}>Apply</button>
          <button onClick={() => { setFilter(''); applyFilter() }}>Clear</button>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
            <span>Preset</span>
            <select onChange={e => {
              const preset = e.target.value
              if (preset === 'structure') {
                setIncludeCalls(false)
                setIncludeImports(false)
                setShowEdgeCalls(false)
                setShowEdgeImports(false)
                setShowEdgeContains(true)
                setShowEdgeDeclares(true)
                setShowEdgeInherits(true)
                reload()
              } else if (preset === 'calls') {
                setIncludeCalls(true)
                setIncludeImports(false)
                setShowEdgeCalls(true)
                setShowEdgeImports(false)
                setShowEdgeContains(false)
                setShowEdgeDeclares(false)
                setShowEdgeInherits(false)
                reload()
              } else if (preset === 'all') {
                setIncludeCalls(true)
                setIncludeImports(true)
                setShowEdgeCalls(true)
                setShowEdgeImports(true)
                setShowEdgeContains(true)
                setShowEdgeDeclares(true)
                setShowEdgeInherits(true)
                reload()
              }
            }} defaultValue="custom">
              <option value="custom">custom</option>
              <option value="structure">structure-only</option>
              <option value="calls">calls-only</option>
              <option value="all">all</option>
            </select>
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4, marginLeft: 8 }}>
            <input type="checkbox" checked={includeCalls} onChange={e => setIncludeCalls(e.target.checked)} /> Calls
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={includeImports} onChange={e => setIncludeImports(e.target.checked)} /> Imports
          </label>
          <div style={{ width: 1, height: 18, background: '#ddd', margin: '0 6px' }} />
          <span style={{ color: '#777' }}>Edges:</span>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showEdgeContains} onChange={e => { setShowEdgeContains(e.target.checked); applyFilter() }} /> Contains
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showEdgeDeclares} onChange={e => { setShowEdgeDeclares(e.target.checked); applyFilter() }} /> Declares
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showEdgeImports} onChange={e => { setShowEdgeImports(e.target.checked); applyFilter() }} /> Imports
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showEdgeCalls} onChange={e => { setShowEdgeCalls(e.target.checked); applyFilter() }} /> Calls
          </label>
          <label style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
            <input type="checkbox" checked={showEdgeInherits} onChange={e => { setShowEdgeInherits(e.target.checked); applyFilter() }} /> Inherits
          </label>
        </div>
      </div>
      {/* Legend */}
      <div aria-label="Legend" style={{ display: 'flex', flexWrap: 'wrap', gap: 16, alignItems: 'center', fontSize: 12, color: '#555', marginBottom: 8 }}>
        {/* Node types */}
        <span role="button" tabIndex={0} aria-pressed={showFiles} onClick={() => { setShowFiles(!showFiles); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowFiles(!showFiles); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showFiles ? 1 : 0.5 }}>
          <span title="File" style={{ width: 14, height: 10, borderRadius: 4, background: '#546e7a', display: 'inline-block' }} />
          <span>File</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showClasses} onClick={() => { setShowClasses(!showClasses); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowClasses(!showClasses); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showClasses ? 1 : 0.5 }}>
          <span title="Class" style={{ width: 14, height: 10, borderRadius: 4, background: '#1976d2', display: 'inline-block' }} />
          <span>Class</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showMethods} onClick={() => { setShowMethods(!showMethods); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowMethods(!showMethods); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showMethods ? 1 : 0.5 }}>
          <span title="Method" style={{ width: 0, height: 0, borderLeft: '7px solid transparent', borderRight: '7px solid transparent', borderBottom: '12px solid #0288d1', display: 'inline-block' }} />
          <span>Method</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showImports} onClick={() => { setShowImports(!showImports); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowImports(!showImports); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showImports ? 1 : 0.5 }}>
          <span title="Import" style={{ width: 12, height: 12, borderRadius: '50%', background: '#009688', display: 'inline-block' }} />
          <span>Import</span>
        </span>

        {/* Edge types */}
        <span role="button" tabIndex={0} aria-pressed={showEdgeContains} onClick={() => { setShowEdgeContains(!showEdgeContains); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowEdgeContains(!showEdgeContains); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showEdgeContains ? 1 : 0.5 }}>
          <span title="Contains" style={{ width: 26, height: 0, borderTop: '2px solid #bdbdbd', position: 'relative', display: 'inline-block' }}>
            <span style={{ position: 'absolute', right: -4, top: -4, width: 0, height: 0, borderLeft: '6px solid #bdbdbd', borderTop: '4px solid transparent', borderBottom: '4px solid transparent' }} />
          </span>
          <span>Contains</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showEdgeDeclares} onClick={() => { setShowEdgeDeclares(!showEdgeDeclares); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowEdgeDeclares(!showEdgeDeclares); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showEdgeDeclares ? 1 : 0.5 }}>
          <span title="Declares" style={{ width: 26, height: 0, borderTop: '2px dashed #90a4ae', position: 'relative', display: 'inline-block' }}>
            <span style={{ position: 'absolute', right: -4, top: -4, width: 0, height: 0, borderLeft: '6px solid #90a4ae', borderTop: '4px solid transparent', borderBottom: '4px solid transparent' }} />
          </span>
          <span>Declares</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showEdgeImports} onClick={() => { setShowEdgeImports(!showEdgeImports); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowEdgeImports(!showEdgeImports); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showEdgeImports ? 1 : 0.5 }}>
          <span title="Imports" style={{ width: 26, height: 0, borderTop: '2px dashed #43a047', position: 'relative', display: 'inline-block' }}>
            <span style={{ position: 'absolute', right: -4, top: -4, width: 0, height: 0, borderLeft: '6px solid #43a047', borderTop: '4px solid transparent', borderBottom: '4px solid transparent' }} />
          </span>
          <span>Imports</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showEdgeCalls} onClick={() => { setShowEdgeCalls(!showEdgeCalls); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowEdgeCalls(!showEdgeCalls); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showEdgeCalls ? 1 : 0.5 }}>
          <span title="Calls" style={{ width: 26, height: 0, borderTop: '3px solid #ef6c00', position: 'relative', display: 'inline-block' }}>
            <span style={{ position: 'absolute', right: -4, top: -5, width: 0, height: 0, borderLeft: '6px solid #ef6c00', borderTop: '4px solid transparent', borderBottom: '4px solid transparent' }} />
          </span>
          <span>Calls</span>
        </span>
        <span role="button" tabIndex={0} aria-pressed={showEdgeInherits} onClick={() => { setShowEdgeInherits(!showEdgeInherits); applyFilter() }} onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setShowEdgeInherits(!showEdgeInherits); applyFilter() } }} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', opacity: showEdgeInherits ? 1 : 0.5 }}>
          <span title="Inherits" style={{ width: 26, height: 0, borderTop: '2px solid #7e57c2', position: 'relative', display: 'inline-block' }}>
            <span style={{ position: 'absolute', right: -4, top: -4, width: 0, height: 0, borderLeft: '6px solid #7e57c2', borderTop: '4px solid transparent', borderBottom: '4px solid transparent' }} />
          </span>
          <span>Inherits</span>
        </span>

        {/* Interaction states */}
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <span style={{ width: 12, height: 12, borderRadius: '50%', background: '#ff9800', display: 'inline-block' }} />
          <span>Selected</span>
        </span>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <span style={{ width: 12, height: 12, borderRadius: '50%', background: '#ccc', display: 'inline-block', opacity: 0.6 }} />
          <span>Faded</span>
        </span>
      </div>
  {edgeInfo && <div aria-live="polite" style={{ marginBottom: 8, fontSize: 12, color: '#555' }}>Edge: {edgeInfo}</div>}
  {pathInfo && <div aria-live="polite" style={{ marginBottom: 8, fontSize: 12, color: '#0d47a1' }}>{pathInfo}</div>}
      <div style={{ position: 'relative' }}>
        <div ref={containerRef} style={{ border: '1px solid #eee', height: 420, borderRadius: 6 }} />
        {/* Minimap container (Navigator renders inside) */}
        <div aria-label="Minimap" style={{ position: 'absolute', right: 8, bottom: 8, width: 160, height: 110, background: '#fff', border: '1px solid #ddd', borderRadius: 4, boxShadow: '0 1px 4px rgba(0,0,0,0.08)', overflow: 'hidden', display: showMinimap ? 'block' : 'none' }}>
          <div ref={minimapRef} style={{ width: '100%', height: '100%' }} />
        </div>
      </div>
      {showHelp && (
        <div role="dialog" aria-modal="true" aria-label="Keyboard Shortcuts" style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center' }} onClick={() => setShowHelp(false)}>
          <div style={{ background: '#fff', padding: 16, borderRadius: 8, minWidth: 360 }} onClick={e => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
              <h3 style={{ margin: 0 }}>Keyboard Shortcuts</h3>
              <button onClick={() => setShowHelp(false)} aria-label="Close">×</button>
            </div>
            <ul style={{ margin: 0, paddingLeft: 16 }}>
              <li>+ / - : Zoom in / out</li>
              <li>R : Reset view</li>
              <li>F : Focus selection</li>
              <li>/ : Focus filter input</li>
              <li>? : Open this help</li>
            </ul>
          </div>
        </div>
      )}
      <style>{`
        .faded { opacity: 0.2; }
        .hidden { display: none; }
        .selected { outline: 2px solid #ff9800; }
        .path { outline: 2px solid #1b5e20; line-color: #1b5e20; target-arrow-color: #1b5e20; background-color: #1b5e20; }
      `}</style>
    </div>
  )
}
