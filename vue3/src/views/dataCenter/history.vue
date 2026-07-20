<template>
  <div class="data-center-history">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('dataCenter.history.384934-0')" name="device">
          <div class="device-wrap">
            <div class="form-wrap">
              <el-form @submit.prevent :model="devQueryParams" ref="devQueryFormRef" :inline="true" label-width="68px">
                <el-form-item prop="deviceId">
                  <el-select
                    style="width: 192px"
                    v-model="devQueryParams.deviceId"
                    :placeholder="$t('dataCenter.history.384934-2')"
                    filterable
                    @change="handleDevDeviceChange"
                    clearable
                  >
                    <el-option
                      v-for="(item, index) in devDeviceList"
                      :key="index"
                      :label="item.deviceName"
                      :value="item.deviceId"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item prop="identifiers">
                  <el-select
                    style="width: 192px"
                    v-model="devQueryParams.identifiers"
                    :placeholder="$t('dataCenter.history.384934-4')"
                    filterable
                    multiple
                    collapse-tags
                  >
                    <el-option
                      v-for="(item, index) in devIdentifierList"
                      :key="index"
                      :label="item.modelName"
                      :value="item.identifier"
                      :disabled="item.isHistory === 0"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item prop="dayDaterange">
                  <el-date-picker
                    style="width: 356px"
                    v-model="devQueryParams.dayDaterange"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    type="datetimerange"
                    range-separator="-"
                    :start-placeholder="$t('dataCenter.history.384934-6')"
                    :end-placeholder="$t('dataCenter.history.384934-7')"
                    :shortcuts="pickerShortcuts"
                  ></el-date-picker>
                </el-form-item>
              </el-form>
              <div class="search-btn-group">
                <el-button type="primary" :icon="Search" @click="handleDevQuery">{{ $t('search') }}</el-button>
                <el-button :icon="Refresh" @click="handleDevResetQuery">{{ $t('reset') }}</el-button>
                <el-button
                  :icon="Download"
                  @click="handleDeviceExport"
                  :disabled="
                    devQueryParams.deviceId === '' ||
                    devQueryParams.identifiers.length === 0 ||
                    devQueryParams.dayDaterange === null
                  "
                >
                  {{ $t('export') }}
                </el-button>
              </div>
            </div>

            <el-row>
              <el-col :span="24">
                <el-card v-loading="loading" shadow="never">
                  <template #header>
                    <span>{{ $t('dataCenter.history.384934-10') }}</span>
                  </template>
                  <div class="el-table--enable-row-hover el-table--medium">
                    <div
                      v-show="devDatas.length !== 0"
                      ref="devLineChartRef"
                      style="width: 100%; height: 480px; background: #fff"
                    ></div>
                    <el-empty
                      v-if="devDatas.length === 0"
                      style="height: 480px"
                      :description="$t('dataCenter.history.384934-12')"
                    ></el-empty>
                    <el-table v-show="devTotal > 0" style="margin-top: 50px" :data="devTableList" :border="false">
                      <el-table-column :label="$t('dataCenter.history.384934-13')" prop="time" width="200" />
                      <el-table-column
                        v-for="item in devTableHeaderTemp"
                        :key="item.value"
                        :label="item.name"
                        :prop="item.value"
                      />
                    </el-table>
                    <pagination
                      style="margin-bottom: 20px"
                      v-show="devTotal > 0"
                      :autoScroll="false"
                      :total="devTotal"
                      v-model:page="devPageNum"
                      v-model:limit="devPageSize"
                    />
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('dataCenter.history.384934-14')" name="scene">
          <div class="scene-wrap">
            <div class="form-wrap">
              <el-form
                @submit.prevent
                :model="sceneQueryParams"
                ref="sceneQueryFormRef"
                :inline="true"
                label-width="68px"
              >
                <el-form-item prop="sceneModelId">
                  <el-select
                    style="width: 192px"
                    v-model="sceneQueryParams.sceneModelId"
                    :placeholder="$t('dataCenter.history.384934-16')"
                    @change="handleSceneModelChange"
                    clearable
                  >
                    <el-option
                      v-for="(item, index) in sceneModelList"
                      :key="index"
                      :label="item.sceneModelName"
                      :value="item.sceneModelId"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item prop="sceneModelDeviceId">
                  <el-select
                    style="width: 192px"
                    v-model="sceneQueryParams.sceneModelDeviceId"
                    :placeholder="$t('dataCenter.history.384934-18')"
                    @change="handleSceneDeviceChange"
                  >
                    <el-option
                      v-for="(item, index) in sceneDeviceList"
                      :key="index"
                      :label="item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item prop="dayDaterange">
                  <el-date-picker
                    style="width: 354px"
                    v-model="sceneQueryParams.dayDaterange"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    type="datetimerange"
                    range-separator="-"
                    :start-placeholder="$t('dataCenter.history.384934-6')"
                    :end-placeholder="$t('dataCenter.history.384934-7')"
                    :shortcuts="pickerShortcuts"
                  ></el-date-picker>
                </el-form-item>
                <template v-if="sceneSearchShow">
                  <el-form-item prop="identifiers">
                    <el-select
                      style="width: 192px"
                      v-model="sceneQueryParams.identifiers"
                      :placeholder="$t('dataCenter.history.384934-4')"
                      multiple
                      collapse-tags
                    >
                      <el-option
                        v-for="(item, index) in sceneIdentifierList"
                        :key="index"
                        :label="item.sourceName"
                        :value="item.identifier"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </template>
              </el-form>
              <div class="search-btn-group">
                <el-button type="primary" :icon="Search" @click="handleSceneQuery">
                  {{ $t('dataCenter.history.384934-8') }}
                </el-button>
                <el-button :icon="Refresh" @click="handleSceneResetQuery">
                  {{ $t('dataCenter.history.384934-9') }}
                </el-button>
                <el-button
                  :icon="Download"
                  @click="handleSceneExport"
                  :disabled="
                    sceneQueryParams.sceneModelId == '' ||
                    sceneQueryParams.sceneModelDeviceId === '' ||
                    sceneQueryParams.identifiers.length === 0 ||
                    sceneQueryParams.dayDaterange === null
                  "
                >
                  {{ $t('export') }}
                </el-button>
                <el-button type="primary" link @click="handleSceneSearchChange">
                  <span style="color: #486ff2; margin-left: 14px">
                    {{ sceneSearchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
                  </span>
                  <el-icon style="color: #486ff2; margin-left: 10px">
                    <ArrowDown v-if="!sceneSearchShow" />
                    <ArrowUp v-else />
                  </el-icon>
                </el-button>
              </div>
            </div>

            <el-row>
              <el-col :span="24">
                <el-card v-loading="loading" shadow="never">
                  <template #header>
                    <span>{{ $t('dataCenter.history.384934-10') }}</span>
                  </template>
                  <div class="el-table--enable-row-hover el-table--medium">
                    <div
                      v-show="sceneDatas.length !== 0"
                      ref="sceneLineChartRef"
                      style="width: 100%; height: 480px; background: #fff"
                    ></div>
                    <el-empty
                      v-if="sceneDatas.length === 0"
                      style="height: 480px"
                      :description="$t('dataCenter.history.384934-12')"
                    ></el-empty>
                    <el-table v-show="sceneTotal > 0" style="margin-top: 50px" :data="sceneTableList" :border="false">
                      <el-table-column :label="$t('dataCenter.history.384934-13')" prop="time" width="200" />
                      <el-table-column
                        v-for="item in sceneTableHeaderTemp"
                        :key="item.value"
                        :label="item.name"
                        :prop="item.value"
                      />
                    </el-table>
                    <pagination
                      style="margin-bottom: 20px"
                      v-show="sceneTotal > 0"
                      :autoScroll="false"
                      :total="sceneTotal"
                      v-model:page="scenePageNum"
                      v-model:limit="scenePageSize"
                    />
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Download, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import * as echarts from 'echarts';

import { listDeviceShort, listThingsModel } from '@/api/iot/device.js';
import { getSceneModelList, getSceneModelDetail, getSceneModelDataList } from '@/api/scene/list.js';
import { getDataCenterDeviceHistory, getDataCenterSceneHistory } from '@/api/data/center.js';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();

const devQueryFormRef = ref();
const sceneQueryFormRef = ref();
const devLineChartRef = ref();
const sceneLineChartRef = ref();

const activeTab = ref('device');
const loading = ref(false);

// ===== 设备相关 =====
const devDeviceList = ref<any[]>([]);
const devIdentifierList = ref<any[]>([]);
const getDefaultDateRange = () => {
  const end = dayjs();
  const start = end.subtract(2, 'hour');
  return [start.format('YYYY-MM-DD HH:mm:ss'), end.format('YYYY-MM-DD HH:mm:ss')];
};

const devQueryParams = ref<any>({
  deviceId: null,
  identifiers: [],
  dayDaterange: getDefaultDateRange(),
});
const devDatas = ref<any[]>([]);
const devTableComTemp = ref<any[]>([]);
const devTableHeaderTemp = ref<any[]>([]);
const devPageNum = ref(1);
const devPageSize = ref(10);
const devTotal = ref(0);

// ===== 场景相关 =====
const sceneSearchShow = ref(false);
const sceneModelList = ref<any[]>([]);
const sceneDeviceList = ref<any[]>([]);
const sceneIdentifierList = ref<any[]>([]);
const sceneQueryParams = ref<any>({
  sceneModelId: null,
  sceneModelDeviceId: null,
  identifiers: [],
  dayDaterange: getDefaultDateRange(),
});
const sceneDatas = ref<any[]>([]);
const sceneTableComTemp = ref<any[]>([]);
const sceneTableHeaderTemp = ref<any[]>([]);
const scenePageNum = ref(1);
const scenePageSize = ref(10);
const sceneTotal = ref(0);

let charts: any = null;

const pickerShortcuts = [
  {
    text: t('dataCenter.history.384934-19'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 2);
      return [start, end];
    },
  },
  {
    text: t('dataCenter.history.384934-20'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24);
      return [start, end];
    },
  },
  {
    text: t('dataCenter.history.384934-21'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      return [start, end];
    },
  },
  {
    text: t('dataCenter.history.384934-22'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
      return [start, end];
    },
  },
];

