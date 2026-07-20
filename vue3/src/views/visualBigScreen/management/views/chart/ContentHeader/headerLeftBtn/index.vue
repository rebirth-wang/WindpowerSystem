<template>
  <n-space class="header-left-btn" :wrap="false" :size="25">
    <n-button size="small" quaternary @click="goHomeHandle()">
      <template #icon>
        <n-icon :depth="3">
          <home-icon></home-icon>
        </n-icon>
      </template>
    </n-button>
    <n-space :wrap="false">
      <!-- 模块展示按钮 -->
      <n-tooltip v-for="item in btnList" :key="item.key" placement="bottom" trigger="hover">
        <template #trigger>
          <n-button size="small" ghost :type="styleHandle(item)" :focusable="false" @click="clickHandle(item)">
            <component :is="item.icon"></component>
          </n-button>
        </template>
        <span>{{ item.title }}</span>
      </n-tooltip>

      <n-divider vertical />

      <!-- 历史记录按钮 -->
      <n-tooltip v-for="item in historyList" :key="item.key" placement="bottom" trigger="hover">
        <template #trigger>
          <n-button size="small" ghost type="primary" :disabled="!item.select" @click="clickHistoryHandle(item)">
            <component :is="item.icon"></component>
          </n-button>
        </template>
        <span>{{ item.title }}</span>
      </n-tooltip>

      <n-divider vertical />

      <!-- 保存 -->
      <n-tooltip placement="bottom" trigger="hover">
        <template #trigger>
          <div class="save-btn">
            <n-button size="small" type="primary" ghost @click="dataSyncUpdate()">
              <template #icon>
                <n-icon>
                  <SaveIcon></SaveIcon>
                </n-icon>
              </template>
            </n-button>
          </div>
        </template>
        <span>{{ t('visualBigScreen.management-674210-104') }}</span>
      </n-tooltip>
    </n-space>
  </n-space>
</template>

<script setup lang="ts">
import { toRefs, Ref, reactive, computed, unref } from 'vue'
import { useI18n } from 'vue-i18n'
import router from '@/router'
import { goDialog, renderIcon } from '@vb/utils'
import { icon } from '@vb/plugins/icon'
import { useRemoveKeyboard } from '../../hooks/useKeyboard.hook'
import { useSync } from '../../hooks/useSync.hook'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { useChartHistoryStore } from '@vb/store/modules/chartHistoryStore/chartHistoryStore'
import { HistoryStackEnum } from '@vb/store/modules/chartHistoryStore/chartHistoryStore.d'
import { useChartLayoutStore } from '@vb/store/modules/chartLayoutStore/chartLayoutStore'
import { ChartLayoutStoreEnum } from '@vb/store/modules/chartLayoutStore/chartLayoutStore.d'

const { t } = useI18n()

const { LayersIcon, BarChartIcon, PrismIcon, HomeIcon, ArrowBackIcon, ArrowForwardIcon } = icon.ionicons5
const { SaveIcon } = icon.carbon
const { setItem } = useChartLayoutStore()
const { dataSyncUpdate } = useSync()
const { getLayers, getCharts, getDetails } = toRefs(useChartLayoutStore())
const chartEditStore = useChartEditStore()
const chartHistoryStore = useChartHistoryStore()

interface ItemType<T> {
  key: T
  select: Ref<boolean> | boolean
  title: string
  icon: any
}

const btnList = reactive<ItemType<ChartLayoutStoreEnum>[]>([
  {
    key: ChartLayoutStoreEnum.CHARTS,
    select: getCharts,
    title: t('visualBigScreen.management-674210-112'),
    icon: renderIcon(BarChartIcon)
  },
  {
    key: ChartLayoutStoreEnum.LAYERS,
    select: getLayers,
    title: t('visualBigScreen.management-674210-113'),
    icon: renderIcon(LayersIcon)
  },
  {
    key: ChartLayoutStoreEnum.DETAILS,
    select: getDetails,
    title: t('visualBigScreen.management-674210-114'),
    icon: renderIcon(PrismIcon)
  }
])

const isBackStack = computed(() => chartHistoryStore.getBackStack.length > 1)

const isForwardStack = computed(() => chartHistoryStore.getForwardStack.length > 0)

const historyList = reactive<ItemType<HistoryStackEnum>[]>([
  {
    key: HistoryStackEnum.BACK_STACK,
    // 一定会有初始化画布
    select: isBackStack,
    title: t('visualBigScreen.management-674210-93'),
    icon: renderIcon(ArrowBackIcon)
  },
  {
    key: HistoryStackEnum.FORWARD_STACK,
    select: isForwardStack,
    title: t('visualBigScreen.management-674210-94'),
    icon: renderIcon(ArrowForwardIcon)
  }
])

// store 描述的是展示的值，所以和 ContentConfigurations 的 collapsed 是相反的
const styleHandle = (item: ItemType<ChartLayoutStoreEnum>) => {
  const selected = unref(item.select as any)
  if (item.key === ChartLayoutStoreEnum.DETAILS) {
    return selected ? '' : 'primary'
  }
  return selected ? 'primary' : ''
}

// 布局处理
const clickHandle = (item: ItemType<ChartLayoutStoreEnum>) => {
  const selected = unref(item.select as any)
  setItem(item.key, !selected)
}

// 历史记录处理
const clickHistoryHandle = (item: ItemType<HistoryStackEnum>) => {
  switch (item.key) {
    case HistoryStackEnum.BACK_STACK:
      chartEditStore.setBack()
      break
    case HistoryStackEnum.FORWARD_STACK:
      chartEditStore.setForward()
      break
  }
}

// 返回项目列表（先保存）
const goHomeHandle = () => {
  goDialog({
    message: t('visualBigScreen.management-674210-17'),
    positiveText: t('visualBigScreen.management-674210-110'),
    negativeText: t('visualBigScreen.management-674210-111'),
    isMaskClosable: true,
    onPositiveCallback: () => {
      router.push('/project/items')
      useRemoveKeyboard()
    }
  })
}
</script>
<style lang="scss" scoped>
.header-left-btn {
  margin-left: -37px;
}
</style>
