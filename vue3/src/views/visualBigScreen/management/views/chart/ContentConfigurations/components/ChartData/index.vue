<template>
  <div class="go-chart-configurations-data" v-if="targetData">
    <setting-item-box :name="t('visualBigScreen.management-674210-816')" :alone="true">
      <n-select v-model:value="targetData.request.requestDataType" :disabled="isNotData" :options="selectOptions" />
    </setting-item-box>
    <!-- 静态 -->
    <chart-data-static v-if="targetData.request.requestDataType === RequestDataTypeEnum.STATIC"></chart-data-static>
    <!-- 动态 -->
    <chart-data-ajax v-if="targetData.request.requestDataType === RequestDataTypeEnum.AJAX"></chart-data-ajax>
    <!-- 数据池 -->
    <chart-data-pond v-if="targetData.request.requestDataType === RequestDataTypeEnum.Pond"></chart-data-pond>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { loadAsyncComponent } from '@vb/utils'
import { SettingItemBox } from '@vb/components/Pages/ChartItemSetting'
import { RequestDataTypeEnum } from '@vb/enums/httpEnum'
import { ChartFrameEnum } from '@vb/packages/index.d'
import { useTargetData } from '../hooks/useTargetData.hook'
import { SelectCreateDataType, SelectCreateDataEnum, SelectCreateDataLangKey } from './index.d'

const ChartDataStatic = loadAsyncComponent(() => import('./components/ChartDataStatic/index.vue'))
const ChartDataAjax = loadAsyncComponent(() => import('./components/ChartDataAjax/index.vue'))
const ChartDataPond = loadAsyncComponent(() => import('./components/ChartDataPond/index.vue'))

const { targetData } = useTargetData()
const { t } = useI18n()

// 选项
const selectOptions = computed<SelectCreateDataType[]>(() => [
  {
    label: t(SelectCreateDataLangKey[SelectCreateDataEnum.STATIC]),
    value: RequestDataTypeEnum.STATIC
  },
  {
    label: t(SelectCreateDataLangKey[SelectCreateDataEnum.AJAX]),
    value: RequestDataTypeEnum.AJAX
  },
  {
    label: t(SelectCreateDataLangKey[SelectCreateDataEnum.Pond]),
    value: RequestDataTypeEnum.Pond
  }
])

// 无数据源
const isNotData = computed(() => {
  return (
    targetData.value.chartConfig?.chartFrame === ChartFrameEnum.STATIC ||
    typeof targetData.value?.option?.dataset === 'undefined'
  )
})
</script>
