import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 用 @ 代表 src 目录（Vue 官方脚手架里的约定）
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      // 前端请求 /api/xxx 时，转发到后端 http://localhost:8081/xxx
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
      // 前端请求 /ws 时，转发到后端 ws://localhost:8081
      '/ws': {
        target: 'ws://localhost:8081',
        ws: true,
      },
    },
  },
})
