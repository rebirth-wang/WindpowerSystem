<template>
  <n-dropdown trigger="hover" @select="handleSelect" :show-arrow="true" :options="options">
    <n-button quaternary>
      <n-icon size="20" :depth="1">
        <language-icon></language-icon>
      </n-icon>
    </n-button>
  </n-dropdown>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { langs } from '@/lang'
import { addOrUpdate } from '@/api/system/language'
import { getToken } from '@/utils/auth'
import defaultSettings from '@/settings'
import { useSettingsStore } from '@/stores/modules/settings'
import { useSettingStore } from '@vb/store/modules/settingStore/settingStore'
import { icon } from '@vb/plugins/icon'

const { LanguageIcon } = icon.ionicons5
const { locale } = useI18n()
const settingsStore = useSettingsStore()
const vbSettingStore = useSettingStore()
const options = Object.entries(langs).map(([key, item]) => ({
  key,
  label: item.language
}))

const setLanguage = () => {
  const lang = (locale.value as string) || defaultSettings.language
  if (getToken()) addOrUpdate({ language: lang })
  settingsStore.setLang(lang)
}

const reloadWithoutUnloadPrompt = () => {
  window.onbeforeunload = null
  window.location.reload()
}

const handleSelect = (key: string) => {
  locale.value = key
  setLanguage()
  if (vbSettingStore.getChangeLangReload) {
    reloadWithoutUnloadPrompt()
  }
}

onMounted(() => {
  setLanguage()
})
</script>
