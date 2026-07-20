<template>
  <div
    class="view-chart-wrapper"
    ref="chartWrapper"
    :id="detail.identifier"
    :style="{
      width: detail.style.position.w + 'px',
      height: detail.style.position.h + 'px',
    }"
  ></div>
</template>

<script lang="ts">
import BaseView from '../View.vue';
import request from '@/utils/request';
import chartOption from '@/assets/scripts/scada/chart-option.js';
import { getEchart } from '@/api/scada/echart';
import { getSecond } from '@/utils/topo/index';
import { ElMessage } from 'element-plus';

export default {
  name: 'ViewChartWrapper',
  extends: BaseView,
  data() {
    return {
      echart: null,
      timer: null,
      lastChartSize: '',
      currentOption: null,
    };
  },
  computed: {
    echartRun() {
      const { echartOption, echartRun } = this.detail.dataBind;
      this.$nextTick(() => {
        if (echartOption && echartRun > new Date().getTime() - 10000) {
          const { echartData, httpFilter } = this.detail.dataBind;
          try {
            const fun = this.createOptionRunner(echartOption);
            let opData = {};
            if (echartData) {
              opData = JSON.parse(echartData);
              if (httpFilter) {
                const filtFunStr = 'function () {\n' + 'const res = opData;' + '\n' + httpFilter + '\n' + '}';
                let filtFun = eval('(' + filtFunStr + ')');
                opData = filtFun();
              }
            }
            const chartOptionResult = fun(this.$echarts, opData);
            this.loadData(chartOptionResult);
            this.onResize();
            ElMessage({ message: this.$t('topo.components.chart.073848-29'), type: 'success' });
          } catch (err) {
            console.warn(err);
            ElMessage({
              message: this.$t('topo.components.chart.073848-30'),
              type: 'warning',
            });
          }
        }
      });
      return echartOption + echartRun;
    },
  },
  watch: {
    'detail.style.position.w'() {
      this.onResize();
    },
    'detail.style.position.h'() {
      this.onResize();
    },
    'detail.dataBind.echartData'(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.refreshChartByData();
      }
    },
    'detail.dataBind.httpFilter'() {
      this.refreshChartByData();
    },
    echartRun(newColor, oldColor) {
      // console.log('',newColor);
    },
  },
  mounted() {
    const { httpSetting, httpPublicSetting, requestType } = this.detail.dataBind || {};
    const source = requestType === 'public' ? httpPublicSetting : httpSetting;
    const { url, time, unit } = source || {};
    if (this.editMode) {
      this.initEchart();
      return;
    }
    if (url) {
      let second = 60 * 1000;
      if (time) {
        second = getSecond(time, unit);
      }
      this.getEchartData();
      this.timer = setInterval(() => {
        this.getEchartData();
      }, second);
    } else {
      this.initEchart();
    }
  },
  methods: {
    loadData(option) {
      if (!option || typeof option !== 'object') {
        return;
      }
      this.currentOption = option;
      const view = this.$refs.chartWrapper;
      if (!view) return;
      const { width, height } = this.getChartSize();
      if (!width || !height) return;
      view.style.width = `${width}px`;
      view.style.height = `${height}px`;
      if (this.echart) {
        this.echart.dispose();
        this.echart = null;
      }
      this.lastChartSize = `${width}x${height}`;
      this.echart = this.$echarts.init(view, 'light', {
        width,
        height,
      });
      const safeOption = this.normalizeChartOption(option);
      try {
        this.echart.clear();
        this.echart.setOption(safeOption, true);
        this.echart.resize({ width, height });
      } catch (err) {
        console.warn('ViewChartWrapper setOption failed', err, safeOption);
      }
    },
    getChartSize() {
      const view = this.$refs.chartWrapper || document.getElementById(this.detail.identifier);
      return {
        width: Number(this.detail.style.position.w) || view?.clientWidth || 0,
        height: Number(this.detail.style.position.h) || view?.clientHeight || 0,
      };
    },
    onResize() {
      if (!this.echart) {
        return;
      }
      this.$nextTick(() => {
        try {
          const view = this.$refs.chartWrapper || document.getElementById(this.detail.identifier);
          const { width, height } = this.getChartSize();
          if (!view || !width || !height) return;
          view.style.width = `${width}px`;
          view.style.height = `${height}px`;
          const sizeKey = `${width}x${height}`;
          const sizeChanged = this.lastChartSize !== sizeKey;
          this.lastChartSize = sizeKey;
          if (sizeChanged) {
            this.rebuildChart(width, height);
            return;
          }
          this.echart.resize({ width, height });
        } catch (err) {
          console.warn('ViewChartWrapper resize skipped', err);
        }
      });
    },
    rebuildChart(width, height) {
      const view = this.$refs.chartWrapper || document.getElementById(this.detail.identifier);
      if (!view || !this.$echarts || !this.currentOption) return;
      try {
        this.echart?.dispose?.();
        view.removeAttribute('_echarts_instance_');
        view.innerHTML = '';
        this.echart = this.$echarts.init(view, 'light', {
          width,
          height,
        });
        this.echart.setOption(this.normalizeChartOption(this.currentOption), true);
        this.echart.resize({ width, height });
      } catch (err) {
        console.warn('ViewChartWrapper rebuild skipped', err);
      }
    },
    normalizeChartOption(option) {
      return {
        ...option,
        series: Array.isArray(option.series)
          ? option.series.filter((item) => item && typeof item === 'object' && typeof item.type === 'string')
          : [],
      };
    },
    refreshChartByData() {
      this.$nextTick(() => {
        this.initEchart();
      });
    },
    initEchart() {
      let { echartOption } = this.detail.dataBind;
      if (!echartOption) {
        this.detail.dataBind.echartOption = chartOption.getOption();
        this.setEchartOption();
      } else if (echartOption.indexOf('echartId-') > -1) {
        let id = echartOption.split('-')[1];
        this.getEchartDataById(id);
      } else {
        this.setEchartOption();
      }
    },
    // 获取自定义echart详情
    getEchartDataById(id) {
      getEchart(id).then((res) => {
        if (res.code === 200) {
          this.detail.dataBind.echartOption = res.data.echartData;
          this.setEchartOption();
        }
      });
    },
    getEchartData() {
      const { httpSetting, httpPublicSetting, requestType } = this.detail.dataBind;
      const source = requestType === 'public' ? httpPublicSetting : httpSetting;
      const { url, type, headers, params, body } = source;
      request({
        url: url,
        method: type,
        headers: headers,
        params: params,
        data: body,
      }).then((res) => {
        const nextData = JSON.stringify(res);
        if (this.detail.dataBind.echartData === nextData) {
          if (!this.echart) {
            this.refreshChartByData();
          }
          return;
        }
        this.detail.dataBind.echartData = nextData;
      });
    },
    // 设置 option
    setEchartOption() {
      let { echartOption, echartData, httpFilter } = this.detail.dataBind;
      try {
        const fun = this.createOptionRunner(echartOption);
        let opData = {};
        if (echartData) {
          opData = JSON.parse(echartData);
          if (httpFilter) {
            const filtFunStr = 'function () {\n' + 'const res = opData;' + '\n' + httpFilter + '\n' + '}';
            let filtFun = eval('(' + filtFunStr + ')');
            opData = filtFun();
          }
        }
        const option = fun(this.$echarts, opData);
        this.loadData(option);
        this.onResize();
      } catch (err) {
        console.warn('setEchartOption failed', err);
      }
    },
    createOptionRunner(echartOption) {
      return new Function('echarts', 'echartData', `${echartOption || ''}\n;return option;`);
    },
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
    if (this.echart) {
      this.echart.dispose();
      this.echart = null;
    }
  },
};
</script>

<style lang="scss">
.view-chart-wrapper {
  box-sizing: border-box;
  height: 100%;
  width: 100%;
  display: block;
  overflow: hidden;
  position: relative;
}
</style>
