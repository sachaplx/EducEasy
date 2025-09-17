import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 8080,
    proxy: {
      '/auth':   { target: 'http://localhost:4700', changeOrigin: true },
      '/ecoles': { target: 'http://localhost:4700', changeOrigin: true },
      '/classes':{ target: 'http://localhost:4700', changeOrigin: true },
      '/eleves': { target: 'http://localhost:4700', changeOrigin: true },
      '/ping':   { target: 'http://localhost:4700', changeOrigin: true }
    }
  }
})