<template>
  <div ref="chartRef"></div>
</template>

<script setup lang="ts">
import { onMounted, PropType, ref, toRefs, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { CreateComponentType } from '@vb/packages/index.d'
import { useChartDataFetch } from '@vb/hooks'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { option } from './config'
import World from './code/world/Word'
import throttle from 'lodash/throttle'

const props = defineProps({
  chartConfig: {
    type: Object as PropType<CreateComponentType & typeof option>,
    required: true
  }
})

const chartEditStore = useChartEditStore()

const chartRef = ref<HTMLElement>()
const { w, h } = toRefs(props.chartConfig.attr)
let threeClassInstance: World
const { t } = useI18n()

// 初始化
const init = () => {
  const dom: HTMLElement | undefined = chartRef.value
  if (dom) {
    threeClassInstance = new World({
      dom: dom,
      data: props.chartConfig.option.dataset,
      width: w.value,
      height: h.value
    })
  }
}

const updateData = (data: any) => {
  try {
    threeClassInstance.updateData(data)
  } catch (error) {
    console.log(error)
  }
}

// 改变大小
watch(
  () => [w.value, h.value],
  throttle(([newWidth], [newHeight]) => {
    threeClassInstance.updateSize(newWidth, newHeight)
  }, 100)
)

watch(
  () => props.chartConfig.option.dataset,
  (newData: any) => {
    updateData(newData)
  },
  {
    deep: false
  }
)

// DOM 渲染之后进行初始化
onMounted(() => {
  try {
    if (navigator.userAgent.indexOf('Chrome') < -1 || navigator.userAgent.indexOf('Edg') < -1) {
      ElMessage.error(t('visualBigScreen.management-674210-236'))
      chartEditStore.removeComponentList(undefined, false)
      return
    }
    init()
  } catch (error) {
    console.log(error)
  }
})

useChartDataFetch(props.chartConfig, useChartEditStore, updateData)
</script>
