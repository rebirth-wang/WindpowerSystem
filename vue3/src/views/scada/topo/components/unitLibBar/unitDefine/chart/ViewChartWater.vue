<template>
  <div>
    <div
      class="view-chart-water"
      ref="chartView"
      :id="detail.identifier"
      :style="chartBoxStyle"
    />
    <div v-show="false">{{ chartsValue }}{{ width }}{{ height }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';
import 'echarts-liquidfill';

import { convertToMilliseconds, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
import BaseView from '../View.vue';

const CONVEX_PATH =
  'path://M349.09.5c-3.06,1-2,3.51-2,5.48q-.08,60.77,0,121.54V133H341.6q-167.55,0-335.09,0c-6.28,0-6-.28-6,6Q.56,196,.52,253c0,5.47,0,5.47,5.49,5.47H801c6,0,6.49-.5,6.49-6.47q0-55.78,0-111.57c0-6-.52-6.48-6.49-6.48H458v-5.48Q458,67.25,458,6C458,.51,458,.5,452.46.5H349.59';
/** 凸形 path 原始包围盒高/宽，液位算法按正圆半径算，扁 path 低液位会被裁掉 */
const CONVEX_PATH_ASPECT = 259 / 807;

export default {
  name: 'ViewChartWater',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  props: {},
  computed: {
    mqttData() {
      return this.topoStore.mqttData;
    },
    chartBoxStyle() {
      const w = this.detail.style.position?.w || 0;
      const h = this.detail.style.position?.h || 0;
      return {
        width: `${w}px`,
        height: `${h}px`,
      };
    },
    chartsValue: function () {
      if (Object.keys(this.mqttData).length !== 0) {
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
        if (bindNum && this.isSameValue(bindNum, mqttNum) && this.detail.dataBind.identifier) {
          for (let i = 0; i < this.mqttData.message.length; i++) {
            if (this.detail.dataBind.identifier == this.mqttData.message[i].id) {
              let modelValue = this.mqttData.message[i].value;
              if (isNaN(modelValue)) {
                modelValue = 0;
              }
              this.modelValue = modelValue;
            }
          }
        }
      }
      return this.detail.dataBind.modelValue;
    },
    width: function () {
      this.$nextTick(() => {
        this.resizeChart();
      });
      return this.detail.style.position.w;
    },
    height() {
      this.$nextTick(() => {
        this.resizeChart();
      });
      return this.detail.style.position.h;
    },
    option() {
      const raw = Number(this.modelValue);
      const w = Number(this.detail.style.position?.w) || 0;
      const h = Number(this.detail.style.position?.h) || 0;
      const visualRatio = (Number.isFinite(raw) ? raw : 0) / 100;
      const waterData = this.mapWaterValueForShape(
        visualRatio,
        this.detail.style.waterShape,
        w,
        h
      );
      const layout = this.getLiquidfillLayout();
      const waterColor = this.detail.style.waterColor || 'rgba(28,181,252,1)';
      const series: Record<string, unknown> = {
        type: 'liquidFill',
        name: 'chartWater',
        label: {
          show: this.detail.style.waterLabel,
          formatter: () => `${parseInt(String(visualRatio * 100), 10)}%`,
          fontSize: this.detail.style.waterFontSize,
          position: ['50%', '50%'],
        },
        color: [waterColor],
        itemStyle: {
          color: waterColor,
          opacity: 0.95,
        },
        tooltip: {
          show: true,
        },
        backgroundStyle: {
          borderWidth: this.detail.style.waterBorderWidth,
          borderColor: this.detail.style.waterBorderColor,
          color: 'rgba(0,0,0,0)',
          opacity: 0,
        },
        data: [waterData],
        shape: layout.shape,
        center: layout.center,
        outline: {
          show: false,
        },
        animationDuration: 0,
        animationDurationUpdate: 2000,
        animationEasingUpdate: 'cubicOut',
        amplitude: '3%',
        waveLength: '80%',
        period() {
          return 2000;
        },
      };
      if (layout.radius) {
        series.radius = layout.radius;
      }
      return {
        backgroundColor: 'transparent',
        series: [series],
      };
    },
  },
  data() {
    return {
      modelValue: null as number | null,
      myChart: null as any,
    };
  },
  watch: {
    option: {
      handler(newVal) {
        if (!this.myChart) {
          this.initEchart();
          return;
        }
        if (newVal) {
          this.myChart.setOption(newVal, true);
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.initEchart();
  },
  beforeUnmount() {
    if (this.myChart) {
      this.myChart.dispose();
      this.myChart = null;
    }
  },
  methods: {
    resizeChart() {
      if (!this.myChart) return;
      const w = this.detail.style.position?.w || 0;
      const h = this.detail.style.position?.h || 0;
      this.myChart.resize({ width: w, height: h });
    },
    getLiquidfillLayout() {
      const { waterShape } = this.detail.style;
      const w = Number(this.detail.style.position?.w) || 0;
      const h = Number(this.detail.style.position?.h) || 0;

      if (waterShape === 'container') {
        return {
          shape: 'container',
          center: ['50%', '50%'],
        };
      }

      let shape = waterShape;
      if (waterShape === 'convex') {
        shape = CONVEX_PATH;
      } else if (w > 0 && h > 0) {
        if (waterShape === 'rect' && w !== h) {
          shape = this.buildAspectRectPath(w, h);
        } else if (waterShape === 'triangle') {
          shape = this.buildTrianglePath(w, h);
        } else if (waterShape === 'arrow') {
          // 倒三角：内置 arrow 在较大 radius 下会向上偏移导致裁切，改用等比 path
          shape = this.buildInvertedTrianglePath(w, h);
        }
      }

      return {
        shape,
        center: ['50%', '50%'],
        radius: this.getLiquidfillRadius(w, h, waterShape),
      };
    },
    getShapeUnitSize(w: number, h: number) {
      const unit = 1000;
      const bw = w >= h ? unit : Math.round((w / h) * unit);
      const bh = h >= w ? unit : Math.round((h / w) * unit);
      return { bw, bh };
    },
    getLiquidfillRadius(_w: number, _h: number, waterShape: string) {
      // 内置尖角符号外接圆偏大，略缩小避免贴边裁切
      const tightBuiltinShapes = ['diamond', 'pin'];
      return tightBuiltinShapes.includes(waterShape) ? '92%' : '100%';
    },
    buildAspectRectPath(w: number, h: number) {
      const { bw, bh } = this.getShapeUnitSize(w, h);
      return `path://M0,0 L${bw},0 L${bw},${bh} L0,${bh} Z`;
    },
    buildTrianglePath(w: number, h: number) {
      const { bw, bh } = this.getShapeUnitSize(w, h);
      const mid = Math.round(bw / 2);
      return `path://M${mid},0 L${bw},${bh} L0,${bh} Z`;
    },
    buildInvertedTrianglePath(w: number, h: number) {
      const { bw, bh } = this.getShapeUnitSize(w, h);
      const mid = Math.round(bw / 2);
      return `path://M0,0 L${bw},0 L${mid},${bh} Z`;
    },
  /** 扁宽 path 在 echarts-liquidfill 中按正圆半径算液位，低比例时液体落在裁剪区外 */
    getPathAspectForShape(waterShape: string, w: number, h: number) {
      if (waterShape === 'convex') {
        return CONVEX_PATH_ASPECT;
      }
      if (['rect', 'triangle', 'arrow'].includes(waterShape) && w > 0 && h > 0) {
        const { bw, bh } = this.getShapeUnitSize(w, h);
        return bh / bw;
      }
      return 1;
    },
    mapWaterValueForPathAspect(value: number, aspect: number) {
      const v = Math.max(0, Math.min(1, value));
      const a = Math.max(0.01, Math.min(1, aspect));
      if (a >= 1) return v;
      return (1 - a + 2 * a * v) / 2;
    },
    mapWaterValueForShape(value: number, waterShape: string, w: number, h: number) {
      const aspect = this.getPathAspectForShape(waterShape, w, h);
      return this.mapWaterValueForPathAspect(value, aspect);
    },
    initEchart() {
      const el = document.getElementById(this.detail.identifier);
      if (!el) return;

      if (this.myChart) {
        this.myChart.dispose();
      }

      const w = this.detail.style.position?.w || 0;
      const h = this.detail.style.position?.h || 0;
      this.myChart = this.$echarts.init(el);
      const zr = this.myChart.getZr?.();
      if (zr) {
        zr.configLayer(0, { clearColor: 'rgba(0,0,0,0)' });
      }
      this.resizeChart();

      const { modelValue } = this.detail.dataBind;
      if (!this.editMode) {
        const { dataType } = this.detail.dataBind || {};
        if (dataType === 1) {
          this.modelValue = modelValue || 0;
        } else {
          this.mockData();
        }
        if (dataType === 4 || dataType === 5) {
          this.initHttp();
        }
      } else {
        this.modelValue = modelValue || 0;
      }

      if (this.option) {
        this.myChart.setOption(this.option, true);
      }
    },
    // 生成模拟数据
    mockData() {
      const { componentShow, dataBind } = this.detail;
      const { simInterval } = dataBind;
      this.timer = setInterval(
        () => {
          const val = this.getRandomData();
          if (val !== undefined && val !== '') {
            if (componentShow.indexOf('参数绑定') !== -1) {
              this.modelValue = val;
            }
            if (componentShow.indexOf('组件填充') !== -1) {
              this.modelValue = val;
            }
          }
        },
        Number(simInterval) * 1000
      );
    },
    // 初始化http数据
    initHttp() {
      const { identifier, dataBind } = this.detail;
      const { dataType, httpSetting, httpSettingId } = dataBind;
      let source = {};
      if (dataType === 4) {
        source = httpSetting;
      } else {
        source = this.httpPublicSetting.find((item) => item.id === httpSettingId);
      }
      const { time, unit } = source as any;
      this.setHttp();
      this.timer = setInterval(
        () => {
          this.setHttp();
        },
        convertToMilliseconds(time, unit)
      );
      this.$busEvent.$on('interactionNotice', (data) => {
        const { compId, params, headers } = data;
        if (identifier === compId) {
          this.setHttp(params, headers);
        }
      });
    },
    async setHttp(params, headers) {
      const { componentShow } = this.detail;
      const val = await this.getHttpData(params, headers);
      if (val !== undefined && val !== '') {
        this.setValue(val);
        if (componentShow.indexOf('组件填充') !== -1) {
          this.setColor(val);
        }
      }
    },
  },
};
</script>

<style lang="scss">
.view-chart-water {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style>
