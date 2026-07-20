<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-295')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-1046')">
      <SettingItem :name="t('visualBigScreen.management-674210-1047')">
        <n-input-number size="small" :min="0" v-model:value="optionData.lineNum"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1048')">
        <n-input-number size="small" :min="0" v-model:value="optionData.lineNumUp"></n-input-number>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-1049')">
      <SettingItem :name="t('visualBigScreen.management-674210-1050')">
        <n-input-number size="small" :min="1" v-model:value="optionData.lineWidth"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1051')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.backgroundCol"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-1052')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.animateCol"></n-color-picker>
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
