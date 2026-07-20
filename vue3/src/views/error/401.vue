<template>
  <div class="error-page">
    <div class="error-code">
      4
      <span>0</span>
      1
    </div>
    <div class="error-desc">啊哦~ 你没有权限访问该页面</div>
    <div class="error-handle">
      <router-link to="/">
        <el-button type="primary" size="large">返回首页</el-button>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

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
