<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-295')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-982')">
      <SettingItem :name="t('visualBigScreen.management-674210-983')">
        <n-input-number size="small" v-model:value="optionData.outCircle"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-984')">
        <n-input-number size="small" v-model:value="optionData.inCircle"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-985')">
        <n-input-number size="small" v-model:value="optionData.outCircleWidth"></n-input-number>
      </SettingItem>

      <SettingItem :name="t('visualBigScreen.management-674210-986')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.outCircleColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-987')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.inCircleColor"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()
</script>
