<template>
  <collapse-item :name="t('visualBigScreen.management-674210-971')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-919')">
      <setting-item :name="t('visualBigScreen.management-674210-958')">
        <n-input-number v-model:value="optionData.dataset" size="small" :min="0"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-959')">
        <n-input-number v-model:value="optionData.flipperLength" size="small" :min="1"></n-input-number>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-242')">
      <setting-item :name="t('visualBigScreen.management-674210-250')">
        <n-input-number v-model:value="optionData.flipperWidth" size="small" :min="1"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-336')">
        <n-input-number v-model:value="optionData.flipperHeight" size="small" :min="1"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-152')">
        <n-input-number v-model:value="optionData.flipperBorderWidth" size="small" :min="0" :max="10"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-251')">
        <n-input-number v-model:value="optionData.flipperRadius" size="small" :min="0"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-960')">
        <n-input-number v-model:value="optionData.flipperGap" size="small" :min="0"></n-input-number>
      </setting-item>
      <setting-item />
      <setting-item :name="t('visualBigScreen.management-674210-253')">
        <n-color-picker
          size="small"
          :show-alpha="false"
          :modes="['hex']"
          v-model:value="optionData.flipperBgColor"
        ></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-501')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.flipperTextColor"></n-color-picker>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-957')">
      <setting-item :name="t('visualBigScreen.management-674210-158')">
        <n-select
          v-model:value="optionData.flipperType"
          size="small"
          :options="[
            { label: t('visualBigScreen.management-674210-962'), value: 'down' },
            { label: t('visualBigScreen.management-674210-963'), value: 'up' }
          ]"
        ></n-select>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-961')">
        <n-input-number
          v-model:value="optionData.flipperSpeed"
          size="small"
          :min="100"
          :max="900"
          :step="100"
        ></n-input-number>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>
<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { OptionType } from './config'

defineProps({
  optionData: {
    type: Object as PropType<OptionType>,
    required: true
  }
})
const { t } = useI18n()
</script>
