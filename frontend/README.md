# Firestick Frontend (React)

This directory is the workspace for the Firestick React 18 frontend.

## Stack
- React 18 (with Vite)
- TypeScript
- Material-UI (MUI)
- ESLint & Prettier

## Setup
1. Install Node.js 18+
2. In this directory, run:
   ```bash
   npm install
   npm run dev
   ```
3. The app will be available at http://localhost:5173 (default Vite port)

## Integration
- The frontend is designed to communicate with the backend at http://localhost:8080
- Update API URLs in the code as needed for your environment

## Development
- Use the design system in `../docs/DESIGN_SYSTEM.md`
- Refer to wireframes in `../docs/UI_WIREFRAMES.md`

## Scripts
- `npm run dev` — Start development server
- `npm run build` — Build for production
- `npm run lint` — Lint code
- `npm run format` — Format code with Prettier
