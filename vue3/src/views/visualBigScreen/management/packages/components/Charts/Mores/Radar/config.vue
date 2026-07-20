<template>
  <div>
    <!-- Echarts 全局设置 -->
    <global-setting :optionData="optionData"></global-setting>
    <CollapseItem :name="t('visualBigScreen.management-674210-175')" :expanded="true">
      <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
        <SettingItem>
          <n-checkbox v-model:checked="radarConfig.splitArea.show">{{
            t('visualBigScreen.management-674210-252')
          }}</n-checkbox>
        </SettingItem>
        <SettingItem>
          <n-checkbox v-model:checked="radarConfig.splitLine.show">{{
            t('visualBigScreen.management-674210-453')
          }}</n-checkbox>
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-907')">
          <n-select
            v-model:value="radarConfig.shape"
            size="small"
            :options="RadarShapeEnumList"
            :placeholder="t('visualBigScreen.management-674210-908')"
          />
        </SettingItem>
      </SettingItemBox>

      <SettingItemBox :name="t('visualBigScreen.management-674210-909')">
        <SettingItem>
          <n-checkbox v-model:checked="radarConfig.axisLine.show">{{
            t('visualBigScreen.management-674210-567')
          }}</n-checkbox>
        </SettingItem>
        <SettingItem>
          <n-checkbox v-model:checked="radarConfig.axisTick.show">{{
            t('visualBigScreen.management-674210-451')
          }}</n-checkbox>
        </SettingItem>
      </SettingItemBox>

      <SettingItemBox :name="t('visualBigScreen.management-674210-460')">
        <setting-item :name="`${t('visualBigScreen.management-674210-461')}：${radarProp.radius[0]}%`">
          <n-slider
            v-model:value="radarProp.radius[0]"
            :min="0"
            :max="100"
            :format-tooltip="sliderFormatTooltip"
            @update:value="updateRadius0"
          ></n-slider>
        </setting-item>
        <setting-item :name="`${t('visualBigScreen.management-674210-462')}：${radarProp.radius[1]}%`">
          <n-slider
            v-model:value="radarProp.radius[1]"
            :min="0"
            :max="100"
            :format-tooltip="sliderFormatTooltip"
            @update:value="updateRadius1"
          ></n-slider>
        </setting-item>
      </SettingItemBox>

      <SettingItemBox :name="t('visualBigScreen.management-674210-910')">
        <setting-item :name="`${t('visualBigScreen.management-674210-911')}：${radarProp.center[0]}%`">
          <n-slider
            v-model:value="radarProp.center[0]"
            :min="0"
            :max="100"
            :format-tooltip="sliderFormatTooltip"
            @update:value="updateCenter0"
          ></n-slider>
        </setting-item>
        <setting-item :name="`${t('visualBigScreen.management-674210-912')}：${radarProp.center[1]}%`">
          <n-slider
            v-model:value="radarProp.center[1]"
            :min="0"
            :max="100"
            :format-tooltip="sliderFormatTooltip"
            @update:value="updateCenter1"
          ></n-slider>
        </setting-item>
      </SettingItemBox>

      <SettingItemBox :name="t('visualBigScreen.management-674210-321')">
        <SettingItem :name="t('visualBigScreen.management-674210-129')">
          <n-color-picker size="small" :modes="['hex']" v-model:value="radarConfig.axisName.color"></n-color-picker>
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-566')">
          <n-input-number v-model:value="radarConfig.axisName.fontSize" size="small" :min="9"></n-input-number>
        </SettingItem>
        <SettingItem>
          <n-checkbox v-model:checked="radarConfig.axisName.show">{{
            t('visualBigScreen.management-674210-447')
          }}</n-checkbox>
        </SettingItem>
      </SettingItemBox>

      <SettingItemBox :name="t('visualBigScreen.management-674210-913')" :alone="true">
        <SettingItem :name="t('visualBigScreen.management-674210-914')">
          <n-input-number
            v-model:value="optionData.series[0].areaStyle.opacity"
            size="small"
            :min="0"
            :max="1"
            :step="0.1"
          ></n-input-number>
        </SettingItem>
      </SettingItemBox>
    </CollapseItem>
  </div>
</template>

<script setup lang="ts">
import { PropType, computed, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option, RadarShapeEnumList } from './config'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option & GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const radarConfig = computed<typeof option.radar>(() => {
  return props.optionData.radar
})

const radarProp = reactive({
  radius: [0, 60],
  center: [50, 50]
})

// 更新处理
const updateRadius0 = (value: number) => {
  props.optionData.radar.radius[0] = `${value}%`
}

const updateRadius1 = (value: number) => {
  props.optionData.radar.radius[1] = `${value}%`
}

// 更新处理
const updateCenter0 = (value: number) => {
  props.optionData.radar.center[0] = `${value}%`
}

const updateCenter1 = (value: number) => {
  props.optionData.radar.center[1] = `${value}%`
}

// 百分比格式化 percent
const sliderFormatTooltip = (v: number) => {
  return `${v}%`
}
</script>
