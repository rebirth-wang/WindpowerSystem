<template>
  <setting-item-box v-if="targetData" :name="t('visualBigScreen.management-674210-1083')">
    <setting-item :name="`${t('visualBigScreen.management-674210-1092')}：${targetData.left || 0}px`">
      <n-input-number v-model:value="targetData.left" size="small" step="10"></n-input-number>
    </setting-item>
    <setting-item :name="`${t('visualBigScreen.management-674210-1093')}：${targetData.top || 0}px`">
      <n-input-number v-model:value="targetData.top" size="small" step="10"></n-input-number>
    </setting-item>
  </setting-item-box>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

type positionType = {
  top?: number | string | null
  left?: number | string | null
  right?: number | string | null
  bottom?: number | string | null
}

const props = defineProps({
  targetData: {
    type: Object as PropType<positionType>,
    required: true
  }
})
</script>
