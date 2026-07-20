<template>
  <!-- 遍历 seriesList -->
  <CollapseItem :name="t('visualBigScreen.management-674210-171')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-891')">
      <SettingItem :name="t('visualBigScreen.management-674210-892')">
        <n-input-number
          v-model:value="config.dataset"
          :min="dialConfig.min"
          :max="dialConfig.max"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-892')"
        >
        </n-input-number>
      </SettingItem>
    </SettingItemBox>
    <!-- Echarts 全局设置 -->
    <!-- 表盘刻度字体 -->
    <SettingItemBox :name="t('visualBigScreen.management-674210-893')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="dialConfig.axisLabel.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="dialConfig.axisLabel.fontSize"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        >
        </n-input-number>
      </SettingItem>
    </SettingItemBox>
    <!-- 表盘 -->
    <SettingItemBox :name="t('visualBigScreen.management-674210-895')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="config.series[1].axisLine.lineStyle.color[1][1]"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <!-- 指针 -->
    <SettingItemBox :name="t('visualBigScreen.management-674210-894')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="dialConfig.data[0].itemStyle.color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number
          v-model:value="dialConfig.pointer.width"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-892')"
        >
        </n-input-number>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-451')">
      <SettingItem :name="t('visualBigScreen.management-674210-461')">
        <n-input-number
          v-model:value="dialConfig.min"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-892')"
        >
        </n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-462')">
        <n-input-number
          v-model:value="dialConfig.max"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-892')"
        >
        </n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="config.series[1].axisTick.lineStyle.color"
          @update:value="updateClick"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes'

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const config = computed(() => {
  return props.optionData
})

const dialConfig = computed(() => {
  return props.optionData.series[0]
})

const updateClick = (val: any) => {
  props.optionData.series[1].splitLine.lineStyle.color = val
}
</script>
