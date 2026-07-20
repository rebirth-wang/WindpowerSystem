<template>
  <canvas ref="elCanvas" :width="detail.style.position.w" :height="detail.style.position.h">
    Your browser does not support the HTML5 canvas tag.
  </canvas>
</template>

<script lang="ts">
import canvasView from './ViewCanvas.vue';

export default {
  name: 'ViewLineWave',
  extends: canvasView,
  methods: {
    drawLine(ctx, width, height, lineWidth, color) {
      let len = Math.sqrt(width * width + height * height);
      ctx.beginPath();
      ctx.strokeStyle = color;
      ctx.lineWidth = lineWidth;
      let x = 0;
      let y = 0;
      let amplitude = 5;
      let frequency = 5;
      while (x < len) {
        y = amplitude * Math.sin(x / frequency);
        ctx.lineTo(x, y);
        x = x + 1;
      }
      ctx.stroke();
    },
    onResize() {
      var w = this.detail.style.position.w;
      var h = this.detail.style.position.h;
      var el = this.$refs.elCanvas;
      if (!el) return;
      var ctx = el.getContext('2d');
      if (!ctx) return;
      ctx.clearRect(0, 0, w, h);
      var color = this.getForeColor();
      var lineWidth = this.detail.style.lineWidth;
      if (lineWidth == undefined || typeof lineWidth != 'number') {
        lineWidth = 2;
      }
      this.drawLine(ctx, w, h, lineWidth, color);
    },
    getForeColor() {
      return this.detail.style.foreColor;
    },
  },
  mounted() {
    this.onResize();
  },
};
</script>
