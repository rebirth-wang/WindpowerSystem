<template>
  <div
    class="view-chart-map"
    :id="detail.identifier"
    ref="xwin"
    :style="{
      width: detail.style.position.w + 'px',
      height: detail.style.position.h + 'px',
    }"
  ></div>
</template>

<script lang="ts">
import henanJson from '@/assets/data/json/scada/map/province/henan.json'; //湖南
import hunanJson from '@/assets/data/json/scada/map/province/hunan.json'; //河南
import anhuiJson from '@/assets/data/json/scada/map/province/anhui.json'; //安徽
import aomenJson from '@/assets/data/json/scada/map/province/aomen.json'; //澳门
import beijingJson from '@/assets/data/json/scada/map/province/beijing.json'; //北京
import chongqingJson from '@/assets/data/json/scada/map/province/chongqing.json'; //重庆
import fujianJson from '@/assets/data/json/scada/map/province/fujian.json'; //福建
import gansuJson from '@/assets/data/json/scada/map/province/gansu.json'; //甘肃
import guangdongJson from '@/assets/data/json/scada/map/province/guangdong.json'; //广州
import guangxiJson from '@/assets/data/json/scada/map/province/guangxi.json'; //广西
import guizhouJson from '@/assets/data/json/scada/map/province/guizhou.json'; //贵州
import hainanJson from '@/assets/data/json/scada/map/province/hainan.json'; //海南
import hebeiJson from '@/assets/data/json/scada/map/province/hebei.json'; //河北
import heilongjiangJson from '@/assets/data/json/scada/map/province/heilongjiang.json'; //黑龙江
import hubeiJson from '@/assets/data/json/scada/map/province/hubei.json'; //湖北

import jiangsuJson from '@/assets/data/json/scada/map/province/jiangsu.json'; //江苏
import jiangxiJson from '@/assets/data/json/scada/map/province/jiangxi.json'; //江西
import jilinJson from '@/assets/data/json/scada/map/province/jilin.json'; //吉林
import liaoningJson from '@/assets/data/json/scada/map/province/liaoning.json'; //辽宁
import neimengguJson from '@/assets/data/json/scada/map/province/neimenggu.json'; //内蒙古

import ningxiaJson from '@/assets/data/json/scada/map/province/ningxia.json'; //宁夏
import qinghaiJson from '@/assets/data/json/scada/map/province/qinghai.json'; //青海
import shandongJson from '@/assets/data/json/scada/map/province/shandong.json'; //山东
import shanghaiJson from '@/assets/data/json/scada/map/province/shanghai.json'; //上海
import shanxiJson from '@/assets/data/json/scada/map/province/shanxi1.json'; //山西
import sichuanJson from '@/assets/data/json/scada/map/province/sichuan.json'; //四川
import taiwanJson from '@/assets/data/json/scada/map/province/taiwan.json'; //台湾
import tianjinJson from '@/assets/data/json/scada/map/province/tianjin.json'; //天津
import xianggangJson from '@/assets/data/json/scada/map/province/xianggang.json'; //香港
import xinjiangJson from '@/assets/data/json/scada/map/province/xinjiang.json'; //新疆
import xizangJson from '@/assets/data/json/scada/map/province/xizang.json'; //西藏
import yunnanJson from '@/assets/data/json/scada/map/province/yunnan.json'; //云南
import zhejiangJson from '@/assets/data/json/scada/map/province/zhejiang.json'; //浙江
import chartOption from '@/assets/scripts/scada/chart-option.js';
import { ElMessage } from 'element-plus';

import request from '@/utils/request';

import BaseView from '../View.vue';
import { getSecond } from '@/utils/topo/index';

