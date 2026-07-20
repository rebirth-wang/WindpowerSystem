<template>
  <div class="view-image" :id="detail.identifier">
    <img
      class="view-image__img"
      :style="[filterClass]"
      :src="imageUrl"
      @dragstart.prevent
      @dragover.prevent
      @drop.prevent
    />
    <svg style="display: none">
      <defs>
        <filter :id="detail.identifier + '_svg'">
          <feColorMatrix color-interpolation-filters="sRGB" type="matrix" :values="matrixVal" />
        </filter>
      </defs>
    </svg>
    <div v-show="false">{{ dataInit }}{{ colorInit }}{{ imageUrlInit }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import { judgeSize, isInCustomRange, isNotInCustomRange, convertToMilliseconds, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
export default {
  name: 'ViewImage',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  props: {},
  data() {
    return {
      baseApi: import.meta.env.VITE_APP_BASE_API,
      filterClass: {
        width: '100%',
        height: '100%',
        filter: '',
      },
      imageUrl: null,
      matrixVal: null,
      defaultForeColor: '',
      defaultImageUrl: '',
    };
  },
  computed: {
    mqttData() {
      return this.topoStore.mqttData;
    },
    colorInit() {
      const { foreColor, isFilter } = this.detail.style;
      this.getFilterClass(foreColor, isFilter);
      return `${foreColor}-${isFilter}`;
    },
    // 监听样式变化
    imageUrlInit() {
      const { url } = this.detail.style;
      this.getImageUrl(url);
      return url;
    },
    dataInit() {
      if (Object.keys(this.mqttData).length !== 0) {
        const type = getScadaRouteType(this.route.query);
        let bindNum = this.detail.dataBind.serialNumber;
        let mqttNum = this.mqttData.serialNumber;
        let actionNum = this.detail.dataAction.serialNumber;
        if (type === 1) {
          const routeSerial = getRouteQueryString(this.route.query, 'serialNumber');
          bindNum = routeSerial;
          actionNum = routeSerial;
        }
        if (type === 2) {
          if (
            (this.detail.dataBind.variableType && this.detail.dataBind.variableType !== 1) ||
            (this.detail.dataAction.variableType && this.detail.dataAction.variableType !== 1)
          ) {
            bindNum = this.detail.dataBind.sceneModelDeviceId;
            mqttNum = this.mqttData.sceneModelDeviceId;
            actionNum = this.detail.dataAction.sceneModelDeviceId;
          }
        }
        // 动作初始化
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
        // 颜色初始化
        if (bindNum && this.isSameValue(bindNum, mqttNum) && this.detail.dataBind.identifier) {
          for (let i = 0; i < this.mqttData.message.length; i++) {
            if (this.detail.dataBind.identifier == this.mqttData.message[i].id) {
              let val = this.mqttData.message[i].value || 0;
              this.setSwitchStatus(val);
            }
          }
        }
      }
    },
  },
  mounted() {
    this.captureSwitchDefaults();
    if (!this.editMode) {
      const { dataType } = this.detail.dataBind || {};
      if (dataType === 1) {
        this.initSwitchStatus();
        this.initAnimate();
      } else {
        this.mockData();
      }
      // 动态数据
      if (dataType === 4 || dataType === 5) {
        this.initHttp();
      }
    }
  },
  methods: {
    captureSwitchDefaults() {
      this.defaultForeColor = this.detail.style.foreColor;
      this.defaultImageUrl = this.detail.style.url || '';
      this.getFilterClass(this.defaultForeColor, this.detail.style.isFilter);
      this.getImageUrl(this.defaultImageUrl);
    },
    resetSwitchDefaults() {
      this.getFilterClass(this.defaultForeColor, this.detail.style.isFilter);
      this.getImageUrl(this.defaultImageUrl);
    },
    initSwitchStatus() {
      const { modelValue } = this.detail.dataBind || {};
      if (modelValue !== undefined && modelValue !== '') {
        this.setSwitchStatus(modelValue);
      }
    },
    // 动画初始化
    initAnimate() {
      const { modelValue } = this.detail.dataAction;
      if (modelValue !== undefined && modelValue !== '') {
        this.animatePlay(modelValue);
      }
    },
    // 开始动画
    animatePlay(val) {
      this.applyAnimationState(val);
    },
    /** 与常用元件 ViewImageSwitch 一致：命中 stateList 切换，未命中恢复默认 */
    setSwitchStatus(val) {
      const { stateList, statusType } = this.detail.dataBind || {};
      if (!this.detail.componentShow?.includes('状态开关') || !stateList?.length || statusType === 2) {
        return;
      }

      const modelValue = this.getFunHandlingResult(val);
      const stateItem = stateList.find((item) => judgeSize(item.paramCondition, modelValue, item.paramData));

      if (stateItem) {
        const { foreColor, imageUrl } = stateItem;
        this.getFilterClass(foreColor || this.defaultForeColor, !!foreColor);
        this.getImageUrl(imageUrl || this.defaultImageUrl);
      } else {
        this.resetSwitchDefaults();
      }
    },
    // 获取filter样式
    getFilterClass(foreColor, isFilterValue) {
      const { style, identifier } = this.detail;
      const isFilter = this.normalizeBool(isFilterValue ?? style.isFilter);
      const color = this.normalizeColor(foreColor);
      if (isFilter) {
        this.filterClass.filter = `url(#${identifier}_svg)`;
        this.matrixVal = color ? this.rgbaTofeColorMatrix(color) : '';
      } else {
        this.matrixVal = '';
        this.filterClass.filter = color ? `drop-shadow(0 10px 2px ${color})` : '';
      }
    },
    normalizeBool(value) {
      return value === true || value === 'true' || value === 1 || value === '1';
    },
    normalizeColor(color) {
      if (!color) return '';
      const text = String(color).trim();
      if (text.startsWith('rgba(')) return text;
      if (text.startsWith('rgb(')) {
        const values = text.match(/[\d.]+/g);
        if (values && values.length >= 3) {
          return `rgba(${values[0]},${values[1]},${values[2]},1)`;
        }
        return '';
      }
      if (text.startsWith('#')) {
        return this.hexToRgba(text);
      }
      return text;
    },
    hexToRgba(hex) {
      let color = hex.replace('#', '');
      if (color.length === 3 || color.length === 4) {
        color = color
          .split('')
          .map((item) => item + item)
          .join('');
      }
      if (color.length === 6) {
        color += 'ff';
      }
      if (color.length !== 8) return '';
      const r = parseInt(color.slice(0, 2), 16);
      const g = parseInt(color.slice(2, 4), 16);
      const b = parseInt(color.slice(4, 6), 16);
      const a = Number((parseInt(color.slice(6, 8), 16) / 255).toFixed(2));
      return `rgba(${r},${g},${b},${a})`;
    },
    rgbaTofeColorMatrix(rgba) {
      if (rgba) {
        const channels = String(rgba).match(/[\d.]+/g)?.map(Number) || [];
        if (channels.length < 3) return '';
        if (channels.length === 3) channels.push(1);
        rgba = channels;
        let RGBA = [];
        let numberList = [1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0];
        for (let i = 0; i < rgba.length; i++) {
          let RGBValue = rgba[i] / 255;
          RGBValue = Number(RGBValue.toFixed(2));
          RGBA.push(RGBValue);
        }
        const r = RGBA[0];
        const g = RGBA[1];
        const b = RGBA[2];
        const a = rgba[3];
        numberList[0] = r;
        numberList[6] = g;
        numberList[12] = b;
        numberList[18] = a;
        return numberList.join(' ');
      }
    },
    // 获取图片url
    getImageUrl(url) {
      if (url) {
        this.imageUrl = this.baseApi + url;
      } else {
        this.imageUrl = null;
      }
    },
    // 生成模拟数据
    mockData() {
      const { componentShow, dataBind } = this.detail;
      const { simInterval } = dataBind;
      this.timer = setInterval(
        () => {
          const val = this.getRandomData();
          if (val !== undefined && val !== '') {
            if (componentShow.indexOf('状态开关') !== -1) {
              this.setSwitchStatus(val);
            }
            if (componentShow.indexOf('参数绑定') !== -1) {
              this.animatePlay(val);
            }
            if (componentShow.indexOf('组件填充') !== -1) {
              this.setSwitchStatus(val);
            }
          }
        },
        Number(simInterval) * 1000
      );
    },
    // 初始化http数据
    initHttp() {
      const { identifier, dataBind } = this.detail;
      const { dataType, httpSetting, httpSettingId } = dataBind;
      let source = {};
      if (dataType === 4) {
        source = httpSetting;
      } else {
        source = this.httpPublicSetting.find((item) => item.id === httpSettingId);
      }
      const { time, unit } = source as any;
      this.setHttp();
      this.timer = setInterval(
        () => {
          this.setHttp();
        },
        convertToMilliseconds(time, unit)
      );
      // 监听交互组件信息
      this.$busEvent.$on('interactionNotice', (data) => {
        const { compId, params, headers } = data;
        if (identifier === compId) {
          this.setHttp(params, headers);
        }
      });
    },
    // 设置http数据
    async setHttp(params, headers) {
      const { componentShow } = this.detail;
      const val = await this.getHttpData(params, headers);
      if (val !== undefined && val !== '') {
        this.setValue(val);
        if (componentShow.indexOf('组件填充') !== -1) {
          this.setColor(val);
        }
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.view-image {
  height: 100%;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}
.view-image__img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center;
  display: block;
}
</style>
