<template>
  <div class="messages" v-if="visible">
    <svg fill="none" viewBox="0 0 16 16" width="1em" height="1em" class="message-icon">
      <path
        fill="currentColor"
        d="M15 8A7 7 0 101 8a7 7 0 0014 0zM8.5 4v5.5h-1V4h1zm-1.1 7h1.2v1.2H7.4V11z"
        fill-opacity="0.9"
        v-if="'warning' == type"
      ></path>
    </svg>
    {{ text }}
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const visible = ref(false);
const text = ref('');
const type = ref('warning');
let timer: ReturnType<typeof setTimeout> | null = null;

function init(param: { text?: string; type?: string }) {
  if (timer) clearTimeout(timer);
  visible.value = true;
  text.value = param.text || '';
  type.value = param.type || 'success';
  timer = setTimeout(() => {
    visible.value = false;
    if (timer) clearTimeout(timer);
  }, 2000);
}

defineExpose({ init });
</script>

<style lang="scss" scoped>
.messages {
  position: fixed;
  min-width: 200px;
  top: 160px;
  left: 50%;
  transform: translate(-50%, 0);
  border: solid 1px #4b4b4b;
  width: fit-content;
  border-radius: 6px;
  display: flex;
  align-items: center;
  z-index: 999;
  color: rgba(255, 255, 255, 0.9);
  background-color: #242424;
  line-height: 22px;
  font-size: 14px;
  padding: 13px 16px;

  .message-icon {
    color: #cf6e2d;
    font-size: 20px;
    margin-right: 8px;
    fill: currentColor;
  }
}
</style>