// ===== 计算属性 =====
const devTableList = computed(() => {
  const start = (devPageNum.value - 1) * devPageSize.value;
  const end = start + devPageSize.value;
  return devTableComTemp.value.slice(start, end);
});

const sceneTableList = computed(() => {
  const start = (scenePageNum.value - 1) * scenePageSize.value;
  const end = start + scenePageSize.value;
  return sceneTableComTemp.value.slice(start, end);
});

// ===== 设备方法 =====
/** 获取设备列表 */
function getDevDeviceList() {
  const params = { showChild: true, pageNum: 1, pageSize: 9999 };
  listDeviceShort(params).then((res: any) => {
    if (res.code === 200) {
      devDeviceList.value = res.rows;
    }
  });
}

/** 选择设备后 */
function handleDevDeviceChange(val: any) {
  devQueryParams.value.identifiers = [];
  getDevIdentifierList(val);
}

function getDevIdentifierList(deviceId: any) {
  const params = { deviceId: deviceId, pageNum: 1, pageSize: 9999 };
  listThingsModel(params).then((res: any) => {
    if (res.code === 200) {
      devIdentifierList.value = res.rows;
    }
  });
}

/** 获取设备图形数据 */
function getDevChartDatas() {
  loading.value = true;
  const devices = devDeviceList.value.find((item: any) => item.deviceId === devQueryParams.value.deviceId);
  const identifierList = devQueryParams.value.identifiers.map((item: any) => {
    const identifiers = devIdentifierList.value.find((chil: any) => chil.identifier === item);
    return { identifier: identifiers.identifier, type: identifiers.type };
  });
  const params = {
    deviceId: devices.deviceId,
    serialNumber: devices.serialNumber,
    identifierList: identifierList,
    beginTime: dayjs(devQueryParams.value.dayDaterange[0]).format('YYYY-MM-DD HH:mm:ss'),
    endTime: dayjs(devQueryParams.value.dayDaterange[1]).format('YYYY-MM-DD HH:mm:ss'),
  };
  getDataCenterDeviceHistory(params).then((res: any) => {
    if (res.code === 200) {
      devDatas.value = res.data;
      formatDevTableDatas();
      if (devDatas.value.length !== 0) {
        setTimeout(() => drawDevLine(), 500);
      }
    }
    setTimeout(() => {
      loading.value = false;
    }, 500);
  });
}

