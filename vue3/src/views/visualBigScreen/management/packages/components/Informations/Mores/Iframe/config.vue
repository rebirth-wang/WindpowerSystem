<template>
  <collapse-item :name="t('visualBigScreen.management-674210-304')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-305')" :alone="true">
      <setting-item :name="t('visualBigScreen.management-674210-431')">
        <n-input
          :value="String(optionData.dataset ?? '')"
          @update:value="value => (optionData.dataset = value)"
          size="small"
        ></n-input>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-242')">
      <setting-item :name="t('visualBigScreen.management-674210-251')">
        <n-input-number
          v-model:value="optionData.borderRadius"
          size="small"
          :min="0"
          :placeholder="t('visualBigScreen.management-674210-251')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { option } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
</script>
