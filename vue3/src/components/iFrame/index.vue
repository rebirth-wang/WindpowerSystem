<template>
  <div v-loading="loading" :style="'height:' + height">
    <iframe :src="src" frameborder="no" style="width: 100%; height: 100%" scrolling="auto" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const props = defineProps<{
  src: string;
}>();

const height = ref(document.documentElement.clientHeight - 94.5 + 'px');
const loading = ref(true);

onMounted(() => {
  setTimeout(() => {
    loading.value = false;
  }, 300);
  window.addEventListener('resize', () => {
    height.value = document.documentElement.clientHeight - 94.5 + 'px';
  });
});
</script>
