<template>
  <collapse-item :name="t('visualBigScreen.management-674210-432')">
    <setting-item-box :alone="true">
      <template #name>
        <n-text>{{ t('visualBigScreen.management-674210-433') }}</n-text>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-icon size="21" :depth="3">
              <help-outline-icon></help-outline-icon>
            </n-icon>
          </template>
          <n-text>{{ t('visualBigScreen.management-674210-434') }}</n-text>
        </n-tooltip>
      </template>
      <EchartsRendererSetting v-model="themeSetting.renderer" />
    </setting-item-box>
    <setting-item-box :alone="true">
      <template #name>
        <n-text>{{ t('visualBigScreen.management-674210-435') }}</n-text>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-icon size="21" :depth="3">
              <help-outline-icon></help-outline-icon>
            </n-icon>
          </template>
          <n-text>{{ t('visualBigScreen.management-674210-436') }}</n-text>
        </n-tooltip>
      </template>
      <EchartsRendererSetting v-model="optionData.renderer" includeInherit />
    </setting-item-box>
  </collapse-item>
  <collapse-item v-if="title" :name="t('visualBigScreen.management-674210-150')">
    <template #header>
      <n-switch v-model:value="title.show" size="small"></n-switch>
    </template>
    <setting-item-box :name="t('visualBigScreen.management-674210-150')">
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="title.textStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="title.textStyle.fontSize" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-437')">
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" v-model:value="title.subtextStyle.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="title.subtextStyle.fontSize" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>

  <collapse-item v-if="grid" :name="t('visualBigScreen.management-674210-438')">
    <setting-item-box :name="t('visualBigScreen.management-674210-439')">
      <setting-item :name="t('visualBigScreen.management-674210-440')">
        <n-input v-model:value="grid.left" size="small"></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-441')">
        <n-input v-model:value="grid.right" size="small"></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-442')">
        <n-input v-model:value="grid.top" size="small"></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-443')">
        <n-input v-model:value="grid.bottom" size="small"></n-input>
      </setting-item>
    </setting-item-box>
  </collapse-item>

  <collapse-item v-if="xAxis" :name="t('visualBigScreen.management-674210-466')">
    <template #header>
      <n-switch v-model:value="xAxis.show" size="small"></n-switch>
    </template>
    <setting-item-box :name="t('visualBigScreen.management-674210-444')">
      <setting-item :name="t('visualBigScreen.management-674210-445')">
        <n-input v-model:value="xAxis.name" size="small"></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" v-model:value="xAxis.nameTextStyle.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="xAxis.nameTextStyle.fontSize" :min="12" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-446')">
        <n-input-number v-model:value="xAxis.nameGap" :min="5" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-447')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="xAxis.axisLabel.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" v-model:value="xAxis.axisLabel.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="xAxis.axisLabel.fontSize" :min="8" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-446')">
        <n-input-number v-model:value="xAxis.axisLabel.rotate" :min="-90" :max="90" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-567')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="xAxis.axisLine.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="xAxis.axisLine.lineStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-448')">
        <n-input-number v-model:value="xAxis.axisLine.lineStyle.width" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-322')">
        <n-select v-model:value="xAxis.position" size="small" :options="axisConfig.xposition"></n-select>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-449')">
        <n-space>
          <n-switch v-model:value="xAxis.axisLine.onZero" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-450')">
        <n-space>
          <n-switch v-model:value="xAxis.inverse" size="small"></n-switch>
        </n-space>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-451')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="xAxis.axisTick.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-452')">
        <n-input-number v-model:value="xAxis.axisTick.length" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-453')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="xAxis.splitLine.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="xAxis.splitLine.lineStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-448')">
        <n-input-number v-model:value="xAxis.splitLine.lineStyle.width" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-268')">
        <n-select
          v-model:value="xAxis.splitLine.lineStyle.type"
          size="small"
          :options="axisConfig.splitLint.lineStyle.type"
        ></n-select>
      </setting-item>
    </setting-item-box>
  </collapse-item>

  <collapse-item v-if="yAxis" :name="t('visualBigScreen.management-674210-467')">
    <template #header>
      <n-switch v-model:value="yAxis.show" size="small"></n-switch>
    </template>
    <setting-item-box :name="t('visualBigScreen.management-674210-444')">
      <setting-item :name="t('visualBigScreen.management-674210-445')">
        <n-input v-model:value="yAxis.name" size="small"></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" v-model:value="yAxis.nameTextStyle.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="yAxis.nameTextStyle.fontSize" :min="8" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-446')">
        <n-input-number v-model:value="yAxis.nameGap" :min="5" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-447')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="yAxis.axisLabel.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" v-model:value="yAxis.axisLabel.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="yAxis.axisLabel.fontSize" :min="8" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-446')">
        <n-input-number v-model:value="yAxis.axisLabel.rotate" :min="-90" :max="90" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-567')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="yAxis.axisLine.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="yAxis.axisLine.lineStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-448')">
        <n-input-number v-model:value="yAxis.axisLine.lineStyle.width" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-322')">
        <n-select v-model:value="yAxis.position" size="small" :options="axisConfig.yposition"></n-select>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-449')">
        <n-space>
          <n-switch v-model:value="yAxis.axisLine.onZero" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-450')">
        <n-space>
          <n-switch v-model:value="yAxis.inverse" size="small"></n-switch>
        </n-space>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-451')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="yAxis.axisTick.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-452')">
        <n-input-number v-model:value="yAxis.axisTick.length" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-453')">
      <setting-item :name="t('visualBigScreen.management-674210-88')">
        <n-space>
          <n-switch v-model:value="yAxis.splitLine.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="yAxis.splitLine.lineStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-448')">
        <n-input-number v-model:value="yAxis.splitLine.lineStyle.width" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-268')">
        <n-select
          v-model:value="yAxis.splitLine.lineStyle.type"
          size="small"
          :options="axisConfig.splitLint.lineStyle.type"
        ></n-select>
      </setting-item>
    </setting-item-box>
  </collapse-item>

  <collapse-item v-if="legend" :name="t('visualBigScreen.management-674210-454')">
    <template #header>
      <n-switch v-model:value="legend.show" size="small"></n-switch>
    </template>
    <setting-item-box :name="t('visualBigScreen.management-674210-455')">
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" v-model:value="legend.textStyle.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="legend.textStyle.fontSize" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-456')">
      <setting-item :name="t('visualBigScreen.management-674210-466')">
        <n-select v-model:value="legend.x" size="small" :options="legendConfig.lengendX" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-467')">
        <n-select v-model:value="legend.y" size="small" :options="legendConfig.lengendY" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-568')">
      <setting-item :name="t('visualBigScreen.management-674210-457')">
        <n-select v-model:value="legend.orient" size="small" :options="legendConfig.orient" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-333')">
        <n-select v-model:value="legend.icon" size="small" :options="legendConfig.shape" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-458')">
      <setting-item :name="t('visualBigScreen.management-674210-250')">
        <n-input-number v-model:value="legend.itemWidth" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-336')">
        <n-input-number v-model:value="legend.itemHeight" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>

  <collapse-item v-if="visualMap" :name="t('visualBigScreen.management-674210-459')">
    <template #header>
      <n-switch v-model:value="visualMap.show" size="small"></n-switch>
    </template>

    <setting-item-box :name="t('visualBigScreen.management-674210-460')">
      <setting-item :name="t('visualBigScreen.management-674210-461')">
        <n-input-number v-model:value="visualMap.min" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-462')">
        <n-input-number v-model:value="visualMap.max" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-129')">
      <setting-item
        :name="`${t('visualBigScreen.management-674210-468')}-${index + 1}`"
        v-for="(item, index) in visualMap.inRange.color"
        :key="index"
      >
        <n-color-picker v-model:value="visualMap.inRange.color[index]" size="small"></n-color-picker>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-463')">
      <setting-item :name="t('visualBigScreen.management-674210-464')">
        <n-select v-model:value="visualMap.orient" size="small" :options="axisConfig.visualMap.orient"></n-select>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-250')">
        <n-input-number v-model:value="visualMap.itemWidth" :min="5" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-336')">
        <n-input-number v-model:value="visualMap.itemHeight" :min="5" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-450')">
        <n-space>
          <n-switch v-model:value="visualMap.inverse" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-465')">
        <n-space>
          <n-switch v-model:value="visualMap.realtime" size="small"></n-switch>
        </n-space>
      </setting-item>
    </setting-item-box>
    <global-setting-position :targetData="visualMap"></global-setting-position>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { axisConfig, legendConfig } from '@vb/packages/chartConfiguration/echarts/index'
