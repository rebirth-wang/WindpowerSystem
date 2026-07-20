<template>
  <setting-item-box :name="t('visualBigScreen.management-674210-1080')">
    <n-input-number v-model:value="chartAttr.w" :min="50" :disabled="isGroup" size="small" placeholder="px">
      <template #prefix>
        <n-text depth="3">{{ t('visualBigScreen.management-674210-1081') }}</n-text>
      </template>
    </n-input-number>
    <n-input-number v-model:value="chartAttr.h" :min="50" :disabled="isGroup" size="small" placeholder="px">
      <template #prefix>
        <n-text depth="3">{{ t('visualBigScreen.management-674210-1082') }}</n-text>
      </template>
    </n-input-number>
  </setting-item-box>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { PickCreateComponentType } from '@vb/packages/index.d'
import { SettingItemBox } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

const props = defineProps({
  chartAttr: {
    type: Object as PropType<Omit<PickCreateComponentType<'attr'>, 'node' | 'conNode'>>,
    required: true
  },
  isGroup: {
    type: Boolean,
    required: false
  }
})
</script>
