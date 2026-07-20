<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

import BaseVeiw from '../View.vue';
import { judgeSize, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewCanvas',
  extends: BaseVeiw,
  computed: {
    dataInit() {
      this.processMqttPayload();
      return '';
    },
  },
  watch: {
    detail: {
      handler() {
        this.onResize?.();
      },
      deep: true,
    },
  },
  mounted() {
    if (!this.editMode) {
      const dataType = this.detail.dataBind?.dataType;
      if (dataType === 1) {
        this.initValue();
      } else if (dataType === 2) {
        this.mockData();
      }
    }
    this.onResize?.();
  },
  data() {
    return {
      defaultColor: this.detail.style.foreColor,
      defaultBorderColor: this.detail.style.borderColor,
      timer: null as ReturnType<typeof setInterval> | null,
    };
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  methods: {
    processMqttPayload() {
      const mqtt = useTopoEditorStore().mqttData || {};
      if (!Array.isArray(mqtt.message) || !mqtt.message.length) return;

      const type = getScadaRouteType(this.$route.query);
      let bindNum = this.detail.dataBind?.serialNumber;
      let mqttNum = mqtt.serialNumber;

      if (type === 1) {
        bindNum = getRouteQueryString(this.$route.query, 'serialNumber');
      } else if (type === 2 && this.detail.dataBind?.variableType !== 1) {
        bindNum = this.detail.dataBind.sceneModelDeviceId;
        mqttNum = mqtt.sceneModelDeviceId;
      }

      if (!bindNum || !this.isSameValue(bindNum, mqttNum) || !this.detail.dataBind?.identifier) return;

      const msg = mqtt.message.find((item) => this.detail.dataBind.identifier == item.id);
      if (!msg) return;

      const val = msg.value == null || msg.value === '' ? 0 : msg.value;
      this.setColor(val);
    },
    getCanvasSize() {
      const { w, h } = this.detail?.style?.position || {};
      return { w: Math.max(1, Number(w) || 1), h: Math.max(1, Number(h) || 1) };
    },
    prepareCanvas(refName: string, w: number, h: number) {
      const el = this.$refs[refName] as HTMLCanvasElement | undefined;
      if (!el) return null;

      const dpr = window.devicePixelRatio || 1;
      el.style.width = `${w}px`;
      el.style.height = `${h}px`;
      el.width = Math.round(w * dpr);
      el.height = Math.round(h * dpr);

      const ctx = el.getContext('2d');
      if (!ctx) return null;
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      ctx.clearRect(0, 0, w, h);
      return ctx;
    },
    getShapeLineWidth(w: number, h: number) {
      const shapeType = this.detail.style.shapeType || 'fill';
      const raw = Math.max(0, Number(this.detail.style.borderWidth) || 0);
      const max = Math.max(1, Math.min(w, h));
      return shapeType === 'stroke' ? Math.max(1, Math.min(raw || 1, max)) : Math.min(raw, max);
    },
    getStyleLineWidth() {
      return Math.max(1, Number(this.detail.style.lineWidth) || 2);
    },
    getForeColor() {
      const color = this.detail.style.foreColor;
      return color == null || color === '' ? 'grey' : color;
    },
    paintClosedPath(ctx: CanvasRenderingContext2D, color: string, lineWidth: number) {
      const shapeType = this.detail.style.shapeType || 'fill';
      const borderColor = this.detail.style.borderColor;

      if (shapeType === 'stroke') {
        ctx.lineWidth = lineWidth;
        ctx.strokeStyle = borderColor || color;
        ctx.stroke();
        return;
      }

      ctx.fillStyle = color;
      ctx.fill();
      if (lineWidth && borderColor) {
        ctx.lineWidth = lineWidth;
        ctx.strokeStyle = borderColor;
        ctx.stroke();
      }
    },
    strokeOpenPath(ctx: CanvasRenderingContext2D, points: Array<{ x: number; y: number }>, lineWidth: number, color: string) {
      if (points.length < 2) return;
      ctx.beginPath();
      ctx.moveTo(points[0].x, points[0].y);
      for (let i = 1; i < points.length; i++) {
        ctx.lineTo(points[i].x, points[i].y);
      }
      ctx.lineWidth = lineWidth;
      ctx.strokeStyle = color;
      ctx.stroke();
    },
    strokeBezierPath(ctx: CanvasRenderingContext2D, points: Array<{ x: number; y: number }>, lineWidth: number, color: string) {
      if (points.length < 4) return;
      ctx.beginPath();
      ctx.moveTo(points[0].x, points[0].y);
      ctx.bezierCurveTo(points[1].x, points[1].y, points[2].x, points[2].y, points[3].x, points[3].y);
      ctx.lineWidth = lineWidth;
      ctx.strokeStyle = color;
      ctx.stroke();
    },
    parseStylePoints(raw: Array<{ x?: number; y?: number }> | undefined, min: number, max?: number) {
      const points = (raw || [])
        .map((p) => ({ x: Number(p?.x), y: Number(p?.y) }))
        .filter((p) => Number.isFinite(p.x) && Number.isFinite(p.y));
      if (points.length >= min) {
        return max ? points.slice(0, max) : points;
      }
      return null;
    },
    initValue() {
      const value = this.detail.dataBind?.modelValue ? this.detail.dataBind.modelValue : 0;
      this.setColor(value);
    },
    setColor(val: unknown) {
      const { stateList } = this.detail.dataBind || {};
      if (!stateList?.length) return;

      const modelValue = this.getFunHandlingResult(val);
      const matched = stateList.find((item) => judgeSize(item.paramCondition, modelValue, item.paramData));

      if (matched) {
        this.detail.style.foreColor = matched.foreColor;
        this.detail.style.borderColor = matched.borderColor;
      } else {
        this.detail.style.foreColor = this.defaultColor;
        this.detail.style.borderColor = this.defaultBorderColor ?? this.defaultColor;
      }
    },
    mockData() {
      if (this.timer) clearInterval(this.timer);
      const interval = Math.max(1, Number(this.detail.dataBind?.simInterval) || 1) * 1000;
      this.timer = setInterval(() => {
        const val = this.getRandomData();
        if (val !== '' && val != null && this.detail.componentShow?.includes('组件填充')) {
          this.setColor(val);
        }
      }, interval);
    },
  },
};
</script>

<style lang="scss">
.shape-canvas {
  display: block;
  width: 100%;
  height: 100%;
}

.view-line-segment,
.view-bizier-curve {
  height: 100%;
  width: 100%;
  position: relative;
}

.view-line-segment .passby {
  position: absolute;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  background: #fff;
  border: 1px solid rgb(34, 14, 223);
  cursor: move;
}

.view-bizier-curve .shape-canvas {
  pointer-events: none;
}

.view-bizier-curve .passby {
  position: absolute;
  z-index: 1000;
  width: 28px;
  height: 28px;
  background: transparent;
  cursor: move;
  pointer-events: auto;
  touch-action: none;
  user-select: none;
}

.view-bizier-curve .passby::after {
  content: '';
  position: absolute;
  left: 9px;
  top: 9px;
  width: 10px;
  height: 10px;
  box-sizing: border-box;
  background: #fff;
  border: 2px solid rgb(34, 14, 223);
}
</style>
