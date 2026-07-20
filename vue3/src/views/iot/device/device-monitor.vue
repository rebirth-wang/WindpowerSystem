<template>
  <div>
    <el-form :inline="true" label-width="78px">
      <el-form-item>
        <el-tooltip class="item" effect="light" :content="$t('device.device-monitor.817489-1')" placement="top">
          <el-input
            v-model="monitorInterval"
            :placeholder="$t('device.device-monitor.817489-2')"
            type="number"
            clearable
            style="width: 218px"
          />
        </el-tooltip>
      </el-form-item>
      <el-form-item>
        <el-tooltip class="item" effect="light" :content="$t('device.device-monitor.817489-4')" placement="top">
          <el-input
            v-model="monitorNumber"
            :placeholder="$t('device.device-monitor.817489-5')"
            type="number"
            clearable
            style="width: 218px"
          />
        </el-tooltip>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="VideoPlay" @click="beginMonitor()" v-hasPermi="['iot:service:invoke']">
          {{ $t('device.device-monitor.817489-6') }}
        </el-button>
        <el-button :icon="VideoPause" @click="stopMonitor()" v-hasPermi="['iot:service:invoke']">
          {{ $t('device.device-monitor.817489-7') }}
        </el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="20" v-loading="chartLoading" :element-loading-text="$t('device.device-monitor.817489-8')">
      <el-col :span="12" v-for="(item, index) in monitorThings" :key="index" style="margin-bottom: 20px">
        <el-card shadow="hover" :body-style="{ paddingTop: '10px', marginBottom: '-20px' }">
          <div :ref="(el: any) => setMonitorRef(el, index)" style="height: 210px; padding: 0"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, getCurrentInstance, onMounted, onBeforeUnmount } from 'vue';
import { VideoPlay, VideoPause } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const monitorInterval = ref(1000);
const monitorNumber = ref(60);
const chart = ref<any[]>([]);
const dataList = ref<any[]>([]);
const monitorThings = ref<any[]>([]);
const chartLoading = ref(false);
const deviceInfo = ref<any>({});
const monitorRefs = ref<any[]>([]);
const monitorObservers = new Map<number, ResizeObserver>();

function setMonitorRef(el: any, index: number) {
  if (el) {
    monitorRefs.value[index] = el;
  }
}

function disposeMonitorChart(index: number) {
  const observer = monitorObservers.get(index);
  if (observer) {
    observer.disconnect();
    monitorObservers.delete(index);
  }
  const instance = chart.value[index];
  if (instance?.dispose) {
    instance.dispose();
  }
  chart.value[index] = undefined;
}

function initMonitorChart(index: number, el: HTMLElement) {
  disposeMonitorChart(index);
  const instance = proxy?.$echarts.getInstanceByDom?.(el);
  if (instance) {
    instance.dispose();
  }
  chart.value[index] = proxy?.$echarts.init(el);
  return chart.value[index];
}

function renderMonitorChart(index: number, el: HTMLElement, option: any) {
  if (!el) return;

  const startInit = () => {
    if (!el.clientWidth || !el.clientHeight) return false;
    const instance = initMonitorChart(index, el);
    instance?.setOption(option);
    return true;
  };

  if (startInit()) return;

  const observer = new ResizeObserver(() => {
    if (startInit()) {
      const current = monitorObservers.get(index);
      current?.disconnect();
      monitorObservers.delete(index);
    }
  });
  observer.observe(el);
  monitorObservers.set(index, observer);
}

function getTime() {
  let date = new Date();
  let y = date.getFullYear();
  let m: any = date.getMonth() + 1;
  let d: any = date.getDate();
  let H: any = date.getHours();
  let mm: any = date.getMinutes();
  let s: any = date.getSeconds();
  m = m < 10 ? '0' + m : m;
  d = d < 10 ? '0' + d : d;
  H = H < 10 ? '0' + H : H;
  return y + '-' + m + '-' + d + ' ' + H + ':' + mm + ':' + s;
}

function formatTimestamp(ts: number) {
  const date = new Date(ts);
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  const H = String(date.getHours()).padStart(2, '0');
  const mm = String(date.getMinutes()).padStart(2, '0');
  const s = String(date.getSeconds()).padStart(2, '0');
  return `${y}-${m}-${d} ${H}:${mm}:${s}`;
}

watch(
  () => props.device,
  (newVal) => {
    deviceInfo.value = newVal;
    if (newVal && newVal.deviceId != 0 && newVal.monitorList) {
      monitorThings.value = newVal.monitorList;
      dataList.value = [];
      for (let i = 0; i < monitorThings.value.length; i++) {
        dataList.value.push({
          id: monitorThings.value[i].id,
          name: monitorThings.value[i].name,
          data: [],
        });
      }
      nextTick(() => {
        getMonitorChart();
      });
      mqttCallback();
    }
  }
);

onMounted(() => {
  handleDeviceChange(props.device);
});

function handleDeviceChange(device: any) {
  if (device && device.deviceId != 0 && device.monitorList && device.monitorList.length > 0) {
    deviceInfo.value = device;
    monitorThings.value = device.monitorList;
    dataList.value = [];
    for (let i = 0; i < monitorThings.value.length; i++) {
      dataList.value.push({
        id: monitorThings.value[i].id,
        name: monitorThings.value[i].name,
        data: [],
      });
    }
    nextTick(() => {
      getMonitorChart();
    });
    mqttCallback();
  }
}

