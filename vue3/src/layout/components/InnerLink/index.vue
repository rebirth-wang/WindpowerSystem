<template>
  <div :style="'height:' + height" v-loading="loading" element-loading-text="正在加载页面，请稍候！">
    <iframe :id="iframeId" style="width: 100%; height: 100%" :src="src" frameborder="no"></iframe>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const props = defineProps({
  src: {
    type: String,
    default: '/',
  },
  iframeId: {
    type: String,
    default: '',
  },
});

const loading = ref(false);
const height = ref(document.documentElement.clientHeight - 94.5 + 'px');

onMounted(() => {
  const iframeSelector = ('#' + props.iframeId).replace(/\//g, '\\/');
  const iframe = document.querySelector(iframeSelector) as HTMLIFrameElement;
  if (iframe) {
    loading.value = true;
    iframe.onload = function () {
      loading.value = false;
    };
  }
});
</script>
