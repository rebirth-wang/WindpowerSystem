<template>
  <div>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="75px">
      <el-form-item label-width="120px" v-if="isSubDev">
        <el-select
          v-model="queryParams.slaveId"
          :placeholder="$t('device.device-statistic.932674-1')"
          @change="selectSlave"
        >
          <el-option
            v-for="slave in slaveList"
            :key="slave.slaveId"
            :label="`${slave.deviceName} (${slave.slaveId})`"
            :value="slave.slaveId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="daterangeTime"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('device.device-statistic.932674-3')"
          :end-placeholder="$t('device.device-statistic.932674-4')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="getListHistory">{{ $t('search') }}</el-button>
      </el-form-item>
    </el-form>
    <div v-for="(item, index) in staticList" :key="index" style="margin-bottom: 30px">
      <el-card shadow="hover" :body-style="{ padding: '10px 0px', overflow: 'auto' }" v-loading="loading">
        <div :ref="(el: any) => setStatisticMapRef(el, index)" style="height: 300px; width: 1080px"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, getCurrentInstance, onMounted, onBeforeUnmount } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { listHistory } from '@/api/iot/deviceLog';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const loading = ref(true);
const deviceInfo = ref<any>({});
const staticList = ref<any[]>([]);
const chart = ref<any[]>([]);
const slaveList = ref<any[]>([]);
const isSubDev = ref(false);
const statisticMapRefs = ref<any[]>([]);
const statisticObservers = new Map<number, ResizeObserver>();

function getTime() {
  let date = new Date();
  let y = date.getFullYear();
  let m: any = date.getMonth() + 1;
  let d: any = date.getDate();
  m = m < 10 ? '0' + m : m;
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}

const daterangeTime = ref([getTime(), getTime()]);

const queryParams = reactive({
  serialNumber: null as any,
  identity: '',
  slaveId: undefined as any,
  beginTime: '' as any,
  endTime: '' as any,
  productId: null as any,
});

function setStatisticMapRef(el: any, index: number) {
  if (el) {
    statisticMapRefs.value[index] = el;
  }
}

function disposeStatisticChart(index: number) {
  const observer = statisticObservers.get(index);
  if (observer) {
    observer.disconnect();
    statisticObservers.delete(index);
  }
  const instance = chart.value[index];
  if (instance?.dispose) {
    instance.dispose();
  }
  chart.value[index] = undefined;
}

function initStatisticChart(index: number, el: HTMLElement) {
  disposeStatisticChart(index);
  const instance = proxy?.$echarts.getInstanceByDom?.(el);
  if (instance) {
    instance.dispose();
  }
  chart.value[index] = proxy?.$echarts.init(el);
  return chart.value[index];
}

function renderStatisticChart(index: number, el: HTMLElement, option: any) {
  if (!el) return;

  const startInit = () => {
    if (!el.clientWidth || !el.clientHeight) return false;
    const instance = initStatisticChart(index, el);
    instance?.setOption(option);
    return true;
  };

  if (startInit()) return;

  const observer = new ResizeObserver(() => {
    if (startInit()) {
      const current = statisticObservers.get(index);
      current?.disconnect();
      statisticObservers.delete(index);
    }
  });
  observer.observe(el);
  statisticObservers.set(index, observer);
}

watch(
  () => props.device,
  (newVal) => {
    if (newVal && newVal.deviceId != 0) {
      deviceInfo.value = newVal;
      isSubDev.value = newVal.subDeviceList && newVal.subDeviceList.length > 0;
      queryParams.slaveId = newVal.slaveId;
      queryParams.serialNumber = newVal.serialNumber;
      slaveList.value = newVal.subDeviceList;
      if (isSubDev.value) {
        staticList.value = newVal.cacheThingsModel['properties'].filter((item: any) => {
          return item.tempSlaveId == queryParams.slaveId;
        });
      } else {
        staticList.value = newVal.staticList || [];
        staticList.value = staticList.value.sort((a: any, b: any) => b.order - a.order);
      }
      nextTick(() => {
        getStatistic();
      });
    }
  }
);

onMounted(() => {
  handleDeviceChange(props.device);
});