/** 搜索按钮操作 */
function handleDevQuery() {
  const isField = areAllFields(devQueryParams.value);
  if (isField) {
    getDevChartDatas();
  }
}

/** 重置按钮操作 */
function handleDevResetQuery() {
  devQueryFormRef.value?.resetFields();
  devQueryParams.value.identifiers = [];
  devQueryParams.value.dayDaterange = getDefaultDateRange();
  devDatas.value = [];
  devTableComTemp.value = [];
  devTotal.value = 0;
  devIdentifierList.value = [];
  handleDevQuery();
}

/** 导出设备变量数据 */
function handleDeviceExport() {
  const isField = areAllFields(devQueryParams.value);
  if (isField) {
    const devices = devDeviceList.value.find((item: any) => item.deviceId === devQueryParams.value.deviceId);
    const identifierList = devQueryParams.value.identifiers.map((item: any) => {
      const identifiers = devIdentifierList.value.find((chil: any) => chil.identifier === item);
      return { identifier: identifiers.identifier, type: identifiers.type };
    });
    const params = {
      deviceId: devices.deviceId,
      serialNumber: devices.serialNumber,
      identifierStr: JSON.stringify(identifierList),
      beginTime: dayjs(devQueryParams.value.dayDaterange[0]).format('YYYY-MM-DD HH:mm:ss'),
      endTime: dayjs(devQueryParams.value.dayDaterange[1]).format('YYYY-MM-DD HH:mm:ss'),
    };
    proxy.download('/data/center/deviceExport', { ...params }, `deviceData_${new Date().getTime()}.xlsx`);
  }
}

