<template>
  <n-watermark
    id="go-edit-watermark"
    :content="t('visualBigScreen.management-674210-1706')"
    cross
    selectable
    :font-size="16"
    :line-height="16"
    :width="500"
    :height="150"
    :x-offset="12"
    :y-offset="80"
    :rotate="-15"
  ></n-watermark>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<style lang="scss" scoped>
#go-edit-watermark {
  display: none;
  position: absolute;
  top: 0;
  width: 100%;
  height: 100%;
}
</style>
