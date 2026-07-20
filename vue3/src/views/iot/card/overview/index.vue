<template>
  <div class="video-card-view">
    <el-card class="main-card">
      <div class="main-title">{{ $t('card.overview.title') }}</div>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <template #header>{{ $t('card.overview.total') }}</template>
            <div class="stat-value">{{ statuses.total }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <template #header>{{ $t('card.overview.online') }}</template>
            <div class="stat-value">{{ statuses.online }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <template #header>{{ $t('card.overview.pending') }}</template>
            <div class="stat-value">{{ statuses.pending }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <template #header>{{ $t('card.overview.shutdown') }}</template>
            <div class="stat-value">{{ statuses.shutdown }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <template #header>{{ $t('card.overview.paused') }}</template>
            <div class="stat-value">{{ statuses.paused }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <template #header>{{ $t('card.overview.unknown') }}</template>
            <div class="stat-value">{{ statuses.unknown }}</div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>{{ $t('card.overview.operator_distribution') }}</template>
            <div class="chart-placeholder">
              <div class="pie-container">
                <svg class="pie-svg" viewBox="0 0 100 100">
                  <path
                    v-for="(slice, index) in operatorSlices"
                    :key="index"
                    :d="slice.path"
                    :class="['slice', slice.colorClass]"
                  >
                    <title v-if="slice.tooltip">{{ slice.tooltip }}</title>
                  </path>
                </svg>
              </div>
              <div class="legend">
                <span v-for="(item, index) in operatorList" :key="index" :class="['legend-item', item.colorClass]">
                  {{ item.name }} {{ item.count }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>{{ $t('card.overview.platform_distribution') }}</template>
            <div class="chart-placeholder">
              <div class="pie-container">
                <svg class="pie-svg" viewBox="0 0 100 100">
                  <path
                    v-for="(slice, index) in cardPlatformSlices"
                    :key="index"
                    :d="slice.path"
                    :class="['slice', slice.colorClass]"
                  >
                    <title v-if="slice.tooltip">{{ slice.tooltip }}</title>
                  </path>
                </svg>
              </div>
              <div class="legend">
                <span v-for="(item, index) in platformList" :key="index" :class="['legend-item', item.colorClass]">
                  {{ item.name }} {{ item.count }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row style="margin-top: 20px">
        <el-col :span="24">
          <el-card class="report-card">
            <template #header>{{ $t('card.overview.top_flow_month') }}</template>
            <el-table :border="false" :data="cardList" :empty-text="$t('card.overview.no_data')" style="width: 100%">
              <el-table-column prop="msisdn" :label="$t('card.overview.sim_number')"></el-table-column>
              <el-table-column prop="totalData" :label="$t('card.overview.used_flow')">
                <template #default="scope">
                  <span>{{ Number(scope.row.dataUsed).toFixed(2) }} MB</span>
                </template>
              </el-table-column>
              <el-table-column prop="dataUsed" :label="$t('card.overview.flow_ratio')">
                <template #default="scope">
                  <span>{{ Number(scope.row.trafficProportion).toFixed(2) }}%</span>
                </template>
              </el-table-column>
              <el-table-column prop="dataUsed" :label="$t('card.sim.data_alert_threshold')">
                <template #default="scope">
                  <span>{{ Number(scope.row.dataAlertThreshold).toFixed(2) }}%</span>
                </template>
              </el-table-column>
              <el-table-column prop="alertFlag" :label="$t('card.overview.is_alert')">
                <template #default="scope">
                  <el-tag v-if="scope.row.alertFlag" type="danger">{{ $t('card.overview.yes') }}</el-tag>
                  <el-tag v-else>{{ $t('card.overview.no') }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { getCardOverview } from '@/api/iot/card';

const { proxy } = getCurrentInstance() as any;

const progress = ref(0);
const statuses = ref({
  total: 0,
  online: 0,
  pending: 0,
  shutdown: 0,
  paused: 0,
  unknown: 0,
});
const operatorList = ref<any[]>([]);
const platformList = ref<any[]>([]);
const cardList = ref<any[]>([]);

const generateDoughnutSlices = (data: any, colorClasses: string[], items: any[]) => {
  const total = Object.values(data).reduce((a: any, b: any) => a + b, 0) as number;
  const slices: any[] = [];
  const keys = Object.keys(data);
  const outerR = 45;
  const innerR = 25;
  let startAngle = -90;
  if (total === 0) {
    let angle = 360 * progress.value;
    if (angle > 359.999) angle = 359.999;
    const endAngle = startAngle + angle;
    const startRad = (startAngle * Math.PI) / 180;
    const endRad = (endAngle * Math.PI) / 180;
    const outerStartX = 50 + outerR * Math.cos(startRad);
    const outerStartY = 50 + outerR * Math.sin(startRad);
    const outerEndX = 50 + outerR * Math.cos(endRad);
    const outerEndY = 50 + outerR * Math.sin(endRad);
    const innerStartX = 50 + innerR * Math.cos(startRad);
    const innerStartY = 50 + innerR * Math.sin(startRad);
    const innerEndX = 50 + innerR * Math.cos(endRad);
    const innerEndY = 50 + innerR * Math.sin(endRad);
    const largeArc = angle > 180 ? 1 : 0;
    const path = `M ${outerStartX} ${outerStartY} A ${outerR} ${outerR} 0 ${largeArc} 1 ${outerEndX} ${outerEndY} L ${innerEndX} ${innerEndY} A ${innerR} ${innerR} 0 ${largeArc} 0 ${innerStartX} ${innerStartY} Z`;
    slices.push({ path, colorClass: 'gray', tooltip: proxy?.$t('card.overview.no_data_tooltip') });
    return slices;
  }
  keys.forEach((key, index) => {
    const value = data[key] as number;
    let angle = (value / total) * 360 * progress.value;
    if (angle === 0) return;
    if (angle > 359.999) angle = 359.999;
    const endAngle = startAngle + angle;
    const startRad = (startAngle * Math.PI) / 180;
    const endRad = (endAngle * Math.PI) / 180;
    const outerStartX = 50 + outerR * Math.cos(startRad);
    const outerStartY = 50 + outerR * Math.sin(startRad);
    const outerEndX = 50 + outerR * Math.cos(endRad);
    const outerEndY = 50 + outerR * Math.sin(endRad);
    const innerStartX = 50 + innerR * Math.cos(startRad);
    const innerStartY = 50 + innerR * Math.sin(startRad);
    const innerEndX = 50 + innerR * Math.cos(endRad);
    const innerEndY = 50 + innerR * Math.sin(endRad);
    const largeArc = angle > 180 ? 1 : 0;
    const path = `M ${outerStartX} ${outerStartY} A ${outerR} ${outerR} 0 ${largeArc} 1 ${outerEndX} ${outerEndY} L ${innerEndX} ${innerEndY} A ${innerR} ${innerR} 0 ${largeArc} 0 ${innerStartX} ${innerStartY} Z`;
    slices.push({ path, colorClass: colorClasses[index], tooltip: `${items[index].name} ${items[index].count}` });
    startAngle = endAngle;
  });
  return slices;
};

const operatorSlices = computed(() => {
  const operatorData: any = {};
  operatorList.value.forEach((item: any) => {
    operatorData[item.operator] = item.count;
  });
  const colorClasses = operatorList.value.map((item: any) => item.colorClass);
  return generateDoughnutSlices(operatorData, colorClasses, operatorList.value);
});

const cardPlatformSlices = computed(() => {
  const platformData: any = {};
  platformList.value.forEach((item: any) => {
    platformData[item.platform] = item.count;
  });
  const colorClasses = platformList.value.map((item: any) => item.colorClass);
  return generateDoughnutSlices(platformData, colorClasses, platformList.value);
});

const fetchCardOverview = () => {
  getCardOverview().then((res: any) => {
    if (res.code === 200) {
      statuses.value.total = res.data.totalCount;
      statuses.value.online = res.data.normalCount;
      statuses.value.pending = res.data.pendingActivationCount;
      statuses.value.shutdown = res.data.shutdownCount;
      statuses.value.paused = res.data.accountTerminationCount;
      statuses.value.unknown = res.data.unknownCount;
      cardList.value = res.data.cardVOList;
      if (res.data.operatorVOList) {
        operatorList.value = res.data.operatorVOList.map((item: any) => {
          let colorClass = 'green';
          if (item.operator === 'CTCC') colorClass = 'blue';
          else if (item.operator === 'CUCC') colorClass = 'orange';
          return { ...item, colorClass };
        });
      }
      if (res.data.platformVOList) {
        const colors = ['green', 'orange', 'blue', 'dark-green', 'red', 'purple'];
        platformList.value = res.data.platformVOList.map((item: any, index: number) => {
          return { ...item, colorClass: colors[index % 6] };
        });
      }
      const duration = 1000;
      const start = performance.now();
      const animate = (time: number) => {
        const t = Math.min(1, (time - start) / duration);
        progress.value = t * t * (3 - 2 * t);
        if (t < 1) requestAnimationFrame(animate);
      };
      requestAnimationFrame(animate);
    }
  });
};

onMounted(() => {
  fetchCardOverview();
});
</script>

<style scoped>
.video-card-view {
  padding: 20px;
  background-color: #f5f7fa;
}
.main-card {
  border: none;
  background: transparent;
}
:deep(.main-card .el-card__body) {
  padding: 0;
}
.main-title {
  font-size: 16px;
  margin-bottom: 12px;
  margin-left: 2px;
  color: #303133;
}
.stat-card {
  border-radius: 4px;
  border: 1px solid #ebeef5;
  box-shadow: none;
}
.stat-card:hover {
  transform: none;
  box-shadow: none !important;
}
:deep(.stat-card .el-card__header) {
  text-align: left;
  font-size: 12px;
  color: #606266;
  padding: 8px 12px;
  border-bottom: 1px solid #ebeef5;
}
:deep(.stat-card .el-card__body) {
  padding: 10px 12px;
}
.stat-value {
  text-align: left;
  font-size: 20px;
  line-height: 24px;
  font-weight: 600;
  color: #303133;
}
.chart-card,
.report-card {
  border: 1px solid #ebeef5;
  box-shadow: none;
}

:deep(.chart-card .el-card__header) {
  text-align: left;
  font-size: 13px;
  color: #303133;
  padding: 10px 14px;
  border-bottom: 1px solid #ebeef5;
}
:deep(.chart-card .el-card__body),
:deep(.report-card .el-card__body) {
  padding: 12px 14px;
}
.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.pie-container {
  position: relative;
  width: 200px;
  height: 200px;
  overflow: visible;
}
.pie-svg {
  width: 100%;
  height: 100%;
  overflow: visible;
}
.slice {
  stroke: none;
  transition: transform 0.3s ease;
  transform-origin: 50% 50%;
}
.slice.orange {
  fill: #ffa940;
}
.slice.green {
  fill: #67c23a;
}
.slice.blue {
  fill: #409eff;
}
.slice.gray {
  fill: #c8c8c8;
}
.slice.dark-green {
  fill: #2dcac0;
}
.slice.red {
  fill: #f56c6c;
}
.slice.purple {
  fill: #722ed1;
}
.slice:hover {
  transform: scale(1.1);
}
.legend {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
.legend-item {
  margin: 0 10px;
  font-size: 12px;
  color: #606266;
}
.legend-item:before {
  content: '';
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-right: 5px;
  border-radius: 50%;
}
.orange:before {
  background-color: #ffa940;
}
.green:before {
  background-color: #67c23a;
}
.blue:before {
  background-color: #409eff;
}
.gray:before {
  background-color: #c8c8c8;
}
.dark-green:before {
  background-color: #2dcac0;
}
.red:before {
  background-color: #f56c6c;
}
.purple:before {
  background-color: #722ed1;
}
:deep(.report-card .el-card__header) {
  background-color: #fff;
  font-size: 13px;
  color: #303133;
  padding: 10px 14px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.report-card .el-table th.el-table__cell) {
  background: #fafafa;
  color: #606266;
  font-weight: 500;
}
</style>
