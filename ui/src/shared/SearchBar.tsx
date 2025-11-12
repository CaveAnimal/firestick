type Props = {
  value: string
  onChange: (v: string) => void
  onSubmit: () => void
}

export default function SearchBar({ value, onChange, onSubmit }: Props) {
  return (
    <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
      <input
        type="text"
        value={value}
        placeholder="Search query..."
        onChange={(e) => onChange(e.target.value)}
        style={{ flex: 1, padding: '8px 10px' }}
      />
      <button onClick={onSubmit}>Search</button>
    </div>
  )
}
