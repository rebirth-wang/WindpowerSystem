<template>
  <div v-if="pageflag" style="overflow: hidden; width: 100%; height: 250px; font-size: 12px; line-height: 24px">
    <Vue3SeamlessScroll
      :list="deviceLogList"
      :step="2"
      :limitScrollNum="4"
      :hover="true"
      :singleHeight="280"
      :isWatch="true"
      :singleWaitTime="3000"
    >
      <div
        style="display: flex; margin-bottom: 10px; border-bottom: 2px dashed #23cdd8; color: #bbb"
        v-for="(item, i) in deviceLogList"
        :key="i"
      >
        <span style="width: 50px; color: #23cdd8; font-size: 16px; line-height: 48px; font-weight: bolder">
          {{ i + 1 }}
        </span>
        <div style="display: flex; flex-wrap: wrap">
          <div style="width: 200px">
            <span>设备编号：</span>
            <span style="color: #23cdd8">{{ item.serialNumber }}</span>
          </div>
          <div style="width: 150px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
            <span>标识符：</span>
            <span style="color: #fff">{{ item.identify }}</span>
          </div>
          <div style="width: 60px; align-items: flex-end; text-align: right">
            <span style="color: #ffdb5c">编码：{{ item.resultCode }}</span>
          </div>
          <div style="width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
            <span>上报时间：</span>
            <span style="color: #fff">{{ item.createTime }}</span>
          </div>
          <div
            style="margin-bottom: 10px; width: 210px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis"
          >
            <span>上报值：</span>
            <span style="color: #fff">{{ item.funValue }}</span>
          </div>
        </div>
      </div>
    </Vue3SeamlessScroll>
  </div>

  <Reacquire v-else @onclick="getData" style="line-height: 200px; color: #23cdd8; margin-left: 210px">
    重新获取
  </Reacquire>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { Vue3SeamlessScroll } from 'vue3-seamless-scroll';
import { listLog } from '@/api/iot/functionLog';
import Reacquire from '../components/reacquire/reacquire.vue';

const deviceLogList = ref<any[]>([]);
const pageflag = ref(true);
let timer: ReturnType<typeof setInterval> | null = null;
const queryParams = ref({
  pageNum: 1,
  pageSize: 5,
});

onMounted(() => {
  getData();
});

onBeforeUnmount(() => {
  clearData();
});

function getData() {
  listLog(queryParams.value).then((response: any) => {
    deviceLogList.value = response.rows;
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
    getData();
  }, 60000);
}
</script>
