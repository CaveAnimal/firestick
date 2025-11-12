# Firestick UI (React + Vite)

This is the frontend UI for Firestick, built with React 18, Vite, and TypeScript.

## Prerequisites
- Node.js 18+ and npm 9+
- Backend Spring Boot app running on http://localhost:8080

## Quick Start
```powershell
# from repository root
cd ui
npm install
npm run dev
```

The dev server runs on http://localhost:5173 and proxies API calls to http://localhost:8080.

## Scripts
- `npm run dev` — start Vite dev server
- `npm run build` — production build
- `npm run preview` — preview the production build locally

## Proxy
Vite dev server is configured to proxy `/health` and `/api/*` to the Spring Boot backend at http://localhost:8080.

## Routing
React Router v6 is configured with sample routes:
- `/` — Welcome page
- `/health` — Calls backend `/health` and displays the result

## Layout
Basic MUI layout with an AppBar header and placeholder sidebar/footer. See `src/components/layout`.

## Wireframes
See `WIREFRAMES.md` for textual wireframes and page structure.
