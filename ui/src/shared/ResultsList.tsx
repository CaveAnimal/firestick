import ResultItem from './ResultItem'
import { SearchResult } from '../state/SearchContext'

type Props = {
  results: SearchResult[]
  onOpen?: (item: SearchResult) => void
}

export default function ResultsList({ results, onOpen }: Props) {
  if (!results || results.length === 0) return <div>No results</div>
  return (
    <div>
      {results.map(r => (
        <ResultItem key={r.id} item={r} onOpen={onOpen} />
      ))}
    </div>
  )
}
