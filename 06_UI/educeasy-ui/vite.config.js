import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 8080,
    proxy: {
      '/auth':   { target: 'http://localhost:4700', changeOrigin: true },
      '/schools': { target: 'http://localhost:4700', changeOrigin: true },
      '/classrooms':{ target: 'http://localhost:4700', changeOrigin: true },
      '/pupils': { target: 'http://localhost:4700', changeOrigin: true },
      '/ping':   { target: 'http://localhost:4700', changeOrigin: true },
      '/whoami': { target: 'http://localhost:4700', changeOrigin: true }
    }
  }
})