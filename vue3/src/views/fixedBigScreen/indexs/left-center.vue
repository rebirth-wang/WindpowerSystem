<template>
  <div>
    <div ref="statsChartRef" style="height: 240px; margin: 20px 0 40px 0"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import { getNettyMqttStats } from '@/api/iot/netty';
import { useUserStore } from '@/stores/modules/user';

const userStore = useUserStore();
const mqtt = userStore.mqtt;

const statsChartRef = ref<HTMLElement | null>(null);
let timer: ReturnType<typeof setInterval> | null = null;
let stats = ref<Record<string, any>>({});

onMounted(() => {
  getMqttStats();
});

onBeforeUnmount(() => {
  clearData();
});

/** 查询mqtt状态数据 */
function getMqttStats() {
  if (mqtt) {
    getNettyMqttStats().then((response: any) => {
      stats.value = response.data;
      drawStats();
      switper();
    });
  } else {
    nextTick(() => {
      // 初始值
      stats.value = {
        'connections.count': 800,
        'connections.max': 8000,
        'sessions.count': 700,
        'sessions.max': 7000,
        'topics.count': 600,
        'topics.max': 6000,
        'subscribers.count': 500,
        'subscribers.max': 5000,
        'routes.count': 400,
        'routes.max': 4000,
        'retained.count': 300,
        'retained.max': 3000,
      };
      drawStats();
      switper();
    });
  }
}

/** EMQX状态统计 */
function drawStats() {
  if (!statsChartRef.value) return;
  let myChart = echarts.init(statsChartRef.value);
  const s = stats.value;
  const option = {
    animationDuration: 3000,
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(58,73,116,0.7)',
      textStyle: { color: 'rgba(65,235,246,1)' },
    },
    legend: {
      textStyle: { color: 'rgba(65,235,246,1)' },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01],
      axisLabel: { fontSize: 12, color: '#fff' },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: 'rgba(65,235,246,1)',
          width: 0.5,
        },
      },
    },
    yAxis: {
      type: 'category',
      axisLabel: { fontSize: 12, color: '#fff' },
      data: mqtt
        ? ['连接数量', '会话数量', '订阅数量', '路由数量', '保留消息']
        : ['连接数量', '会话数量', '主题数量', '订阅数量', '路由数量', '保留消息'],
    },
    series: [
      {
        name: '当前数量',
        type: 'bar',
        data: mqtt
          ? [s['connection_count'], s['session_count'], s['subscription_count'], s['retain_count'], s['retain_count']]
          : [
              s['connections.count'],
              s['sessions.count'],
              s['topics.count'],
              s['subscribers.count'],
              s['routes.count'],
              s['retained.count'],
            ],
        itemStyle: { color: '#67e0e3' },
      },
      {
        name: mqtt ? '累计总数' : '历史最大数',
        type: 'bar',
        data: mqtt
          ? [s['connection_total'], s['session_total'], s['subscription_total'], s['retain_total'], s['retain_total']]
          : [
              s['connections.max'],
              s['sessions.max'],
              s['topics.max'],
              s['subscribers.max'],
              s['routes.max'],
              s['retained.max'],
            ],
        itemStyle: { color: '#ffdb5c' },
      },
    ],
  };
  myChart.setOption(option);
}

function clearData() {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
}

function switper() {
  if (timer) return;
  timer = setInterval(() => {
    getMqttStats();
  }, 60000);
}
</script>
