<template>
  <n-button quaternary @click="changeTheme" :title="t('visualBigScreen.management-674210-530')">
    <n-icon size="20" :depth="1">
      <moon-icon v-if="designStore.darkTheme"></moon-icon>
      <sunny-icon v-else></sunny-icon>
    </n-icon>
  </n-button>
</template>

<script lang="ts" setup>
import { useI18n } from 'vue-i18n'
import { useDesignStore } from '@vb/store/modules/designStore/designStore'
import { setHtmlTheme } from '@vb/utils'
import { icon } from '@vb/plugins/icon'

const { t } = useI18n()
const { MoonIcon, SunnyIcon } = icon.ionicons5
const designStore = useDesignStore()

// 改变样式
const changeTheme = () => {
  designStore.changeTheme()
  setHtmlTheme()
}
</script>
