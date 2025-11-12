type Props = {
  page: number
  pageSize: number
  total: number
  onPageChange: (p: number) => void
}

export default function Pagination({ page, pageSize, total, onPageChange }: Props) {
  const pages = Math.max(1, Math.ceil(total / pageSize))
  const prevDisabled = page <= 1
  const nextDisabled = page >= pages
  return (
    <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginTop: 12 }}>
      <button disabled={prevDisabled} onClick={() => onPageChange(page - 1)}>Prev</button>
      <span>Page {page} / {pages}</span>
      <button disabled={nextDisabled} onClick={() => onPageChange(page + 1)}>Next</button>
    </div>
  )
}
