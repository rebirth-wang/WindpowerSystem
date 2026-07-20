<template>
  <svg
    version="1.1"
    xmlns="http://www.w3.org/2000/svg"
    :width="detail.style.position.w"
    :height="detail.style.position.h"
    style="pointer-events: none"
    v-html="svgContent"
  ></svg>
</template>

<script lang="ts">
import svgView from './ViewSvg.vue';
import request from '@/utils/request';

export default {
  name: 'ViewStaticSvg',
  extends: svgView,
  data() {
    return {
      svgContent: '',
    };
  },
  methods: {
    loadData() {
      var that = this;
      request
        .get(this.svgURL)
        .then(function (response) {
          that.svgContent = response.data;
        })
        .catch(function () {
          // Error handled silently
        });
    },
    onResize() {},
  },
  mounted() {
    this.loadData();
  },
};
</script>
