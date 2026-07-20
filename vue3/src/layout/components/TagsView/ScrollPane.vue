<template>
  <el-scrollbar ref="scrollContainer" class="scroll-container" @wheel.prevent="handleScroll">
    <slot />
  </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';

const emit = defineEmits(['scroll']);
const scrollContainer = ref<any>(null);

const scrollWrapper = computed(() => {
  return scrollContainer.value?.wrapRef;
});

onMounted(() => {
  scrollWrapper.value?.addEventListener('scroll', emitScroll, true);
});

onBeforeUnmount(() => {
  scrollWrapper.value?.removeEventListener('scroll', emitScroll);
});

function handleScroll(e: WheelEvent) {
  const eventDelta = -e.deltaY * 40;
  const $scrollWrapper = scrollWrapper.value;
  if ($scrollWrapper) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollLeft + eventDelta / 4;
  }
}

function emitScroll() {
  emit('scroll');
}

function moveToTarget(currentTag: any) {
  const $container = scrollContainer.value?.$el;
  if (!$container) return;
  const $containerWidth = $container.offsetWidth;
  const $scrollWrapper = scrollWrapper.value;
  if (!$scrollWrapper) return;
  $scrollWrapper.scrollLeft = 0;
}

defineExpose({
  moveToTarget,
});
</script>

<style lang="scss" scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  width: 100%;

  :deep(.el-scrollbar__bar) {
    bottom: 0px;
  }

  :deep(.el-scrollbar__wrap) {
    height: 49px;
  }
}
</style>
