<template>
  <div class="data-center-analysis">
    <el-card class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item prop="deviceId">
          <el-select
            v-model="queryParams.deviceId"
            :placeholder="$t('dataCenter.analysis.349202-1')"
            filterable
            @change="handleDevDeviceChange"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="(item, index) in deviceList"
              :key="index"
              :label="item.deviceName"
              :value="item.deviceId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="dayDaterange">
          <el-date-picker
            style="width: 356px"
            v-model="queryParams.dayDaterange"
            value-format="YYYY-MM-DD HH:mm:ss"
            type="datetimerange"
            range-separator="-"
            :start-placeholder="$t('dataCenter.analysis.349202-3')"
            :end-placeholder="$t('dataCenter.analysis.349202-4')"
            :shortcuts="pickerShortcuts"
          ></el-date-picker>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="16">
        <el-card class="card-box" style="margin-bottom: 20px">
          <template #header>
            <span>{{ $t('dataCenter.analysis.349202-21') }}</span>
          </template>
          <div v-show="deviceLineList.length !== 0" ref="deviceLineChartRef" style="width: 100%; height: 400px"></div>
          <el-empty
            v-if="deviceLineList.length === 0"
            style="height: 400px"
            :description="$t('dataCenter.analysis.349202-7')"
          ></el-empty>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="card-box" style="margin-bottom: 20px">
          <template #header>
            <span>{{ $t('dataCenter.analysis.349202-22') }}</span>
          </template>
          <div
            v-show="alertProcessList.length !== 0"
            ref="alertProcessPieChartRef"
            style="width: 100%; height: 400px"
          ></div>
          <el-empty
            v-if="alertProcessList.length === 0"
            style="height: 400px"
            :description="$t('dataCenter.analysis.349202-7')"
          ></el-empty>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="card-box" style="margin-bottom: 0">
          <template #header>
            <span>{{ $t('dataCenter.analysis.349202-23') }}</span>
          </template>
          <div v-show="deviceBarList.length !== 0" ref="deviceBarChartRef" style="width: 100%; height: 480px"></div>
          <el-empty
            v-if="deviceBarList.length === 0"
            style="height: 480px"
            :description="$t('dataCenter.analysis.349202-7')"
          ></el-empty>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="card-box" style="margin-bottom: 0">
          <template #header>
            <span>{{ $t('dataCenter.analysis.349202-24') }}</span>
          </template>
          <div class="scroll-board-wrap">
            <dv-scroll-board
              v-if="realTimeConfig.data && realTimeConfig.data.length !== 0"
              :config="realTimeConfig"
              style="width: 100%; height: 100%"
            />
            <el-empty
              v-if="!realTimeConfig.data || realTimeConfig.data.length === 0"
              style="height: 100%"
              :description="$t('dataCenter.analysis.349202-7')"
            ></el-empty>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="card-box" style="margin-bottom: 0">
          <template #header>
            <span>{{ $t('dataCenter.analysis.349202-25') }}</span>
          </template>
          <div class="scroll-board-wrap">
            <dv-scroll-board
              v-if="alertInfoConfig.data && alertInfoConfig.data.length !== 0"
              :config="alertInfoConfig"
              style="width: 100%; height: 100%; margin-bottom: 15px"
            />
            <el-empty
              v-if="!alertInfoConfig.data || alertInfoConfig.data.length === 0"
              style="height: 100%; margin-bottom: 15px"
              :image-size="120"
              :description="$t('dataCenter.analysis.349202-7')"
            ></el-empty>
          </div>
        </el-card>
        <el-card class="card-box" style="margin-top: 20px">
          <template #header>
            <span>{{ $t('dataCenter.analysis.349202-26') }}</span>
          </template>
          <div v-show="alertLevelPieList.length !== 0" ref="alertPieChartRef" style="width: 100%; height: 155px"></div>
          <el-empty
            v-if="alertLevelPieList.length === 0"
            style="height: 155px"
            :image-size="90"
            :description="$t('dataCenter.analysis.349202-7')"
          ></el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
import { listDeviceShort, listThingsModel } from '@/api/iot/device.js';
import {
  getDataCenterDeviceHistory,
  getDataCenterCountAlertProcess,
  getDataCenterCountAlertLevel,
  getDataCenterCountThingsModelInvoke,
} from '@/api/data/center.js';
import { listAlertLog } from '@/api/iot/alertLog';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const { iot_process_status, iot_alert_level } = useDict('iot_process_status', 'iot_alert_level');

