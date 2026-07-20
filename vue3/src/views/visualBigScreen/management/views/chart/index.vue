<template>
  <n-config-provider
    :theme="darkTheme"
    :hljs="hljsTheme"
    :theme-overrides="overridesTheme"
    :locale="locale"
    :date-locale="dateLocale"
  >
    <!-- 工作台相关 -->
    <div class="go-chart">
      <n-layout>
        <layout-header-pro>
          <template #left>
            <header-left-btn></header-left-btn>
          </template>
          <template #center>
            <header-title></header-title>
          </template>
          <template #ri-left>
            <header-right-btn></header-right-btn>
          </template>
        </layout-header-pro>
        <n-layout-content content-style="overflow:hidden; display: flex">
          <div style="overflow: hidden; display: flex">
            <content-charts></content-charts>
            <content-layers></content-layers>
          </div>
          <content-configurations></content-configurations>
        </n-layout-content>
      </n-layout>
    </div>
    <!-- 右键 -->
    <n-dropdown
      placement="bottom-start"
      trigger="manual"
      size="small"
      :x="mousePosition.x"
      :y="mousePosition.y"
      :options="menuOptions"
      :show="chartEditStore.getRightMenuShow"
      :on-clickoutside="onClickOutSide"
      @select="handleMenuSelect"
    ></n-dropdown>
    <!-- 加载蒙层 -->
    <content-load></content-load>
  </n-config-provider>
</template>

<script setup lang="ts">
import { NConfigProvider } from 'naive-ui'
import { loadAsyncComponent } from '@vb/utils'
import { LayoutHeaderPro } from '@vb/layout/components/LayoutHeaderPro'
import { useContextMenu } from './hooks/useContextMenu.hook'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { useChartHistoryStore } from '@vb/store/modules/chartHistoryStore/chartHistoryStore'
import { useCode, useDarkThemeHook, useThemeOverridesHook, useLang } from '@vb/hooks'

const chartHistoryStoreStore = useChartHistoryStore()
const chartEditStore = useChartEditStore()

// Naive 主题（与子项目 App.vue 对齐）
const darkTheme = useDarkThemeHook()
const hljsTheme = useCode()
const overridesTheme = useThemeOverridesHook()
const { locale, dateLocale } = useLang()

// 记录初始化
chartHistoryStoreStore.canvasInit(chartEditStore.getEditCanvas)

const HeaderLeftBtn = loadAsyncComponent(() => import('./ContentHeader/headerLeftBtn/index.vue'))
const HeaderRightBtn = loadAsyncComponent(() => import('./ContentHeader/headerRightBtn/index.vue'))
const HeaderTitle = loadAsyncComponent(() => import('./ContentHeader/headerTitle/index.vue'))
const ContentLayers = loadAsyncComponent(() => import('./ContentLayers/index.vue'))
const ContentCharts = loadAsyncComponent(() => import('./ContentCharts/index.vue'))
const ContentConfigurations = loadAsyncComponent(() => import('./ContentConfigurations/index.vue'))
const ContentLoad = loadAsyncComponent(() => import('./ContentLoad/index.vue'))

// 右键
const { menuOptions, onClickOutSide, mousePosition, handleMenuSelect } = useContextMenu()
</script>

<style lang="scss" scoped>
.go-chart {
  height: 100vh;
  width: 100vw;
  overflow: hidden;

  @include themeify {
    background-color: themed('background-color1') !important;
    color: themed('color') !important;

    :deep(.n-layout),
    :deep(.n-layout-content),
    :deep(.n-layout-scroll-container) {
      background-color: themed('background-color1') !important;
      color: themed('color') !important;
    }
  }
}
</style>
