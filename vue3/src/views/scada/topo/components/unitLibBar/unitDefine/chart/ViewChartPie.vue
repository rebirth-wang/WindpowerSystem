<template>
  <div class="view-chart-pie-wrap">
    <div
      class="view-chart-pie"
      :id="detail.identifier"
      ref="chartView"
      :style="{
        width: detail.style.position.w + 'px',
        height: detail.style.position.h + 'px',
      }"
    />
    <div v-show="false">{{ height }}{{ width }}{{ chartsValue }}{{ foreColor }}</div>
  </div>
</template>

<script lang="ts">
import { useUserStore } from '@/stores/modules/user';
import BaseView from '../View.vue';
import { getDeviceStatistic } from '@/api/scada/topo';

export default {
  name: 'ViewChartPie',
  extends: BaseView,
  watch: {
    'detail.style.position.w'() {
      this.onResize();
    },
    'detail.style.position.h'() {
      this.onResize();
    },
  },
  computed: {
    width() {
      this.$nextTick(function () {
        this.onResize();
      });
      return this.detail.style.position.w;
    },
    height() {
      this.$nextTick(function () {
        this.onResize();
      });
      return this.detail.style.position.h;
    },
    chartsValue() {
      //将回调延迟到下次DOM更新循环之后执行。在修改数据之后立即使用它，然后等待DOM更新
      this.$nextTick(function () {
        let current = {
          deviceOnlineCount: 0,
          deviceOfflineCount: 0,
          alertDeviceCount: 0,
          alertProcessedCount: 0,
          alertNotProcessedCount: 0,
          orderRecordProcessedNum: 0,
          orderRecordUntreatedNum: 0,
          orderRecordAbandonNum: 0,
        };
        if (this.detail.style.pieType == this.$t('device.index.105953-4')) {
          this.setChartOption(this.getDeviceChartsData(current));
        } else if (this.detail.style.pieType == this.$t('topo.components.chart.073848-15')) {
          this.setChartOption(this.getWarnChartsData(current));
        } else {
          this.setChartOption(this.getOrderChartsData(current));
        }
      });
      return this.detail.style.pieType;
    },
    foreColor() {
      this.$nextTick(function () {
        this.getStaticData();
      });
      return this.detail.style.foreColor;
    },
  },
  data() {
    return {
      myChart: null,
      option: {},
      timer: null,
      legendSelected: null,
      lastChartSize: '',
    };
  },
  methods: {
    onResize() {
      if (!this.myChart) return;
      this.$nextTick(() => {
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
          if (sizeChanged) {
            this.rebuildChart(width, height);
            return;
          }
          this.myChart?.resize({
            width,
            height,
          });
        } catch (err) {
          console.warn('ViewChartPie resize skipped', err);
        }
      });
    },
    rebuildChart(width, height) {
      const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
      if (!chartDom || !this.$echarts) return;
      try {
        this.myChart?.off?.('legendselectchanged');
        this.myChart?.dispose?.();
        chartDom.removeAttribute('_echarts_instance_');
        chartDom.innerHTML = '';
        this.myChart = this.$echarts.init(chartDom, undefined, {
          width,
          height,
        });
        this.bindChartEvents();
        if (this.option && Object.keys(this.option).length > 0) {
          this.setChartOption(this.option);
        }
        this.myChart.resize({ width, height });
      } catch (err) {
        console.warn('ViewChartPie rebuild skipped', err);
      }
    },
    bindChartEvents() {
      if (!this.myChart) return;
      this.myChart.off?.('legendselectchanged');
      this.myChart.on?.('legendselectchanged', (params) => {
        this.legendSelected = params?.selected || null;
      });
    },
    normalizeChartOption(option) {
      const safeOption = option && typeof option === 'object' ? { ...option } : {};
      const layout = this.getPieLayout();
      if (safeOption.title) {
        safeOption.title = {
          ...safeOption.title,
          top: layout.titleTop,
          textStyle: {
            ...(safeOption.title.textStyle || {}),
            fontSize: layout.titleFontSize,
          },
        };
      }
      safeOption.tooltip = {
        show: true,
        trigger: 'item',
        triggerOn: 'mousemove|click',
        confine: true,
        ...(safeOption.tooltip || {}),
      };
      if (safeOption.legend) {
        safeOption.legend = {
          ...safeOption.legend,
          top: layout.legendTop,
          itemWidth: layout.legendItemWidth,
          itemHeight: layout.legendItemHeight,
          itemGap: layout.legendItemGap,
          selectedMode: safeOption.legend.selectedMode === undefined ? true : safeOption.legend.selectedMode,
          textStyle: {
            ...(safeOption.legend.textStyle || {}),
            fontSize: layout.legendFontSize,
          },
          selected: this.legendSelected
            ? { ...(safeOption.legend.selected || {}), ...this.legendSelected }
            : safeOption.legend.selected,
        };
      }
      safeOption.series = Array.isArray(safeOption.series)
        ? safeOption.series
            .filter((item) => item && typeof item === 'object' && typeof item.type === 'string')
            .map((item) => ({
              ...item,
              center: layout.center,
              radius: layout.radius,
              silent: false,
            }))
        : [];
      return safeOption;
    },
    getPieLayout() {
      const width = Number(this.detail?.style?.position?.w) || 300;
      const height = Number(this.detail?.style?.position?.h) || 250;
      const shortSide = Math.min(width, height);
      const titleFontSize = this.clamp(Math.round(shortSide / 18), 14, 20);
      const legendFontSize = this.clamp(Math.round(shortSide / 24), 12, 16);
      const titleTop = this.clamp(Math.round(height * 0.035), 4, 18);
      const legendTop = titleTop + titleFontSize + this.clamp(Math.round(height * 0.035), 8, 16);
      const legendHeight = legendFontSize + 12;
      const topReserved = legendTop + legendHeight + this.clamp(Math.round(height * 0.05), 12, 24);
      const bottomReserved = this.clamp(Math.round(height * 0.06), 10, 26);
      const availableHeight = Math.max(height - topReserved - bottomReserved, 80);
      const availableWidth = Math.max(width - 28, 80);
      const outerRadius = this.clamp(
        Math.floor(Math.min(availableWidth, availableHeight) * 0.46),
        34,
        Math.floor(shortSide * 0.4)
      );
      const innerRadius = Math.max(Math.floor(outerRadius * 0.72), outerRadius - 24);
      const centerY = Math.round(topReserved + availableHeight / 2);
      return {
        titleTop,
        titleFontSize,
        legendTop,
        legendFontSize,
        legendItemWidth: this.clamp(Math.round(width / 32), 14, 24),
        legendItemHeight: this.clamp(Math.round(height / 28), 8, 14),
        legendItemGap: this.clamp(Math.round(width / 45), 8, 18),
        center: ['50%', centerY],
        radius: [innerRadius, outerRadius],
      };
    },
    clamp(value, min, max) {
      return Math.min(Math.max(value, min), max);
    },
    setChartOption(option) {
      if (!this.myChart || !option) return;
      this.option = option;
      const safeOption = this.normalizeChartOption(option);
      try {
        this.myChart.clear();
        this.myChart.setOption(safeOption, true);
        this.onResize();
      } catch (err) {
        console.warn('ViewChartPie setOption failed', err, safeOption);
      }
    },
    getStaticData() {
      const { dept } = useUserStore();
      const params = { deptId: (dept as any)?.deptId };
      getDeviceStatistic(params).then((res) => {
        if (res.code === 200) {
          if (this.detail.style.pieType == this.$t('device.index.105953-4')) {
            this.setChartOption(this.getDeviceChartsData(res.data));
          } else if (this.detail.style.pieType == this.$t('topo.components.chart.073848-15')) {
            this.setChartOption(this.getWarnChartsData(res.data));
          } else {
            this.setChartOption(this.getOrderChartsData(res.data));
          }
        }
      });
    },
    //设备状态
    getDeviceChartsData(currData) {
      let option = {
        color: ['#13ce66', '#909399', '#ff4949'],
        title: {
          text: this.$t('device.index.105953-4'),
          left: 'center',
          textStyle: {
            fontFamily: 'Microsoft YaHei',
            fontStyle: 'normal',
            fontWeight: 'normal', //标题颜色
            color: this.detail.style.foreColor,
          },
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          top: '10%',
          left: 'center',
          textStyle: { color: this.detail.style.foreColor },
        },
        series: [
          {
            name: this.$t('topo.components.chart.073848-16'),
            type: 'pie',
            radius: ['40%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center',
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '30',
                fontWeight: 'bold',
              },
            },
            labelLine: {
              show: false,
            },
            data: [
              {
                value: currData.deviceOnlineCount,
                name: this.$t('device.device-linkage.188958-2') + currData.deviceOnlineCount,
                label: { color: this.detail.style.foreColor },
              },
              {
                value: currData.deviceOfflineCount,
                name: this.$t('device.device-linkage.188958-3') + currData.deviceOfflineCount,
                label: { color: this.detail.style.foreColor },
              },
              {
                value: currData.alertDeviceCount,
                name: this.$t('topo.components.chart.073848-17') + currData.alertDeviceCount,
                label: { color: this.detail.style.foreColor },
              },
            ],
          },
        ],
      };
      return option;
    },
    //报警状态
    getWarnChartsData(currData) {
      let option = {
        color: ['#13ce66', '#ff4949'],
        title: {
          text: this.$t('topo.components.chart.073848-15'),
          left: 'center',
          textStyle: {
            fontFamily: 'Microsoft YaHei',
            fontStyle: 'normal',
            fontWeight: 'normal', //标题颜色
            color: this.detail.style.foreColor,
          },
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          top: '10%',
          left: 'center',
          textStyle: { color: this.detail.style.foreColor },
        },
        series: [
          {
            name: this.$t('topo.components.chart.073848-16'),
            type: 'pie',
            radius: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2,
            },
            label: {
              show: false,
              position: 'center',
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '30',
                fontWeight: 'bold',
              },
            },
            labelLine: {
              show: false,
            },
            data: [
              {
                value: currData.alertProcessedCount,
                name: this.$t('topo.components.chart.073848-18') + currData.alertProcessedCount,
                label: { color: this.detail.style.foreColor },
              },
              {
                value: currData.alertNotProcessedCount,
                name: this.$t('topo.components.chart.073848-19') + currData.alertNotProcessedCount,
                label: { color: this.detail.style.foreColor },
              },
            ],
          },
        ],
      };
      return option;
    },
    //工单状态
    getOrderChartsData(currData) {
      let option = {
        color: ['#2979ff', '#fa3534', '#ff9900'], // '#5C7BD9',
        title: {
          text: this.$t('topo.components.chart.073848-20'),
          left: 'center',
          textStyle: {
            fontFamily: 'Microsoft YaHei',
            fontStyle: 'normal',
            fontWeight: 'normal', //标题颜色
            color: this.detail.style.foreColor,
          },
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          top: '10%',
          left: 'center',
          textStyle: { color: this.detail.style.foreColor },
        },
        series: [
          {
            name: this.$t('topo.components.chart.073848-16'),
            type: 'pie',
            radius: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2,
            },
            label: {
              show: false,
              position: 'center',
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '30',
                fontWeight: 'bold',
              },
            },
            labelLine: {
              show: false,
            },
            data: [
              {
                value: currData.orderRecordProcessedNum,
                name: this.$t('topo.components.chart.073848-21') + currData.orderRecordProcessedNum,
                label: { color: this.detail.style.foreColor },
              },
              {
                value: currData.orderRecordUntreatedNum,
                name: this.$t('topo.components.chart.073848-22') + currData.orderRecordUntreatedNum,
                label: { color: this.detail.style.foreColor },
              },
              {
                value: currData.orderRecordAbandonNum,
                name: this.$t('topo.components.chart.073848-23') + currData.orderRecordAbandonNum,
                label: { color: this.detail.style.foreColor },
              },
            ],
          },
        ],
      };
      return option;
    },
  },
  mounted() {
    const chartDom = this.$refs.chartView || document.getElementById(this.detail.identifier);
    if (!chartDom) return;
    const width = Number(this.detail.style.position.w) || chartDom.clientWidth || 0;
    const height = Number(this.detail.style.position.h) || chartDom.clientHeight || 0;
    this.myChart = this.$echarts.init(chartDom, undefined, {
      width,
      height,
    });
    this.bindChartEvents();
    this.onResize();
    this.getStaticData();
    this.timer = setInterval(() => {
      this.getStaticData();
    }, 60000);
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
    if (this.myChart) {
      this.myChart.dispose();
      this.myChart = null;
    }
  },
};
</script>

<style lang="scss">
.view-chart-pie-wrap {
  height: 100%;
  width: 100%;
}

.view-chart-pie {
  height: 100%;
  width: 100%;
  text-align: center;
  display: block;
  overflow: hidden;
  position: relative;
}
</style>
