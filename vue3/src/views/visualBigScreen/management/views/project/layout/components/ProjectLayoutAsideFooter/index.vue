<template>
  <div class="go-aside-footer">
    <n-divider class="go-mt-0"></n-divider>
    <n-space justify="space-around">
      <n-tooltip v-if="collapsed" placement="right" trigger="hover">
        <template #trigger>
          <n-button secondary @click="handleDoc">
            <template #icon>
              <n-icon size="18">
                <document-text-icon></document-text-icon>
              </n-icon>
            </template>
          </n-button>
        </template>
        <n-text>
          {{ t('visualBigScreen.management-674210-36') }}
        </n-text>
      </n-tooltip>

      <n-button v-else secondary @click="handleDoc">
        <template #icon>
          <n-icon size="18">
            <document-text-icon></document-text-icon>
          </n-icon>
        </template>
        <n-text>{{ t('visualBigScreen.management-674210-36') }}</n-text>
      </n-button>

      <n-tooltip v-if="collapsed" placement="right" trigger="hover">
        <template #trigger>
          <n-button secondary @click="handleDoc">
            <template #icon>
              <n-icon size="18">
                <code-slash-icon></code-slash-icon>
              </n-icon>
            </template>
          </n-button>
        </template>
        <n-text>
          {{ t('visualBigScreen.management-674210-32') }}
        </n-text>
      </n-tooltip>

      <n-button v-else secondary @click="handleCode">
        <template #icon>
          <n-icon size="18">
            <code-slash-icon></code-slash-icon>
          </n-icon>
        </template>
        <n-text v-show="!collapsed">{{ t('visualBigScreen.management-674210-32') }}</n-text>
      </n-button>
    </n-space>
  </div>
</template>
<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { openDoc, openGiteeSourceCode } from '@vb/utils'

import { icon } from '@vb/plugins/icon'
const { DocumentTextIcon, CodeSlashIcon } = icon.ionicons5

const { t } = useI18n()

const props = defineProps({
  collapsed: Boolean
})

const handleDoc = () => {
  openDoc()
}
const handleCode = () => {
  openGiteeSourceCode()
}
</script>

<style lang="scss" scoped>
@include go('aside-footer') {
  padding-bottom: 20px;
}
</style>
