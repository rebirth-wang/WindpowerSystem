<template>
  <div class="full-screen-wrap">
    <topo-render :defaultValue="selectedValue" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import TopoRender from './components/topoRender.vue';
import { getRouteQueryString } from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const selectedValue = ref(100);

onMounted(() => {
  if (!getRouteQueryString(route.query, 'guid')) {
    proxy?.$notify?.warning({
      title: proxy.$t('topo.topoRender.038944-3'),
      message: proxy.$t('scada.topo.fullscreen.764059-0'),
      position: 'top-left',
    });
  }
});
</script>

<style lang="scss" scoped>
.full-screen-wrap {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-height: 100vh;
  overflow: hidden;
}
</style>
