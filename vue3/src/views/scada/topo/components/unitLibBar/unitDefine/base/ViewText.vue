<template>
  <div
    class="view-text"
    :id="detail.identifier"
    :style="{
      fontWeight: detail.style.fontWeight ? detail.style.fontWeight : 'normal',
      fontStyle: detail.style.fontStyle ? detail.style.fontStyle : 'normal',
      fontSize: detail.style.fontSize + 'px',
      fontFamily: detail.style.fontFamily,
      color: detail.style.foreColor,
      textAlign: detail.style.textAlign,
      lineHeight: detail.style.position.h + 'px',
      border: styleBorder,
      borderRadius: detail.style.borderRadius + 'px !important',
      borderColor: detail.style.waterBorderColor,
    }"
  >
    {{ textInit }}
    <div v-show="false">{{ dataInit }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import {
  judgeSize,
  isInCustomRange,
  isNotInCustomRange,
  convertToMilliseconds,
  getScadaRouteType,
  getRouteQueryString,
} from '@/utils/topo/topoUtil';
export default {
  name: 'ViewText',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  computed: {
    mqttData() {
      return this.topoStore.mqttData;
    },
    textInit() {
      const { text } = this.detail.style;
      return text;
    },
    dataInit() {
      this.processMqttPayload();
      return '';
    },
    styleBorder() {
      const { waterBorderWidth, waterBorderColor } = this.detail.style;
      if (waterBorderWidth && waterBorderColor) {
        return `${waterBorderWidth}px solid ${waterBorderColor}`;
      } else {
        return null;
      }
    },
  },
  mounted() {
    if (!this.editMode) {
      const { dataType } = this.detail.dataBind || {};
      // 设备数据
      if (dataType === 1) {
        this.initValue();
        this.initAnimate();
        this.initColor();
        this.processMqttPayload();
      }
      // 模拟数据
      if (dataType === 2) {
        this.initMock();
      }
      // 动态数据
      if (dataType === 4 || dataType === 5) {
        this.initHttp();
      }
    }
  },
  data() {
    return {
      defaultColor: this.detail.style.foreColor,
      timer: null,
    };
  },
  methods: {
    processMqttPayload() {
      const mqtt = this.mqttData;
      if (!mqtt || Object.keys(mqtt).length === 0 || !Array.isArray(mqtt.message)) {
        return;
      }
      const type = getScadaRouteType(this.route.query);
      const routeSerial = getRouteQueryString(this.route.query, 'serialNumber');
      let bindNum = this.detail.dataBind.serialNumber;
      let mqttNum = mqtt.serialNumber;
      let actionNum = this.detail.dataAction.serialNumber;
      if (type === 1) {
        bindNum = routeSerial;
        actionNum = routeSerial;
      }
      if (type === 2) {
        if (
          (this.detail.dataBind.variableType && this.detail.dataBind.variableType !== 1) ||
          (this.detail.dataAction.variableType && this.detail.dataAction.variableType !== 1)
        ) {
          bindNum = this.detail.dataBind.sceneModelDeviceId;
          mqttNum = mqtt.sceneModelDeviceId;
          actionNum = this.detail.dataAction.sceneModelDeviceId;
        }
      }
      if (bindNum && this.isSameValue(bindNum, mqttNum) && this.detail.dataBind.identifier) {
        for (let i = 0; i < mqtt.message.length; i++) {
          if (this.detail.dataBind.identifier == mqtt.message[i].id) {
            const val = mqtt.message[i].value == null || mqtt.message[i].value === '' ? 0 : mqtt.message[i].value;
            this.setValue(val);
          }
        }
      }
      if (
        actionNum &&
        this.isSameValue(actionNum, mqttNum) &&
        this.detail.dataAction.identifier &&
        this.isAnimationJudgeReady()
      ) {
        for (let i = 0; i < mqtt.message.length; i++) {
          if (this.detail.dataAction.identifier == mqtt.message[i].id) {
            this.setAnimate(mqtt.message[i].value);
          }
        }
      }
      if (bindNum && this.isSameValue(bindNum, mqttNum) && this.detail.dataBind.identifier) {
        for (let i = 0; i < mqtt.message.length; i++) {
          if (this.detail.dataBind.identifier == mqtt.message[i].id) {
            this.setColor(mqtt.message[i].value);
          }
        }
      }
    },
    // 设置初始值
    initValue() {
      const { componentShow, dataBind } = this.detail;
      if (componentShow.indexOf('参数绑定') !== -1) {
        const { modelValue } = dataBind;
        if (modelValue !== undefined && modelValue !== '') {
          this.setValue(modelValue);
        }
      }
    },
    // 设置值
    setValue(val) {
      const { dataType, decimalDigits } = this.detail.style;
      const modelValue = this.getFunHandlingResult(val, this.detail.dataBind && this.detail.dataBind.identifier);
      const num = this.getTextFormat(modelValue, dataType, decimalDigits);
      this.detail.style.text = num;
    },
    // 动画初始化
    initAnimate() {
      const { componentShow, dataAction } = this.detail;
      if (componentShow.indexOf('动画') !== -1) {
        const { modelValue } = dataAction;
        if (modelValue !== undefined && modelValue !== '') {
          this.setAnimate(modelValue);
        }
      }
    },
    // 开始动画
    setAnimate(val) {
      this.applyAnimationState(val);
    },
    // 颜色初始化
    initColor() {
      const { componentShow, dataBind } = this.detail;
      if (componentShow.indexOf('组件填充') !== -1) {
        const { modelValue } = dataBind;
        if (modelValue !== undefined && modelValue !== '') {
          this.setColor(modelValue);
        }
      }
    },
    // 设置颜色
    setColor(val) {
      const modelValue = this.getFunHandlingResult(val, this.detail.dataBind && this.detail.dataBind.identifier);
      const { stateList } = this.detail.dataBind || {};
      if (stateList && stateList.length !== 0) {
        const stateItem = stateList.find((item) => judgeSize(item.paramCondition, modelValue, item.paramData));
        if (stateItem) {
          this.detail.style.foreColor = stateItem.foreColor;
        } else {
          this.detail.style.foreColor = this.defaultColor;
        }
      }
    },
    /**
     * 数据格式
     * number类型数据超过22位进行运算会丢失或者变成科学计数法
     * 如果想保持原样可以使用Text组件进行展示，因为设备过来的是字符串
     */
    getTextFormat(text, dtype, digit) {
      const { type } = this.detail;
      if (type === 'num') {
        if (dtype === 10) {
          return Number(text).toFixed(digit);
        } else {
          return parseInt(text).toString(dtype);
        }
      } else {
        return text;
      }
    },
    // 初始化模拟数据
    initMock() {
      const { dataBind } = this.detail;
      const { simInterval } = dataBind;
      this.setMock();
      this.timer = setInterval(
        () => {
          this.setMock();
        },
        Number(simInterval) * 1000
      );
    },
    // 设置模拟数据
    setMock() {
      const { componentShow } = this.detail;
      const val = this.getRandomData();
      if (val !== undefined && val !== '') {
        this.setValue(val);
        if (componentShow.indexOf('组件填充') !== -1) {
          this.setColor(val);
        }
        if (componentShow.indexOf('动画') !== -1) {
          this.setAnimate(val);
        }
      }
    },
    // 初始化http数据
    initHttp() {
      const { identifier, dataBind } = this.detail;
      const { dataType, httpSetting, httpSettingId } = dataBind;
      let source: any = {};
      if (dataType === 4) {
        source = httpSetting;
      } else {
        source = this.httpPublicSetting.find((item) => item.id === httpSettingId);
      }
      const { time, unit } = source;
      this.setHttp();
      this.timer = setInterval(
        () => {
          this.setHttp();
        },
        convertToMilliseconds(time, unit)
      );
      // 监听交互组件信息
      this._interactionNoticeHandler = (data) => {
        const { compId, params, headers } = data;
        if (identifier === compId) {
          this.setHttp(params, headers);
        }
      };
      this.$busEvent.on('interactionNotice', this._interactionNoticeHandler);
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
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
    if (this._interactionNoticeHandler) {
      this.$busEvent.off('interactionNotice', this._interactionNoticeHandler);
      this._interactionNoticeHandler = null;
    } else {
      this.$busEvent.off('interactionNotice');
    }
  },
};
</script>

<style lang="scss" scoped>
.view-text {
  height: 100%;
  width: 100%;
}
</style>
