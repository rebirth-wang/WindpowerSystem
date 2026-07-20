<template>
  <div :id="detail.identifier">
    <dv-scroll-board
      :config="{
        rowNum: detail.style.rowNum,
        data: detail.style.data || [],
        header: detail.style.header || header,
        headerBGC: detail.style.headerBGC,
        oddRowBGC: detail.style.oddRowBGC,
        evenRowBGC: detail.style.evenRowBGC,
        waitTime: detail.style.waitTime,
        headerHeight: detail.style.headerHeight,
        columnWidth: (detail.style.columnWidth || '').split(',').filter(Boolean),
        align: detail.style.align,
        index: detail.style.index,
        indexHeader: detail.style.indexHeader,
        carousel: detail.style.carousel,
      }"
      :style="{
        width: detail.style.position.w + 'px',
        height: detail.style.position.h + 'px',
        color: detail.style.foreColor,
      }"
    />
    <div v-show="false">{{ dataInit }}</div>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';
import { useUserStore } from '@/stores/modules/user';
import { useRoute } from 'vue-router';
import { listAlertLog } from '@/api/iot/alertLog';
import { getPageAlertLog } from '@/api/scada/topo';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewWarn',
  dicts: ['iot_alert_level', 'iot_process_status'],
  extends: BaseView,
  setup() {
    const route = useRoute();
    return { route };
  },
  computed: {
    dataInit() {
      const { warnLogType } = this.detail.dataBind || {};
      this.timer = null;
      if (warnLogType === 1) {
        this.getALLAlertLogList();
        if (!this.editMode) {
          this.timer = setInterval(() => {
            this.getALLAlertLogList();
          }, 60000 * 5);
        }
      } else {
        this.getPageAlertLogList();
        if (!this.editMode) {
          this.timer = setInterval(() => {
            this.getPageAlertLogList();
          }, 60000 * 5);
        }
      }
    },
  },
  data() {
    return {
      classStyle: null,
      config: {
        header: [], //表头数据
        data: [], //表数据
        rowNum: 10, //表行数
        headerBGC: '#00BAFF', //表头背景色
        oddRowBGC: '#003B51', //奇数行背景色
        evenRowBGC: '#0A2732', //偶数行背景色
        waitTime: 2000, //轮播时间间隔
        headerHeight: 35, //表头高度
        columnWidth: [], //列宽度
        align: ['center'], //对齐方式
        index: true, //行号
        indexHeader: '序号', //行号表头
        carousel: 'single', //轮播方式'single'|'page'
        hoverPause: true, //悬停停止轮播
      },
      header: ['告警时间', '告警名称', '设备名称', '告警级别', '处理状态'],
      timer: null,
    };
  },
  mounted() {},
  methods: {
    // 所有设备
    getALLAlertLogList() {
      const { dept } = useUserStore();
      let params = {
        deptUserId: (dept as any).deptUserId,
        pageNum: 1,
        pageSize: 9999,
      };
      listAlertLog(params).then((res) => {
        if (res.code === 200) {
          const dictType = (this as any).dict?.type || {};
          const alertLevelDict = dictType.iot_alert_level || [];
          const processStatusDict = dictType.iot_process_status || [];
          const rows = Array.isArray(res.rows) ? res.rows : [];
          const data: any[] = [];
          rows.forEach((item: any) => {
            const mdata = [
              item.createTime,
              item.alertName,
              item.deviceName,
              this.getSpecifiedElement(alertLevelDict, item.alertLevel),
              this.getSpecifiedElement(processStatusDict, item.status),
            ];
            data.push(mdata);
          });
          this.detail.style.data = data;
        }
      });
    },
    getSpecifiedElement(dict, val) {
      const list = Array.isArray(dict) ? dict : [];
      let obj = list.find((item) => item.value == val);
      if (obj && (obj.raw?.listClass == 'default' || !obj.raw?.listClass)) {
        return obj.label;
      } else if (obj) {
        return `<span style="color:${this.getColor(obj.raw?.listClass)};">${obj.label}</span>`;
      }
      return val;
    },
    getColor(type) {
      switch (type) {
        case 'primary':
          return '#1890ff';
        case 'success':
          return '#13ce66';
        case 'warning':
          return '#ffba00';
        case 'danger':
          return '#ff4949';
      }
    },
    // 当前设备
    getPageAlertLogList() {
      const type = getScadaRouteType(this.route.query);
      let params = {
        guid: getRouteQueryString(this.route.query, 'guid'),
        scadaType: type,
        sceneModelId: getRouteQueryString(this.route.query, 'sceneModelId'),
        serialNumber: getRouteQueryString(this.route.query, 'serialNumber'),
      };
      getPageAlertLog(params).then((res) => {
        if (res.code === 200) {
          const dictType = (this as any).dict?.type || {};
          const alertLevelDict = dictType.iot_alert_level || [];
          const processStatusDict = dictType.iot_process_status || [];
          const rows = Array.isArray(res.rows) ? res.rows : [];
          const data: any[] = [];
          rows.forEach((item: any) => {
            const mdata = [
              item.createTime,
              item.alertName,
              item.deviceName,
              this.getSpecifiedElement(alertLevelDict, item.alertLevel),
              this.getSpecifiedElement(processStatusDict, item.status),
            ];
            data.push(mdata);
          });
          this.detail.style.data = data;
        }
      });
    },
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
  },
};
</script>

<style lang="scss" scoped></style>
