<template>
  <collapse-item :name="t('visualBigScreen.management-674210-304')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-242')">
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1043')">
        <n-input-number
          v-model:value="optionData.size"
          size="small"
          :min="0"
          :placeholder="t('visualBigScreen.management-674210-1043')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-1044')">
      <setting-item :name="t('visualBigScreen.management-674210-1045')">
        <n-select v-model:value="optionData.rotate" size="small" :options="rotateMode"></n-select>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { option } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()

// 旋转方式
const rotateMode = [
  {
    value: 0,
    label: '0°'
  },
  {
    value: 90,
    label: '90°'
  },
  {
    value: 180,
    label: '180°'
  },
  {
    value: 270,
    label: '270°'
  }
]
</script>
