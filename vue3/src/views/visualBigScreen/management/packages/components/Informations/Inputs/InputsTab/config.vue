<template>
  <collapse-item :name="t('visualBigScreen.management-674210-293')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-294')" :alone="true">
      <n-select size="small" v-model:value="optionData.tabType" :options="tabTypeOptions" />
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-269')" :alone="true">
      <n-select size="small" v-model:value="optionData.tabLabel" value-field="label" :options="optionData.dataset" />
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

const tabTypeOptions = [
  {
    label: t('visualBigScreen.management-674210-295'),
    value: 'bar'
  },
  {
    label: t('visualBigScreen.management-674210-296'),
    value: 'segment'
  }
]
</script>
