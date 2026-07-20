<template>
  <div class="go-redirect">
    <n-empty :description="t('visualBigScreen.management-674210-1676')">
      <template #extra>
        <n-button size="small" @click="goHome">{{ t('visualBigScreen.management-674210-1677') }}</n-button>
      </template>
    </n-empty>
  </div>
</template>
<script lang="ts" setup>
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const goHome = () => {
  router.replace({ path: '/' })
}
</script>
<style lang="scss" scoped>
@include go(redirect) {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  padding: 100px 0;
  @include background-image('background-image');
}
</style>
