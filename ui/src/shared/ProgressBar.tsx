type Props = { value: number }
export default function ProgressBar({ value }: Props) {
  const v = Math.max(0, Math.min(100, Math.round(value)))
  return (
    <div style={{ height: 12, background: '#eee', borderRadius: 6, overflow: 'hidden' }}>
      <div style={{ width: `${v}%`, background: '#1976d2', height: '100%' }} />
    </div>
  )
}
