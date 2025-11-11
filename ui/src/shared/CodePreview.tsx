import 'prismjs/themes/prism.css'
import Prism from 'prismjs'
import 'prismjs/components/prism-java'
import { useEffect, useRef } from 'react'

type Props = {
  code: string
  language?: 'java' | 'js' | 'ts' | 'json'
}

export default function CodePreview({ code, language = 'java' }: Props) {
  const ref = useRef<HTMLElement>(null)
  useEffect(() => {
    if (ref.current) Prism.highlightElement(ref.current)
  }, [code, language])

  return (
    <pre style={{ margin: 0 }}>
      <code ref={ref} className={`language-${language}`}>
        {code}
      </code>
    </pre>
  )
}
