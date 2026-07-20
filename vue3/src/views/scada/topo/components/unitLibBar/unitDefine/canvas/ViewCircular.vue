<template>
  <canvas :id="detail.identifier" ref="elCanvasCircular" class="shape-canvas" />
  <div v-show="false">{{ dataInit }}</div>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

const canvasRef = 'elCanvasCircular';

export default {
  name: 'ViewCircular',
  extends: canvasView,
  methods: {
    onResize() {
      const { w, h } = this.getCanvasSize();
      const ctx = this.prepareCanvas(canvasRef, w, h);
      if (!ctx) return;

      const lineWidth = this.getShapeLineWidth(w, h);
      const rx = Math.max(0.5, (w - lineWidth) / 2);
      const ry = Math.max(0.5, (h - lineWidth) / 2);

      ctx.beginPath();
      ctx.ellipse(w / 2, h / 2, rx, ry, 0, 0, Math.PI * 2);
      ctx.closePath();
      this.paintClosedPath(ctx, this.getForeColor(), lineWidth);
    },
  },
};
</script>
