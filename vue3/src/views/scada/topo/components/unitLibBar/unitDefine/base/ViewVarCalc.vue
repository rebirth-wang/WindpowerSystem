<template>
  <div
    class="view-var-calc"
    :id="detail.identifier"
    :style="{
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
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewVarCalc',
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
    styleBorder() {
      const { waterBorderWidth, waterBorderColor } = this.detail.style;
      if (waterBorderWidth && waterBorderColor) {
        return `${waterBorderWidth}px solid ${waterBorderColor}`;
      } else {
        return null;
      }
    },
  },
  watch: {
    mqttData: {
      deep: true,
      handler() {
        this.processMqttPayload();
      },
    },
  },
  mounted() {
    if (!this.editMode) {
      this.initValue();
      this.processMqttPayload();
    }
  },
  data() {
    return {
      defaultColor: this.detail.style.foreColor,
      /** 各别名最近一次有效值（A、B 分次上报时累加保留） */
      tempVariateList: [] as { id: string; value: number }[],
    };
  },
  methods: {
    getCalcConfig() {
      const { calcFormule, calcVariates } = this.detail.dataBind || {};
      if (!calcFormule || !Array.isArray(calcVariates) || calcVariates.length === 0) {
        return null;
      }
      return { calcFormule, calcVariates };
    },
    /** 从当前 MQTT 报文中读取变量；报文里没有该点时返回 undefined（表示沿用上次值） */
    peekMessageValue(message: any[], identifier: string) {
      if (!message?.length || !identifier) return undefined;
      const mess = message.find((me) => me.id == identifier);
      if (!mess || mess.value == null || mess.value === '') return undefined;
      const num = Number(mess.value);
      return Number.isNaN(num) ? undefined : num;
    },
    seedVariateMap(calcVariates: any[]) {
      const map = new Map<string, number>();
      calcVariates.forEach((item: any) => {
        const cached = this.tempVariateList.find((v) => v.id === item.alias);
        const value = cached ? cached.value : Number(item.modelValue) || 0;
        map.set(item.alias, value);
      });
      return map;
    },
    isItemInCurrentMqtt(item: any, type: number, mqtt: any) {
      const { serialNumber, sceneModelDeviceId } = mqtt;
      if (type === 1) {
        const curSerialNumber = getRouteQueryString(this.route.query, 'serialNumber');
        return this.isSameValue(curSerialNumber, serialNumber);
      }
      if (type === 2) {
        if (item.variableType && item.variableType === 1) {
          return this.isSameValue(item.serialNumber, serialNumber);
        }
        return this.isSameValue(item.sceneModelDeviceId, sceneModelDeviceId);
      }
      if (type === 3) {
        return this.isSameValue(item.serialNumber, serialNumber);
      }
      return false;
    },
    applyMqttToVariateMap(map: Map<string, number>, mqtt: any, calcVariates: any[]) {
      const type = getScadaRouteType(this.route.query);
      const { message } = mqtt;
      if (!Array.isArray(message)) return;

      calcVariates.forEach((item: any) => {
        if (!this.isItemInCurrentMqtt(item, type, mqtt)) return;

        const nextValue = this.peekMessageValue(message, item.identifier);
        if (nextValue === undefined) return;

        item.modelValue = nextValue;
        map.set(item.alias, nextValue);
      });
    },
    mapToVariateList(map: Map<string, number>) {
      return Array.from(map.entries()).map(([id, value]) => ({ id, value }));
    },
    processMqttPayload() {
      const mqtt = this.mqttData;
      if (!mqtt || Object.keys(mqtt).length === 0) {
        return;
      }

      const config = this.getCalcConfig();
      if (!config) return;

      try {
        const map = this.seedVariateMap(config.calcVariates);
        this.applyMqttToVariateMap(map, mqtt, config.calcVariates);
        this.tempVariateList = this.mapToVariateList(map);

        const value = this.templateReplace(config.calcFormule, this.tempVariateList);
        this.setValue(value);
      } catch (_e) {
        // 公式非法或计算失败时保持当前显示
      }
    },
    initValue() {
      const config = this.getCalcConfig();
      if (!config) return;

      try {
        const map = this.seedVariateMap(config.calcVariates);
        this.tempVariateList = this.mapToVariateList(map);
        const value = this.templateReplace(config.calcFormule, this.tempVariateList);
        this.setValue(value);
      } catch (_e) {
        // ignore
      }
    },
    setValue(val: number) {
      const { dataType, decimalDigits } = this.detail.style;
      const num = this.getTextFormat(val, dataType, decimalDigits);
      this.detail.style.text = num;
    },
    getTextFormat(text: number, dtype: number, digit: number) {
      if (dtype === 10) {
        return Number(text).toFixed(digit);
      } else {
        return parseInt(String(text), 10).toString(dtype);
      }
    },
    templateReplace(formula: string, list: { id: string; value: number }[]) {
      const obj = list.reduce((acc, val) => ({ ...acc, [val.id]: val.value }), {} as Record<string, number>);
      const filledFormula = formula.replace(/([A-Za-z_]\w*)/g, (_match, key) => String(obj[key] ?? 0));
      return new Function(`return ${filledFormula}`)() as number;
    },
  },
};
</script>

<style lang="scss" scoped>
.view-var-calc {
  height: 100%;
  width: 100%;
}
</style>
