<template>
  <canvas :id="detail.identifier" ref="elCanvasLine" class="shape-canvas" />
  <div v-show="false">{{ dataInit }}</div>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

const canvasRef = 'elCanvasLine';

export default {
  name: 'ViewLine',
  extends: canvasView,
  methods: {
    isVerticalLine() {
      const type = this.detail?.type || this.detail?.info?.type;
      return type === 'vertical-line';
    },
    onResize() {
      const { w, h } = this.getCanvasSize();
      const ctx = this.prepareCanvas(canvasRef, w, h);
      if (!ctx) return;

      const lineWidth = this.getStyleLineWidth();
      const pad = lineWidth / 2;
      const color = this.getForeColor();
      let x1 = pad;
      let y1 = h / 2;
      let x2 = Math.max(pad, w - pad);
      let y2 = h / 2;

      if (this.isVerticalLine()) {
        x1 = x2 = w / 2;
        y1 = pad;
        y2 = Math.max(pad, h - pad);
      }

      this.strokeOpenPath(ctx, [
        { x: x1, y: y1 },
        { x: x2, y: y2 },
      ], lineWidth, color);
    },
  },
};
</script>
