<template>
  <collapse-item :name="t('visualBigScreen.management-674210-136')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-211')" :alone="true">
      <setting-item>
        <n-input
          :value="String(optionData.dataset ?? '')"
          @update:value="value => (optionData.dataset = value)"
          type="textarea"
          size="small"
        ></n-input>
      </setting-item>
    </setting-item-box>
  </collapse-item>

  <collapse-item :name="t('visualBigScreen.management-674210-242')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-211')">
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.fontColor"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="optionData.fontSize"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-244')">
        <n-select v-model:value="optionData.fontWeight" size="small" :options="fontWeightOptions" />
      </setting-item>

      <setting-item :name="t('visualBigScreen.management-674210-249')">
        <n-input-number
          v-model:value="optionData.letterSpacing"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-402')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-259')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.showShadow" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-260') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.colorShadow"></n-color-picker
      ></setting-item>
      <setting-item name="x">
        <n-input-number v-model:value="optionData.hShadow" size="small"></n-input-number
      ></setting-item>
      <setting-item name="y">
        <n-input-number v-model:value="optionData.vShadow" size="small"></n-input-number
      ></setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-261')">
        <n-input-number v-model:value="optionData.blurShadow" size="small"></n-input-number
      ></setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-158')">
      <setting-item :name="t('visualBigScreen.management-674210-262')">
        <n-input-number
          v-model:value="optionData.animationSpeed"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-262')"
          :min="0"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-263')">
        <n-input-number
          v-model:value="optionData.animationTime"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-263')"
          :min="0"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { option, FontWeightEnum, FontWeightObject, FontWeightLabelObject } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

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
