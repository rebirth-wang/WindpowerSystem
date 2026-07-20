<template>
  <div
    class="view-panel"
    :id="detail.identifier"
    :style="{
      fontSize: detail.style.fontSize + 'px',
      fontFamily: detail.style.fontFamily,
      color: detail.style.foreColor,
      textAlign: detail.style.textAlign,
      border: detail.style.waterBorderWidth + 'px solid',
      borderRadius: detail.style.borderRadius + 'px',
      borderColor: detail.style.waterBorderColor,
    }"
  >
    <iframe
      :style="{
        borderRadius: detail.style.borderRadius + 'px',
        width: detail.style.position.w - 20 + 'px',
        height: detail.style.position.h - 20 + 'px',
        border: 'transparent',
      }"
      :src="childUrl"
    ></iframe>
    <div v-show="false">{{ dataInit }}</div>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';

export default {
  name: 'ViewPanel',
  extends: BaseView,
  data() {
    return {
      childUrl: '',
    };
  },
  computed: {
    dataInit() {
      this.updateChildUrl();
      return this.detail.dataBind.scadaGuid;
    },
  },
  watch: {
    'detail.dataBind.scadaGuid': {
      handler() {
        this.updateChildUrl();
      },
    },
  },
  mounted() {
    this.updateChildUrl();
  },
  methods: {
    updateChildUrl() {
      const scadaGuid = this.detail?.dataBind?.scadaGuid;
      if (!scadaGuid) {
        this.childUrl = '';
        return;
      }
      const split = String(scadaGuid).split('&');
      if (split.length < 3 || !split[1]) {
        this.childUrl = '';
        return;
      }
      const basePath = (import.meta.env.BASE_URL || '/').replace(/\/?$/, '/');
      this.childUrl =
        window.location.origin +
        basePath +
        'scada/topo/fullscreen?&id=' +
        split[0] +
        '&guid=' +
        split[1] +
        '&type=' +
        split[2] +
        '&t=' +
        new Date().getTime();
    },
  },
};
</script>

<style lang="scss" scoped>
.view-panel {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
