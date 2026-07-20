<template>
  <n-config-provider
    :theme="darkTheme"
    :hljs="hljsTheme"
    :theme-overrides="overridesTheme"
    :locale="locale"
    :date-locale="dateLocale"
  >
    <suspense>
      <suspense-index></suspense-index>
    </suspense>
  </n-config-provider>
</template>

<script setup lang="ts">
import { NConfigProvider } from 'naive-ui'
import { useCode, useDarkThemeHook, useThemeOverridesHook, useLang } from '@vb/hooks'
import suspenseIndex from './suspenseIndex.vue'

const darkTheme = useDarkThemeHook()
const hljsTheme = useCode()
const overridesTheme = useThemeOverridesHook()
const { locale, dateLocale } = useLang()
</script>
