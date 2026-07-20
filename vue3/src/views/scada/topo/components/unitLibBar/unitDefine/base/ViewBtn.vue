<template>
  <div
    class="view-btn"
    :style="{
      fontSize: detail.style.fontSize + 'px',
      fontFamily: detail.style.fontFamily,
      color: detail.style.foreColor,
      textAlign: detail.style.textAlign,
      lineHeight: detail.style.position.h + 'px',
      borderRadius: detail.style.borderRadius + 'px !important',
      backgroundImage: `url(${imageUrl.replace(/\s/g, encodeURIComponent(' '))})`, // 避免图片路径带有空格不显示
      backgroundColor: detail.style.backColor,
    }"
    :id="detail.identifier"
  >
    {{ detail.style.text }}
    <div v-show="false">{{ imageUrlInit }}{{ dataInit }}</div>
  </div>
</template>

<script lang="ts">
import { useRoute } from 'vue-router';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

import BaseView from '../View.vue';
import { judgeSize, isInCustomRange, isNotInCustomRange, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
export default {
  name: 'ViewBtn',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  data() {
    return {
      baseApi: import.meta.env.VITE_APP_BASE_API,
      imageUrl: '',
    };
  },
  computed: {
    mqttData() {
      return this.topoStore.mqttData;
    },
    imageUrlInit() {
      this.imageUrl = this.baseApi + this.detail.style.url;
      return this.detail.style.url;
    },
    dataInit() {
      if (Object.keys(this.mqttData).length !== 0) {
        const type = getScadaRouteType(this.route.query);
        let mqttNum = this.mqttData.serialNumber;
        let actionNum = this.detail.dataAction.serialNumber;
        if (type === 1) {
          actionNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (type === 2) {
          if (this.detail.dataAction.variableType && this.detail.dataAction.variableType !== 1) {
            mqttNum = this.mqttData.sceneModelDeviceId;
            actionNum = this.detail.dataAction.sceneModelDeviceId;
          }
        }
        // 动画初始化
        if (
          actionNum &&
          this.isSameValue(actionNum, mqttNum) &&
          this.detail.dataAction.identifier &&
          this.detail.dataAction.paramJudge &&
          (this.detail.dataAction.paramJudgeData !== '' ||
            (this.detail.dataAction.paramJudgeDatarangeMin !== '' &&
              this.detail.dataAction.paramJudgeDatarangeMax !== ''))
        ) {
          for (let i = 0; i < this.mqttData.message.length; i++) {
            if (this.detail.dataAction.identifier == this.mqttData.message[i].id) {
              let val = this.mqttData.message[i].value;
              this.animatePlay(val);
            }
          }
        }
      }
    },
  },
  mounted() {
    if (!this.editMode) {
      this.initAnimate();
    }
  },
  methods: {
    // 动画初始化
    initAnimate() {
      let value = !this.detail.dataBind.modelValue ? 0 : this.detail.dataBind.modelValue;
      this.animatePlay(value);
    },
    // 开始动画
    animatePlay(val) {
      this.applyAnimationState(val);
    },
  },
};
</script>

<style lang="scss" scoped>
.view-btn {
  height: 100%;
  width: 100%;
  background-image: none;
  background-repeat: round;
  background-size: 100% 100%;
}
</style>
