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
      <setting-item :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="optionData.size"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-254')">
      <setting-item :name="t('visualBigScreen.management-674210-255')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.gradient.from"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-256')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.gradient.to"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-257')">
        <n-input-number
          v-model:value="optionData.gradient.deg"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-258')"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { option } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
</script>