/** 搜索展开隐藏 */
function handleSceneSearchChange() {
  sceneSearchShow.value = !sceneSearchShow.value;
}

// ===== 场景方法 =====
/** 获取场景列表 */
function getSceneModelListDatas() {
  const params = { pageNum: 1, pageSize: 9999 };
  getSceneModelList(params).then((res: any) => {
    if (res.code === 200) {
      sceneModelList.value = res.rows;
    }
  });
}

/** 场景选择后 */
function handleSceneModelChange(val: any) {
  sceneQueryParams.value.sceneModelDeviceId = null;
  sceneQueryParams.value.identifiers = [];
  if (val) {
    getSceneModelDetailDatas(val);
  }
}

function getSceneModelDetailDatas(sceneModelId: any) {
  getSceneModelDetail(sceneModelId).then((res: any) => {
    if (res.code === 200) {
      sceneDeviceList.value = res.data.sceneModelDeviceVOList;
    }
  });
}

/** 场景-设备选择后 */
function handleSceneDeviceChange(val: any) {
  sceneQueryParams.value.identifiers = [];
  getSceneIdentifierList(val);
}

/** 获取变量情况列表 */
function getSceneIdentifierList(sceneModelDeviceId: any) {
  const params = {
    sceneModelId: sceneQueryParams.value.sceneModelId,
    sceneModelDeviceId: sceneModelDeviceId,
    pageNum: 1,
    pageSize: 9999,
  };
  getSceneModelDataList(params).then((res: any) => {
    if (res.code === 200) {
      sceneIdentifierList.value = res.rows;
    }
  });
}

