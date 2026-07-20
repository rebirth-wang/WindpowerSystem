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
    <setting-item-box :name="t('visualBigScreen.management-674210-240')" :alone="true">
      <setting-item>
        <n-input-group>
          <n-select
            v-model:value="optionData.linkHead"
            size="small"
            :style="{ width: '80%' }"
            :options="linkHeadOptions"
          />
          <n-input
            :value="String(optionData.link ?? '')"
            @update:value="value => (optionData.link = value)"
            size="small"
          ></n-input>
          <n-button :disabled="!optionData.link" secondary size="small" @click="handleLinkClick">
            {{ t('visualBigScreen.management-674210-241') }}
          </n-button>
        </n-input-group>
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
      <setting-item :name="t('visualBigScreen.management-674210-245')">
        <n-input-number
          v-model:value="optionData.paddingX"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-401')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-246')">
        <n-input-number
          v-model:value="optionData.paddingY"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-401')"
        ></n-input-number>
      </setting-item>

      <setting-item :name="t('visualBigScreen.management-674210-247')">
        <n-select v-model:value="optionData.textAlign" size="small" :options="textAlignOptions" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-248')">
        <n-select v-model:value="optionData.writingMode" size="small" :options="verticalOptions" />
      </setting-item>

      <setting-item :name="t('visualBigScreen.management-674210-249')">
        <n-input-number
          v-model:value="optionData.letterSpacing"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-402')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-152')">
      <setting-item :name="t('visualBigScreen.management-674210-250')">
        <n-input-number
          v-model:value="optionData.borderWidth"
          size="small"
          :min="0"
          :placeholder="t('visualBigScreen.management-674210-250')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.borderColor"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-251')">
        <n-input-number
          v-model:value="optionData.borderRadius"
          size="small"
          :min="0"
          :placeholder="t('visualBigScreen.management-674210-251')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-252')" :alone="true">
      <setting-item :name="t('visualBigScreen.management-674210-253')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.backgroundColor"></n-color-picker>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  option,
  WritingModeEnum,
  WritingModeObject,
  WritingModeLabelObject,
  FontWeightEnum,
  FontWeightObject,
  FontWeightLabelObject
} from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()
const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

const textAlignOptions = [
  { label: t('visualBigScreen.management-674210-375'), value: 'start' },
  { label: t('visualBigScreen.management-674210-376'), value: 'center' },
  { label: t('visualBigScreen.management-674210-377'), value: 'end' }
]

const verticalOptions = [
  {
    label: WritingModeLabelObject[WritingModeEnum.HORIZONTAL],
    value: WritingModeObject[WritingModeEnum.HORIZONTAL]
  },
  {
    label: WritingModeLabelObject[WritingModeEnum.VERTICAL],
    value: WritingModeObject[WritingModeEnum.VERTICAL]
  }
]
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
const handleLinkClick = () => {
  window.open(props.optionData.linkHead + props.optionData.link)
}
const linkHeadOptions = [
  { label: 'http://', value: 'http://' },
  { label: 'https://', value: 'https://' }
]
</script>
