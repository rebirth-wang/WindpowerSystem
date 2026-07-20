<template>
  <collapse-item :name="t('visualBigScreen.management-674210-300')" :expanded="true">
    <setting-item-box :alone="false" :name="t('visualBigScreen.management-674210-301')">
      <setting-item :name="t('visualBigScreen.management-674210-302')" :alone="true">
        <n-input-number
          v-model:value="optionData.pageValue"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-302')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-218')" :alone="true">
        <n-select v-model:value="optionData.pageSize" size="small" :options="page" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-303')" :alone="true">
        <n-input-number
          v-model:value="optionData.dataset"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-303')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>
<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItem, SettingItemBox } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const { t } = useI18n()

const page = [
  { label: '2', value: 2 },
  { label: '4', value: 4 },
  { label: '8', value: 8 },
  { label: '10', value: 10 },
  { label: '20', value: 20 }
]
defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
</script>