const queryFormRef = ref();
const loading = ref(false);
const loadingCount = ref(0);
const deviceList = ref<any[]>([]);
const identifierList = ref<any[]>([]);

const pickerShortcuts = [
  {
    text: t('dataCenter.analysis.349202-8'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 2);
      return [start, end];
    },
  },
  {
    text: t('dataCenter.analysis.349202-9'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24);
      return [start, end];
    },
  },
  {
    text: t('dataCenter.analysis.349202-10'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      return [start, end];
    },
  },
  {
    text: t('dataCenter.analysis.349202-11'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
      return [start, end];
    },
  },
];

const queryParams = ref<any>({
  deviceId: null,
  dayDaterange: [new Date(new Date().getTime() - 3600 * 1000 * 2), new Date()],
});

const deviceLineList = ref<any[]>([]);
const alertProcessList = ref<any[]>([]);
const deviceBarList = ref<any[]>([]);
const realTimeConfig = ref<any>({});
const alertInfoConfig = ref<any>({});
const alertLevelPieList = ref<any[]>([]);

// echarts refs
const deviceLineChartRef = ref();
const alertProcessPieChartRef = ref();
const deviceBarChartRef = ref();
const alertPieChartRef = ref();

let deviceLineChart: any = null;
let alertProcessPieChart: any = null;
let deviceBarChart: any = null;
let alertPieChart: any = null;

/** 获取设备列表 */
function getDeviceList() {
  const params = { showChild: true, pageNum: 1, pageSize: 9999 };
  listDeviceShort(params).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows;
    }
  });
}

/** 选择设备后 */
function handleDevDeviceChange(val: any) {
  getDevIdentifierList(val);
}

function getDevIdentifierList(deviceId: any) {
  const params = { deviceId: deviceId, pageNum: 1, pageSize: 9999 };
  listThingsModel(params).then((res: any) => {
    if (res.code === 200) {
      identifierList.value = res.rows;
    }
  });
}

/** 查询设备的历史数据 */
function getDevChartDatas() {
  const devices = deviceList.value.find((item: any) => item.deviceId === queryParams.value.deviceId);
  if (!devices) return;
  myLoading();
  const params = {
    deviceId: devices.deviceId,
    serialNumber: devices.serialNumber,
    beginTime: dayjs(queryParams.value.dayDaterange[0]).format('YYYY-MM-DD HH:mm:ss'),
    endTime: dayjs(queryParams.value.dayDaterange[1]).format('YYYY-MM-DD HH:mm:ss'),
  };
  getDataCenterDeviceHistory(params).then((res: any) => {
    if (res.code === 200) {
      deviceLineList.value = res.data;
      if (deviceLineList.value.length !== 0) {
        setTimeout(() => drawDevLineChart(), 200);
      }
    }
    myLoadingClose();
  });
}

/** 折线图 */
function drawDevLineChart() {
  deviceLineChart = echarts.init(deviceLineChartRef.value);
  deviceLineChart.clear();
  deviceLineChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { align: 'right', left: '3%', top: '6%' },
    grid: { top: '20%', left: '5%', right: '5%', bottom: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      axisTick: { alignWithLabel: true },
      data: deviceLineList.value.length !== 0 && deviceLineList.value.map((item: any) => Object.keys(item)[0]),
    },
    yAxis: { type: 'value', scale: true },
    series: getDevSeries(),
  });
}

function getDevSeries() {
  if (deviceLineList.value && deviceLineList.value.length !== 0) {
    const identifiers =
      (Object.values(deviceLineList.value[0])[0] as any[]).map((item: any) => Object.keys(item)[0]) || [];
    return identifiers.map((item: any, index: number) => {
      return {
        name: identifierList.value.find((chil: any) => chil.identifier === item)?.modelName,
        type: 'line',
        stack: t('dataCenter.analysis.349202-12', [index]),
        data: deviceLineList.value.map((d: any) => {
          const ide = (Object.values(d)[0] as any[]).find((f: any) => Object.keys(f)[0] === item);
          return Object.values(ide as any)[0];
        }),
      };
    });
  }
  return [];
}

