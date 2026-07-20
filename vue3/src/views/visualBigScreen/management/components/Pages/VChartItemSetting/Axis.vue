<template>
  <collapse-item v-model:name="axis.name">
    <template #header>
      <n-switch v-model:value="axis.visible" size="small"></n-switch>
    </template>
    <setting-item-box :name="t('visualBigScreen.management-674210-1684')">
      <setting-item :name="t('visualBigScreen.management-674210-1685')">
        <n-space>
          <n-switch v-model:value="axis.label.visible" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-482')">
        <n-input-number v-model:value="axis.label.style.angle" :min="0" :max="360" size="small" />
      </setting-item>
      <FontStyle :style="axis.label.style"></FontStyle>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-1686')">
      <setting-item :name="t('visualBigScreen.management-674210-1685')">
        <n-space>
          <n-switch v-model:value="axis.title.visible" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1689')">
        <n-input v-model:value="axis.title.style.text" size="small"></n-input>
      </setting-item>
      <FontStyle :style="axis.title.style"></FontStyle>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-567')">
      <setting-item :name="t('visualBigScreen.management-674210-1685')">
        <n-space>
          <n-switch v-model:value="axis.domainLine.visible" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name=""> </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-448')">
        <n-input-number v-model:value="axis.domainLine.style.lineWidth" :min="0" size="small" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="axis.domainLine.style.stroke" size="small" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-1687')">
      <setting-item :name="t('visualBigScreen.management-674210-1685')">
        <n-space>
          <n-switch v-model:value="axis.grid.visible" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name=""> </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-448')">
        <n-input-number v-model:value="axis.grid.style.lineWidth" :min="0" size="small" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="axis.grid.style.stroke" size="small" />
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import FontStyle from './common/FontStyle.vue'
import { vChartGlobalThemeJsonType } from '@vb/settings/vchartThemes/index'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  axis: {
    type: Object as PropType<vChartGlobalThemeJsonType>,
    required: true
  }
})
</script>
