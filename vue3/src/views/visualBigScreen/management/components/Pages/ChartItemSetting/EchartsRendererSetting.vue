<template>
  <n-radio-group :value="props.modelValue || INHERIT_VALUE" @update:value="handleChange">
    <n-space>
      <n-tooltip :show-arrow="false" trigger="hover" v-for="item in rendererList" :key="item.value">
        <template #trigger>
          <n-radio :value="item.value">
            {{ item.value }}
          </n-radio>
        </template>
        {{ item.desc }}
      </n-tooltip>
    </n-space>
  </n-radio-group>
</template>
<script setup lang="ts">
import { type EchartsRenderer } from '@vb/settings/chartThemes'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps<{ modelValue?: EchartsRenderer; includeInherit?: boolean }>()
const emits = defineEmits(['update:modelValue'])

const INHERIT_VALUE = 'inherit'

const handleChange = (val: EchartsRenderer & typeof INHERIT_VALUE) => {
  emits('update:modelValue', val === INHERIT_VALUE ? undefined : val)
}

const rendererList = [
  {
    value: 'svg',
    desc: t('visualBigScreen.management-674210-1094')
  },
  {
    value: 'canvas',
    desc: t('visualBigScreen.management-674210-1095')
  },
  ...(props.includeInherit
    ? [
        {
          value: INHERIT_VALUE,
          desc: t('visualBigScreen.management-674210-1096')
        }
      ]
    : [])
]
</script>
