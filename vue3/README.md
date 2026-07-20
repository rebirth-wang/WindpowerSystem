# 项目概述

FastBee IoT 平台前端项目，基于 Vue 3 + TypeScript + Vite 重构升级。原项目基于 Vue 2，本次重构全面采用现代前端技术栈，提升开发体验和应用性能。

- 演示地址：https://iot.fastbee.cn
- 重构目标：Vue 2 + JavaScript → Vue 3 + TypeScript + Vite

# 技术栈

| 模块     | 原技术栈 (Vue 2)    | 新技术栈 (Vue 3)                   |
| -------- | ------------------- | ---------------------------------- |
| 核心框架 | Vue 2.6.12          | Vue 3.4+                           |
| 构建工具 | Vue CLI 4 + Webpack | Vite 6                             |
| UI组件库 | Element UI 2.15     | Element Plus 2.9                   |
| 状态管理 | Vuex 3.6            | Pinia 2                            |
| 路由     | Vue Router 3.4      | Vue Router 4                       |
| 开发语言 | JavaScript          | TypeScript                         |
| 组合方式 | Options API         | Composition API + `<script setup>` |
| 国际化   | vue-i18n 8          | vue-i18n 9                         |

# 快速开始

### 环境要求

- Node.js 18+
- npm 9+ 或 yarn 1.22+ 或 pnpm 8+

### 安装与运行

```
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd vue3

# 安装依赖
npm install

# 开发环境运行
npm run dev

# 生产环境构建
npm run build

# 预览构建结果
npm run preview

# 代码格式化
npm run format
```

### 可用脚本

| 命令                 | 说明           |
| -------------------- | -------------- |
| npm run dev          | 启动开发服务器 |
| npm run build        | 构建生产版本   |
| npm run preview      | 预览构建结果   |
| npm run format       | 格式化代码     |
| npm run format:check | 检查代码格式   |

# 核心配置

### Vite 配置

```
// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import viteCompression from 'vite-plugin-compression'

export default defineConfig({
  base: '/',  // 二级目录部署时修改为 '/子路径/'
  plugins: [
    vue(),
    AutoImport({ resolvers: [ElementPlusResolver()] }),
    Components({ resolvers: [ElementPlusResolver()] }),
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
      symbolId: 'icon-[dir]-[name]'
    }),
    viteCompression({
      algorithm: 'gzip',
      threshold: 10240
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 环境变量

```
# .env.development
VITE_APP_BASE_API = '/api'
VITE_APP_TITLE = 'FastBee IoT (开发环境)'
VITE_APP_VERSION = '1.0.0'

# .env.production
VITE_APP_BASE_API = 'https://api.fastbee.cn'
VITE_APP_TITLE = 'FastBee IoT'
VITE_APP_VERSION = '1.0.0'
```

### 路由配置

```
typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory('/'),  // 二级目录部署时修改
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

export default router
```

### 入口文件

```
typescript
// main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import i18n from './lang'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(i18n)

// 全局组件注册
// 全局属性
app.config.globalProperties.$echarts = echarts
app.config.globalProperties.$mqttTool = mqttTool

app.mount('#app')
```

# 核心改造示例

### 1. Options API → Composition API

```
vue
<!-- Vue 2 -->
<script>
export default {
  data() { return { count: 0 } },
  methods: { increment() { this.count++ } }
}
</script>

<!-- Vue 3 -->
<script setup lang="ts">
import { ref } from 'vue'
const count = ref(0)
const increment = () => count.value++
</script>
```

### 2. Vuex → Pinia

```
typescript
// stores/modules/user.ts
import { defineStore } from 'pinia'
import { getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())

  function setToken(value: string) {
    token.value = value
  }

  return { token, setToken }
})

// 组件中使用
import { useUserStore } from '@/stores/modules/user'
const userStore = useUserStore()
```

### 3. Element UI → Element Plus

```
vue
<!-- Element UI -->
<el-dialog :visible.sync="dialogVisible">
  <span slot="footer">...</span>
</el-dialog>

<!-- Element Plus -->
<el-dialog v-model="dialogVisible">
  <template #footer>...</template>
</el-dialog>
```

### 4. 动态路由

- `loadView` 函数更新：`require` 改为 `() => import()`
- 所有路由路径和组件映射保持不变

### 5. 环境变量迁移

- `.env.development`: `VUE_APP_*` -> `VITE_APP_*`
- `.env.production`: 同上
- `process.env.VUE_APP_*` -> `import.meta.env.VITE_APP_*`

### 6. HTML 入口文件

- `public/index.html` -> `index.html` (Vite 根目录)
- 引入 `public/` 下的静态 JS 资源

# 部署说明

### 根目录部署

```
typescript
// vite.config.ts
base: '/'

// router/index.ts
history: createWebHistory('/')
```

### 二级目录部署

```
typescript
// vite.config.ts
base: '/admin/'

// router/index.ts
history: createWebHistory('/admin/')
```

# 第三方库升级

| 原库                           | Vue 3 替代方案                            | 处理方式          |
| ------------------------------ | ----------------------------------------- | ----------------- |
| `@jiaminghi/data-view`         | `@kjgl77/datav-vue3`                      | 替换为 Vue 3 版本 |
| `vue-video-player`             | `video.js` 直接使用                       | 封装组件          |
| `vue-baidu-map`                | `vue-baidu-map-3x`                        | 替换              |
| `vue-contextmenujs`            | `@imengyu/vue3-context-menu`              | 替换              |
| `vue-print-nb`                 | `vue3-print-nb`                           | 替换              |
| `vue-clipboard2`               | `vue-clipboard3` 或 `@vueuse/core`        | 替换              |
| `vue-count-to`                 | `vue3-count-to`                           | 替换              |
| `vue-seamless-scroll`          | `vue3-seamless-scroll`                    | 替换              |
| `vue-cropper`                  | `vue-cropper@next`                        | 升级              |
| `vue-qr`                       | `vue-qr@^4`                               | 兼容 Vue 3        |
| `vue2-ace-editor`              | `vue3-ace-editor`                         | 替换              |
| `vue-json-viewer`              | `vue3-json-viewer`                        | 替换              |
| `vue-easytable`                | `vue-easytable@next` 或 `vxe-table`       | 评估替换          |
| `vue-codemirror`               | `vue-codemirror@^6`                       | 升级              |
| `vue-3d-model`                 | 自行封装 Three.js                         | 封装              |
| `@riophae/vue-treeselect`      | `vue-treeselect@next` 或 `el-tree-select` | Element Plus 内置 |
| `vant` 2.x                     | `vant` 4.x                                | 升级              |
| `vuedraggable` 2.x             | `vuedraggable@^4`                         | 升级              |
| `monaco-editor-webpack-plugin` | `vite-plugin-monaco-editor`               | 替换              |

# 性能优化

- Tree Shaking：ES Module 支持
- 按需导入：Element Plus 自动按需加载
- 路由懒加载：动态 import
- Gzip压缩：vite-plugin-compression
- SVG雪碧图：vite-plugin-svg-icons
