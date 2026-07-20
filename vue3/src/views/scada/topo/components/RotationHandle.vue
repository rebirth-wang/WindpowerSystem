<template>
  <div class="rotation-handle" @mousedown="onMouseDown" :title="t('scada.topo.components.rotationHandle.764059-0')">
    <div class="rotation-handle-icon"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps({
  angle: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(['rotate-start', 'rotating', 'rotate-end']);

const isDragging = ref(false);
const startAngle = ref(0);
const centerX = ref(0);
const centerY = ref(0);

const el = ref<HTMLElement | null>(null);

function onMouseDown(e: MouseEvent) {
  e.stopPropagation();
  isDragging.value = true;
  startAngle.value = props.angle;

  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
  centerX.value = rect.left + rect.width / 2;
  centerY.value = rect.top + rect.height / 2;

  emit('rotate-start', startAngle.value);

  document.addEventListener('mousemove', onMouseMove);
  document.addEventListener('mouseup', onMouseUp);
}

function onMouseMove(e: MouseEvent) {
  if (!isDragging.value) return;

  const deltaX = e.clientX - centerX.value;
  const deltaY = e.clientY - centerY.value;

  const radians = Math.atan2(deltaY, deltaX);
  let degrees = radians * (180 / Math.PI) + 90;

  if (degrees < 0) {
    degrees += 360;
  }

  emit('rotating', degrees);
}

function onMouseUp(e: MouseEvent) {
  if (!isDragging.value) return;

  isDragging.value = false;
  document.removeEventListener('mousemove', onMouseMove);
  document.removeEventListener('mouseup', onMouseUp);

  const deltaX = e.clientX - centerX.value;
  const deltaY = e.clientY - centerY.value;

  const radians = Math.atan2(deltaY, deltaX);
  let degrees = radians * (180 / Math.PI) + 90;

  if (degrees < 0) {
    degrees += 360;
  }

  emit('rotate-end', { startAngle: startAngle.value, endAngle: degrees });
}

onBeforeUnmount(() => {
  document.removeEventListener('mousemove', onMouseMove);
  document.removeEventListener('mouseup', onMouseUp);
});
</script>

<style scoped lang="scss">
.rotation-handle {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 20px;
  background-color: #486ff2;
  border: 2px solid #ffffff;
  border-radius: 50%;
  cursor: grab;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
}

.rotation-handle:hover {
  background-color: #5a7cff;
  transform: translateX(-50%) scale(1.1);
}

.rotation-handle:active {
  cursor: grabbing;
}

.rotation-handle-icon {
  width: 10px;
  height: 10px;
  border: 2px solid #ffffff;
  border-right-color: transparent;
  border-bottom-color: transparent;
  border-radius: 50%;
  transform: rotate(45deg);
}
</style>
