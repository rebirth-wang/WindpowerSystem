<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"> </global-setting>
  <!-- 漏斗图 -->
  <collapse-item
    v-for="(item, index) in seriesList"
    :key="index"
    :name="t('visualBigScreen.management-674210-147')"
    expanded
  >
    <setting-item-box :name="t('visualBigScreen.management-674210-896')" alone>
      <setting-item>
        <n-select v-model:value="item.sort" :options="FunnelOrderEnumList" size="small" />
      </setting-item>
    </setting-item-box>

    <SettingItemBox :name="t('visualBigScreen.management-674210-460')" :alone="true">
      <setting-item :name="`${t('visualBigScreen.management-674210-897')}：${item.top}px`">
        <n-slider v-model:value="item.top" :min="0" :max="300" :format-tooltip="sliderFormatTooltip"></n-slider>
      </setting-item>
    </SettingItemBox>

    <setting-item-box :name="t('visualBigScreen.management-674210-898')">
      <setting-item :name="t('visualBigScreen.management-674210-504')">
        <n-input-number v-model:value="item.itemStyle.borderWidth" :min="0" :max="10" size="small" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-372')">
        <n-color-picker v-model:value="item.itemStyle.borderColor" :modes="['hex']" size="small" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-946')">
        <n-input-number v-model:value="item.gap" :min="0" :max="20" size="small" />
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-447')">
      <setting-item :name="t('visualBigScreen.management-674210-533')">
        <n-checkbox v-model:checked="item.label.show" size="small">{{
          t('visualBigScreen.management-674210-447')
        }}</n-checkbox>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-322')">
        <n-select v-model:value="item.label.position" :options="FunnelLabelPositionEnumList" size="small" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="item.label.fontSize" :min="0" size="small" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-899')">
        <n-input-number v-model:value="item.emphasis.label.fontSize" :min="0" size="small" />
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { option, FunnelOrderEnumList, FunnelLabelPositionEnumList } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option & GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const seriesList = computed(() => {
  return props.optionData.series
})

const sliderFormatTooltip = (v: number) => {
  return `${v}px`
}
</script>
