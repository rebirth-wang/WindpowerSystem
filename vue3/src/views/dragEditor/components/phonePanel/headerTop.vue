<template>
  <div class="header-top" :style="{ height: pageSetup.titleHeight + 'px' }">
    <div class="lef" v-show="showBack">
      <drag-icon name="ArrowLeft" :size="14" />
    </div>
    <div
      class="header-title"
      :style="{
        height: pageSetup.titleHeight + 'px',
        'line-height': pageSetup.titleHeight + 'px',
      }"
    >
      {{ pageSetup.name }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import DragIcon from '@/components/DragEditor/DragIcon.vue';

const props = defineProps<{ pageSetup: any }>();

const showBack = computed(() => {
  const val = props.pageSetup?.isBack;
  if (typeof val === 'boolean') return val;
  if (typeof val === 'string') return val === 'true' || val === '1';
  if (typeof val === 'number') return val === 1;
  return !!val;
});
</script>

<style lang="scss" scoped>
.header-top {
  height: 35px;
  width: 100%;
  background: #fff;
  display: flex;
  padding: 0 5px;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  border-bottom: 1px solid #f7f8fa;
  position: relative;

  .lef {
    position: absolute;
    left: 18px;
    top: 50%;
    transform: translateY(-50%);
    span {
      font-weight: 400;
      font-size: 12px;
      color: #555;
    }
  }

  .header-title {
    width: 100%;
    text-align: center;
    font-size: 14px;
    line-height: 35px;
    color: #333333;
  }
}
</style>
