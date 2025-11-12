export default function Footer() {
  return (
    <footer style={{ padding: '1rem', borderTop: '1px solid #eee', textAlign: 'center' }}>
      <small>© {new Date().getFullYear()} Firestick · <a href="mailto:feedback@example.com" aria-label="Send feedback">Send feedback</a></small>
    </footer>
  )
}
