<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-919')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-892')">
      <SettingItem :name="t('visualBigScreen.management-674210-256')">
        <n-input-number v-model:value="optionData.dataset" size="small" :min="1"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="optionData.numberSize" size="small" :min="1"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-953')">
        <n-input-number v-model:value="optionData.precision" size="small" :min="0"></n-input-number>
      </SettingItem>
      <SettingItem>
        <n-space>
          <n-switch v-model:value="optionData.showSeparator" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-954') }}</n-text>
        </n-space>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-892')">
      <SettingItem :name="t('visualBigScreen.management-674210-955')">
        <n-input
          :value="String(optionData.prefixText ?? '')"
          @update:value="value => (optionData.prefixText = value)"
          size="small"
        ></n-input>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-956')">
        <n-input
          :value="String(optionData.suffixText ?? '')"
          @update:value="value => (optionData.suffixText = value)"
          size="small"
        ></n-input>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-129')">
      <SettingItem :name="t('visualBigScreen.management-674210-955')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.prefixColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-956')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.suffixColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-892')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.numberColor"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>

  <CollapseItem :name="t('visualBigScreen.management-674210-158')" :expanded="true">
    <SettingItemBox :name="`${t('visualBigScreen.management-674210-520')}(s)`">
      <SettingItem>
        <n-input-number v-model:value="optionData.dur" size="small" :step="0.5" :min="0.5"></n-input-number>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()
</script>
