<template>
  <div class="view-chart-wrap">
    <div
      class="view-chart"
      :id="detail.identifier"
      ref="chartView"
      v-show="detail?.dataBind?.identifiers?.length > 0"
      :style="{
        width: detail.style.position.w + 'px',
        height: detail.style.position.h + 'px',
      }"
      @dblclick="handleDblclick"
    />
    <div v-if="showCustomLegend" class="view-chart-legend" @mousedown.stop @click.stop>
      <button
        v-for="(name, index) in chartLegendNames"
        :key="name"
        type="button"
        class="view-chart-legend-item"
        :class="{ 'is-inactive': !isLegendActive(name), 'is-bar-chart': detail.type === 'chart-bar' }"
        @click.stop="toggleLegend(name)"
      >
        <span class="view-chart-legend-symbol" :style="{ color: getLegendColor(index) }"></span>
        <span class="view-chart-legend-text" :style="{ color: detail.style.foreColor }">{{ name }}</span>
      </button>
    </div>
    <div
      v-if="chartTooltip.visible"
      class="view-chart-tooltip"
      :style="{
        left: chartTooltip.x + 'px',
        top: chartTooltip.y + 'px',
      }"
    >
      <div class="view-chart-tooltip-title">{{ chartTooltip.title }}</div>
      <div v-for="item in chartTooltip.items" :key="item.name" class="view-chart-tooltip-item">
        <span class="view-chart-tooltip-dot" :style="{ backgroundColor: item.color }"></span>
        <span class="view-chart-tooltip-name">{{ item.name }}:</span>
        <span class="view-chart-tooltip-value">{{ item.value }}</span>
      </div>
    </div>
    <div v-show="false">{{ height }}{{ width }}{{ foreColor }}</div>
    <div
      v-show="!(detail?.dataBind?.identifiers?.length > 0)"
      :style="{
        width: detail.style.position.w + 'px',
        height: detail.style.position.h + 'px',
        'text-align': 'center',
        'line-height': detail.style.position.h + 'px',
        'font-size': '30px',
        color: '#486ff2',
      }"
      @dblclick="handleDblclick"
    >
      {{ $t('scada.topo.components.chart.view-chart.073848-0') }}
    </div>
    <el-dialog
      class="data-bind-dialog"
      :title="$t('scada.topo.components.chart.view-chart.073848-1')"
      v-model="isVariableDia"
      width="600px"
      append-to-body
    >
      <div class="variable-dialog-body" v-loading="isVariableLoading">
        <div class="data-wrap" v-if="variableTree.length > 0">
          <div class="title-wrap">
            <div class="left">{{ $t('scada.topo.components.chart.view-chart.073848-2') }}</div>
            <div class="right"><i class="el-icon-refresh" @click="handleVariableRefresh"></i></div>
          </div>
          <div class="form-wrap">
            <el-form label-position="top">
              <el-form-item v-for="item in variableTree" :key="item.id" :label="item.name">
                <el-checkbox-group v-model="checkList" style="margin-left: 10px">
                  <el-checkbox
                    v-for="chil in item.children"
                    :key="`${item.id}-${chil.id}`"
                    :value="`${item.id}-${chil.id}`"
                  >
                    {{ chil.name }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <el-empty v-else-if="!isVariableLoading" :description="$t('scada.topo.components.chart.view-chart.073848-3')" />
      </div>
      <template #footer>
        <el-button type="primary" @click="handleConfirmClick">
          {{ $t('scada.topo.components.chart.view-chart.073848-4') }}
        </el-button>
        <el-button @click="isVariableDia = false">
          {{ $t('scada.topo.components.chart.view-chart.073848-5') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { useRoute } from 'vue-router';
import BaseView from '../View.vue';
import { getTime, getNowTime, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
import { getListVariable, getListVariableHistory } from '@/api/scada/topo';

export default {
  name: 'ViewChart',
  extends: BaseView,
  setup() {
    const route = useRoute();
    return { route };
  },
  watch: {
    'detail.style.position.w'() {
      this.resizeChart();
    },
    'detail.style.position.h'() {
      this.resizeChart();
    },
  },
  computed: {
    width() {
      this.$nextTick(function () {
        this.resizeChart();
      });
      return this.detail.style.position.w;
    },
    height() {
      this.$nextTick(function () {
        this.resizeChart();
      });
      return this.detail.style.position.h;
    },
    foreColor() {
      this.$nextTick(function () {
        this.applyChartTextColor(this.option);
        this.myChart && this.setOption(this.option);
      });
      return this.detail.style.foreColor;
    },
    chartLegendNames() {
      const legend = this.option?.legend;
      const legendData = Array.isArray(legend) ? legend[0]?.data : legend?.data;
      if (!Array.isArray(legendData)) return [];
      return legendData
        .map((item) => {
          if (typeof item === 'string') return item;
          return item?.name;
        })
        .filter(Boolean);
    },
    showCustomLegend() {
      return (
        this.usesCustomLegend() && this.detail?.dataBind?.identifiers?.length > 0 && this.chartLegendNames.length > 0
      );
    },
  },
  data() {
    return {
      isVariableDia: false, // 变量对话框
      variableList: [], // 变量列表
      variableTree: [], // 变量树
      checkList: [], // 选中数据
      isVariableLoading: false,
      myChart: null,
      option: {
        title: {
          text: '',
        },
        tooltip: {
          trigger: 'axis',
          show: true,
          triggerOn: 'mousemove|click',
          confine: true,
          axisPointer: {
            type: 'line',
          },
        },
        legend: {
          align: 'right',
          left: '3%',
          top: '6%',
          selectedMode: true,
          textStyle: { color: this.detail.style.foreColor },
        },
        grid: {
          top: '18%',
          left: '10%',
          right: '8%',
          bottom: '14%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {
            formatter: function (param) {
              let time = String(param).split(' ')[1];
              return time || param;
            },
            inside: false,
          },
          axisTick: {
            alignWithLabel: true,
          },
        },
        yAxis: {
          type: 'value',
        },
        series: [],
        graphic: {
          type: 'text', // 类型：文本
          left: 'center',
          top: 'middle',
          silent: true, // 不响应事件
          invisible: false, // 有数据就隐藏
          style: {
            fill: '#9d9d9d',
            fontWeight: 'bold',
            text: '暂无数据',
            fontFamily: 'Microsoft YaHei',
            fontSize: '25px',
          },
        },
      },
      timer: null,
      lastChartSize: '',
      legendVisibleMap: {},
      legendColors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'],
      chartTooltip: {
        visible: false,
        x: 0,
        y: 0,
        title: '',
        items: [],
      },
      tooltipMoveHandler: null,
      tooltipOutHandler: null,
    };
  },
  async mounted() {
    const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
    if (!chartDom) return;
    this.myChart = this.$echarts.init(chartDom);
    this.bindChartEvents();
    this.onResize();
    await this.getVariableList();
    const identifiers = Array.isArray(this.detail?.dataBind?.identifiers) ? this.detail.dataBind.identifiers : [];
    if (!this.editMode && identifiers.length !== 0) {
      this.getChartsData();
      this.timer = setInterval(() => {
        this.getChartsData();
      }, 60000);
    } else {
      const { ldata, xdata, ydata } = this.getDemoChartData();
      this.setChartDatas(ldata, xdata, ydata);
      this.setOption(this.option);
    }
  },
  methods: {
    onResize() {
      this.$nextTick(() => {
        this.resizeChart();
      });
    },
    resizeChart() {
      if (!this.myChart) return;
      try {
        const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
        const width = Number(this.detail.style.position.w) || chartDom?.clientWidth || 0;
        const height = Number(this.detail.style.position.h) || chartDom?.clientHeight || 0;
        if (!chartDom || !width || !height) return;
        chartDom.style.width = `${width}px`;
        chartDom.style.height = `${height}px`;
        const sizeKey = `${width}x${height}`;
        const sizeChanged = this.lastChartSize !== sizeKey;
        this.lastChartSize = sizeKey;
        if (sizeChanged && this.usesCustomLegend()) {
          this.rebuildChart(width, height);
          return;
        }
        this.myChart.resize({
          width,
          height,
        });
      } catch (err) {
        console.warn('ViewChart resize skipped', err);
      }
    },
    rebuildChart(width, height) {
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      if (!chartDom || !this.$echarts) return;
      try {
        this.unbindChartEvents();
        this.myChart?.dispose?.();
        chartDom.removeAttribute('_echarts_instance_');
        chartDom.innerHTML = '';
        this.myChart = this.$echarts.init(chartDom, undefined, {
          width,
          height,
        });
        this.bindChartEvents();
        this.refreshChartOption();
        this.myChart.resize({
          width,
          height,
        });
      } catch (err) {
        console.warn('ViewChart rebuild skipped', err);
      }
    },
    refreshChartOption() {
      if (!this.myChart || !this.option) return;
      const safeOption = this.normalizeChartOption(this.option);
      this.myChart.clear();
      this.myChart.setOption(safeOption, {
        notMerge: true,
        lazyUpdate: false,
        silent: true,
      });
    },
    bindChartEvents() {
      if (!this.myChart || !this.isLineLikeChart()) return;
      this.unbindChartEvents();
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      if (!chartDom) return;
      this.tooltipMoveHandler = (event) => {
        this.showLocalTooltip(event);
      };
      this.tooltipOutHandler = () => {
        this.hideLocalTooltip();
      };
      chartDom.addEventListener('mousemove', this.tooltipMoveHandler);
      chartDom.addEventListener('mouseleave', this.tooltipOutHandler);
    },
    unbindChartEvents() {
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      if (chartDom && this.tooltipMoveHandler) {
        chartDom.removeEventListener('mousemove', this.tooltipMoveHandler);
      }
      if (chartDom && this.tooltipOutHandler) {
        chartDom.removeEventListener('mouseleave', this.tooltipOutHandler);
      }
      this.tooltipMoveHandler = null;
      this.tooltipOutHandler = null;
    },
    showLocalTooltip(event) {
      if (!this.myChart || !this.isLineLikeChart()) return;
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      if (!chartDom) return;
      const rect = chartDom.getBoundingClientRect();
      const point = [Number(event?.clientX || 0) - rect.left, Number(event?.clientY || 0) - rect.top];
      const xData = this.getXAxisData();
      const coordIndex = this.getTooltipDataIndex(point, xData.length);
      const dataIndex = this.normalizeTooltipIndex(coordIndex, xData.length);
      if (dataIndex < 0 || dataIndex >= xData.length) {
        this.hideLocalTooltip();
        return;
      }
      const items = this.getLocalTooltipItems(dataIndex);
      if (items.length === 0) {
        this.hideLocalTooltip();
        return;
      }
      const chartWidth = chartDom?.clientWidth || Number(this.detail.style.position.w) || 0;
      const chartHeight = chartDom?.clientHeight || Number(this.detail.style.position.h) || 0;
      const tooltipWidth = 190;
      const tooltipHeight = Math.min(40 + items.length * 24, 180);
      const nextX = Math.min(Math.max(point[0] + 12, 6), Math.max(chartWidth - tooltipWidth - 6, 6));
      const nextY = Math.min(Math.max(point[1] + 12, 6), Math.max(chartHeight - tooltipHeight - 6, 6));
      this.chartTooltip = {
        visible: true,
        x: nextX,
        y: nextY,
        title: xData[dataIndex],
        items,
      };
    },
    getTooltipDataIndex(point, length) {
      try {
        const inGrid = this.myChart?.containPixel?.({ gridIndex: 0 }, point);
        if (!inGrid) return -1;
        const coord = this.myChart?.convertFromPixel?.({ gridIndex: 0 }, point);
        const coordIndex = Array.isArray(coord) ? Number(coord[0]) : -1;
        if (!Number.isNaN(coordIndex) && coordIndex >= 0) return coordIndex;
      } catch (err) {
        console.warn('ViewChart tooltip position skipped', err);
      }
      return this.getTooltipIndexByGrid(point, length);
    },
    getTooltipIndexByGrid(point, length) {
      const rect = this.getChartGridRect();
      if (!rect || !length) return -1;
      if (point[0] < rect.x || point[0] > rect.x + rect.width || point[1] < rect.y || point[1] > rect.y + rect.height) {
        return -1;
      }
      if (length === 1) return 0;
      const ratio = (point[0] - rect.x) / Math.max(rect.width, 1);
      return ratio * (length - 1);
    },
    getChartGridRect() {
      try {
        const gridModel = this.myChart?.getModel?.()?.getComponent?.('grid', 0);
        const rect = gridModel?.coordinateSystem?.getRect?.();
        if (rect) {
          return {
            x: Number(rect.x) || 0,
            y: Number(rect.y) || 0,
            width: Number(rect.width) || 0,
            height: Number(rect.height) || 0,
          };
        }
      } catch (_err) {
        // fallback below
      }
      const width = Number(this.detail?.style?.position?.w) || 0;
      const height = Number(this.detail?.style?.position?.h) || 0;
      return {
        x: width * 0.12,
        y: height * 0.18,
        width: width * 0.78,
        height: height * 0.66,
      };
    },
    normalizeTooltipIndex(index, length) {
      if (!length) return -1;
      const safeIndex = Math.round(Number(index));
      if (Number.isNaN(safeIndex)) return -1;
      return Math.min(Math.max(safeIndex, 0), length - 1);
    },
    hideLocalTooltip() {
      if (!this.chartTooltip.visible) return;
      this.chartTooltip = {
        ...this.chartTooltip,
        visible: false,
      };
    },
    getXAxisData() {
      const xAxis = Array.isArray(this.option?.xAxis) ? this.option.xAxis[0] : this.option?.xAxis;
      return Array.isArray(xAxis?.data) ? xAxis.data : [];
    },
    getLocalTooltipItems(dataIndex) {
      const series = Array.isArray(this.option?.series) ? this.option.series : [];
      return series
        .filter((item) => item?.type === 'line' && this.isLegendActive(item.name))
        .map((item, index) => ({
          name: item.name,
          value: this.getSeriesPointValue(item.data?.[dataIndex]),
          color: item.lineStyle?.color || item.itemStyle?.color || this.legendColors[index % this.legendColors.length],
        }))
        .filter((item) => item.value !== undefined && item.value !== null && item.value !== '');
    },
    getSeriesPointValue(point) {
      if (Array.isArray(point)) return point[point.length - 1];
      if (point && typeof point === 'object' && 'value' in point) {
        return Array.isArray(point.value) ? point.value[point.value.length - 1] : point.value;
      }
      return point;
    },
    isLineLikeChart() {
      return this.detail.type === 'chart-line' || this.detail.type === 'chart-line-step';
    },
    usesCustomLegend() {
      return this.isLineLikeChart() || this.detail.type === 'chart-bar';
    },
    isLegendActive(name) {
      return this.legendVisibleMap[name] !== false;
    },
    getChartTextColor() {
      return this.detail?.style?.foreColor || '#333';
    },
    getChartSplitLineColors() {
      const color = 'rgba(84, 112, 198, 0.18)';
      return ['transparent', color, color, color, color, color, color, color, color, color];
    },
    applyAxisTextColor(axis) {
      const color = this.getChartTextColor();
      const safeAxis = axis || {};
      return {
        ...safeAxis,
        axisLabel: {
          ...(safeAxis.axisLabel || {}),
          color,
        },
        axisLine: {
          ...(safeAxis.axisLine || {}),
          lineStyle: {
            ...(safeAxis.axisLine?.lineStyle || {}),
            color,
          },
        },
        axisTick: {
          ...(safeAxis.axisTick || {}),
          lineStyle: {
            ...(safeAxis.axisTick?.lineStyle || {}),
            color,
          },
        },
        nameTextStyle: {
          ...(safeAxis.nameTextStyle || {}),
          color,
        },
      };
    },
    applyChartTextColor(option) {
      if (!option || typeof option !== 'object') return option;
      const color = this.getChartTextColor();
      const applyLegend = (legend) => ({
        ...(legend || {}),
        textStyle: {
          ...(legend?.textStyle || {}),
          color,
        },
      });
      if (option.legend) {
        option.legend = Array.isArray(option.legend)
          ? option.legend.map((legend) => applyLegend(legend))
          : applyLegend(option.legend);
      }
      option.xAxis = Array.isArray(option.xAxis)
        ? option.xAxis.map((axis) => this.applyAxisTextColor(axis))
        : this.applyAxisTextColor(option.xAxis);
      option.yAxis = Array.isArray(option.yAxis)
        ? option.yAxis.map((axis) => this.applyAxisTextColor(axis))
        : this.applyAxisTextColor(option.yAxis);
      return option;
    },
    toggleLegend(name) {
      const nextVisible = this.legendVisibleMap[name] === false;
      this.legendVisibleMap = {
        ...this.legendVisibleMap,
        [name]: nextVisible,
      };
      this.hideLocalTooltip();
      this.refreshChartOption();
    },
    getLegendColor(index) {
      const series = Array.isArray(this.option?.series) ? this.option.series : [];
      const seriesColor = series[index]?.lineStyle?.color || series[index]?.itemStyle?.color;
      if (seriesColor) return seriesColor;
      return this.legendColors[index % this.legendColors.length];
    },
    syncLegendVisibleMap(legendData) {
      const nextMap = {};
      (Array.isArray(legendData) ? legendData : []).forEach((item) => {
        const name = typeof item === 'string' ? item : item?.name;
        if (name) {
          nextMap[name] = this.legendVisibleMap[name] !== false;
        }
      });
      this.legendVisibleMap = nextMap;
    },
    formatTooltip(params) {
      const list = (Array.isArray(params) ? params : [params])
        .map((item) => ({ item, value: this.getTooltipValue(item) }))
        .filter(({ item, value }) => {
          const hasValue = value !== undefined && value !== null && value !== '';
          return hasValue && (!this.isLineLikeChart() || this.isLegendActive(item.seriesName));
        });
      if (list.length === 0) return '';
      const title = this.getTooltipTitle(list[0].item);
      const values = list.map(({ item, value }) => `${item.marker || ''}${item.seriesName}: ${value}`);
      return [title, ...values].filter(Boolean).join('<br/>');
    },
    getTooltipTitle(item) {
      if (!item) return '';
      const xAxisData = this.getXAxisData();
      const dataIndex = Array.isArray(item.value) ? Number(item.value[0]) : item.dataIndex;
      if (Number.isInteger(dataIndex) && xAxisData[dataIndex]) return xAxisData[dataIndex];
      return item.axisValueLabel || item.axisValue || item.name || '';
    },
    getTooltipValue(item) {
      if (!item) return '';
      if (Array.isArray(item.value)) return item.value[item.value.length - 1];
      if (item.value !== undefined && item.value !== null) return item.value;
      if (Array.isArray(item.data)) return item.data[item.data.length - 1];
      if (item.data && typeof item.data === 'object' && 'value' in item.data) {
        return Array.isArray(item.data.value) ? item.data.value[item.data.value.length - 1] : item.data.value;
      }
      return item.data;
    },
    getDemoLegendNames() {
      const identifiers = Array.isArray(this.detail?.dataBind?.identifiers) ? this.detail.dataBind.identifiers : [];
      const names = identifiers
        .map((identifier) => {
          return this.variableList.find((item) => item.identifier === identifier)?.modelName || identifier;
        })
        .filter(Boolean);
      return names.length > 0 ? names : ['XXX'];
    },
    getDemoChartData() {
      const ldata = this.getDemoLegendNames();
      const xdata = ['2024:01:01 12:00:00', '2024:01:01 13:00:00', '2024:01:01 14:00:00'];
      const ydata = ldata.map((name, seriesIndex) => {
        const start = 10 + seriesIndex * 8 + Math.floor(Math.random() * 16);
        return {
          name,
          data: xdata.map((_time, pointIndex) => {
            if (pointIndex === 0) return start;
            return Math.max(0, start + pointIndex * 4 + Math.floor(Math.random() * 21) - 10);
          }),
        };
      });
      return { ldata, xdata, ydata };
    },
    // 双击
    async handleDblclick() {
      if (this.editMode) {
        this.isVariableDia = true;
        await this.getVariableList();
        this.syncVariableCheckList();
      }
    },
    // 设备列表刷新
    handleVariableRefresh() {
      this.getVariableList(true).then(() => {
        this.syncVariableCheckList();
      });
    },
    // 获取设备变量
    async getVariableList(force = false) {
      if (this.variableTree.length !== 0 && !force) {
        this.isVariableLoading = false;
        return this.variableTree;
      }
      const params = {
        scadaGuid: getRouteQueryString(this.route.query, 'guid'),
        type: getScadaRouteType(this.route.query),
        page: 1,
        size: 9999,
        ts: Date.now(),
      };
      this.isVariableLoading = true;
      try {
        const res = await getListVariable(params);
        if (res.code == 200) {
          this.variableList = Array.isArray(res.rows) ? res.rows : [];
          this.variableTree = this.formatVariableTree(this.variableList);
        }
        return res;
      } catch (err) {
        console.warn('get chart variable list failed', err);
        return err;
      } finally {
        this.isVariableLoading = false;
      }
    },
    // 回显已绑定变量
    syncVariableCheckList() {
      const bind = this.detail?.dataBind || {};
      const identifiers = Array.isArray(bind.identifiers) ? bind.identifiers.map((item) => String(item)) : [];
      if (identifiers.length === 0 || !Array.isArray(this.variableTree) || this.variableTree.length === 0) {
        this.checkList = [];
        return;
      }

      const type = getScadaRouteType(this.route.query);
      const routeSerialNumber = getRouteQueryString(this.route.query, 'serialNumber');
      const bindDeviceId =
        type === 1
          ? routeSerialNumber || bind.serialNumber || bind.sceneModelDeviceId
          : type === 2
            ? bind.sceneModelDeviceId
            : bind.serialNumber;
      const matched = this.getVariableCheckedLabels(identifiers, bindDeviceId);
      this.checkList = matched.length > 0 ? matched : this.getVariableCheckedLabels(identifiers);
    },
    getVariableCheckedLabels(identifiers, deviceId) {
      const identifierSet = new Set(identifiers.map((item) => String(item)));
      const targetDeviceId = deviceId === undefined || deviceId === null ? '' : String(deviceId);
      return this.variableTree.reduce((labels, item) => {
        const itemId = item?.id === undefined || item?.id === null ? '' : String(item.id);
        if (targetDeviceId && itemId !== targetDeviceId) return labels;
        const children = Array.isArray(item?.children) ? item.children : [];
        children.forEach((child) => {
          const childId = child?.id === undefined || child?.id === null ? '' : String(child.id);
          if (identifierSet.has(childId)) {
            labels.push(`${item.id}-${child.id}`);
          }
        });
        return labels;
      }, []);
    },
    // 格式化属性数据
    formatVariableTree(list) {
      let datas = [];
      if (Array.isArray(list) && list.length !== 0) {
        list.forEach((item, i) => {
          const type = getScadaRouteType(this.route.query);
          let serialNum = item.serialNumber;
          let deviceNam = item.deviceName;
          if (type === 1) {
            deviceNam = item.productName;
          }
          if (type === 2) {
            serialNum = item.sceneModelDeviceId;
            deviceNam = item.sceneModelDeviceName;
          }
          if (i === 0) {
            let par = {
              id: serialNum,
              name: deviceNam,
              children: [
                {
                  id: item.identifier,
                  name: item.modelName,
                },
              ],
            };
            datas.push(par);
          } else {
            const chil = {
              id: item.identifier,
              name: item.modelName,
            };
            if (type === 1) {
              datas[0].children.push(chil);
            } else {
              const par = datas.find((d) => d.id === serialNum);
              if (par) {
                par.children.push(chil);
              } else {
                const par = {
                  id: serialNum,
                  name: deviceNam,
                  children: [
                    {
                      id: item.identifier,
                      name: item.modelName,
                    },
                  ],
                };
                datas.push(par);
              }
            }
          }
        });
      }
      return datas;
    },
    // 确认数据
    handleConfirmClick() {
      if (this.checkList.length !== 0) {
        let id = this.checkList[0].split('-')[0];
        let res = this.checkList.find((item) => item.split('-')[0] !== id);
        if (res) {
          this.$message.error(this.$t('scada.topo.components.chart.view-chart.073848-6'));
          return;
        } else {
          const type = getScadaRouteType(this.route.query);
          if (type === 1) {
            this.detail.dataBind.serialNumber = '';
          } else if (type === 2) {
            this.detail.dataBind.sceneModelDeviceId = this.checkList[0].split('-')[0];
          } else {
            this.detail.dataBind.serialNumber = this.checkList[0].split('-')[0];
          }
          this.detail.dataBind.identifiers = this.checkList.map((item) => item.split('-')[1]);
        }
        this.$modal.msgSuccess(this.$t('scada.topo.components.chart.view-chart.073848-7'));
        // 例子
        const { ldata, xdata, ydata } = this.getDemoChartData();
        this.setChartDatas(ldata, xdata, ydata);
        this.setOption(this.option);
        this.isVariableDia = false;
      }
    },
    // 设置表格数据
    setChartDatas(ldata, xdata, ydata) {
      const isLineLikeChart = this.isLineLikeChart();
      const isStepLineChart = this.detail.type == 'chart-line-step';
      const safeXData = Array.isArray(xdata) ? xdata : [];
      this.option.legend.data = ldata;
      this.syncLegendVisibleMap(ldata);
      this.option.xAxis.data = safeXData;
      this.option.xAxis.boundaryGap = !isLineLikeChart;
      delete this.option.yAxis.min;
      delete this.option.yAxis.max;
      this.option.series = [];
      ydata.forEach((element, index) => {
        let data = {};
        if (isLineLikeChart) {
          data = {
            id: `${this.detail.identifier}-${isStepLineChart ? 'line-step' : 'line'}-${index}`,
            name: element.name,
            type: 'line',
            stack: 'Total' + index,
            ...(isStepLineChart ? { step: 'start' } : {}),
            data: element.data,
            legendHoverLink: false,
            animation: false,
            symbol: 'circle',
            symbolSize: 6,
            showSymbol: true,
            connectNulls: true,
            emphasis: {
              scale: true,
            },
            itemStyle: {},
          };
        } else {
          data = {
            id: `${this.detail.identifier}-bar-${index}`,
            name: element.name,
            type: 'bar',
            coordinateSystem: 'cartesian2d',
            xAxisIndex: 0,
            yAxisIndex: 0,
            stack: 'Total',
            barWidth: 17,
            data: element.data,
            legendHoverLink: false,
            animation: false,
            itemStyle: {},
          };
        }
        this.option.series.push(data);
      });
      this.option.graphic.invisible = ydata.length > 0;
    },
    normalizeChartOption(option) {
      const isLineLikeChart = this.isLineLikeChart();
      const usesCustomLegend = this.usesCustomLegend();
      const safeOption = option && typeof option === 'object' ? { ...option } : {};
      const rawSeries = Array.isArray(safeOption.series)
        ? safeOption.series
            .filter((item) => item && typeof item === 'object' && typeof item.type === 'string')
            .map((item, index) => {
              const color =
                item.itemStyle?.color || item.lineStyle?.color || this.legendColors[index % this.legendColors.length];
              const seriesType = this.detail.type === 'chart-bar' ? 'bar' : item.type;
              const isCartesianSeries = ['line', 'bar', 'scatter', 'effectScatter', 'custom'].includes(seriesType);
              const sourceData = Array.isArray(item.data) ? item.data : [];
              const normalizedData = sourceData.map((value) => {
                if (Array.isArray(value) && seriesType === 'bar') return value[value.length - 1];
                return value === undefined ? null : value;
              });
              const normalizedSeries = {
                ...item,
                type: seriesType,
                coordinateSystem: isCartesianSeries ? item.coordinateSystem || 'cartesian2d' : item.coordinateSystem,
                xAxisIndex: isCartesianSeries ? (item.xAxisIndex ?? 0) : item.xAxisIndex,
                yAxisIndex: isCartesianSeries ? (item.yAxisIndex ?? 0) : item.yAxisIndex,
                silent: false,
                tooltip: item.tooltip,
                itemStyle: {
                  ...(item.itemStyle || {}),
                  color,
                },
                lineStyle:
                  item.type === 'line'
                    ? {
                        ...(item.lineStyle || {}),
                        color,
                      }
                    : item.lineStyle,
                data: normalizedData,
              };
              if (seriesType === 'bar' && 'renderItem' in normalizedSeries) {
                delete normalizedSeries.renderItem;
              }
              return normalizedSeries;
            })
        : [];
      const series = usesCustomLegend ? rawSeries.filter((item) => this.isLegendActive(item.name)) : rawSeries;
      safeOption.series = series;
      safeOption.color = safeOption.color || this.legendColors;
      const hasOriginalSeries = rawSeries.length > 0;

      const hasCartesianSeries = rawSeries.some((item) =>
        ['line', 'bar', 'scatter', 'effectScatter', 'custom'].includes(item.type)
      );
      if (hasCartesianSeries) {
        const firstXAxis = Array.isArray(safeOption.xAxis) ? safeOption.xAxis[0] : safeOption.xAxis;
        const xAxisData = Array.isArray(firstXAxis?.data) ? firstXAxis.data : [];
        const normalizeXAxis = (axis) => ({
          ...(axis || {}),
          type: axis?.type || 'category',
          data: Array.isArray(axis?.data) ? axis.data : xAxisData,
        });
        const normalizeYAxis = (axis) => ({
          ...(axis || {}),
          type: axis?.type || 'value',
        });
        if (!safeOption.xAxis || (Array.isArray(safeOption.xAxis) && safeOption.xAxis.length === 0)) {
          safeOption.xAxis = { type: 'category', data: [] };
        }
        if (!safeOption.yAxis || (Array.isArray(safeOption.yAxis) && safeOption.yAxis.length === 0)) {
          safeOption.yAxis = { type: 'value' };
        }
        if (!safeOption.grid || (Array.isArray(safeOption.grid) && safeOption.grid.length === 0)) {
          safeOption.grid = {
            top: '18%',
            left: '10%',
            right: '8%',
            bottom: '14%',
            containLabel: true,
          };
        }
        safeOption.xAxis = Array.isArray(safeOption.xAxis)
          ? safeOption.xAxis.map((axis) => normalizeXAxis(axis || {}))
          : normalizeXAxis(safeOption.xAxis || {});
        safeOption.yAxis = Array.isArray(safeOption.yAxis)
          ? safeOption.yAxis.map((axis) => normalizeYAxis(axis || {}))
          : normalizeYAxis(safeOption.yAxis || {});
      }

      if (isLineLikeChart) {
        const setLineXAxis = (axis) => ({
          ...(axis || {}),
          type: axis?.type || 'category',
          data: Array.isArray(axis?.data) ? axis.data : [],
          boundaryGap: false,
          axisLabel: {
            ...(axis?.axisLabel || {}),
            hideOverlap: true,
            margin: 12,
            showMinLabel: true,
            showMaxLabel: true,
          },
          axisLine: {
            ...(axis?.axisLine || {}),
            show: true,
            onZero: false,
            lineStyle: {
              ...(axis?.axisLine?.lineStyle || {}),
              color: this.getChartTextColor(),
            },
          },
          axisTick: {
            ...(axis?.axisTick || {}),
            alignWithLabel: false,
            lineStyle: {
              ...(axis?.axisTick?.lineStyle || {}),
              color: this.getChartTextColor(),
            },
          },
          splitLine: {
            ...(axis?.splitLine || {}),
            show: false,
          },
        });
        safeOption.xAxis = Array.isArray(safeOption.xAxis)
          ? safeOption.xAxis.map((axis) => setLineXAxis(axis || {}))
          : setLineXAxis(safeOption.xAxis || {});

        const setLineYAxis = (axis) => ({
          ...(axis || {}),
          type: axis?.type || 'value',
          axisLine: {
            ...(axis?.axisLine || {}),
            lineStyle: {
              ...(axis?.axisLine?.lineStyle || {}),
              color: this.getChartTextColor(),
            },
          },
          axisTick: {
            ...(axis?.axisTick || {}),
            lineStyle: {
              ...(axis?.axisTick?.lineStyle || {}),
              color: this.getChartTextColor(),
            },
          },
          splitLine: {
            ...(axis?.splitLine || {}),
            show: true,
            lineStyle: {
              ...(axis?.splitLine?.lineStyle || {}),
              color: this.getChartSplitLineColors(),
              type: 'solid',
            },
          },
        });
        safeOption.yAxis = Array.isArray(safeOption.yAxis)
          ? safeOption.yAxis.map((axis) => setLineYAxis(axis || {}))
          : setLineYAxis(safeOption.yAxis || {});

        const setLineGrid = (grid) => ({
          ...grid,
          top: '18%',
          left: '10%',
          right: '10%',
          bottom: '16%',
          containLabel: true,
        });
        safeOption.grid = Array.isArray(safeOption.grid)
          ? safeOption.grid.map((grid) => setLineGrid(grid || {}))
          : setLineGrid(safeOption.grid || {});
        if (safeOption.graphic) {
          safeOption.graphic = {
            ...safeOption.graphic,
            invisible: hasOriginalSeries ? true : safeOption.graphic.invisible,
          };
        }
      }

      const rawTooltip = safeOption.tooltip || {};
      safeOption.tooltip = {
        ...rawTooltip,
        show: rawTooltip.show !== false,
        confine: rawTooltip.confine ?? true,
        renderMode: 'html',
        enterable: false,
        alwaysShowContent: false,
        transitionDuration: 0,
        extraCssText: [
          rawTooltip.extraCssText || '',
          'z-index: 99999',
          'pointer-events: none',
          'box-shadow: 0 2px 8px rgba(0,0,0,0.15)',
        ]
          .filter(Boolean)
          .join(';'),
        trigger: rawTooltip.trigger || 'axis',
        triggerOn: rawTooltip.triggerOn || 'mousemove|click',
        formatter: hasCartesianSeries ? (params) => this.formatTooltip(params) : rawTooltip.formatter,
      };
      safeOption.tooltip.axisPointer = {
        type: 'line',
        snap: true,
        ...(rawTooltip.axisPointer || {}),
      };

      const legendNames = rawSeries.map((item) => item.name).filter(Boolean);
      if (usesCustomLegend) {
        delete safeOption.legend;
      } else if (safeOption.legend || legendNames.length > 0) {
        const applyLegend = (legend) => ({
          ...legend,
          data: Array.isArray(legend.data) && legend.data.length > 0 ? legend.data : legendNames,
          textStyle: {
            ...(legend.textStyle || {}),
            color: this.getChartTextColor(),
          },
          show: legend.show !== false,
          selectedMode: legend.selectedMode === undefined ? true : legend.selectedMode,
        });
        safeOption.legend = Array.isArray(safeOption.legend)
          ? safeOption.legend.map((legend) => applyLegend(legend || {}))
          : applyLegend(safeOption.legend || {});
      }

      return this.applyChartTextColor(safeOption);
    },
    setOption(option) {
      if (!this.myChart) return;
      try {
        this.option = option;
        this.refreshChartOption();
        this.onResize();
      } catch (err) {
        console.warn('ViewChart setOption failed', err, option);
      }
    },
    // 获取属性数据
    getChartsData() {
      const type = getScadaRouteType(this.route.query);
      const { serialNumber, identifiers, sceneModelDeviceId } = this.detail.dataBind;
      const safeIdentifiers = Array.isArray(identifiers) ? identifiers : [];
      const tempIdentifiers = safeIdentifiers
        .map((item) => {
          const obj = this.variableList.find((p) => p.identifier === item);
          if (!obj) return null;
          return {
            identifier: obj.identifier,
            type: obj.type,
          };
        })
        .filter(Boolean);
      if (safeIdentifiers.length !== 0 && tempIdentifiers.length === 0) {
        this.setChartDatas([], [], []);
        this.setOption(this.option);
        return;
      }
      let params: any = {
        scadaType: type,
        thingsModelList: tempIdentifiers,
        beginTime: getTime(8),
        endTime: getNowTime(),
        ts: Date.now() + Math.floor(Math.random() * 100),
      };
      if (type === 1) {
        const routeSerial = getRouteQueryString(this.route.query, 'serialNumber');
        if (!routeSerial) {
          return;
        } else {
          params = { ...params, serialNumber: routeSerial };
        }
      } else if (type === 2) {
        params = { ...params, sceneModelDeviceId: sceneModelDeviceId };
      } else {
        params = { ...params, serialNumber: serialNumber };
      }
      getListVariableHistory(params)
        .then((res) => {
          if (res.code == 200) {
            const historyData = res.data || {};
            const modelIdens = Object.keys(historyData) || [];
            if (modelIdens.length !== 0) {
              const ldata = modelIdens.map(
                (item) => this.variableList.find((p) => p.identifier === item)?.modelName || item
              );
              const xdata = (historyData[modelIdens[0]] || []).map((item) => item.time);
              const ydata = modelIdens.map((item) => ({
                name: this.variableList.find((p) => p.identifier === item)?.modelName || item,
                data: (historyData[item] || []).map((v) => v.value),
              }));
              this.setChartDatas(ldata, xdata, ydata);
              this.setOption(this.option);
            } else {
              this.setChartDatas([], [], []);
              this.setOption(this.option);
            }
          }
        })
        .catch((err) => {
          console.warn('get chart history failed', err);
          this.setChartDatas([], [], []);
          this.setOption(this.option);
        });
    },
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
    this.unbindChartEvents();
    if (this.myChart) {
      this.myChart.dispose();
      this.myChart = null;
    }
  },
};
</script>

<style lang="scss" scoped>
.view-chart {
  height: 100%;
  width: 100%;
  text-align: center;
  display: block;
  overflow: hidden;
  position: relative;
}

.view-chart-wrap {
  position: relative;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.view-chart-legend {
  position: absolute;
  top: 14px;
  left: 44px;
  z-index: 2;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 14px;
  max-width: calc(100% - 88px);
  pointer-events: auto;
}

.view-chart-legend-item {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0;
  font-size: 14px;
  line-height: 22px;
  white-space: nowrap;
  cursor: pointer;
  background: transparent;
  border: 0;
  outline: none;

  &.is-inactive {
    opacity: 0.45;
  }
}

.view-chart-legend-symbol {
  position: relative;
  width: 26px;
  height: 0;
  margin-right: 6px;
  border-top: 3px solid currentColor;

  &::after {
    position: absolute;
    top: -7px;
    left: 8px;
    width: 10px;
    height: 10px;
    content: '';
    background: currentColor;
    border-radius: 50%;
  }
}

.view-chart-legend-item.is-bar-chart {
  .view-chart-legend-symbol {
    width: 24px;
    height: 12px;
    border-top: 0;
    background: currentColor;
    border-radius: 2px;

    &::after {
      display: none;
    }
  }
}

.view-chart-tooltip {
  position: absolute;
  z-index: 6;
  min-width: 160px;
  max-width: 220px;
  padding: 10px 12px;
  pointer-events: none;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid #d9e2ff;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.16);
}

.view-chart-tooltip-title {
  margin-bottom: 8px;
  font-size: 13px;
  line-height: 18px;
  color: #606266;
  white-space: nowrap;
}

.view-chart-tooltip-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  line-height: 22px;
  color: #303133;
}

.view-chart-tooltip-dot {
  flex: none;
  width: 10px;
  height: 10px;
  margin-right: 6px;
  border-radius: 50%;
}

.view-chart-tooltip-name {
  margin-right: 4px;
}

.view-chart-tooltip-value {
  font-weight: 600;
}

.data-bind-dialog {
  .variable-dialog-body {
    min-height: 400px;
  }

  .data-wrap {
    min-height: 400px;

    :deep(.el-form-item__label) {
      padding: 0;
    }

    :deep(.el-form-item__content) {
      margin-left: 14px;
    }

    .title-wrap {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      margin-bottom: 15px;

      .left {
        color: red;
      }

      .right {
        cursor: pointer;
      }
    }

    .form-wrap {
      height: 390px;
      overflow-y: auto;
    }
  }
}
</style>
