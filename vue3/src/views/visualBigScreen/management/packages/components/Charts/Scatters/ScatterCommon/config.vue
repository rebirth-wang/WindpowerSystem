<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData" :in-chart="true"></global-setting>

  <CollapseItem
    :name="`${t('visualBigScreen.management-674210-1035')}-${index + 1}`"
    expanded
    v-for="(item, index) in optionData.series"
    :key="index"
  >
    <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
      <SettingItem :name="t('visualBigScreen.management-674210-268')">
        <n-select
          v-model:value="item.type"
          size="small"
          :options="ScatterEffectTypeEnumList"
          :placeholder="t('visualBigScreen.management-674210-1036')"
        />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="item.symbolSize" size="small" :min="1"></n-input-number>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-1037')">
      <SettingItem :name="t('visualBigScreen.management-674210-1038')">
        <n-input-number v-model:value="item.markArea.itemStyle.borderWidth" size="small" :min="0"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1039')">
        <n-select
          v-model:value="item.markArea.itemStyle.borderType"
          size="small"
          :options="axisConfig.splitLint.lineStyle.type"
          :placeholder="t('visualBigScreen.management-674210-1036')"
        />
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-1040')">
      <SettingItem :name="t('visualBigScreen.management-674210-333')">
        <n-select
          v-model:value="item.markPoint.symbol"
          size="small"
          :options="SymbolEnumList"
          :placeholder="t('visualBigScreen.management-674210-1036')"
        />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="item.markPoint.symbolSize" size="small" :min="0"></n-input-number>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'
import { ScatterEffectTypeEnumList, SymbolEnumList } from '../shard'
import { axisConfig } from '@vb/packages/chartConfiguration/echarts/index'

// eslint-disable-next-line no-unused-vars
const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType & typeof option>,
    required: true
  }
})
const { t } = useI18n()
</script>
