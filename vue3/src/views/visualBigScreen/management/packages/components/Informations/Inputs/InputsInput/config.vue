<template>
  <collapse-item :name="t('visualBigScreen.management-674210-297')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-269')" :alone="true">
      <n-input
        :value="String(optionData.dataset ?? '')"
        @update:value="value => (optionData.dataset = value)"
        :placeholder="t('visualBigScreen.management-674210-298')"
      />
    </setting-item-box>
  </collapse-item>
</template>
<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const { t } = useI18n()

defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
</script>
