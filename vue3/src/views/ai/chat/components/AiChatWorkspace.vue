<template>
  <div class="ai-chat-workspace">
    <AiChatPage />
  </div>
</template>

<script setup>
import AiChatPage from '../index.vue';
</script>

<style scoped>
.ai-chat-workspace {
  min-height: 0;
}

.ai-chat-workspace :deep(.ai-chat-page) {
  height: calc(100vh - 138px);
}

@media (max-width: 1280px) {
  .ai-chat-workspace :deep(.ai-chat-page) {
    height: auto;
  }
}
</style>
