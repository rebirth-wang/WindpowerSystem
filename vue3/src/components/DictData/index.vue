<template>
  <div class="dict-data">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { provide, ref } from 'vue';

const dictData = ref<Record<string, any[]>>({});

provide('dictData', dictData);

const install = () => {
  console.log('DictData installed');
};

defineExpose({
  install,
});
</script>
