import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import process from 'process';

function resolve(dir) {
  return path.join(process.cwd(), dir);
}

export default defineConfig({
  base: '/',
  esbuild: {
    drop: process.env.NODE_ENV === 'production' ? ['console', 'debugger'] : [],
  },
  build: {
    outDir: 'sunos',
    assetsDir: 'static',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('echarts') || id.includes('zrender') || id.includes('element-plus')) {
            return 'vendor';
          }
        },
      },
    },
  },
  plugins: [vue()],
  resolve: {
    extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    alias: {
      '@': resolve('src'),
      '@elink/shared': path.resolve(__dirname, '../packages/shared/src'),
      localStorage: resolve('./src/utils/localStorageUtil.js'),
    },
  },
  css: {
    devSourcemap: false,
  },
  server: {
    host: '0.0.0.0',
    port: 9000,
    open: false,
    proxy: {
      '/proxy': {
        target: process.env.VITE_PROXY_TARGET || 'http://localhost:5000',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/proxy/, ''),
        ws: true,
      },
    },
    cors: true,
  },
  define: {
    __VUE_OPTIONS_API__: true,
    __VUE_PROD_DEVTOOLS__: false,
    __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false,
  },
});
