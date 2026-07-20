<template>
  <div class="error-page">
    <div class="error-code">
      4
      <span>0</span>
      4
    </div>
    <div class="error-desc">啊哦~ 你所访问的页面不存在</div>
    <div class="error-handle">
      <el-button type="primary" size="large" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  if (window.self !== window.top) {
    window.top.location.href = '/';
  } else {
    router.replace('/index');
  }
};
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 100%;
  height: calc(100vh - 50px);
  box-sizing: border-box;

  .error-code {
    line-height: 1;
    font-size: 200px;
    font-weight: bolder;
    color: #486ff2;
  }
  .error-code span {
    color: #262626;
  }
  .error-desc {
    font-size: 24px;
    color: #262626;
    font-weight: bold;
    margin: 30px 0;
  }
}
</style>
