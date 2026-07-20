<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-135')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-267')">
      <SettingItem :name="t('visualBigScreen.management-674210-339')">
        <n-input-number
          v-model:value="optionData.rowNum"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-403')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-340')">
        <n-input-number
          v-model:value="optionData.waitTime"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-404')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-341')">
        <n-input
          v-model:value="optionData.unit"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-341')"
        ></n-input>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
      <SettingItem :name="t('visualBigScreen.management-674210-342')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-343')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.textColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-344')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.borderColor"></n-color-picker>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-345')">
      <SettingItem :name="t('visualBigScreen.management-674210-346')">
        <n-input-number size="small" v-model:value="optionData.indexFontSize" :min="12" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-347')">
        <n-input-number size="small" v-model:value="optionData.leftFontSize" :min="12" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-348')">
        <n-input-number size="small" v-model:value="optionData.rightFontSize" :min="12" />
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const { t } = useI18n()

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
</script>
