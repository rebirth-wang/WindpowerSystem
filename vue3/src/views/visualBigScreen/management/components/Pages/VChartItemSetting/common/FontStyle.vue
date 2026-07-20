<template>
  <setting-item :name="t('visualBigScreen.management-674210-129')">
    <n-color-picker v-model:value="style.fill" size="small" />
  </setting-item>
  <setting-item :name="t('visualBigScreen.management-674210-566')">
    <n-input-number v-model:value="style.fontSize" :min="1" size="small" />
  </setting-item>
  <setting-item :name="t('visualBigScreen.management-674210-893')">
    <n-select v-model:value="style.fontFamily" :options="fontStyleConfig.fontFamily" size="small" />
  </setting-item>
  <setting-item :name="t('visualBigScreen.management-674210-1679')">
    <n-select v-model:value="style.fontSize" :options="fontStyleConfig.fontWeight" size="small" />
  </setting-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { fontStyleConfig } from '@vb/packages/chartConfiguration/vcharts/index'
import { FontType } from '@vb/settings/vchartThemes/index'
import { SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  style: {
    type: Object as PropType<FontType>,
    required: true
  }
})
</script>
