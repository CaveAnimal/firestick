# Firestick Design System

## Color Palette
- Primary: #2196f3 (blue)
- Secondary: #f50057 (pink)
- Success: #4caf50 (green)
- Warning: #ff9800 (orange)
- Error: #f44336 (red)
- Background: #fafafa (light gray)
- Surface: #ffffff (white)
- Text Primary: rgba(0,0,0,0.87)
- Text Secondary: rgba(0,0,0,0.54)
- Text Disabled: rgba(0,0,0,0.38)

## Typography
- Font Family: "Roboto", "Helvetica", "Arial", sans-serif
- h1: 2.5rem, 500 weight
- h2: 2rem, 500 weight
- h3: 1.75rem, 500 weight
- Body: 1rem, 400 weight

## Spacing System
- xs: 4px
- sm: 8px
- md: 16px
- lg: 24px
- xl: 32px

## UI Component Library
- [Material-UI (MUI)](https://mui.com/) for React

## Design Tokens (theme.js)
```js
export const theme = {
  colors: {
    primary: '#2196f3',
    secondary: '#f50057',
    success: '#4caf50',
    warning: '#ff9800',
    error: '#f44336',
    background: '#fafafa',
    surface: '#ffffff',
    text: {
      primary: 'rgba(0, 0, 0, 0.87)',
      secondary: 'rgba(0, 0, 0, 0.54)',
      disabled: 'rgba(0, 0, 0, 0.38)',
    },
  },
  spacing: {
    xs: '4px',
    sm: '8px',
    md: '16px',
    lg: '24px',
    xl: '32px',
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontSize: '2.5rem', fontWeight: 500 },
    h2: { fontSize: '2rem', fontWeight: 500 },
    h3: { fontSize: '1.75rem', fontWeight: 500 },
    body: { fontSize: '1rem', fontWeight: 400 },
  },
};
```

## Reference
- Use this design system for all UI components and styles.
- Update as needed for accessibility and branding.
