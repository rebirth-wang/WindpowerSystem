<template>
  <div ref="chartRef" :id="id" :class="className" :style="{ height: height, width: width }" />
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import tdTheme from './theme.json';

const props = defineProps<{
  className?: string;
  id?: string;
  width?: string;
  height?: string;
  options?: Record<string, any>;
}>();

const chartRef = ref<HTMLElement | null>(null);
let chart: echarts.ECharts | null = null;

// 注册主题
echarts.registerTheme('tdTheme', tdTheme as any);

watch(
  () => props.options,
  (newOptions) => {
    if (chart && newOptions) {
      // 设置true清空echart缓存
      chart.setOption(newOptions, true);
    }
  },
  { deep: true }
);

onMounted(() => {
  initChart();
});

onBeforeUnmount(() => {
  if (chart) {
    chart.dispose();
    chart = null;
  }
});

function initChart() {
  if (!chartRef.value) return;
  // 初始化echart
  chart = echarts.init(chartRef.value, 'tdTheme');
  if (props.options) {
    chart.setOption(props.options, true);
  }
}
</script>

<style></style>
