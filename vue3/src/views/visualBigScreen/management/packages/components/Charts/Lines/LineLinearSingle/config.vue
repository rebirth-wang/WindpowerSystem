<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"></global-setting>
  <CollapseItem
    v-for="(item, index) in seriesList"
    :key="index"
    :name="t('visualBigScreen.management-674210-242')"
    :expanded="true"
  >
    <SettingItemBox :name="t('visualBigScreen.management-674210-295')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="item.lineStyle.color.colorStops[0].color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="item.lineStyle.color.colorStops[1].color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number
          v-model:value="item.lineStyle.width"
          :min="1"
          :max="100"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-995')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-268')">
        <n-select v-model:value="item.lineStyle.type" size="small" :options="lineConf.lineStyle.type"></n-select>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-998')">
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number
          v-model:value="item.symbolSize"
          :min="1"
          :max="100"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-995')"
        ></n-input-number>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-259')" :alone="true">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="item.lineStyle.shadowColor"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-949')">
      <SettingItem :name="t('visualBigScreen.management-674210-259')">
        <n-button size="small" @click="item.lineStyle.shadowColor = 'rgba(0, 0, 0, 0)'">
          {{ t('visualBigScreen.management-674210-1005') }}
        </n-button>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { lineConf } from '@vb/packages/chartConfiguration/echarts/index'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const seriesList = computed(() => {
  return props.optionData.series
})
</script>
