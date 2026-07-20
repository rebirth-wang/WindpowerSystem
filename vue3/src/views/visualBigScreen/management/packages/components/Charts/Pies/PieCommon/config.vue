<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"></global-setting>
  <CollapseItem :name="t('visualBigScreen.management-674210-1006')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-268')">
      <SettingItem>
        <n-select v-model:value="optionData.type" size="small" :options="fontWeightOptions" />
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-158')" :alone="true">
      <SettingItem>
        <n-space>
          <n-switch v-model:value="optionData.isCarousel" size="small"></n-switch>
          <n-text>
            {{ t('visualBigScreen.management-674210-933') }}
            <n-text :depth="3">({{ t('visualBigScreen.management-674210-1007') }})</n-text>
          </n-text>
        </n-space>
      </SettingItem>
      <SettingItem>
        <n-text :depth="3">{{ t('visualBigScreen.management-674210-1008') }}</n-text>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-994')">
      <setting-item :name="t('visualBigScreen.management-674210-1009')">
        <n-input
          :value="String(optionData.series[0].radius[0] ?? '')"
          @update:value="value => (optionData.series[0].radius[0] = value)"
          size="small"
        ></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1010')">
        <n-input
          :value="String(optionData.series[0].radius[1] ?? '')"
          @update:value="value => (optionData.series[0].radius[1] = value)"
          size="small"
        ></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1011')">
        <n-input
          :value="String(optionData.series[0].center[0] ?? '')"
          @update:value="value => (optionData.series[0].center[0] = value)"
          size="small"
        ></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1012')">
        <n-input
          :value="String(optionData.series[0].center[1] ?? '')"
          @update:value="value => (optionData.series[0].center[1] = value)"
          size="small"
        ></n-input>
      </setting-item>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-447')">
      <SettingItem>
        <n-space>
          <n-switch v-model:value="optionData.series[0].label.show" size="small"></n-switch>
          <n-text>{{ t('visualBigScreen.management-674210-996') }}</n-text>
        </n-space>
      </SettingItem>
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.series[0].labelLine.show" size="small"></n-switch>
          <n-text>{{ t('visualBigScreen.management-674210-1013') }}</n-text>
        </n-space>
      </setting-item>
      <SettingItem :name="t('visualBigScreen.management-674210-322')">
        <n-select v-model:value="optionData.series[0].label.position" size="small" :options="labelConfig.position" />
      </SettingItem>
      <setting-item :name="t('visualBigScreen.management-674210-1014')">
        <n-select v-model:value="optionData.series[0].label.formatter" size="small" :options="labelFormatterOptions" />
      </setting-item>
    </SettingItemBox>
    <setting-item-box :name="t('visualBigScreen.management-674210-893')">
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="optionData.series[0].label.fontSize" size="small" :min="0"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')" v-if="optionData.series[0].label.color">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.series[0].label.color"></n-color-picker>
      </setting-item>
      <SettingItem :name="t('visualBigScreen.management-674210-1015')" v-if="optionData.series[0].label.fontWeight">
        <n-select
          v-model:value="optionData.series[0].label.fontWeight"
          size="small"
          :options="labelConfig.fontWeight"
        />
      </SettingItem>
      <setting-item
        :name="t('visualBigScreen.management-674210-1016')"
        v-if="optionData.series[0].label.textBorderWidth"
      >
        <n-input-number
          v-model:value="optionData.series[0].label.textBorderWidth"
          size="small"
          :min="0"
        ></n-input-number>
      </setting-item>
      <setting-item
        :name="t('visualBigScreen.management-674210-1017')"
        v-if="optionData.series[0].label.textBorderColor"
      >
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="optionData.series[0].label.textBorderColor"
        ></n-color-picker>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-251')">
      <setting-item :name="t('visualBigScreen.management-674210-1018')">
        <n-input-number
          v-model:value="optionData.series[0].itemStyle.borderRadius"
          size="small"
          :min="0"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1019')">
        <n-input-number
          v-model:value="optionData.series[0].itemStyle.borderWidth"
          size="small"
          :min="0"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-1020')">
      <setting-item :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="optionData.series[0].emphasis.label.fontSize"
          size="small"
          :min="0"
        ></n-input-number>
      </setting-item>
      <SettingItem
        :name="t('visualBigScreen.management-674210-1015')"
        v-if="optionData.series[0].emphasis.label.fontWeight"
      >
        <n-select
          v-model:value="optionData.series[0].emphasis.label.fontWeight"
          size="small"
          :options="labelConfig.fontWeight"
        />
      </SettingItem>
    </setting-item-box>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { PieTypeObject, PieTypeEnum, PieTypeLabelObject } from './config'
import { labelConfig } from '@vb/packages/chartConfiguration/echarts'

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()
const fontWeightOptions = [
  {
    label: PieTypeLabelObject[PieTypeEnum.NORMAL],
    value: PieTypeObject[PieTypeEnum.NORMAL]
  },
  {
    label: PieTypeLabelObject[PieTypeEnum.RING],
    value: PieTypeObject[PieTypeEnum.RING]
  },
  {
    label: PieTypeLabelObject[PieTypeEnum.ROSE],
    value: PieTypeObject[PieTypeEnum.ROSE]
  }
]

const labelFormatterOptions = [
  { label: t('visualBigScreen.management-674210-1021'), value: '{b}' },
  { label: t('visualBigScreen.management-674210-1022'), value: '{d}' },
  { label: t('visualBigScreen.management-674210-1023'), value: '{b}:{d}%' }
]
</script>
