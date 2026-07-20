<script lang="ts">
import BaseVeiw from '../View.vue';

export default {
  name: 'ViewSvg',
  extends: BaseVeiw,
  computed: {
    svgURL: function () {
      if (this.detail.style.url == undefined || this.detail.style.url == '') {
        return '';
      } else {
        return this.baseApi + this.detail.style.url;
      }
    },
  },
  watch: {
    detail: {
      handler(newVal: any, oldVal: any) {
        this.onResize();
      },
      deep: true,
    },
  },
  data() {
    return {
      baseApi: import.meta.env.VITE_APP_BASE_API,
    };
  },
  mounted() {},
  methods: {
    getForeColor() {
      var foreColor = this.detail.style.foreColor;
      if (foreColor == undefined || foreColor === '') {
        return 'grey';
      } else if (foreColor.startsWith('#')) {
        return this.hex2rgba(foreColor);
      } else {
        return foreColor;
      }
    },
    hex2rgba(hex: string) {
      let colorArr: number[] = [];
      for (let i = 1; i < 7; i += 2) {
        colorArr.push(parseInt('0x' + hex.slice(i, i + 2)));
      }
      var alpha = parseInt('0x' + hex.slice(7, 9)) / 255;
      return `rgba(${colorArr.join(',')},${alpha})`;
    },
    inRange(x: number, y: number, points: number[][]) {
      let inside = false;
      for (let i = 0, j = points.length - 1; i < points.length; j = i++) {
        let xi = points[i][0],
          yi = points[i][1];
        let xj = points[j][0],
          yj = points[j][1];
        let intersect = yi > y !== yj > y && x < ((xj - xi) * (y - yi)) / (yj - yi) + xi;
        if (intersect) inside = !inside;
      }
      return inside;
    },
  },
};
</script>