import {
  CollapseItem,
  SettingItemBox,
  SettingItem,
  GlobalSettingPosition
} from '@vb/components/Pages/ChartItemSetting'
import { icon } from '@vb/plugins/icon'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import EchartsRendererSetting from './EchartsRendererSetting.vue'

const { HelpOutlineIcon } = icon.ionicons5
const { t } = useI18n()

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  },
  inChart: {
    type: Boolean,
    required: false,
    default: false
  }
})

const chartEditStore = useChartEditStore()
const themeSetting = computed(() => {
  const chartThemeSetting = chartEditStore.getEditCanvasConfig.chartThemeSetting
  return chartThemeSetting
})

const title = computed(() => {
  return props.optionData.title
})

const xAxis = computed(() => {
  return props.optionData.xAxis
})

const yAxis = computed(() => {
  return props.optionData.yAxis
})

const legend = computed(() => {
  return props.optionData.legend
})

const grid = computed(() => {
  return props.optionData.grid
})

const visualMap = computed(() => {
  return props.optionData.visualMap
})

// 监听legend color颜色改变type = scroll的颜色
watch(
  () => legend.value?.textStyle?.color,
  newVal => {
    if (!legend.value || !newVal) return
    if (!legend.value.pageTextStyle || typeof legend.value.pageTextStyle !== 'object') {
      legend.value.pageTextStyle = { color: newVal }
      return
    }
    legend.value.pageTextStyle.color = newVal
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
