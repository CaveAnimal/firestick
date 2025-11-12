import { SearchFilters } from '../state/SearchContext'

type Props = {
  filters: SearchFilters
  onChange: (f: Partial<SearchFilters>) => void
}

export default function Filters({ filters, onChange }: Props) {
  return (
    <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
      <input
        type="text"
        value={filters.pathIncludes ?? ''}
        placeholder="Path includes..."
        onChange={(e) => onChange({ pathIncludes: e.target.value })}
        style={{ flex: 1, padding: '6px 8px' }}
      />
      <select
        value={filters.language ?? ''}
        onChange={(e) => onChange({ language: e.target.value || undefined })}
        style={{ padding: '6px 8px' }}
      >
        <option value="">All Languages</option>
        <option value="java">Java</option>
        <option value="xml">XML</option>
        <option value="properties">Properties</option>
      </select>
    </div>
  )
}
