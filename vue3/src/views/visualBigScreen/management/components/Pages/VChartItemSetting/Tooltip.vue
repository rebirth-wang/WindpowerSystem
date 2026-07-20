<template>
  <div v-if="optionData.tooltip">
    <collapse-item :name="t('visualBigScreen.management-674210-1681')">
      <template #header>
        <n-switch v-model:value="optionData.tooltip.visible" size="small"></n-switch>
      </template>
      <setting-item-box :name="t('visualBigScreen.management-674210-1683')">
        <setting-item :name="t('visualBigScreen.management-674210-1682')">
          <n-color-picker v-model:value="optionData.tooltip.style.panel.backgroundColor" size="small" />
        </setting-item>
        <setting-item :name="t('visualBigScreen.management-674210-152')">
          <n-color-picker v-model:value="optionData.tooltip.style.panel.border.color" size="small" />
        </setting-item>
        <setting-item :name="t('visualBigScreen.management-674210-448')">
          <n-input-number v-model:value="optionData.tooltip.style.panel.border.width" :min="0" size="small" />
        </setting-item>
        <setting-item :name="t('visualBigScreen.management-674210-251')">
          <n-input-number v-model:value="optionData.tooltip.style.panel.border.radius" :min="0" size="small" />
        </setting-item>
      </setting-item-box>
      <setting-item-box :name="t('visualBigScreen.management-674210-150')">
        <FontStyle :style="optionData.tooltip.style.titleLabel"></FontStyle>
      </setting-item-box>
      <setting-item-box :name="t('name')">
        <FontStyle :style="optionData.tooltip.style.keyLabel"></FontStyle>
      </setting-item-box>
      <setting-item-box :name="t('visualBigScreen.management-674210-473')">
        <FontStyle :style="optionData.tooltip.style.valueLabel"></FontStyle>
      </setting-item-box>
    </collapse-item>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import FontStyle from './common/FontStyle.vue'
import { vChartGlobalThemeJsonType } from '@vb/settings/vchartThemes/index'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  optionData: {
    type: Object as PropType<vChartGlobalThemeJsonType>,
    required: true
  }
})
</script>
