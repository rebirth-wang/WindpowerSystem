<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"></global-setting>
  <CollapseItem
    v-for="(item, index) in seriesList"
    :key="index"
    :name="`${t('visualBigScreen.management-674210-140')}-${index + 1}`"
    :expanded="true"
  >
    <SettingItemBox :name="t('visualBigScreen.management-674210-994')">
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number
          v-model:value="item.barWidth"
          :min="1"
          :max="100"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-995')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-251')">
        <n-input-number v-model:value="item.itemStyle.borderRadius" :min="0" size="small"></n-input-number>
      </SettingItem>
    </SettingItemBox>
    <setting-item-box :name="t('visualBigScreen.management-674210-447')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="item.label.show" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-996') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="item.label.fontSize" size="small" :min="1"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="item.label.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-322')">
        <n-select
          v-model:value="item.label.position"
          :options="[
            { label: t('visualBigScreen.management-674210-569'), value: 'top' },
            { label: t('visualBigScreen.management-674210-571'), value: 'left' },
            { label: t('visualBigScreen.management-674210-572'), value: 'right' },
            { label: t('visualBigScreen.management-674210-570'), value: 'bottom' }
          ]"
        />
      </setting-item>
    </setting-item-box>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { GlobalSetting, CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'

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
