<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-211')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-919')" :alone="true">
      <SettingItem>
        <n-input
          :value="String(optionData.dataset ?? '')"
          @update:value="value => (optionData.dataset = value)"
          size="small"
        ></n-input>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.textColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="optionData.textSize" size="small" :min="12"></n-input-number>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>

  <CollapseItem :name="t('visualBigScreen.management-674210-242')" :expanded="true">
    <SettingItemBox
      :name="`${t('visualBigScreen.management-674210-129')}-${index + 1}`"
      v-for="(item, index) in optionData.colors"
      :key="index"
    >
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.colors[index]"></n-color-picker>
      </SettingItem>
      <SettingItem>
        <n-button size="small" @click="optionData.colors[index] = option.colors[index]">
          {{ t('visualBigScreen.management-674210-947') }}
        </n-button>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { option } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option & GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()
</script>
