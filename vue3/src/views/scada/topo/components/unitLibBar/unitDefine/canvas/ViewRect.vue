<template>
  <canvas :id="detail.identifier" ref="elCanvasRect" class="shape-canvas" />
  <div v-show="false">{{ dataInit }}</div>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

const canvasRef = 'elCanvasRect';

export default {
  name: 'ViewRect',
  extends: canvasView,
  methods: {
    onResize() {
      const { w, h } = this.getCanvasSize();
      const ctx = this.prepareCanvas(canvasRef, w, h);
      if (!ctx) return;

      const lineWidth = this.getShapeLineWidth(w, h);
      const padding = lineWidth / 2;
      const width = Math.max(1, w - lineWidth);
      const height = Math.max(1, h - lineWidth);
      const radius = Math.max(0, Math.min(Number(this.detail.style.borderRadius ?? this.detail.style.radius) || 0, width / 2, height / 2));

      ctx.beginPath();
      ctx.moveTo(padding, padding + radius);
      ctx.lineTo(padding, padding + height - radius);
      ctx.quadraticCurveTo(padding, padding + height, padding + radius, padding + height);
      ctx.lineTo(padding + width - radius, padding + height);
      ctx.quadraticCurveTo(padding + width, padding + height, padding + width, padding + height - radius);
      ctx.lineTo(padding + width, padding + radius);
      ctx.quadraticCurveTo(padding + width, padding, padding + width - radius, padding);
      ctx.lineTo(padding + radius, padding);
      ctx.quadraticCurveTo(padding, padding, padding, padding + radius);
      ctx.closePath();
      this.paintClosedPath(ctx, this.getForeColor(), lineWidth);
    },
  },
};
</script>
