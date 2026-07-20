<template>
  <div style="display: flex">
    <dv-active-ring-chart :config="config" style="width: 250px; height: 250px" />
    <div style="font-size: 14px; margin-top: 80px; line-height: 10px; margin-left: -20px">
      <div style="margin-bottom: 20px; color: #23cdd8" v-if="mqtt">
        <div style="margin-bottom: 20px; color: #23cdd8">
          发送消息总数：
          <span style="color: #fff">{{ formatter(stat['send_total']) }}</span>
          <dv-decoration-3 style="width: 200px; height: 20px; margin-top: 5px" />
        </div>
      </div>
      <div style="margin-bottom: 20px; color: #23cdd8" v-else>
        发送字节：
        <span style="color: #fff">{{ formatter(stat['bytes.sent']) }}</span>
        <dv-decoration-3 style="width: 200px; height: 20px; margin-top: 5px" />
      </div>
      <div style="margin-bottom: 20px; color: #23cdd8" v-if="mqtt">
        接收消息总数：
        <span style="color: #fff">{{ formatter(stat['receive_total']) }}</span>
        <dv-decoration-3 style="width: 200px; height: 20px; margin-top: 5px" />
      </div>
      <div style="margin-bottom: 20px; color: #23cdd8" v-else>
        接收字节：
        <span style="color: #fff">{{ formatter(stat['bytes.received']) }}</span>
        <dv-decoration-3 style="width: 200px; height: 20px; margin-top: 5px" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { statisticNettyMqtt } from '@/api/iot/netty';
import { useUserStore } from '@/stores/modules/user';

const userStore = useUserStore();
const mqtt = userStore.mqtt;

const stat = ref<Record<string, any>>({});
const config = ref<Record<string, any>>({});
let timer: ReturnType<typeof setInterval> | null = null;

onMounted(() => {
  statisticMqttData();
});

onBeforeUnmount(() => {
  clearData();
});

/** 查询emqx统计 */
function statisticMqttData() {
  if (mqtt) {
    statisticNettyMqtt().then((response: any) => {
      stat.value = response.data;
      config.value = {
        data: [
          { name: '发送', value: stat.value['send_total'] },
          { name: '接收', value: stat.value['receive_total'] },
        ],
        color: ['#ffdb5c', '#67e0e3'],
      };
      switper();
    });
  } else {
    config.value = {
      data: [
        { name: '发送', value: 32761563 },
        { name: '接收', value: 31910071 },
      ],
      color: ['#ffdb5c', '#67e0e3'],
    };
    switper();
  }
}

// 数字格式化
function formatter(number: any) {
  if (number) {
    const numbers = number.toString().split('').reverse();
    const segs: string[] = [];
    while (numbers.length) segs.push(numbers.splice(0, 3).join(''));
    return segs.join(',').split('').reverse().join('');
  }
  return 1024;
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
    statisticMqttData();
  }, 60000);
}
</script>
