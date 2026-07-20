<template>
  <div style="display: flex; margin-top: 60px">
    <div style="margin-left: 10px; color: #32c5e9">
      <dv-decoration-9 style="width: 90px; height: 90px; font-weight: 600" :dur="10" :color="['#32c5e9', '#238aa4']">
        {{ deviceStatistic.deviceCount }}
      </dv-decoration-9>
      <div style="text-align: center; margin-top: 25px">设备总数</div>
    </div>
    <div style="margin: 0 30px; color: #9fe6b8">
      <dv-decoration-9 style="width: 90px; height: 90px; font-weight: 600" :dur="10" :color="['#9fe6b8', '#70a181']">
        {{ deviceStatistic.deviceOnlineCount }}
      </dv-decoration-9>
      <div style="text-align: center; margin-top: 25px; font-weight: 400">在线设备</div>
    </div>
    <div style="margin-right: 30px; color: #ffdb5c">
      <dv-decoration-9 style="width: 90px; height: 90px; font-weight: 600" :dur="10" :color="['#ffdb5c', '#b39a41']">
        {{ deviceStatistic.deviceOfflineCount }}
      </dv-decoration-9>
      <div style="text-align: center; margin-top: 25px; font-weight: 400">离线设备</div>
    </div>
    <div style="color: #fb7293">
      <dv-decoration-9 style="width: 90px; height: 90px; font-weight: 600" :dur="10" :color="['#fb7293', '#ad4f65']">
        {{ deviceStatistic.alertCount }}
      </dv-decoration-9>
      <div style="text-align: center; margin-top: 25px; font-weight: 400">告警数量</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { getDeviceCountStatistic } from '@/api/iot/device';

const deviceStatistic = ref({
  deviceCount: 0,
  deviceOnlineCount: 0,
  deviceOfflineCount: 0,
  alertCount: 0,
});
let timer: ReturnType<typeof setInterval> | null = null;

onMounted(() => {
  getDeviceStatic();
});

onBeforeUnmount(() => {
  clearData();
});

function clearData() {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
}

function getDeviceStatic() {
  getDeviceCountStatistic().then((res: any) => {
    if (res.code == 200) {
      deviceStatistic.value = res.data;
      switper();
    }
  });
}

function switper() {
  if (timer) return;
  timer = setInterval(() => {
    getDeviceStatic();
  }, 60000);
}
</script>
