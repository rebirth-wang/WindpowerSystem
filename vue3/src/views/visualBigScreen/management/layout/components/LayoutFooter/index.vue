<template>
  <div class="go-footer">
    <slot>
      <n-space :size="50">
        <n-text depth="2" v-if="isDoc">
          <n-a>{{ t('visualBigScreen.management-674210-98') }}: </n-a>
          <n-a italic :href="docPath" target="_blank">
            {{ docPath }}
          </n-a>
        </n-text>
        <n-text depth="3" v-if="copyRight">
          <n-a italic href="https://beian.miit.gov.cn/" target="_blank">
            滇ICP备2023000466号-6
          </n-a>
        </n-text>
      </n-space>
    </slot>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { docPath } from '@vb/settings/pathConst'

const { t } = useI18n()

const props = defineProps({
  isDoc: {
    type: Boolean,
    default: true
  },
  copyRight: {
    type: Boolean,
    required: true
  }
})
</script>

<style lang="scss" scoped>
@include go(footer) {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 0 40px;
  height: $--footer-height;
}
</style>
