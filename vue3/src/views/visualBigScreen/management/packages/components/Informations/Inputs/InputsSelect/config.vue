<template>
  <collapse-item :name="t('visualBigScreen.management-674210-292')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-269')" :alone="true">
      <n-select size="small" v-model:value="optionData.selectValue" :options="optionData.dataset" />
    </setting-item-box>
  </collapse-item>
</template>

<script lang="ts" setup>
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
