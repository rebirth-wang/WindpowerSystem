<template>
  <div class="view-line-segment" :id="detail.identifier" @mousemove="onMousemove" @mouseup="onMouseUp">
    <canvas ref="elCanvasLineSegment" class="shape-canvas" />
    <div v-show="false">{{ dataInit }}</div>
    <template v-if="editMode && selected">
      <div
        v-for="(pass, index) in points"
        :key="index"
        class="passby"
        @mousedown.stop="onPassDown(pass, $event)"
        :style="{ left: pass.x - 7.5 + 'px', top: pass.y - 7.5 + 'px' }"
      />
    </template>
  </div>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

const canvasRef = 'elCanvasLineSegment';

export default {
  name: 'ViewLineSegment',
  extends: canvasView,
  data() {
    return {
      lineWidth: 2,
      dragging: false,
      dragPass: null as any,
      points: [] as Array<{ x: number; y: number; startX?: number; startY?: number; temp?: { x: number; y: number } }>,
    };
  },
  methods: {
    getDefaultPoints(w: number, h: number) {
      const pad = this.lineWidth / 2;
      return [
        { x: pad, y: h / 2 },
        { x: Math.max(pad, w - pad), y: h / 2 },
      ];
    },
    normalizePoints() {
      const { w, h } = this.getCanvasSize();
      const parsed = this.parseStylePoints(this.detail.style.points, 2);
      this.points = parsed || this.getDefaultPoints(w, h);
    },
    paint() {
      const { w, h } = this.getCanvasSize();
      const ctx = this.prepareCanvas(canvasRef, w, h);
      if (ctx) this.strokeOpenPath(ctx, this.points, this.lineWidth, this.getForeColor());
    },
    onResize() {
      if (this.dragging) return this.paint();
      this.lineWidth = this.getStyleLineWidth();
      this.normalizePoints();
      this.paint();
    },
    onPassDown(pass: any, event: MouseEvent) {
      this.dragging = true;
      this.dragPass = pass;
      pass.startX = event.pageX;
      pass.startY = event.pageY;
      pass.temp = { x: pass.x, y: pass.y };
      document.addEventListener('mousemove', this.onMousemove);
      document.addEventListener('mouseup', this.onMouseUp);
      event.preventDefault();
    },
    onMousemove(event: MouseEvent) {
      const pass = this.dragPass;
      if (!this.dragging || !pass?.temp) return;
      pass.x = pass.temp.x + event.pageX - pass.startX;
      pass.y = pass.temp.y + event.pageY - pass.startY;
      this.paint();
    },
    onMouseUp() {
      if (this.dragging) {
        this.detail.style.points = this.points.map((p) => ({ x: p.x, y: p.y }));
      }
      this.dragging = false;
      this.dragPass = null;
      document.removeEventListener('mousemove', this.onMousemove);
      document.removeEventListener('mouseup', this.onMouseUp);
    },
  },
  beforeUnmount() {
    this.onMouseUp();
  },
};
</script>
