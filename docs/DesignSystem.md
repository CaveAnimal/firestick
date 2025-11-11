# Design System (UI)

This document captures our UI design tokens and usage examples. These tokens are defined as CSS variables in `ui/src/styles.css`.

## Tokens

- Spacing:
  - `--space-1: 4px`
  - `--space-2: 8px`
  - `--space-3: 12px`
  - `--space-4: 16px`
  - `--space-5: 24px`

- Color palette:
  - `--color-bg: #ffffff`
  - `--color-bg-muted: #f7f7f7`
  - `--color-surface: #fafafa`
  - `--color-border: #e0e0e0`
  - `--color-text: #1a1a1a`
  - `--color-text-muted: #5f6368`
  - `--color-primary: #1976d2` (blue 700)
  - `--color-primary-contrast: #ffffff`
  - `--color-secondary: #009688` (teal 500)
  - `--color-accent: #ff9800` (orange 500)
  - `--color-danger: #b00020`

- Typography:
  - `--font-sans`: system stack
  - `--font-size-xs: 12px`
  - `--font-size-sm: 14px`
  - `--font-size-md: 16px`
  - `--font-size-lg: 20px`
  - `--font-size-xl: 24px`

## Utilities

Available classes in `styles.css`:

- `.card`: bordered, rounded surface
- `.muted`: secondary text color
- `.btn`, `.btn-primary`, `.btn-danger`: buttons
- `.pill`: rounded label with small type

## Usage examples

```tsx
<button className="btn btn-primary">Run</button>

<div className="card" style={{ padding: 'var(--space-3)' }}>
  <h3 style={{ fontSize: 'var(--font-size-lg)' }}>Widget</h3>
  <p className="muted">Some info…</p>
</div>
```

## Notes

- These tokens complement MUI components; prefer variables when styling custom elements.
- Keep tokens minimal and stable; adjust only as part of a deliberate design update.
