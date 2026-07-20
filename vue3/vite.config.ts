import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';
import viteCompression from 'vite-plugin-compression';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import { nodePolyfills } from 'vite-plugin-node-polyfills';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());
  const isE2E = process.env.FASTBEE_E2E === 'true';
  return {
    base: process.env.NODE_ENV === 'production' ? '/' : '/',
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
        '@vb': path.resolve(__dirname, 'src/views/visualBigScreen/management'),
        three: path.resolve(__dirname, 'node_modules/three'),
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern',
          quietDeps: true,
          silenceDeprecations: ['import', 'global-builtin', 'color-functions'],
          additionalData: (source: string, filename: string) => {
            const normalized = filename.replace(/\\/g, '/');
            if (normalized.includes('/src/views/visualBigScreen/management/')) {
              return `@import "@/views/visualBigScreen/management/styles/common/style.scss";\n${source}`;
            }
            return source;
          },
        },
      },
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_APP_PORT) || 80,
      open: true,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_APP_SERVER_API_URL,
          changeOrigin: true,
          rewrite: (p: string) => p.replace(new RegExp('^' + env.VITE_APP_BASE_API), ''),
        },
      },
    },
    plugins: [
      vue({
        template: {
          compilerOptions: {
            isCustomElement: (tag) => tag === 'iconify-icon' || tag.startsWith('iconify-icon')
          }
        }
      }),
      nodePolyfills({
        include: ['buffer', 'process', 'util'],
        globals: {
          Buffer: true,
          global: true,
          process: true,
        },
      }),
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), 'src/assets/icons/svg')],
        symbolId: 'icon-[name]',
      }),
      viteCompression({
        verbose: true,
        disable: false,
        threshold: 10240,
        algorithm: 'gzip',
        ext: '.gz',
      }),
      AutoImport({
        resolvers: [ElementPlusResolver()],
        imports: ['vue', 'vue-router', 'pinia', 'vue-i18n'],
        dts: isE2E ? false : 'src/auto-imports.d.ts',
      }),
      Components({
        resolvers: [ElementPlusResolver({ importStyle: false })],
        dts: isE2E ? false : 'src/components.d.ts',
      }),
    ],
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: false,
      chunkSizeWarningLimit: 1500,
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true,
          pure_funcs: ['console.log'],
        },
      },
      rollupOptions: {
        external: [],
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
          manualChunks(id) {
            if (!id.includes('node_modules')) return;
            // Vue 核心 + Element Plus 合并为一个 chunk，避免循环依赖
            if (
              id.includes('/element-plus/') ||
              id.includes('/@element-plus/icons-vue/') ||
              id.includes('/node_modules/vue/') ||
              id.includes('/node_modules/@vue/') ||
              id.includes('/node_modules/vue-router/') ||
              id.includes('/node_modules/pinia/') ||
              id.includes('/node_modules/vue-i18n/')
            ) {
              return 'vendor';
            }
            // ECharts
            if (id.includes('/echarts/') || id.includes('/echarts-liquidfill/')) return 'echarts';
            // Three.js
            if (id.includes('/node_modules/three/')) return 'three';
          },
        },
      },
    },
  };
});
