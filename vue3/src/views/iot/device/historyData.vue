<template>
  <div class="data-center-history-wrap">
    <el-drawer :title="title" v-model="drawer" size="55%">
      <div class="device-wrap">
        <el-form :model="devQueryParams" ref="devQueryFormRef" :inline="true" label-width="68px">
          <el-form-item prop="dayDaterange" style="margin-left: 20px">
            <el-date-picker
              style="width: 357px"
              v-model="devQueryParams.dayDaterange"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="datetimerange"
              range-separator="-"
              :start-placeholder="$t('dataCenter.history.384934-6')"
              :end-placeholder="$t('dataCenter.history.384934-7')"
              :shortcuts="shortcuts"
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :icon="Search"
              @click="handleDevQuery"
              :disabled="devQueryParams.dayDaterange == null"
            >
              {{ $t('dataCenter.history.384934-8') }}
            </el-button>
            <el-button :icon="Refresh" @click="handleDevResetQuery">{{ $t('dataCenter.history.384934-9') }}</el-button>
            <el-button :icon="Download" @click="handleDeviceExport" :disabled="devQueryParams.dayDaterange == null">
              {{ $t('export') }}
            </el-button>
          </el-form-item>
        </el-form>

        <el-row>
          <el-col :span="24" style="margin-bottom: 15px">
            <el-card class="card-wrap" v-loading="loading" shadow="never">
              <template #header>
                <span>{{ $t('dataCenter.history.384934-10') }}</span>
              </template>
              <div class="el-table--enable-row-hover el-table--medium">
                <div
                  v-if="devDatas.length !== 0 && !['string', 'bool', 'enum'].includes(model.datatype)"
                  ref="devLineChartRef"
                  style="width: 100%; height: 480px; background: #fff"
                ></div>
                <el-empty v-else style="height: 480px" :description="$t('dataCenter.history.384934-12')"></el-empty>
                <el-table
                  v-show="devTotal > 0"
                  style="margin-top: 50px"
                  :data="devTableList"
                  :border="false"
                  :default-sort="{ prop: 'time', order: 'descending' }"
                  @sort-change="handleSortChange"
                >
                  <el-table-column :label="$t('dataCenter.history.384934-13')" prop="time" width="200" sortable />
                  <el-table-column
                    v-for="item in devTableHeaderTemp"
                    :key="item.value"
                    :label="item.name"
                    :prop="item.value"
                  />
                </el-table>
                <pagination
                  v-show="devTotal > 0"
                  :autoScroll="false"
                  :total="devTotal"
                  v-model:page="devPageNum"
                  v-model:limit="devPageSize"
                  :pager-count="5"
                  style="margin: 20px 0"
                />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance } from 'vue';
import { Search, Refresh, Download } from '@element-plus/icons-vue';
import { getDataCenterDeviceHistory } from '@/api/data/center.js';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  model: {
    type: Object,
    default: null,
  },
});

function formatDateTime(date: Date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  const H = String(date.getHours()).padStart(2, '0');
  const mm = String(date.getMinutes()).padStart(2, '0');
  const s = String(date.getSeconds()).padStart(2, '0');
  return `${y}-${m}-${d} ${H}:${mm}:${s}`;
}

const drawer = ref(false);
const title = ref(proxy?.$t('device.running-status.866086-37'));
const loading = ref(false);
const devLineChartRef = ref<any>(null);
const devQueryFormRef = ref<any>(null);
let charts: any = null;

const shortcuts = [
  {
    text: proxy?.$t('dataCenter.history.384934-19'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 2);
      return [start, end];
    },
  },
  {
    text: proxy?.$t('dataCenter.history.384934-20'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24);
      return [start, end];
    },
  },
  {
    text: proxy?.$t('dataCenter.history.384934-21'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      return [start, end];
    },
  },
  {
    text: proxy?.$t('dataCenter.history.384934-22'),
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
      return [start, end];
    },
  },
];

