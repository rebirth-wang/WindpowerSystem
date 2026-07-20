<template>
  <div class="view-chart-gauge-wrap">
    <div class="view-chart-gauge" :id="detail.identifier" ref="chartView" />
    <div v-show="false">{{ height }}{{ width }}{{ chartsValue }} {{ fontSize }} {{ foreColor }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import { convertToMilliseconds, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewChartGauge',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  props: {},
  computed: {
    mqttData() {
      return this.topoStore?.mqttData || {};
    },
    width() {
      this.$nextTick(function () {
        this.myChart?.resize({
          width: this.detail.style.position.w,
          height: this.detail.style.position.h,
        });
      });
      return this.detail.style.position.w;
    },
    height() {
      this.$nextTick(function () {
        this.myChart?.resize({
          width: this.detail.style.position.w,
          height: this.detail.style.position.h,
        });
      });
      return this.detail.style.position.h;
    },
    fontSize() {
      this.$nextTick(function () {
        this.myChart && this.setOption(this.normalizeChartValue(this.detail.dataBind?.modelValue));
      });
      return this.detail.style.fontSize;
    },
    foreColor() {
      this.$nextTick(function () {
        this.myChart && this.setOption(this.normalizeChartValue(this.detail.dataBind?.modelValue));
      });
      return this.detail.style.foreColor;
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
              let modelValue = messages[i].value;
              // 处理数据
              modelValue = this.getFunHandlingResult(
                modelValue,
                this.detail.dataBind && this.detail.dataBind.identifier
              );
              if (isNaN(modelValue)) {
                modelValue = 0;
              }
              //将回调延迟到下次DOM更新循环之后执行。在修改数据之后立即使用它，然后等待DOM更新
              this.$nextTick(function () {
                this.setOption(modelValue);
              });
            }
          }
        }
      }
      return (
        this.detail.dataBind.modelValue +
        this.detail.style.foreColor +
        this.detail.style.fontSize +
        this.detail.dataBind.unit
      );
    },
  },
  data() {
    return {
      myChart: null,
      option: {},
      timer: null,
      _interactionNoticeHandler: null,
    };
  },
  mounted() {
    const { modelValue } = this.detail.dataBind;
    const { dataType } = this.detail.dataBind || {};
    const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
    if (!chartDom) return;
    this.myChart = this.$echarts.init(chartDom);
    if (this.editMode) {
      this.setOption(0);
    } else {
      if (dataType === 1) {
        let val = modelValue || 0;
        // 处理数据
        val = this.getFunHandlingResult(val, this.detail.dataBind && this.detail.dataBind.identifier);
        this.$nextTick(function () {
          this.setOption(val);
        });
      } else if (dataType === 4 || dataType === 5) {
        this.initHttp();
      } else {
        this.mockData();
      }
    }
  },
  methods: {
    normalizeChartValue(value) {
      const num = Number(value);
      return Number.isFinite(num) ? num : 0;
    },
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
    setOption(modelValue) {
      if (!this.myChart) return;
      const safeValue = this.normalizeChartValue(modelValue);
      var data = [
        {
          title: this.detail.dataBind.modelName,
          sub_title: safeValue + this.detail.dataBind.unit,
          value: safeValue,
          min: this.normalizeChartValue(this.detail.dataBind.paramMin),
          max: this.normalizeChartValue(this.detail.dataBind.paramMax) || 100,
        },
        // { 'title': '湿度', 'sub_title': '50%', value: "50", 'min': 0, 'max': 100 }
      ];
      // 不同数据长度，圆心位置
      var pos_cfg = {
        1: [['50%', '50%']],
      };
      var data_len = data.length;
      // 获取位置信息
      var pos_info = pos_cfg[data_len];
      // 圆环颜色配置
      var color_cfg = [
        [
          { offset: 0, color: 'rgba(90, 255, 163, 1)' },
          { offset: 0.5, color: 'rgba(80, 192, 255, 1)' },
          { offset: 1, color: 'rgba(102, 255, 255, 1)' },
        ],
        [
          { offset: 0, color: 'rgba(50, 197, 255, 1)' },
          { offset: 0.5, color: 'rgba(254, 219, 101, 1)' },
          { offset: 1, color: 'rgba(250, 100, 0, 1)' },
        ],
      ];
      // 渲染数据
      var series = [],
        item = null;
      for (var i in data) {
        item = data[i];
        // 处理最大值及最小值
        if (!Number.isFinite(Number(item.min))) item.min = 0;
        if (!Number.isFinite(Number(item.max)) || Number(item.max) <= Number(item.min)) {
          item.max = Math.max(Number(item.value) || 0, Number(item.min) || 0) + 100;
        }
        // 获取比率
        item.rate = Math.max(0, Math.min(100, Math.round((item.value / item.max) * 10000) / 100));
        // 拼接图表参数
        series.push(
          {
            name: this.$t('topo.components.chart.073848-5'),
            type: 'gauge',
            center: pos_info[i],
            radius: '95%',
            startAngle: 150,
            endAngle: -209.999,
            axisLine: {
              show: true,
              lineStyle: { width: 2, color: [[1, 'rgba(25, 235, 255,1)']] },
            },
            axisLabel: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            detail: { show: false },
            pointer: { show: false },
          },
          {
            name: this.$t('topo.components.chart.073848-6'),
            type: 'gauge',
            radius: '60%',
            splitNumber: 10, // 刻度数量
            center: pos_info[i],
            startAngle: 150,
            endAngle: -209.999,
            axisLine: {
              lineStyle: {
                color: [
                  [
                    1,
                    {
                      type: 'radial',
                      colorStops: [
                        { offset: 0.72, color: '#0320462e' },
                        { offset: 0.84, color: '#08698961' },
                        { offset: 0.98, color: '#0FAFCBa6' },
                        { offset: 1, color: '#0EA4C1f0' },
                      ],
                    },
                  ],
                ],
                width: 1000,
              },
            },
            splitLine: { show: false }, // 分隔线
            axisTick: { show: false }, // 刻度线
            axisLabel: { show: false }, // 刻度标签
            pointer: { show: false }, // 仪表盘指针
            detail: { show: false },
          },
          {
            name: this.$t('topo.components.chart.073848-7'),
            type: 'gauge',
            center: pos_info[i],
            radius: '80%',
            min: item.min, // 最小刻度
            max: item.max, // 最大刻度
            splitNumber: 10, // 刻度数量
            startAngle: 245,
            endAngle: -65,
            data: [{ value: item.rate }],
            axisLine: {
              show: true,
              lineStyle: {
                width: 10,
                color: [
                  [item.rate / 100, this.$echarts.graphic.LinearGradient(0, 1, 1, 0, color_cfg[i])],
                  [1, 'rgba(50, 197, 255,.1)'],
                ],
              },
            },
            axisLabel: {
              show: true,
              color: this.detail.style.foreColor,
              distance: 32,
              textStyle: { fontSize: this.detail.style.fontSize - 4 },
            },
            axisTick: { show: true, length: -5, distance: -10, lineStyle: { color: 'rgba(25, 235, 255, 1)' } },
            splitLine: {
              show: true,
              length: -10,
              distance: -10,
              lineStyle: { width: 1, color: 'rgba(25, 235, 255, 1)' },
            },
            detail: {
              offsetCenter: [0, '-5%'],
              textStyle: { fontSize: this.detail.style.fontSize, color: this.detail.style.foreColor },
              formatter: [item.title, '{name|' + item.sub_title + '}'].join('\n'),
              rich: {
                name: {
                  fontSize: this.detail.style.fontSize + 4,
                  lineHeight: 18,
                  color: this.detail.style.foreColor,
                  fontWeight: '600',
                },
              },
            },
            title: { color: this.detail.style.foreColor },
            pointer: { show: false },
          }
        );
      }
      var option = {
        series: series,
      };
      this.myChart.setOption(option);
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
            // 处理数据
            const processedVal = this.getFunHandlingResult(
              val,
              this.detail.dataBind && this.detail.dataBind.identifier
            );
            if (componentShow.indexOf('参数绑定') !== -1) {
              this.$nextTick(function () {
                this.setOption(processedVal);
              });
            }
            if (componentShow.indexOf('组件填充') !== -1) {
              this.$nextTick(function () {
                this.setOption(processedVal);
              });
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
.view-chart-gauge {
  height: 100%;
  width: 100%;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.view-chart-gauge-wrap {
  height: 100%;
  width: 100%;
}
</style>
