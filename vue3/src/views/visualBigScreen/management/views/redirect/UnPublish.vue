<template>
  <div class="go-redirect-un-publish">
    <div class="text-center">
      <img src="~@vb/assets/images/exception/nodata.svg" alt="" />
    </div>
    <div class="text-center">
      <h1>{{ t('visualBigScreen.management-674210-1678') }}</h1>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<style lang="scss" scoped>
@include go(redirect-un-publish) {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  padding: 100px 0;
  @include background-image('background-image');
  .text-center {
    h1 {
      color: #666;
      padding: 20px 0;
    }
  }

  img {
    width: 350px;
    margin: 0 auto;
  }
}
</style>
