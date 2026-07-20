<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-919')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-893')">
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="optionData.timeSize" size="small" :min="1"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-448')">
        <n-select v-model:value="optionData.fontWeight" size="small" :options="fontWeightOptions" />
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-946')">
      <SettingItem :name="t('visualBigScreen.management-674210-249')">
        <n-input-number v-model:value="optionData.timeTextIndent" size="small" :min="1"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-951')">
        <n-input-number v-model:value="optionData.timeLineHeight" size="small" :min="1"></n-input-number>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-129')">
      <SettingItem :name="t('visualBigScreen.management-674210-952')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.timeColor"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-259')">
      <SettingItem>
        <n-space>
          <n-switch v-model:value="optionData.showShadow" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-260') }}</n-text>
        </n-space>
      </SettingItem>
      <SettingItem name="x">
        <n-input-number v-model:value="optionData.hShadow" size="small"></n-input-number
      ></SettingItem>
      <SettingItem name="y">
        <n-input-number v-model:value="optionData.vShadow" size="small"></n-input-number
      ></SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-261')">
        <n-input-number v-model:value="optionData.blurShadow" size="small"></n-input-number
      ></SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.colorShadow"></n-color-picker
      ></SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>
<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option, FontWeightEnum, FontWeightObject, FontWeightLabelObject } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()
const fontWeightOptions = [
  {
    label: FontWeightLabelObject[FontWeightEnum.NORMAL],
    value: FontWeightObject[FontWeightEnum.NORMAL]
  },
  {
    label: FontWeightLabelObject[FontWeightEnum.BOLD],
    value: FontWeightObject[FontWeightEnum.BOLD]
  }
]
</script>
