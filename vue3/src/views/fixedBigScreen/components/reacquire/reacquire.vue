<template>
  <div class="reacquire flex justify-center blocks cursor-pointer" :style="{ lineHeight: lineHeight }" @click="getData">
    <span>重新获取</span>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  lineHeight?: string;
}>();

const emit = defineEmits<{
  (e: 'onclick', event: MouseEvent): void;
}>();

function getData(e: MouseEvent) {
  emit('onclick', e);
}
</script>

<style lang="scss" scoped>
.reacquire {
  user-select: none;
  color: rgb(168, 168, 168);
  span:hover {
    text-decoration: underline;
  }
}

.blocks {
  width: 100%;
  height: 100%;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
