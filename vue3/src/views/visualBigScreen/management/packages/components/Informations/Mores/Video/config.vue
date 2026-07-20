<!-- eslint-disable vue/multi-word-component-names -->
<!-- eslint-disable vue/no-mutating-props -->
<template>
  <collapse-item :name="t('visualBigScreen.management-674210-219')" expanded>
    <setting-item-box :name="t('visualBigScreen.management-674210-306')" alone>
      <setting-item :name="t('visualBigScreen.management-674210-307')">
        <n-input
          :value="String(optionData.dataset ?? '')"
          @update:value="value => (optionData.dataset = value)"
          size="small"
        ></n-input>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-308')">
      <setting-item>
        <n-checkbox v-model:checked="optionData.loop" size="small">
          {{ t('visualBigScreen.management-674210-309') }}
        </n-checkbox>
      </setting-item>
      <setting-item>
        <n-checkbox v-model:checked="optionData.muted" size="small">
          {{ t('visualBigScreen.management-674210-310') }}
        </n-checkbox>
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-242')">
      <setting-item :name="t('visualBigScreen.management-674210-268')">
        <n-select v-model:value="optionData.fit" size="small" :options="fitList"></n-select>
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

// 适应类型
const fitList = [
  {
    value: 'fill',
    label: 'fill'
  },
  {
    value: 'contain',
    label: 'contain'
  },
  {
    value: 'cover',
    label: 'cover'
  },
  {
    value: 'scale-down',
    label: 'scale-down'
  },
  {
    value: 'none',
    label: 'none'
  }
]

defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
</script>
