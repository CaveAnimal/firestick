type Props = { value: number, color?: string }
export default function ProgressBar({ value, color = '#1976d2' }: Props) {
  const v = Math.max(0, Math.min(100, Math.round(value)))
  const barColor = v < 100 ? '#2ecc40' : '#1976d2';
  return (
    <div style={{ height: 12, background: '#eee', borderRadius: 6, overflow: 'hidden' }}>
      <div style={{ width: `${v}%`, background: barColor, height: '100%' }} />
    </div>
  )
}
