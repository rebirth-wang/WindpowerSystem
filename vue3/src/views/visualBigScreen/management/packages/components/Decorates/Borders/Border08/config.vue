<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-152')" :expanded="true">
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
  <CollapseItem :name="t('visualBigScreen.management-674210-158')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-949')">
      <SettingItem :name="`${t('visualBigScreen.management-674210-520')}(s)`">
        <n-input-number v-model:value="optionData.dur" size="small" :step="0.5" :min="0.5"></n-input-number>
      </SettingItem>
      <SettingItem>
        <n-space>
          <n-switch size="small" v-model:value="optionData.reverse" />
          <n-text>{{ t('visualBigScreen.management-674210-950') }}</n-text>
        </n-space>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
  <CollapseItem :name="t('visualBigScreen.management-674210-252')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-129')">
      <SettingItem>
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.backgroundColor"></n-color-picker>
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