/** 获取场景图形数据 */
function getSceneChartDatas() {
  loading.value = true;
  const sceneDatasItem = sceneDeviceList.value.find(
    (item: any) => item.id === sceneQueryParams.value.sceneModelDeviceId
  );
  const ids = sceneQueryParams.value.identifiers
    .map((item: any) => sceneIdentifierList.value.find((chil: any) => chil.identifier === item).id)
    .join(',');
  const params = {
    sceneModelId: sceneQueryParams.value.sceneModelId,
    sceneModelDeviceId: sceneQueryParams.value.sceneModelDeviceId,
    variableType: sceneDatasItem.variableType,
    ids: ids,
    beginTime: dayjs(sceneQueryParams.value.dayDaterange[0]).format('YYYY-MM-DD HH:mm:ss'),
    endTime: dayjs(sceneQueryParams.value.dayDaterange[1]).format('YYYY-MM-DD HH:mm:ss'),
    serialNumber: sceneDatasItem.variableType === 1 ? sceneDatasItem.serialNumber : '',
  };
  getDataCenterSceneHistory(params).then((res: any) => {
    if (res.code === 200) {
      sceneDatas.value = res.data;
      formatSceneTableDatas();
      if (sceneDatas.value.length !== 0) {
        setTimeout(() => drawSceneLine(), 500);
      }
    }
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleSceneQuery() {
  const isField = areAllFields(sceneQueryParams.value);
  if (isField) {
    getSceneChartDatas();
  }
}

/** 重置按钮操作 */
function handleSceneResetQuery() {
  sceneQueryFormRef.value?.resetFields();
  sceneQueryParams.value.identifiers = [];
  sceneQueryParams.value.dayDaterange = getDefaultDateRange();
  sceneDatas.value = [];
  sceneTableComTemp.value = [];
  sceneTotal.value = 0;
  handleSceneQuery();
}

/** 导出场景数据 */
function handleSceneExport() {
  const isField = areAllFields(sceneQueryParams.value);
  if (isField) {
    const variableType = sceneDeviceList.value.find(
      (item: any) => item.id === sceneQueryParams.value.sceneModelDeviceId
    ).variableType;
    const ids = sceneQueryParams.value.identifiers
      .map((item: any) => sceneIdentifierList.value.find((chil: any) => chil.identifier === item).id)
      .join(',');
    const params = {
      sceneModelId: sceneQueryParams.value.sceneModelId,
      sceneModelDeviceId: sceneQueryParams.value.sceneModelDeviceId,
      variableType: variableType,
      ids: ids,
      beginTime: dayjs(sceneQueryParams.value.dayDaterange[0]).format('YYYY-MM-DD HH:mm:ss'),
      endTime: dayjs(sceneQueryParams.value.dayDaterange[1]).format('YYYY-MM-DD HH:mm:ss'),
    };
    proxy.download('/data/center/sceneExport', { ...params }, `sceneData_${new Date().getTime()}.xlsx`);
  }
}

// ===== 通用方法 =====
/** 判断对象是否都有值 */
function areAllFields(obj: any) {
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      if (!obj[key] || obj[key] === '' || obj[key].length === 0) {
        return false;
      }
    }
  }
  return true;
}

// ===== 设备图表 =====
function drawDevLine() {
  charts = echarts.init(devLineChartRef.value);
  charts.clear();
  charts.setOption({
    tooltip: { trigger: 'axis' },
    legend: { align: 'right', left: '4.5%', top: '15%' },
    grid: { top: '30%', left: '8%', right: '10.5%', bottom: '5%', containLabel: true },
    toolbox: { feature: { restore: {}, saveAsImage: {} } },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { start: 0, end: 100 },
    ],
    xAxis: {
      type: 'category',
      boundaryGap: true,
      axisTick: { alignWithLabel: true },
      data: devDatas.value.length !== 0 && devDatas.value.map((item: any) => Object.keys(item)[0]),
    },
    yAxis: { type: 'value', scale: true },
    series: getDevSeries(),
  });
}

function getDevSeries() {
  return devQueryParams.value.identifiers.map((item: any, index: number) => {
    return {
      name: devIdentifierList.value.find((chil: any) => chil.identifier === item)?.modelName,
      type: 'line',
      stack: t('dataCenter.history.384934-23', [index]),
      data: devDatas.value.map((d: any) => {
        const ide = (Object.values(d)[0] as any[]).find((f: any) => Object.keys(f)[0] === item);
        return Object.values(ide as any)[0];
      }),
    };
  });
}

