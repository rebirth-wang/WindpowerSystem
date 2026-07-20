<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-206')" expanded>
    <SettingItemBox :name="t('visualBigScreen.management-674210-972')">
      <SettingItem :name="t('visualBigScreen.management-674210-253')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.bgColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-372')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.borderColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-504')">
        <n-input-number v-model:value="optionData.border" size="small" :step="0.5" :min="0"></n-input-number>
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
