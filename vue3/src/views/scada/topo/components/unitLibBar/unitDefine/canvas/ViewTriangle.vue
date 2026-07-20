<template>
  <canvas :id="detail.identifier" ref="elCanvasTriangle" class="shape-canvas" />
  <div v-show="false">{{ dataInit }}</div>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

const canvasRef = 'elCanvasTriangle';

export default {
  name: 'ViewTriangle',
  extends: canvasView,
  methods: {
    onResize() {
      const { w, h } = this.getCanvasSize();
      const ctx = this.prepareCanvas(canvasRef, w, h);
      if (!ctx) return;

      const lineWidth = this.getShapeLineWidth(w, h);
      const padding = lineWidth > 0 ? lineWidth / 2 : 0;
      const topY = padding;
      const bottomY = Math.max(topY, h - padding);
      const leftX = padding;
      const rightX = Math.max(leftX, w - padding);

      ctx.beginPath();
      ctx.moveTo(w / 2, topY);
      ctx.lineTo(leftX, bottomY);
      ctx.lineTo(rightX, bottomY);
      ctx.closePath();
      this.paintClosedPath(ctx, this.getForeColor(), lineWidth);
    },
  },
};
</script>
