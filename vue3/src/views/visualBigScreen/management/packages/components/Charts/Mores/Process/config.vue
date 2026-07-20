<template>
  <!-- 默认展开 -->
  <CollapseItem :name="t('visualBigScreen.management-674210-918')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-919')">
      <SettingItem :name="t('visualBigScreen.management-674210-892')">
        <!-- 与 config.ts 里的 option 对应 --><!-- n-input-number 是 NaiveUI 的控件 -->
        <n-input-number
          v-model:value="optionData.dataset"
          size="small"
          :min="0"
          :placeholder="t('visualBigScreen.management-674210-920')"
        ></n-input-number>
      </SettingItem>
      <setting-item :name="t('visualBigScreen.management-674210-444')">
        <n-input
          :value="String(optionData.unit ?? '')"
          @update:value="value => (optionData.unit = value)"
          size="small"
        ></n-input>
      </setting-item>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-921')">
      <SettingItem :name="t('visualBigScreen.management-674210-333')">
        <n-select
          v-model:value="optionData.type"
          :options="types"
          :placeholder="t('visualBigScreen.management-674210-908')"
        />
      </SettingItem>

      <!-- 颜色粗细等等... -->
      <SettingItem :name="t('visualBigScreen.management-674210-922')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-923')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.railColor"></n-color-picker>
      </SettingItem>
      <setting-item :name="t('visualBigScreen.management-674210-257')" v-if="optionData.type !== types[0].value">
        <n-input-number v-model:value="optionData.offsetDegree" size="small"></n-input-number>
      </setting-item>
      <SettingItem v-if="optionData.type == types[0].value">
        <n-space>
          <n-switch v-model:value="optionData.processing" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-924') }}</n-text>
        </n-space>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-321')">
      <SettingItem :name="t('visualBigScreen.management-674210-322')" v-if="optionData.type == types[0].value">
        <n-select v-model:value="optionData.indicatorPlacement" :options="indicatorPlacements" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-925')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.indicatorTextColor"></n-color-picker>
      </SettingItem>
      <setting-item :name="t('visualBigScreen.management-674210-926')">
        <n-input-number v-model:value="optionData.indicatorTextSize" size="small"></n-input-number>
      </setting-item>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
// 以下是封装的设置模块布局组件，具体效果可在官网查看
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

// 获取 option 的数据，便于使用 typeof 获取类型
import { option, types, indicatorPlacements } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()
</script>
