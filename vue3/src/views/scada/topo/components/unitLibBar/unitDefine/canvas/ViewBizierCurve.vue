<template>
  <div class="view-bizier-curve" :id="detail.identifier" @mousemove="onMousemove" @mouseup="onMouseUp">
    <canvas ref="elCanvasBizierCurve" class="shape-canvas" />
    <div v-show="false">{{ dataInit }}</div>
    <template v-if="editMode && selected">
      <div
        v-for="(pass, index) in points"
        :key="index"
        class="passby"
        @pointerdown.stop.prevent="onPassDown(pass, $event)"
        @mousedown.stop.prevent="onPassDown(pass, $event)"
        :style="{ left: pass.x - 14 + 'px', top: pass.y - 14 + 'px' }"
      />
    </template>
  </div>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

const canvasRef = 'elCanvasBizierCurve';

export default {
  name: 'ViewBizierCurve',
  extends: canvasView,
  data() {
    return {
      lineWidth: 2,
      dragging: false,
      dragPass: null as any,
      points: [] as Array<{
        x: number;
        y: number;
        startX?: number;
        startY?: number;
        temp?: { x: number; y: number };
        pointerOffset?: { x: number; y: number };
      }>,
    };
  },
  methods: {
    getDefaultPoints(w: number, h: number) {
      const pad = this.lineWidth / 2;
      return [
        { x: pad, y: pad },
        { x: pad, y: Math.max(pad, h - pad) },
        { x: Math.max(pad, w - pad), y: Math.max(pad, h - pad) },
        { x: Math.max(pad, w - pad), y: pad },
      ];
    },
    normalizePoints() {
      const { w, h } = this.getCanvasSize();
      const parsed = this.parseStylePoints(this.detail.style.points, 4, 4);
      this.points = parsed || this.getDefaultPoints(w, h);
    },
    getLocalPoint(event: MouseEvent | PointerEvent) {
      const el = this.$el as HTMLElement | undefined;
      const { w, h } = this.getCanvasSize();
      if (!el) return { x: event.pageX, y: event.pageY };
      const rect = el.getBoundingClientRect();
      return {
        x: (event.clientX - rect.left) * (rect.width ? w / rect.width : 1),
        y: (event.clientY - rect.top) * (rect.height ? h / rect.height : 1),
      };
    },
    paint() {
      const { w, h } = this.getCanvasSize();
      const ctx = this.prepareCanvas(canvasRef, w, h);
      if (ctx) this.strokeBezierPath(ctx, this.points, this.lineWidth, this.getForeColor());
    },
    onResize() {
      if (this.dragging) return this.paint();
      this.lineWidth = this.getStyleLineWidth();
      this.normalizePoints();
      this.paint();
    },
    onPassDown(pass: any, event: MouseEvent | PointerEvent) {
      if (this.dragging) return;
      this.dragging = true;
      this.dragPass = pass;
      pass.startX = event.pageX;
      pass.startY = event.pageY;
      pass.temp = { x: pass.x, y: pass.y };
      const local = this.getLocalPoint(event);
      pass.pointerOffset = { x: local.x - pass.x, y: local.y - pass.y };
      const target = event.currentTarget as HTMLElement | undefined;
      if (target?.setPointerCapture && 'pointerId' in event) {
        try {
          target.setPointerCapture(event.pointerId);
        } catch {
          /* ignore */
        }
      }
      document.addEventListener('pointermove', this.onMousemove);
      document.addEventListener('pointerup', this.onMouseUp);
      document.addEventListener('mousemove', this.onMousemove);
      document.addEventListener('mouseup', this.onMouseUp);
      event.preventDefault();
    },
    onMousemove(event: MouseEvent | PointerEvent) {
      const pass = this.dragPass;
      if (!this.dragging || !pass?.temp) return;
      if (pass.pointerOffset) {
        const local = this.getLocalPoint(event);
        pass.x = local.x - pass.pointerOffset.x;
        pass.y = local.y - pass.pointerOffset.y;
      } else {
        pass.x = pass.temp.x + event.pageX - pass.startX;
        pass.y = pass.temp.y + event.pageY - pass.startY;
      }
      this.paint();
    },
    onMouseUp() {
      if (this.dragging) {
        this.detail.style.points = this.points.map((p) => ({ x: p.x, y: p.y }));
      }
      this.dragging = false;
      this.dragPass = null;
      document.removeEventListener('pointermove', this.onMousemove);
      document.removeEventListener('pointerup', this.onMouseUp);
      document.removeEventListener('mousemove', this.onMousemove);
      document.removeEventListener('mouseup', this.onMouseUp);
    },
  },
  beforeUnmount() {
    this.onMouseUp();
  },
};
</script>
