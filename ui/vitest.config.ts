import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./vitest.setup.ts'],
    exclude: [
      'node_modules/**',
      'dist/**',
      // Exclude Playwright end-to-end specs handled by @playwright/test
      'tests/**',
      'e2e/**'
    ]
  }
})
