<template>
  <div class="center_bottom">
    <div>
      <dv-scroll-board :config="config" style="width: 360px; height: 175px" />
    </div>
    <div style="display: flex; height: 115px; margin-top: 30px">
      <div>
        <dv-water-level-pond :config="configCpu" style="width: 115px; height: 100%" />
        <div style="text-align: center; margin-top: 10px; color: #23cdd8; font-weight: 600">CPU</div>
      </div>
      <div style="margin: 0 20px">
        <dv-water-level-pond :config="configMemery" style="width: 115px; height: 100%" />
        <div style="text-align: center; margin-top: 10px; color: #23cdd8; font-weight: 600">内存</div>
      </div>
      <div style="">
        <dv-water-level-pond :config="configDisk" style="width: 115px; height: 100%" />
        <div style="text-align: center; margin-top: 10px; color: #23cdd8; font-weight: 600">系统盘</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { getServer } from '@/api/monitor/server';

let timer: ReturnType<typeof setInterval> | null = null;

const config = ref<Record<string, any>>({});
const configCpu = ref({
  data: [50],
  shape: 'roundRect',
  formatter: '{value}%',
  waveHeight: 10,
});
const configMemery = ref({
  data: [50],
  shape: 'roundRect',
  formatter: '{value}%',
  waveHeight: 10,
});
const configDisk = ref({
  data: [50],
  shape: 'roundRect',
  formatter: '{value}%',
  waveHeight: 10,
});

onMounted(() => {
  getServerData();
});

onBeforeUnmount(() => {
  clearData();
});

/** 查询服务器信息 */
function getServerData() {
  getServer().then((response: any) => {
    const server = response.data;
    config.value = {
      rowNum: 6,
      oddRowBGC: '',
      evenRowBGC: '',
      columnWidth: [105, 230],
      data: [
        ['服务器名：', server.sys.computerName],
        ['服务器IP：', server.sys.computerIp],
        ['操作系统：', server.sys.osName],
        ['系统架构：', server.sys.osArch],
        ['CPU核心：', server.cpu.cpuNum],
        ['系统内存：', server.mem.total],
        ['Java名称：', server.jvm.name],
        ['Java版本：', server.jvm.version],
        ['启动时间：', server.jvm.startTime],
        ['运行时长：', server.jvm.runTime],
        ['运行内存：', server.jvm.used],
        ['JVM总内存：', server.jvm.total],
      ],
    };
    // 计算CPU使用
    let cpu = ((server.cpu.used + server.cpu.sys) / (server.cpu.used + server.cpu.sys + server.cpu.free)) * 100;
    configCpu.value = {
      data: [parseFloat(cpu.toFixed(1)), parseFloat(cpu.toFixed(1)) - 10],
      shape: 'roundRect',
      formatter: '{value}%',
      waveHeight: 10,
    };
    // 计算内存
    let memery = (server.mem.used / (server.mem.used + server.mem.free)) * 100;
    configMemery.value = {
      data: [parseFloat(memery.toFixed(1)), parseFloat(memery.toFixed(1)) - 10],
      shape: 'roundRect',
      formatter: '{value}%',
      waveHeight: 10,
    };
    // 计算硬盘
    let disk =
      (Number(server.sysFiles[0].used.replace('GB', '')) /
        (Number(server.sysFiles[0].used.replace('GB', '')) + Number(server.sysFiles[0].free.replace('GB', '')))) *
      100;
    configDisk.value = {
      data: [parseFloat(disk.toFixed(1)), parseFloat(disk.toFixed(1)) - 10],
      shape: 'roundRect',
      formatter: '{value}%',
      waveHeight: 10,
    };
    // 轮询
    switper();
  });
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
    getServerData();
  }, 60000);
}
</script>

<style lang="scss" scoped>
.center_bottom {
  width: 100%;
  height: 100%;
  padding: 10px;
  display: flex;
}
</style>
