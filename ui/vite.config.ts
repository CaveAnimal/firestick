import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
// Prefer explicit Vite env var; fallback to BACKEND_URL for older setups, otherwise 8081
// NOTE: in Vite the env is read at module import time. Keep backward-compatible read here.
const env = (import.meta as any).env || {}
const apiTarget = (env.VITE_API_URL as string) || (env.BACKEND_URL as string) || 'http://localhost:8081'

export default defineConfig({

  // Use the same returned options object expected by defineConfig
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          react: ['react', 'react-dom'],
          graph: ['cytoscape'],
          monaco: ['monaco-editor'],
          recharts: ['recharts'],
        },
      },
    },
  },
  server: {
    port: 5173,
    proxy: (() => {
      // Allow configuring backend used by Vite dev server through environment variable.
      // Use VITE_API_URL for consistency with client side code; fallback to 8081 for ONNX dev.
  const target = apiTarget
      return {
        '/health': {
          target,
          changeOrigin: true,
          secure: false,
        },
        '/api': {
          target,
          changeOrigin: true,
          secure: false,
        },
      }
    })(),
  }
});
