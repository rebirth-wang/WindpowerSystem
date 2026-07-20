<template>
  <CollapseItem
    v-for="(item, index) in seriesList"
    :key="index"
    :name="t('visualBigScreen.management-674210-179')"
    :expanded="true"
  >
    <SettingItemBox :name="t('visualBigScreen.management-674210-919')">
      <SettingItem :name="t('visualBigScreen.management-674210-892')">
        <n-input-number
          v-model:value="item.data[0]"
          :min="0"
          :step="0.01"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-938')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-333')">
        <n-select
          v-model:value="item.shape"
          :options="shapes"
          :placeholder="t('visualBigScreen.management-674210-908')"
        />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-149')">
        <n-input-number
          v-model:value="item.label.normal.textStyle.fontSize"
          :min="0"
          :step="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-926')"
        >
        </n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-939')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="item.color[0].colorStops[0].color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-940')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="item.color[0].colorStops[1].color"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-252')" :alone="true">
      <SettingItem>
        <n-color-picker size="small" :modes="['hex']" v-model:value="item.backgroundStyle.color"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { option, shapes } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()

const seriesList = computed(() => {
  return props.optionData.series
})
</script>
