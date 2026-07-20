<template>
  <div class="go-error">
    <div class="text-center">
      <img src="~@vb/assets/images/exception/500.svg" alt="" />
    </div>
    <div class="text-center">
      <h1 class="text-base text-gray-500">{{ t('visualBigScreen.management-674210-1659') }}</h1>
    </div>
    <n-button type="primary" secondary @click="goLogin">{{ t('visualBigScreen.management-674210-1661') }}</n-button>
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from 'vue-i18n'
import { PageEnum } from '@vb/enums/pageEnum'
import { routerTurnByName } from '@vb/utils'

const { t } = useI18n()
function goLogin() {
  routerTurnByName(PageEnum.BASE_LOGIN_NAME)
}
</script>

<style lang="scss" scoped>
@include go(error) {
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