function formatDevTableDatas() {
  devTableComTemp.value = devDatas.value.map((item: any) => {
    const time = Object.keys(item)[0];
    let obj: any = {};
    (Object.values(item)[0] as any[]).forEach((chil: any) => {
      obj[Object.keys(chil)[0]] = Object.values(chil)[0];
    });
    return { time, ...obj };
  });
  devTotal.value = devDatas.value.length;
  devTableHeaderTemp.value = devQueryParams.value.identifiers.map((item: any) => ({
    name: devIdentifierList.value.find((chil: any) => chil.identifier === item)?.modelName,
    value: item,
  }));
}

// ===== 场景图表 =====
function drawSceneLine() {
  charts = echarts.init(sceneLineChartRef.value);
  charts.clear();
  charts.setOption({
    tooltip: { trigger: 'axis' },
    legend: { align: 'right', left: '4.5%', top: '15%' },
    grid: { top: '30%', left: '8%', right: '10.5%', bottom: '5%', containLabel: true },
    toolbox: { feature: { restore: {}, saveAsImage: {} } },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { start: 0, end: 100 },
    ],
    xAxis: {
      type: 'category',
      boundaryGap: true,
      axisTick: { alignWithLabel: true },
      data: sceneDatas.value.length !== 0 && sceneDatas.value.map((item: any) => Object.keys(item)[0]),
    },
    yAxis: { type: 'value', boundaryGap: true, splitNumber: 4, interval: 250 },
    series: getSceneSeries(),
  });
}

function getSceneSeries() {
  return sceneQueryParams.value.identifiers.map((item: any, index: number) => {
    return {
      name: sceneIdentifierList.value.find((chil: any) => chil.identifier === item)?.sourceName,
      type: 'line',
      stack: t('dataCenter.history.384934-23', [index]),
      data: sceneDatas.value.map((d: any) => {
        const ide = (Object.values(d)[0] as any[]).find((f: any) => Object.keys(f)[0] === item);
        return Object.values(ide as any)[0];
      }),
    };
  });
}

function formatSceneTableDatas() {
  sceneTableComTemp.value = sceneDatas.value.map((item: any) => {
    const time = Object.keys(item)[0];
    let obj: any = {};
    (Object.values(item)[0] as any[]).forEach((chil: any) => {
      obj[Object.keys(chil)[0]] = Object.values(chil)[0];
    });
    return { time, ...obj };
  });
  sceneTotal.value = sceneDatas.value.length;
  sceneTableHeaderTemp.value = sceneQueryParams.value.identifiers.map((item: any) => ({
    name: sceneIdentifierList.value.find((chil: any) => chil.identifier === item)?.sourceName,
    value: item,
  }));
}

// ===== 生命周期 =====
onMounted(() => {
  getDevDeviceList();
  getSceneModelListDatas();
  const { activeName, deviceId, identifier, sceneModelId, sceneModelDeviceId } = route.query as any;
  if (activeName) {
    activeTab.value = activeName || 'device';
    if (activeName === 'device') {
      devQueryParams.value.deviceId = Number(deviceId);
      getDevIdentifierList(Number(deviceId));
      devQueryParams.value.identifiers = [identifier];
      setTimeout(() => handleDevQuery(), 500);
    } else {
      sceneQueryParams.value.sceneModelId = Number(sceneModelId);
      getSceneModelDetailDatas(Number(sceneModelId));
      sceneQueryParams.value.sceneModelDeviceId = Number(sceneModelDeviceId);
      getSceneIdentifierList(Number(sceneModelDeviceId));
      sceneQueryParams.value.identifiers = [identifier];
      setTimeout(() => handleSceneQuery(), 2000);
    }
  }
});
</script>

<style lang="scss" scoped>
.data-center-history {
  padding: 20px;

  .form-wrap {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-end;

    .search-btn-group {
      display: flex;
      flex-direction: row;
      margin-bottom: 22px;
    }
  }

  .device-wrap {
    margin-top: 5px;
  }

  .scene-wrap {
    margin-top: 5px;
  }
}
</style>
