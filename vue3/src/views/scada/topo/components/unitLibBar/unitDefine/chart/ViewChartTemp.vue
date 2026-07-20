<template>
  <div class="view-chart-temp-wrap">
    <div
      class="view-chart-temp"
      ref="chartView"
      :id="detail.identifier"
      :style="{
        width: detail.style.position.w + 'px',
        height: detail.style.position.h + 'px',
      }"
    />
    <div v-show="false">{{ height }}{{ width }}{{ chartsValue }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import { judgeSize, convertToMilliseconds, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewChartTemp',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  props: {},
  watch: {
    'detail.style.position.w'() {
      this.resizeChart();
    },
    'detail.style.position.h'() {
      this.resizeChart();
    },
  },
  computed: {
    mqttData() {
      return this.topoStore?.mqttData || {};
    },
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
    chartsValue() {
      if (Object.keys(this.mqttData || {}).length !== 0) {
        const type = getScadaRouteType(this.route.query);
        let bindNum = this.detail.dataBind.serialNumber;
        let mqttNum = this.mqttData.serialNumber;
        if (type === 1) {
          bindNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (type === 2) {
          if (this.detail.dataBind.variableType && this.detail.dataBind.variableType !== 1) {
            bindNum = this.detail.dataBind.sceneModelDeviceId;
            mqttNum = this.mqttData.sceneModelDeviceId;
          }
        }
        const messages = Array.isArray(this.mqttData.message) ? this.mqttData.message : [];
        if (bindNum && this.isSameValue(bindNum, mqttNum) && this.detail.dataBind.identifier) {
          for (let i = 0; i < messages.length; i++) {
            if (this.detail.dataBind.identifier == messages[i].id) {
              const value = messages[i].value || 0;
              this.setColor(value);
              this.setOption(value);
            }
          }
        }
      }
    },
  },
  data() {
    return {
      defaultColor: this.detail.style.foreColor,
      myChart: null,
      option: {},
      timer: null,
      lastChartSize: '',
      currentValue: null,
      _interactionNoticeHandler: null,
    };
  },
  mounted() {
    const { modelValue } = this.detail.dataBind;
    const { dataType } = this.detail.dataBind || {};
    const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
    if (!chartDom) return;
    const { width, height } = this.getChartSize();
    this.myChart = this.$echarts.init(chartDom, undefined, {
      width,
      height,
    });
    this.resizeChart();
    if (this.editMode) {
      this.setOption(0);
    } else {
      if (dataType === 1) {
        const value = modelValue || 0;
        this.setColor(value);
        this.setOption(value);
      } else if (dataType === 4 || dataType === 5) {
        this.initHttp();
      } else {
        this.mockData();
      }
    }
  },
  methods: {
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    bindInteractionNotice() {
      const bus = this.$busEvent;
      if (!bus || this._interactionNoticeHandler) return;
      const { identifier } = this.detail;
      this._interactionNoticeHandler = (data) => {
        const { compId, params, headers } = data || {};
        if (identifier === compId) {
          this.setHttp(params, headers);
        }
      };
      if (typeof bus.on === 'function') bus.on('interactionNotice', this._interactionNoticeHandler);
      else if (typeof bus.$on === 'function') bus.$on('interactionNotice', this._interactionNoticeHandler);
    },
    unbindInteractionNotice() {
      const bus = this.$busEvent;
      if (!bus || !this._interactionNoticeHandler) return;
      if (typeof bus.off === 'function') bus.off('interactionNotice', this._interactionNoticeHandler);
      else if (typeof bus.$off === 'function') bus.$off('interactionNotice', this._interactionNoticeHandler);
      this._interactionNoticeHandler = null;
    },
    getChartSize() {
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      return {
        width: Number(this.detail.style.position.w) || chartDom?.clientWidth || 0,
        height: Number(this.detail.style.position.h) || chartDom?.clientHeight || 0,
      };
    },
    resizeChart() {
      if (!this.myChart) return;
      this.$nextTick(() => {
        try {
          const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
          const { width, height } = this.getChartSize();
          if (!chartDom || !width || !height) return;
          chartDom.style.width = `${width}px`;
          chartDom.style.height = `${height}px`;
          const sizeKey = `${width}x${height}`;
          const sizeChanged = this.lastChartSize !== sizeKey;
          this.lastChartSize = sizeKey;
          if (sizeChanged) {
            this.rebuildChart(width, height);
            return;
          }
          this.myChart.resize({ width, height });
        } catch (err) {
          console.warn('ViewChartTemp resize skipped', err);
        }
      });
    },
    rebuildChart(width, height) {
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      if (!chartDom || !this.$echarts) return;
      try {
        this.myChart?.dispose?.();
        chartDom.removeAttribute('_echarts_instance_');
        chartDom.innerHTML = '';
        this.myChart = this.$echarts.init(chartDom, undefined, {
          width,
          height,
        });
        if (this.currentValue !== null && this.currentValue !== undefined) {
          this.setOption(this.currentValue);
        }
        this.myChart.resize({ width, height });
      } catch (err) {
        console.warn('ViewChartTemp rebuild skipped', err);
      }
    },
    getTempLayout() {
      const { width, height } = this.getChartSize();
      const safeWidth = width || 300;
      const safeHeight = height || 250;
      const shortSide = Math.min(safeWidth, safeHeight);
      const axisLabelFontSize = this.clamp(Math.round(shortSide / 24), 10, 14);
      const valueFontSize = this.clamp(Math.round(shortSide / 18), 12, 18);
      const tubeWidth = this.clamp(Math.round(safeWidth / 42), 6, 14);
      const tubeInnerWidth = this.clamp(Math.round(tubeWidth * 0.72), 5, 10);
      const shellBorderWidth = this.clamp(Math.round(tubeWidth * 1.45), 8, 18);
      const bulbOuter = this.clamp(Math.round(shortSide * 0.16), 42, 72);
      const bulbInner = this.clamp(Math.round(bulbOuter * 0.52), 24, 40);
      const top = this.clamp(Math.round(safeHeight * 0.15), 28, 72);
      const bottom = this.clamp(Math.round(safeHeight * 0.22), bulbOuter + 22, 98);
      const gridHeight = Math.max(safeHeight - top - bottom, 80);
      const gridWidth = Math.max(bulbOuter, tubeWidth + shellBorderWidth * 2);
      const axisReserve = axisLabelFontSize * 3 + 24;
      const centerX = safeWidth * 0.5;
      const gridLeft = this.clamp(Math.round(centerX - gridWidth / 2), axisReserve, safeWidth - gridWidth - 16);
      return {
        gridLeft,
        gridTop: top,
        gridWidth,
        gridHeight,
        axisLabelFontSize,
        axisLabelMargin: this.clamp(Math.round(safeWidth / 48), 10, 22),
        tickLength: this.clamp(Math.round(shortSide / 48), 4, 8),
        minorTickLength: this.clamp(Math.round(shortSide / 70), 2, 5),
        tubeWidth,
        tubeInnerWidth,
        shellBorderWidth,
        bulbOuter,
        bulbInner,
        valueFontSize,
        valueLabelPosition: [Math.round(tubeWidth * 1.8), -Math.round(valueFontSize * 1.2)],
      };
    },
    clamp(value, min, max) {
      return Math.min(Math.max(value, min), max);
    },
    toFiniteNumber(value, fallback) {
      const number = Number(value);
      return Number.isFinite(number) ? number : fallback;
    },
    setOption(value) {
      if (!this.myChart) return;
      // 将回调延迟到下次DOM更新循环之后执行。在修改数据之后立即使用它，然后等待DOM更新
      this.currentValue = value;
      const layout = this.getTempLayout();
      let max = this.toFiniteNumber(this.detail.dataBind.paramMax, 100),
        min = this.toFiniteNumber(this.detail.dataBind.paramMin, 0),
        temp = this.toFiniteNumber(value, 0),
        tempColor = this.detail.style.foreColor,
        interval = this.toFiniteNumber(this.detail.dataBind.interval, undefined),
        unit = this.detail.dataBind.unit == null ? '' : this.detail.dataBind.unit;
      if (max <= min) {
        max = min + 100;
      }
      temp = this.clamp(temp, min, max);
      this.option = {
        grid: {
          left: layout.gridLeft,
          top: layout.gridTop,
          width: layout.gridWidth,
          height: layout.gridHeight,
          containLabel: false,
        },
        title: {
          bottom: 'bottom',
          left: 'center',
          textStyle: { fontSize: 14 },
        },
        xAxis: [
          {
            type: 'category',
            data: [''],
            show: false,
            axisTick: {
              alignWithLabel: true,
            },
            axisLine: {
              onZero: false,
            },
          },
        ],
        yAxis: [
          {
            type: 'value',
            min: min,
            max: max,
            interval: interval && interval > 0 ? interval : undefined,
            axisTick: {
              show: true,
              length: layout.tickLength,
              lineStyle: {
                // color: 'white',
              },
            },
            minorTick: {
              show: true,
              length: layout.minorTickLength,
            },
            nameTextStyle: {
              color: 'white',
            },
            axisLine: {
              // onZero:false,
            },
            splitLine: {
              show: false,
            },
            offset: 10,
            axisLabel: {
              margin: layout.axisLabelMargin,
              // color: '#fff',
              fontSize: layout.axisLabelFontSize,
            },
          },
        ],
        series: [
          {
            name: this.$t('topo.components.chart.073848-24'),
            type: 'bar',
            // xAxisIndex: 2,
            data: [max],
            barWidth: layout.tubeWidth,
            itemStyle: {
              color: 'transparent',
              barBorderRadius: 50,
              borderWidth: layout.shellBorderWidth,
              borderType: 'solid',
              borderColor: 'grey',
            },
            z: 1,
          },
          {
            z: 19,
            barGap: '-100%',
            type: 'bar',
            barWidth: layout.tubeInnerWidth,
            stack: 'Total',
            label: {
              show: true,
              formatter: function (p) {
                return '  {temp|' + p.value + unit + '}';
              },
              position: layout.valueLabelPosition,
              rich: {
                temp: {
                  // color: 'white',
                  lineHeight: 30,
                  padding: [0, 0, 0, 3],
                  fontSize: layout.valueFontSize,
                  verticalAlign: 'middle',
                  align: 'center',
                  height: 30,
                },
              },
            },
            itemStyle: {
              borderWidth: 3,
              borderColor: tempColor,
            },
            showBackground: true,
            backgroundStyle: {
              color: '#cccccc',
            },
            data: [temp],
          },
          {
            name: this.$t('topo.components.chart.073848-25'),
            type: 'scatter',
            hoverAnimation: false,
            data: [min],
            symbolSize: layout.bulbOuter,
            itemStyle: {
              normal: {
                color: 'grey',
                opacity: 1,
              },
            },
            z: 12,
          },
          {
            name: this.$t('topo.components.chart.073848-26'),
            type: 'scatter',
            hoverAnimation: false,
            data: [min],
            symbolSize: layout.bulbInner,
            itemStyle: {
              normal: {
                color: tempColor,
                opacity: 1,
              },
            },
            z: 18,
          },
        ],
      };
      this.$nextTick(function () {
        const { width, height } = this.getChartSize();
        this.myChart?.clear();
        this.myChart?.setOption(this.option, true);
        this.myChart?.resize({ width, height });
      });
    },
    // 颜色初始化
    initColor() {
      let value = !this.detail.dataBind.modelValue ? 0 : this.detail.dataBind.modelValue;
      this.setColor(value);
    },
    // 设置颜色
    setColor(val) {
      const { stateList } = this.detail.dataBind || {};
      if (stateList && stateList.length !== 0) {
        const stateItem = stateList.find((item) => judgeSize(item.paramCondition, val, item.paramData));
        if (stateItem) {
          this.detail.style.foreColor = stateItem.foreColor;
        } else {
          this.detail.style.foreColor = this.defaultColor;
        }
      }
    },
    // 生成模拟数据
    mockData() {
      const { componentShow, dataBind } = this.detail;
      const { simInterval } = dataBind;
      this.clearTimer();
      this.timer = setInterval(
        () => {
          const val = this.getRandomData();
          if (val !== undefined && val !== '') {
            if (componentShow.indexOf('参数绑定') !== -1) {
              this.setColor(val);
              this.setOption(val);
            }
            if (componentShow.indexOf('组件填充') !== -1) {
              this.setColor(val);
              this.setOption(val);
            }
          }
        },
        Number(simInterval) * 1000
      );
    },
    // 初始化http数据
    initHttp() {
      const { dataBind } = this.detail;
      const { dataType, httpSetting, httpSettingId } = dataBind;
      let source = {};
      if (dataType === 4) {
        source = httpSetting;
      } else {
        source = this.httpPublicSetting.find((item) => item.id === httpSettingId) || {};
      }
      const { time = 30, unit = 's' } = source as any;
      this.setHttp();
      this.clearTimer();
      this.timer = setInterval(
        () => {
          this.setHttp();
        },
        Math.max(1000, Number(convertToMilliseconds(time, unit)) || 30000)
      );
      this.bindInteractionNotice();
    },
    // 设置http数据
    async setHttp(params, headers) {
      const val = await this.getHttpData(params, headers);
      if (val !== undefined && val !== '') {
        const processedVal = this.getFunHandlingResult(val, this.detail.dataBind && this.detail.dataBind.identifier);
        this.detail.dataBind.modelValue = processedVal;
        this.setColor(processedVal);
        this.setOption(processedVal);
      }
    },
  },
  beforeUnmount() {
    this.clearTimer();
    this.unbindInteractionNotice();
    if (this.myChart) {
      this.myChart.dispose();
      this.myChart = null;
    }
  },
};
</script>

<style lang="scss">
.view-chart-temp {
  height: 100%;
  width: 100%;
  text-align: center;
  display: block;
  overflow: hidden;
  position: relative;
}

.view-chart-temp-wrap {
  height: 100%;
  width: 100%;
}
</style>
