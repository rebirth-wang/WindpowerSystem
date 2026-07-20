<template>
  <div class="view-image-switch">
    <img class="view-image-switch__img" :style="filterClass" :src="imageUrl" @dragstart.prevent @dragover.prevent @drop.prevent />
    <svg style="display: none">
      <defs>
        <filter :id="detail.identifier + '_svg'">
          <feColorMatrix color-interpolation-filters="sRGB" type="matrix" :values="matrixVal" />
        </filter>
      </defs>
    </svg>
    <div v-show="false">{{ dataInit }}{{ colorInit }}{{ imageUrlInit }}{{ deviceStatusInit }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';
import BaseView from '../View.vue';
import defaultImage from '@/assets/images/scada/unitIcon/base/switch.png';
import { judgeSize, convertToMilliseconds, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
import { getDeviceStatus } from '@/api/scada/topo';

export default {
  name: 'ViewImageSwitch',
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
    deviceStatus() {
      return this.topoStore.deviceStatus;
    },
    colorInit() {
      const { foreColor } = this.detail.style;
      this.getFilterClass(foreColor);
      return foreColor;
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
        let eventNum = this.detail.dataEvent.serialNumber;
        let mqttNum = this.mqttData.serialNumber;
        if (type === 1) {
          bindNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (type === 2) {
          if (this.detail.dataBind.variableType && this.detail.dataBind.variableType !== 1) {
            bindNum = this.detail.dataBind.sceneModelDeviceId;
            mqttNum = this.mqttData.sceneModelDeviceId;
          }
        }
        if (
          bindNum &&
          this.isSameValue(bindNum, mqttNum) &&
          this.detail.dataBind.identifier &&
          this.detail.dataBind.statusType !== 2
        ) {
          for (let i = 0; i < this.mqttData.message.length; i++) {
            if (this.detail.dataBind.identifier == this.mqttData.message[i].id) {
              let val = this.mqttData.message[i].value || 0;
              this.setSwitchStatus(val);
            }
          }
        }
        // 动作判断（开关控制）
        if (eventNum && this.isSameValue(eventNum, mqttNum) && this.detail.dataEvent.identifier) {
          for (let j = 0; j < this.mqttData.message.length; j++) {
            if (this.detail.dataEvent.identifier === this.mqttData.message[j].id) {
              let val = this.mqttData.message[j].value || 0;
              this.setActionSwitchValue(val);
            }
          }
        }
      }
    },
    deviceStatusInit() {
      if (Object.keys(this.deviceStatus).length !== 0) {
        const { serialNumber: mqttNum, status } = this.deviceStatus;
        let { serialNumber: bindNum } = this.detail.dataBind;
        const type = getScadaRouteType(this.route.query);
        if (type === 1) {
          bindNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (this.isSameValue(mqttNum, bindNum)) {
          this.setOnlineStatus(status);
        }
      }
    },
  },
  data() {
    return {
      baseApi: import.meta.env.VITE_APP_BASE_API,
      filterClass: {
        filter: '',
      },
      imageUrl: null,
      matrixVal: null,
      defaultForeColor: '',
      defaultImageUrl: '',
    };
  },
  mounted() {
    this.captureSwitchDefaults();
    if (!this.editMode) {
      let { statusType, modelValue, serialNumber: bindNum } = this.detail.dataBind;
      const { dataType } = this.detail.dataBind || {};
      const type = getScadaRouteType(this.route.query);
      if (statusType === 2) {
        if (type === 1) {
          bindNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        this.getDeviceRealStatus(bindNum);
      } else {
        if (dataType === 1) {
          if (modelValue !== undefined && modelValue !== '') {
            this.setSwitchStatus(modelValue);
          }
        }
        // 模拟
        if (dataType === 2) {
          this.mockData();
        }
        // 动态数据
        if (dataType === 4 || dataType === 5) {
          this.initHttp();
        }
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
    // 通过接口获取设备状态
    getDeviceRealStatus(serialNumber) {
      const params = {
        serialNumber: serialNumber,
      };
      getDeviceStatus(params).then((res) => {
        if (res.code == 200) {
          let status = res.data;
          this.setOnlineStatus(status);
        }
      });
    },
    // 获取filter样式
    getFilterClass(foreColor, forceFilter = false) {
      const { style, identifier } = this.detail;
      const isFilter = forceFilter || this.normalizeBool(style.isFilter);
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
        const values = text
          .replace('rgb(', '')
          .replace(')', '')
          .split(',')
          .map((item) => item.trim());
        return `rgba(${values[0]},${values[1]},${values[2]},1)`;
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
        rgba = rgba.slice(5, -1).split(',').map(Number);
        let RGBA = [];
        let numberList = [1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0];
        for (let i = 0; i < rgba.length; i++) {
          let RGBValue: number = rgba[i] / 255;
          RGBA.push(Number(RGBValue.toFixed(2)));
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
        this.imageUrl = defaultImage;
      }
    },
    setSwitchStatus(val) {
      const { stateList, statusType } = this.detail.dataBind || {};
      if (!stateList?.length || statusType === 2) {
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
    setOnlineStatus(status) {
      const { openImageUrl, warnImageUrl, shutImageUrl } = this.detail.dataBind || {};
      if (status === 3 && openImageUrl) {
        this.imageUrl = this.baseApi + openImageUrl;
      } else if (status === 2 && warnImageUrl) {
        this.imageUrl = this.baseApi + warnImageUrl;
      } else if (status === 4 && shutImageUrl) {
        this.imageUrl = this.baseApi + shutImageUrl;
      } else {
        this.resetSwitchDefaults();
      }
    },
    // 设置按钮动作切换值
    setActionSwitchValue(val) {
      const { dataEvent } = this.detail;
      const { switchControl } = dataEvent;
      if (switchControl === 1) {
        if (val === '0') {
          this.detail.dataAction.switchValue = '关';
        } else {
          this.detail.dataAction.switchValue = '开';
        }
      } else if (switchControl === 2) {
        if (val === '0') {
          this.detail.dataAction.switchValue = '开';
        } else {
          this.detail.dataAction.switchValue = '关';
        }
      }
    },
    // 生成模拟数据
    mockData() {
      const { dataBind } = this.detail;
      const { simInterval, statusType } = dataBind;
      this.timer = setInterval(
        () => {
          const val = this.getRandomData();
          if (val !== undefined && val !== '') {
            if (statusType !== 2) {
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
.view-image-switch {
  height: 100%;
  width: 100%;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.view-image-switch__img {
  width: 100%;
  height: 100%;
  object-fit: fill;
  display: block;
}
</style>