function handleDeviceChange(device: any) {
  deviceInfo.value = device;
  if (device && device.deviceId != 0 && staticList.value.length > 0) {
    isSubDev.value = device.subDeviceList && device.subDeviceList.length > 0;
    queryParams.slaveId = device.slaveId;
    queryParams.serialNumber = device.serialNumber;
    slaveList.value = device.subDeviceList;
    if (isSubDev.value) {
      staticList.value = device.cacheThingsModel['properties'].filter((item: any) => {
        return item.tempSlaveId == queryParams.slaveId;
      });
    } else {
      staticList.value = device.staticList || [];
      staticList.value = staticList.value.sort((a: any, b: any) => b.order - a.order);
    }
    nextTick(() => {
      getStatistic();
    });
  }
}

function getListHistory() {
  loading.value = true;
  queryParams.serialNumber = deviceInfo.value.serialNumber;
  if (daterangeTime.value != null && daterangeTime.value.length > 0) {
    queryParams.beginTime = daterangeTime.value[0] + ' 00:00:00';
    queryParams.endTime = daterangeTime.value[1] + ' 23:59:59';
  } else {
    queryParams.beginTime = getTime() + ' 00:00:00';
    queryParams.endTime = getTime() + ' 23:59:59';
  }
  queryParams.productId = deviceInfo.value.productId;
  listHistory(queryParams).then((res: any) => {
    if (res.data && Object.keys(res.data).length === 0) {
      for (let i = 0; i < staticList.value.length; i++) {
        chart.value[i]?.setOption({
          series: [{ data: [] }],
        });
      }
      loading.value = false;
    } else {
      for (let key in res.data) {
        for (let i = 0; i < staticList.value.length; i++) {
          if (key == staticList.value[i].id) {
            let dataList: any[] = [];
            for (let j = 0; j < res.data[key].length; j++) {
              let item: any[] = [];
              item[0] = res.data[key][j].time;
              item[1] = res.data[key][j].value;
              dataList.push(item);
            }
            chart.value[i]?.setOption({
              series: [{ data: dataList }],
            });
          }
        }
      }
    }
    loading.value = false;
  });
}

function getStatistic() {
  let color = ['#1890FF', '#91CB74', '#FAC858', '#EE6666', '#73C0DE', '#3CA272', '#FC8452', '#9A60B4', '#ea7ccc'];
  chart.value.forEach((_, index) => {
    if (index >= staticList.value.length) {
      disposeStatisticChart(index);
    }
  });
  for (let i = 0; i < staticList.value.length; i++) {
    const el = statisticMapRefs.value[i];
    if (!el) continue;
    el.style.width = document.documentElement.clientWidth - 304 + 'px';
    const option = {
      animationDurationUpdate: 3000,
      tooltip: { trigger: 'axis' },
      title: {
        left: 'center',
        text:
          staticList.value[i].name +
          '统计 （单位 ' +
          (staticList.value[i].datatype && staticList.value[i].datatype.unit != undefined
            ? staticList.value[i].datatype.unit
            : proxy?.$t('device.device-statistic.932674-7')) +
          '）',
      },
      grid: { top: '80px', left: '40px', right: '20px', bottom: '60px', containLabel: true },
      toolbox: {
        feature: {
          dataZoom: { yAxisIndex: 'none' },
          restore: {},
          saveAsImage: {},
        },
      },
      xAxis: { type: 'time' },
      yAxis: {
        type: 'value',
        scale: true,
        axisLabel: {
          formatter: function (value: number) {
            return value.toFixed(2);
          },
        },
      },
      dataZoom: [
        { type: 'inside', start: 0, end: 100 },
        { start: 0, end: 100 },
      ],
      series: [
        {
          name: staticList.value[i].name,
          type: 'line',
          symbol: 'none',
          sampling: 'lttb',
          itemStyle: { color: i > 9 ? color[0] : color[i] },
          areaStyle: {},
          data: [],
        },
      ],
    };
    renderStatisticChart(i, el, option);
  }
}

function selectSlave() {
  staticList.value = deviceInfo.value.cacheThingsModel['properties'].filter((item: any) => {
    return item.tempSlaveId == queryParams.slaveId;
  });
  nextTick(() => {
    getStatistic();
    getListHistory();
  });
}

defineExpose({ handleDeviceChange, getListHistory });

onBeforeUnmount(() => {
  statisticObservers.forEach((observer) => observer.disconnect());
  statisticObservers.clear();
  chart.value.forEach((item) => item?.dispose?.());
});
</script>
