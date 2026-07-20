<template>
  <!-- 遍历 seriesList -->
  <CollapseItem
    v-for="(item, index) in config.series"
    :key="index"
    :name="t('visualBigScreen.management-674210-1027')"
    :expanded="true"
  >
    <SettingItemBox :name="t('visualBigScreen.management-674210-891')">
      <SettingItem :name="t('visualBigScreen.management-674210-892')">
        <n-input-number
          v-model:value="config.dataset"
          :min="0"
          :max="1"
          :step="0.01"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-892')"
        >
        </n-input-number>
      </SettingItem>
    </SettingItemBox>
    <!-- 中心标题 -->
    <SettingItemBox v-if="config.title" :name="t('visualBigScreen.management-674210-150')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="config.title.textStyle.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="config.title.textStyle.fontSize"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        >
        </n-input-number>
      </SettingItem>
    </SettingItemBox>
    <!-- Echarts 全局设置 -->
    <SettingItemBox :name="t('visualBigScreen.management-674210-918')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="item.data[0].itemStyle.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1028')">
        <n-input-number
          v-model:value="item.data[0].itemStyle.shadowBlur"
          :min="0"
          :max="50"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-1028')"
        >
        </n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1029')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="item.data[0].itemStyle.shadowColor"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <!-- 其他样式 -->
    <SettingItemBox :name="t('visualBigScreen.management-674210-921')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="item.data[1].itemStyle.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1028')">
        <n-input-number
          v-model:value="item.data[1].itemStyle.shadowBlur"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-1028')"
        >
        </n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1029')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="item.data[1].itemStyle.shadowColor"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1030')">
        <n-select
          v-model:value="item.radius[0]"
          size="small"
          :options="[
            { label: t('visualBigScreen.management-674210-1031'), value: '75%' },
            { label: t('visualBigScreen.management-674210-1032'), value: '60%' },
            { label: t('visualBigScreen.management-674210-1033'), value: '45%' },
            { label: t('visualBigScreen.management-674210-1034'), value: '30%' }
          ]"
        />
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
// 以下是封装的设置模块布局组件，具体效果可在官网查看
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
</script>
