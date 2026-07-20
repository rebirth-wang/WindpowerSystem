<template>
  <div class="go-project-template-market">
    <n-space vertical>
      <n-image object-fit="contain" height="300" preview-disabled :src="requireErrorImg()"></n-image>
      <n-h3>{{ t('visualBigScreen.management-674210-1675') }}</n-h3>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { requireErrorImg } from '@vb/utils'
const { t } = useI18n()
</script>

<style lang="scss" scoped>
@include go('project-template-market') {
  margin-top: 100px;
  @extend .go-flex-center;
}
</style>
