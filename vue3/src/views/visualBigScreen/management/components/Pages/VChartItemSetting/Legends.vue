<template>
  <div v-if="optionData.legends">
    <div v-for="(legendItem, index) in optionData.legends" :key="index">
      <collapse-item :name="t('visualBigScreen.management-674210-454')">
        <template #header>
          <n-switch v-model:value="legendItem.visible" size="small"></n-switch>
        </template>
        <setting-item-box :name="t('visualBigScreen.management-674210-335')">
          <setting-item :name="t('visualBigScreen.management-674210-322')">
            <n-select v-model:value="legendItem.orient" size="small" :options="legendsConfig.orient" />
          </setting-item>
          <setting-item :name="t('visualBigScreen.management-674210-362')">
            <n-select v-model:value="legendItem.position" size="small" :options="legendsConfig.position" />
          </setting-item>
        </setting-item-box>
        <setting-item-box :name="t('visualBigScreen.management-674210-1680')">
          <FontStyle :style="legendItem.item.label.style"></FontStyle>
        </setting-item-box>
      </collapse-item>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { legendsConfig } from '@vb/packages/chartConfiguration/vcharts/index'
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
