<template>
  <global-setting :optionData="optionData"></global-setting>
  <collapse-item :name="t('visualBigScreen.management-674210-222')" expanded>
    <setting-item-box :name="t('visualBigScreen.management-674210-333')">
      <setting-item>
        <n-select v-model:value="optionData.series[0].shape" size="small" :options="ShapeEnumList" />
      </setting-item>
      <setting-item>
        <n-checkbox v-model:checked="optionData.series[0].drawOutOfBound" size="small">
          {{ t('visualBigScreen.management-674210-334') }}
        </n-checkbox>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-335')">
      <setting-item :name="t('visualBigScreen.management-674210-250')">
        <n-slider
          v-model:value="series.width"
          :min="0"
          :max="100"
          :format-tooltip="sliderFormatTooltip"
          @update:value="updateWidth"
        ></n-slider>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-336')">
        <n-slider
          v-model:value="series.height"
          :min="0"
          :max="100"
          :format-tooltip="sliderFormatTooltip"
          @update:value="updateHeight"
        ></n-slider>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-242')" alone>
      <setting-item :name="t('visualBigScreen.management-674210-337')">
        <n-slider v-model:value="optionData.series[0].sizeRange" range :step="1" :min="6" :max="100" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-338')">
        <n-slider v-model:value="series.rotationStep" :step="15" :min="0" :max="45" @update:value="updateRotation" />
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { option, ShapeEnumList } from './config'
// eslint-disable-next-line no-unused-vars
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

const series = computed(() => {
  const { width, height, rotationStep } = props.optionData.series[0]
  return {
    width: +width.replace('%', ''),
    height: +height.replace('%', ''),
    rotationStep
  }
})

const sliderFormatTooltip = (v: number) => {
  return `${v}%`
}

const updateWidth = (value: number) => {
  props.optionData.series[0].width = `${value}%`
}

const updateHeight = (value: number) => {
  props.optionData.series[0].height = `${value}%`
}

const updateRotation = (value: number) => {
  props.optionData.series[0].rotationStep = value
  props.optionData.series[0].rotationRange = value === 0 ? [0, 0] : [-90, 90]
}
</script>
