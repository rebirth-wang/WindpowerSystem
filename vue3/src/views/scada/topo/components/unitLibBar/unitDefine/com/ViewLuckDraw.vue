<template>
  <div class="view-luck-draw" :id="detail.identifier">
    <div class="luck-placeholder">
      <div class="luck-title">{{ detail.style?.title || '抽奖组件' }}</div>
      <div class="luck-desc">{{ editMode ? '编辑态预览' : '请配置抽奖内容' }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';

export default {
  name: 'ViewLuckDraw',
  extends: BaseView,
};
</script>

<style lang="scss" scoped>
.view-luck-draw {
  width: 100%;
  height: 100%;
}
.luck-placeholder {
  width: 100%;
  height: 100%;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.65);
  color: #909399;
}
.luck-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}
.luck-desc {
  font-size: 12px;
}
</style>