/** 查询统计告警信息处理 */
function getCountAlertProcess() {
  const devices = deviceList.value.find((item: any) => item.deviceId === queryParams.value.deviceId);
  if (!devices) return;
  myLoading();
  const params = { serialNumber: devices.serialNumber };
  getDataCenterCountAlertProcess(params).then((res: any) => {
    if (res.code === 200) {
      alertProcessList.value = res.data;
      if (alertProcessList.value.length !== 0) {
        setTimeout(() => drawAlertProcessPieChart(), 200);
      }
    }
    myLoadingClose();
  });
}

function drawAlertProcessPieChart() {
  alertProcessPieChart = echarts.init(alertProcessPieChartRef.value);
  alertProcessPieChart.clear();
  alertProcessPieChart.setOption({
    title: { text: t('dataCenter.analysis.349202-13'), left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { show: true, x: 'center', y: 'bottom', padding: [0, 0, 12, 0] },
    color: ['#5470C6', '#91CC75', '#73C0DE', '#FAC858', '#EE6666'],
    series: [
      {
        type: 'pie',
        radius: '70%',
        center: ['50%', '49%'],
        labelLine: { show: true, smooth: true, length: 10, length2: 10 },
        label: {
          show: true,
          position: 'outer',
          fontSize: 11,
          color: '#1d1d1d',
          formatter: function (params: any) {
            return `${params.name}：${params.value}个\n占：${params.percent}%`;
          },
        },
        data: getAlertPieProcessData(),
      },
    ],
  });
}

function getAlertPieProcessData() {
  if (alertProcessList.value && alertProcessList.value.length !== 0) {
    return alertProcessList.value.map((item: any) => {
      return {
        name: iot_process_status.value.find((chil: any) => chil.value === String(item.type))?.label,
        value: item.count,
      };
    });
  }
  return [];
}

/** 获取设备物模型指令下发数量 */
function getThingsModelInvoke() {
  const devices = deviceList.value.find((item: any) => item.deviceId === queryParams.value.deviceId);
  if (!devices) return;
  myLoading();
  const params = { serialNumber: devices.serialNumber };
  getDataCenterCountThingsModelInvoke(params).then((res: any) => {
    if (res.code === 200) {
      deviceBarList.value = res.data;
      if (deviceBarList.value.length !== 0) {
        setTimeout(() => drawDeviceBarChart(), 200);
      }
    }
    myLoadingClose();
  });
}

function drawDeviceBarChart() {
  deviceBarChart = echarts.init(deviceBarChartRef.value);
  deviceBarChart.clear();
  deviceBarChart.setOption({
    title: { text: t('dataCenter.analysis.349202-14'), left: 'center' },
    color: ['#1890FF'],
    textStyle: { fontSize: 12, fontStyle: 'normal' },
    grid: { top: '10%', left: '1%', right: '6%', bottom: '5%', containLabel: true, borderWidth: 0 },
    xAxis: {
      type: 'value',
      axisLine: { show: true, lineStyle: { color: '#fefef' } },
      axisLabel: {
        show: true,
        interval: 0,
        formatter: function (val: any) {
          return `${val}次`;
        },
      },
    },
    yAxis: {
      axisLine: { show: true, lineStyle: { color: '#fefef' } },
      type: 'category',
      data: deviceBarList.value.length !== 0 && deviceBarList.value.map((item: any) => item.modelName),
    },
    series: [
      {
        itemStyle: {
          label: { show: true, position: 'right' },
          backgroundStyle: { color: '#EBEEF5' },
        },
        data: deviceBarList.value.length !== 0 && deviceBarList.value.map((item: any) => item.counts),
        type: 'bar',
        barWidth: 30,
      },
    ],
  });
}

/** 查询实时数据 */
function getDataRealTimeUpload() {
  if (!queryParams.value.deviceId) return;
  myLoading();
  const params = { deviceId: queryParams.value.deviceId, pageNum: 1, pageSize: 9999 };
  listThingsModel(params).then((res: any) => {
    if (res.code === 200) {
      const header = [
        t('dataCenter.analysis.349202-15'),
        t('dataCenter.analysis.349202-16'),
        t('dataCenter.analysis.349202-17'),
      ];
      let data: any[] = [];
      if (res.rows && res.rows.length !== 0) {
        data = res.rows.map((item: any) => [item.modelName, item.value, item.ts]);
      }
      realTimeConfig.value = { rowNum: 10, columnWidth: [100, 80, 190], header, data };
    }
    myLoadingClose();
  });
}

/** 查询告警信息 */
function getAlertInfo() {
  const devices = deviceList.value.find((item: any) => item.deviceId === queryParams.value.deviceId);
  if (!devices) return;
  myLoading();
  const params = { serialNumber: devices.serialNumber, pageNum: 1, pageSize: 9999 };
  listAlertLog(params).then((res: any) => {
    const header = [
      t('dataCenter.analysis.349202-18'),
      t('dataCenter.analysis.349202-19'),
      t('dataCenter.analysis.349202-20'),
    ];
    let data: any[] = [];
    if (res.rows && res.rows.length !== 0) {
      data = res.rows.map((item: any) => [
        item.alertName,
        iot_alert_level.value.find((chil: any) => chil.value === String(item.alertLevel))?.label,
        item.createTime,
      ]);
    }
    alertInfoConfig.value = { rowNum: 5, columnWidth: [90, 90, 180], header, data };
    myLoadingClose();
  });
}

/** 查询统计告警信息级别 */
function getCountAlertLevel() {
  const devices = deviceList.value.find((item: any) => item.deviceId === queryParams.value.deviceId);
  if (!devices) return;
  myLoading();
  const params = { serialNumber: devices.serialNumber };
  getDataCenterCountAlertLevel(params).then((res: any) => {
    if (res.code === 200) {
      alertLevelPieList.value = res.data;
      if (alertLevelPieList.value.length !== 0) {
        setTimeout(() => drawAlertLevelPieChart(), 200);
      }
    }
    myLoadingClose();
  });
}

function drawAlertLevelPieChart() {
  alertPieChart = echarts.init(alertPieChartRef.value);
  alertPieChart.clear();
  alertPieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { show: true, x: 'center', y: 'bottom', padding: [14, 0, 0, 0] },
    color: ['#5470C6', '#91CC75', '#73C0DE', '#FAC858', '#EE6666'],
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '44%'],
        labelLine: { show: true, smooth: true, length: 10, length2: 10 },
        label: {
          show: true,
          position: 'outer',
          fontSize: 11,
          color: '#1d1d1d',
          formatter: function (params: any) {
            return `${params.name}：${params.value}个\n占：${params.percent}%`;
          },
        },
        data: getAlertLevelPieData(),
      },
    ],
  });
}