export default {
  name: 'ViewChartMap',
  extends: BaseView,
  data() {
    return {
      echart: null,
      mapJsons: {},
      timer: null,
      lastChartSize: '',
      currentOption: null,
      currentMapJson: null,
    };
  },
  computed: {
    echartRun() {
      const { echartOption, echartRun, mapAddress, mapUrl } = this.detail.dataBind;
      this.$nextTick(function () {
        if (echartOption && echartRun > new Date().getTime() - 10000) {
          try {
            let flag = false;
            if (mapAddress == this.$t('topo.components.chart.073848-8')) {
              flag = this.getMapJson(mapUrl);
            } else {
              flag = this.initEcharts();
            }
            flag && ElMessage({ message: this.$t('topo.components.chart.073848-9'), type: 'success' });
          } catch (error) {
            ElMessage({
              message: this.$t('topo.components.chart.073848-10'),
              type: 'warning',
            });
          }
        }
      });
      return echartOption + echartRun;
    },
    mapChange() {
      this.$nextTick(function () {
        try {
          if (
            this.detail.dataBind.mapAddress == this.$t('topo.components.chart.073848-8') &&
            this.detail.dataBind.mapUrl
          ) {
            this.getMapJson(this.detail.dataBind.mapUrl);
          } else {
            this.initEcharts();
          }
        } catch (error) {
          console.warn(error);
          ElMessage({
            message: this.$t('topo.components.chart.073848-10'),
            type: 'warning',
          });
        }
      });
      return this.detail.dataBind.mapAddress;
    },
  },
  watch: {
    'detail.style.position.w'() {
      this.onResize();
    },
    'detail.style.position.h'() {
      this.onResize();
    },
    'detail.dataBind.echartData'(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.refreshChartByData();
      }
    },
    'detail.dataBind.httpFilter'() {
      this.refreshChartByData();
    },
    echartRun(newColor, oldColor) {
      // console.log('',newColor);
    },
    mapChange() {},
  },
  mounted() {
    const { httpSetting, mapAddress, mapUrl } = this.detail.dataBind;
    const { url, time, unit } = httpSetting || {};
    if (!this.editMode && url) {
      let second = 60 * 1000;
      if (time) {
        second = getSecond(time, unit);
      }
      this.getEchartData();
      this.timer = setInterval(() => {
        this.getEchartData();
      }, second);
    } else {
      if (mapAddress == this.$t('topo.components.chart.073848-8')) {
        this.getMapJson(mapUrl);
      } else {
        this.initEcharts();
      }
    }
  },
  methods: {
    loadData(option, mapJson) {
      if (this.echart) {
        this.echart.dispose();
        this.echart = null;
      }
      switch (this.detail.dataBind.mapAddress) {
        case '安徽':
          mapJson = anhuiJson;
          break;
        case '澳门':
          mapJson = aomenJson;
          break;
        case '北京':
          mapJson = beijingJson;
          break;
        case '重庆':
          mapJson = chongqingJson;
          break;
        case '福建':
          mapJson = fujianJson;
          break;
        case '甘肃':
          mapJson = gansuJson;
          break;
        case '广东':
          mapJson = guangdongJson;
          break;
        case '广西':
          mapJson = guangxiJson;
          break;
        case '贵州':
          mapJson = guizhouJson;
          break;
        case '海南':
          mapJson = hainanJson;
          break;
        case '河北':
          mapJson = hebeiJson;
          break;
        case '黑龙江':
          mapJson = heilongjiangJson;
          break;
        case '河南':
          mapJson = henanJson;
          break;
        case '湖北':
          mapJson = hubeiJson;
          break;
        case '湖南':
          mapJson = hunanJson;
          break;
        case '江苏':
          mapJson = jiangsuJson;
          break;
        case '江西':
          mapJson = jiangxiJson;
          break;
        case '吉林':
          mapJson = jilinJson;
          break;
        case '辽宁':
          mapJson = liaoningJson;
          break;
        case '内蒙古':
          mapJson = neimengguJson;
          break;
        case '宁夏':
          mapJson = ningxiaJson;
          break;
        case '青海':
          mapJson = qinghaiJson;
          break;
        case '山东':
          mapJson = shandongJson;
          break;
        case '上海':
          mapJson = shanghaiJson;
          break;
        case '山西':
          mapJson = shanxiJson;
          break;
        case '四川':
          mapJson = sichuanJson;
          break;
        case '台湾':
          mapJson = taiwanJson;
          break;
        case '天津':
          mapJson = tianjinJson;
          break;
        case '香港':
          mapJson = xianggangJson;
          break;
        case '新疆':
          mapJson = xinjiangJson;
          break;
        case '西藏':
          mapJson = xizangJson;
          break;
        case '云南':
          mapJson = yunnanJson;
          break;
        case '浙江':
          mapJson = zhejiangJson;
          break;
        case '自定义':
          mapJson = this.mapJsons;
          break;
        default:
          mapJson = henanJson;
          break;
      }
      if (!option || typeof option !== 'object' || !mapJson) return;
      this.currentOption = option;
      this.currentMapJson = mapJson;
      this.$echarts.registerMap('mapJson', mapJson);
      let view = this.$refs.xwin;
      if (!view) return;
      const { width, height } = this.getChartSize();
      if (this.echart) {
        this.echart.dispose();
        this.echart = null;
      }
      this.echart = this.$echarts.init(view, undefined, {
        width,
        height,
      });
      try {
        this.echart.clear();
        this.echart.setOption(this.normalizeMapOption(option), true);
        this.echart.resize({ width, height });
      } catch (err) {
        console.warn('ViewChartMap setOption failed', err, option);
      }
    },
    getChartSize() {
      const view = this.$refs.xwin || document.getElementById(this.detail.identifier);
      return {
        width: Number(this.detail.style.position.w) || view?.clientWidth || 0,
        height: Number(this.detail.style.position.h) || view?.clientHeight || 0,
      };
    },
    onResize() {
      if (!this.echart) return;
      this.$nextTick(() => {
        try {
          const view = this.$refs.xwin || document.getElementById(this.detail.identifier);
          const { width, height } = this.getChartSize();
          if (!view || !width || !height) return;
          view.style.width = `${width}px`;
          view.style.height = `${height}px`;
          const sizeKey = `${width}x${height}`;
          const sizeChanged = this.lastChartSize !== sizeKey;
          this.lastChartSize = sizeKey;
          if (sizeChanged) {
            this.rebuildChart(width, height);
            return;
          }
          this.echart.resize({ width, height });
        } catch (err) {
          console.warn('ViewChartMap resize skipped', err);
        }
      });
    },
    rebuildChart(width, height) {
      const view = this.$refs.xwin || document.getElementById(this.detail.identifier);
      if (!view || !this.$echarts || !this.currentOption || !this.currentMapJson) return;
      try {
        this.echart?.dispose?.();
        view.removeAttribute('_echarts_instance_');
        view.innerHTML = '';
        this.$echarts.registerMap('mapJson', this.currentMapJson);
        this.echart = this.$echarts.init(view, undefined, {
          width,
          height,
        });
        this.echart.setOption(this.normalizeMapOption(this.currentOption), true);
        this.echart.resize({ width, height });
      } catch (err) {
        console.warn('ViewChartMap rebuild skipped', err);
      }
    },
    normalizeMapOption(option) {
      const safeOption = {
        ...(option || {}),
        series: Array.isArray(option?.series) ? option.series : [],
      };
      safeOption.series = safeOption.series
        .filter((item) => item && typeof item === 'object' && typeof item.type === 'string')
        .map((item) => {
          if (item.type !== 'map') return item;
          return {
            ...item,
            map: item.map || 'mapJson',
            layoutCenter: item.layoutCenter || ['50%', '52%'],
            layoutSize: item.layoutSize || '88%',
          };
        });
      return safeOption;
    },
    refreshChartByData() {
      this.$nextTick(() => {
        const { mapAddress, mapUrl } = this.detail.dataBind || {};
        if (mapAddress == this.$t('topo.components.chart.073848-8') && mapUrl && !this.currentMapJson) {
          this.getMapJson(mapUrl);
          return;
        }
        this.initEcharts();
      });
    },
    // https://www.isqqw.com/asset/get/areas_v3/country/china.json
    getMapJson(mapUrl) {
      request
        .get(mapUrl)
        .then((res) => {
          this.mapJsons = (res as any).data;
          return this.initEcharts();
        })
        .catch((err) => {
          ElMessage({
            message: this.$t('topo.components.chart.073848-13'),
            type: 'warning',
          });
        });
    },
    initEcharts() {
      let { echartOption } = this.detail.dataBind;
      if (!echartOption) {
        this.detail.dataBind.echartOption = chartOption.getOptionMap();
      }
      this.setEchartOption();
      return true;
    },
    getEchartData() {
      const { httpSetting } = this.detail.dataBind;
      const { url, type, headers, params, body } = httpSetting;
      request({
        url: url,
        method: type,
        headers: headers,
        params: params,
        data: body,
      }).then((res) => {
        const nextData = JSON.stringify((res as any).data);
        if (this.detail.dataBind.echartData === nextData) {
          if (!this.echart) {
            this.refreshChartByData();
          }
          return;
        }
        this.detail.dataBind.echartData = nextData;
      });
    },
    // 设置 option
    setEchartOption() {
      let { echartOption, echartData, httpFilter } = this.detail.dataBind;
      try {
        const fun = this.createOptionRunner(echartOption);
        let opData = {};
        if (echartData) {
          opData = JSON.parse(echartData);
          if (httpFilter) {
            const filtFunStr = 'function () {\n' + 'const res = opData;' + '\n' + httpFilter + '\n' + '}';
            let filtFun = eval('(' + filtFunStr + ')');
            opData = filtFun();
          }
        }
        const chartOptionResult = fun(this.$echarts, opData);
        this.loadData(chartOptionResult);
        this.onResize();
      } catch (err) {
        console.warn('setEchartOption failed', err);
      }
    },
    createOptionRunner(echartOption) {
      return new Function('echarts', 'echartData', `${echartOption || ''}\n;return option;`);
    },
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
    if (this.echart) {
      this.echart.dispose();
      this.echart = null;
    }
  },
};
</script>

<style lang="scss">
.view-chart-map {
  box-sizing: border-box;
  height: 100%;
  width: 100%;
  padding: 10px;
}
</style>
