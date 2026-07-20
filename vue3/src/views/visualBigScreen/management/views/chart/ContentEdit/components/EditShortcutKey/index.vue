<template>
  <div class="go-edit-shortcut">
    <shortcut-key-modal v-model:modelShow="modelShow"></shortcut-key-modal>
    <n-tooltip trigger="hover">
      <template #trigger>
        <n-button class="scale-btn" secondary size="small" @click="modelShow = true">
          <n-icon size="21" :depth="3">
            <dicom-overlay-icon></dicom-overlay-icon>
          </n-icon>
        </n-button>
      </template>
      <span>{{ t('visualBigScreen.management-674210-71') }}</span>
    </n-tooltip>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { icon } from '@vb/plugins/icon'
import ShortcutKeyModal from './ShortcutKeyModal.vue'

const { t } = useI18n()
const { DicomOverlayIcon } = icon.carbon
const modelShow = ref<boolean>(false)
</script>

<style lang="scss" scoped>
.go-edit-shortcut {
  .scale-btn {
    margin: 0 10px 0 0;

    .btn-text {
      font-size: 12px;
      margin-right: 3px;
    }
  }
}
</style>
