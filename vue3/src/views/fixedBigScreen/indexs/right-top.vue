<template>
  <div style="padding-top: 10px">
    <dv-capsule-chart :config="config" style="width: 430px; height: 230px" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { statisticNettyMqtt } from '@/api/iot/netty';
import { useUserStore } from '@/stores/modules/user';

const userStore = useUserStore();
const mqtt = userStore.mqtt;

const config = ref<Record<string, any>>({});
let timer: ReturnType<typeof setInterval> | null = null;

onMounted(() => {
  statisticMqtt();
});

onBeforeUnmount(() => {
  clearData();
});

/** 查询mqtt统计 */
function statisticMqtt() {
  if (mqtt) {
    statisticNettyMqtt().then((response: any) => {
      const stat = response.data;
      config.value = {
        data: [
          { name: '今日接收', value: stat['today_received'] },
          { name: '今日发送', value: stat['today_send'] },
          { name: '订阅总数', value: stat['subscribe_total'] },
          { name: '发布消息', value: stat['send_total'] },
          { name: '接收消息', value: stat['receive_total'] },
          { name: '认证次数', value: stat['auth_total'] },
          { name: '认证成功', value: stat['auth_total'] },
        ],
        unit: '次数',
        showValue: true,
      };
      switper();
    });
  } else {
    config.value = {
      data: [
        { name: '今日接收', value: 6000 },
        { name: '今日发送', value: 5000 },
        { name: '订阅总数', value: 4000 },
        { name: '发布消息', value: 3000 },
        { name: '接收消息', value: 2000 },
        { name: '认证次数', value: 1000 },
        { name: '认证成功', value: 1000 },
      ],
      unit: '次数',
      showValue: true,
    };
    switper();
  }
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
    statisticMqtt();
  }, 60000);
}
</script>
