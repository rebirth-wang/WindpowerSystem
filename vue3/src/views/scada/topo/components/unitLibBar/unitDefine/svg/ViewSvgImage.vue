<template>
  <embed
    :src="svgURL"
    :width="detail.style.position.w"
    :height="detail.style.position.h"
    style="pointer-events: none"
    type="image/svg+xml"
    pluginspage="http://www.adobe.com/svg/viewer/install/"
  />
  <!-- <iframe :src="svgURL" :width="detail.style.position.w" :height="detail.style.position.h" >
</iframe> -->
</template>

<script lang="ts">
import svgView from './ViewSvg.vue';

export default {
  name: 'ViewSvgImage',
  extends: svgView,
  methods: {
    onResize() {},
  },
  mounted() {
    this.onResize();
  },
};
</script>
