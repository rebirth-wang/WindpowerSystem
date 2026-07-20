import type { App } from 'vue'
import { GoSkeleton } from '@vb/components/GoSkeleton'
import { GoLoading } from '@vb/components/GoLoading'
import { GoThemeSelect } from '@vb/components/GoThemeSelect'
import { SketchRule } from 'vue3-sketch-ruler'

/**
 * 全局注册自定义组件
 * @param app
 */
export function setupCustomComponents(app: App) {
  // 骨架屏
  app.component('GoSkeleton', GoSkeleton)
  // 加载
  app.component('GoLoading', GoLoading)
  // 主题切换
  app.component('GoThemeSelect', GoThemeSelect)
  app.component('go-theme-select', GoThemeSelect)
  // 标尺
  app.component('SketchRule', SketchRule)
}