const devQueryParams = reactive({
  deviceId: null as any,
  identifiers: [] as any[],
  dayDaterange: [formatDateTime(new Date(new Date().getTime() - 3600 * 1000 * 2)), formatDateTime(new Date())] as any,
});

const devDatas = ref<any[]>([]);
const devTableComTemp = ref<any[]>([]);
const devTableHeaderTemp = ref<any[]>([]);
const devPageNum = ref(1);
const devPageSize = ref(10);
const devTotal = ref(0);

const devTableList = computed(() => {
  const start = (devPageNum.value - 1) * devPageSize.value;
  const end = start + devPageSize.value;
  return devTableComTemp.value.slice(start, end);
});

function getDevChartDatas() {
  loading.value = true;
  const identifierList = [{ identifier: props.model.identifier, type: props.model.type }];
  const params = {
    deviceId: props.model.deviceId,
    serialNumber: props.model.serialNumber,
    identifierList: identifierList,
    beginTime: devQueryParams.dayDaterange[0],
    endTime: devQueryParams.dayDaterange[1],
  };
  getDataCenterDeviceHistory(params).then((res: any) => {
    if (res.code === 200) {
      devDatas.value = res.data;
      formatDevTableDatas();
      if (devDatas.value.length !== 0 && !['string', 'bool', 'enum'].includes(props.model.datatype)) {
        setTimeout(() => {
          drawDevLine();
        }, 500);
      }
    }
    loading.value = false;
  });
}

function handleDevQuery() {
  getDevChartDatas();
}

function handleDevResetQuery() {
  proxy?.resetForm(devQueryFormRef.value);
  devQueryParams.identifiers = [];
  devDatas.value = [];
  devTableComTemp.value = [];
  devTotal.value = 0;
  handleDevQuery();
}

function handleSortChange({ column, prop, order }: any) {
  devTableComTemp.value.sort((a: any, b: any) => {
    const timeA = new Date(a[prop]).getTime();
    const timeB = new Date(b[prop]).getTime();
    return order === 'ascending' ? timeA - timeB : timeB - timeA;
  });
}

function handleDeviceExport() {
  const identifierList = [{ identifier: props.model.identifier, type: props.model.type }];
  const params = {
    deviceId: props.model.deviceId,
    serialNumber: props.model.serialNumber,
    identifierStr: JSON.stringify(identifierList),
    beginTime: devQueryParams.dayDaterange[0],
    endTime: devQueryParams.dayDaterange[1],
  };
  proxy?.download('/data/center/deviceExport', { ...params }, `deviceData_${new Date().getTime()}.xlsx`);
}

function drawDevLine() {
  if (!devLineChartRef.value) return;
  charts = proxy?.$echarts.init(devLineChartRef.value);
  charts.clear();
  charts.setOption({
    tooltip: { trigger: 'axis' },
    legend: { align: 'right', left: '3%', top: '15%' },
    grid: { top: '30%', left: '5%', right: '5%', bottom: '5%', containLabel: true },
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
  return {
    name: props.model.identifier,
    type: 'line',
    stack: proxy?.$t('device.history-data.887543-0'),
    data: devDatas.value.map((d: any) => {
      const ide = Object.values(d)[0] as any[];
      const found = ide.find((f: any) => Object.keys(f)[0] === props.model.identifier);
      return found ? Object.values(found)[0] : null;
    }),
  };
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
  const arr = [props.model.identifier];
  devTableHeaderTemp.value = arr.map((item: string) => ({
    name: props.model.identifier,
    value: item,
  }));
}

defineExpose({ drawer, getDevChartDatas });
</script>

<style lang="scss" scoped>
.data-center-history-wrap {
  padding: 20px;

  .device-wrap {
    margin-top: 5px;
  }

  .scene-wrap {
    margin-top: 5px;
  }

  .card-wrap {
    border: none;
  }
}
</style>