function mqttPublish(device: any, model: any) {
  let topic = '';
  let message = '';
  if (model.type == 4) {
    topic = '/' + device.productId + '/' + device.serialNumber + '/monitor/get';
    message = '{"count":' + model.value + ',"interval":' + monitorInterval.value + '}';
  } else {
    return;
  }
  if (topic != '') {
    proxy?.$mqttTool
      .publish(topic, message, model.name)
      .then((res: any) => {
        proxy?.$modal.notifySuccess(res);
      })
      .catch((res: any) => {
        proxy?.$modal.notifyError(res);
      });
  }
}

function mqttCallback() {
  proxy?.$mqttTool.client.on('message', (topic: string, message: any) => {
    let topics = topic.split('/');
    let deviceNum = topics[2];
    message = JSON.parse(message.toString());
    if (!message) {
      return;
    }
    if (topics[3] == 'status') {
      if (deviceInfo.value.serialNumber == deviceNum) {
        deviceInfo.value.status = message.status;
        deviceInfo.value.isShadow = message.isShadow;
        deviceInfo.value.rssi = message.rssi;
      }
    }
    if (topics[3] == 'monitor') {
      chartLoading.value = false;
      for (let k = 0; k < message.length; k++) {
        let value = message[k].value;
        let id = message[k].id;
        let remark = message[k].remark;
        const time = remark ? formatTimestamp(Number(remark)) : getTime();
        for (let i = 0; i < dataList.value.length; i++) {
          if (id == dataList.value[i].id) {
            if (dataList.value[i].data.length > 50) {
              dataList.value[i].data.shift();
            }
            dataList.value[i].data.push([time, value]);
            chart.value[i]?.setOption({
              series: [{ data: dataList.value[i].data }],
            });
            break;
          } else if (dataList.value[i].id.indexOf('array_') == 0) {
            let index = dataList.value[i].id.substring(6, 8);
            let identity = dataList.value[i].id.substring(9);
            if (identity == id) {
              let values = value.split(',');
              if (dataList.value[i].data.length > 50) {
                dataList.value[i].data.shift();
              }
              dataList.value[i].data.push([time, values[index]]);
              chart.value[i]?.setOption({
                series: [{ data: dataList.value[i].data }],
              });
              break;
            }
          }
        }
      }
    }
  });
}

function beginMonitor() {
  if (deviceInfo.value.status != 3) {
    proxy?.$modal.alertError(proxy?.$t('device.device-monitor.817489-13'));
    return;
  }
  for (let i = 0; i < dataList.value.length; i++) {
    dataList.value[i].data = [];
  }
  if (monitorInterval.value < 500 || monitorInterval.value > 10000) {
    proxy?.$modal.alertError(proxy?.$t('device.device-monitor.817489-14'));
  }
  if (monitorNumber.value == 0 || monitorNumber.value > 300) {
    proxy?.$modal.alertError(proxy?.$t('device.device-monitor.817489-15'));
  }
  let model: any = {};
  model.name = proxy?.$t('device.device-monitor.817489-16');
  model.value = monitorNumber.value;
  model.type = 4;
  mqttPublish(deviceInfo.value, model);
  chartLoading.value = true;
}

function stopMonitor() {
  if (deviceInfo.value.status != 3) {
    proxy?.$modal.alertError(proxy?.$t('device.device-monitor.817489-13'));
    return;
  }
  chartLoading.value = false;
  let model: any = {};
  model.name = proxy?.$t('device.device-monitor.817489-17');
  model.value = 0;
  model.type = 4;
  mqttPublish(deviceInfo.value, model);
}

function getMonitorChart() {
  let color = ['#1890FF', '#91CB74', '#FAC858', '#EE6666', '#73C0DE', '#3CA272', '#FC8452', '#9A60B4', '#ea7ccc'];
  chart.value.forEach((_, index) => {
    if (index >= monitorThings.value.length) {
      disposeMonitorChart(index);
    }
  });
  for (let i = 0; i < monitorThings.value.length; i++) {
    const el = monitorRefs.value[i];
    if (!el) continue;
    el.style.width = document.documentElement.clientWidth / 2 - 194 + 'px';
    const option = {
      title: {
        left: 'center',
        text:
          monitorThings.value[i].name +
          ' （单位 ' +
          (monitorThings.value[i].datatype?.unit != undefined
            ? monitorThings.value[i].datatype.unit
            : proxy?.$t('device.device-monitor.817489-19')) +
          '）',
        textStyle: { fontSize: 14 },
      },
      grid: { top: '50px', left: '20px', right: '20px', bottom: '10px', containLabel: true },
      tooltip: { trigger: 'axis', axisPointer: { animation: true } },
      xAxis: { type: 'time', show: false, splitLine: { show: false } },
      yAxis: {
        type: 'value',
        boundaryGap: [0, '100%'],
        splitLine: { show: true },
        scale: true,
        axisLabel: {
          formatter: function (value: number) {
            return value.toFixed(2);
          },
        },
      },
      series: [
        {
          name: monitorThings.value[i].name,
          type: 'line',
          symbol: 'none',
          sampling: 'lttb',
          itemStyle: { color: i > 9 ? color[0] : color[i] },
          areaStyle: {},
          data: [],
        },
      ],
    };
    renderMonitorChart(i, el, option);
  }
}

defineExpose({ handleDeviceChange });

onBeforeUnmount(() => {
  monitorObservers.forEach((observer) => observer.disconnect());
  monitorObservers.clear();
  chart.value.forEach((item) => item?.dispose?.());
});
</script>
