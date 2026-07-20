<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"> </global-setting>
  <!-- 胶囊柱图 -->
  <collapse-item :name="t('visualBigScreen.management-674210-872')" expanded>
    <SettingItemBox :name="t('visualBigScreen.management-674210-335')">
      <setting-item :name="t('visualBigScreen.management-674210-999')">
        <n-input-number v-model:value="optionData.paddingLeft" :min="10" :step="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1000')">
        <n-input-number v-model:value="optionData.paddingRight" :min="10" :step="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-1001')">
        <n-input-number v-model:value="optionData.itemHeight" :min="0" :step="1" size="small"></n-input-number>
      </setting-item>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-149')">
      <setting-item :name="t('visualBigScreen.management-674210-1002')">
        <n-input-number v-model:value="optionData.valueFontSize" :min="0" :step="1" size="small"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-444')">
        <n-input
          :value="String(optionData.unit ?? '')"
          @update:value="value => (optionData.unit = value)"
          size="small"
        ></n-input>
      </setting-item>

      <SettingItem>
        <n-space>
          <n-switch v-model:value="optionData.showValue" size="small"></n-switch>
          <n-text>{{ t('visualBigScreen.management-674210-1003') }}</n-text>
        </n-space>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-129')">
      <setting-item
        v-for="(item, index) in optionData.colors"
        :key="index"
        :name="`${t('visualBigScreen.management-674210-129')}${index}`"
      >
        <n-color-picker v-model:value="optionData.colors[index]" size="small" :modes="['hex']"></n-color-picker>
      </setting-item>
    </SettingItemBox>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'

import { option } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option & GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()
</script>