function getAlertLevelPieData() {
  if (alertLevelPieList.value && alertLevelPieList.value.length !== 0) {
    return alertLevelPieList.value.map((item: any) => {
      return {
        name: iot_alert_level.value.find((chil: any) => chil.value === String(item.type))?.label,
        value: item.count,
      };
    });
  }
  return [];
}

/** 搜索按钮操作 */
function handleQuery() {
  getDevChartDatas();
  getCountAlertProcess();
  getThingsModelInvoke();
  getDataRealTimeUpload();
  getAlertInfo();
  getCountAlertLevel();
}

/** 重置按钮操作 */
function handleResetQuery() {
  queryFormRef.value?.resetFields();
  deviceLineList.value = [];
  alertProcessList.value = [];
  deviceBarList.value = [];
  realTimeConfig.value = {};
  alertInfoConfig.value = {};
  alertLevelPieList.value = [];
  loading.value = false;
  loadingCount.value = 0;
}

function myLoading() {
  if (!loading.value) {
    loading.value = true;
  }
}

function myLoadingClose() {
  if (loadingCount.value === 5) {
    loadingCount.value = 0;
    loading.value = false;
    return;
  }
  loadingCount.value = loadingCount.value + 1;
}

onMounted(() => {
  getDeviceList();
});
</script>

<style lang="scss" scoped>
.data-center-analysis {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }

  .card-box {
    padding: 0px;

    .scroll-board-wrap {
      width: 100%;
      height: 480px;

      :deep(.header) {
        background-color: #f6f8fa !important;
        color: #909399;
        font-weight: bold;

        .header-item {
          height: 45px !important;
          line-height: 45px !important;
        }
      }

      :deep(.rows) {
        .row-item {
          height: 45px !important;
          line-height: 45px !important;
          color: #606266;
          background-color: #fff !important;
        }
      }
    }
  }
  .clearfix {
    font-size: 18px;
  }
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: '';
  }
  .clearfix:after {
    clear: both;
  }
}
</style>
