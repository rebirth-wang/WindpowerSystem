<template>
  <div :id="detail.identifier">
    <dv-scroll-board
      :config="{
        rowNum: detail.style.rowNum,
        data: detail.style.data || [],
        header: detail.style.header || [],
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
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';
import { useUserStore } from '@/stores/modules/user';
import { listAlertLog } from '@/api/iot/alertLog';

export default {
  name: 'ViewOrder',
  dicts: ['iot_alert_level'],
  extends: BaseView,
  computed: {},
  data() {
    return {
      timer: null,
    };
  },
  mounted() {
    this.getList();
    if (!this.editMode) {
      this.timer = setInterval(() => {
        this.getList();
      }, 60000 * 5);
    }
  },
  methods: {
    getList() {
      const { dept } = useUserStore();
      let params = {
        deptUserId: (dept as any).deptUserId,
        status: 3,
        pageNum: 1,
        pageSize: 9999,
      };
      listAlertLog(params).then((res) => {
        if (res.code === 200) {
          const dictType = (this as any).dict?.type || {};
          const alertLevelDict = dictType.iot_alert_level || [];
          const rows = Array.isArray(res.rows) ? res.rows : [];
          const data: any[] = [];
          rows.forEach((item: any) => {
            const mdata = [
              item.createTime,
              item.alertName,
              item.deviceName,
              this.getSpecifiedElement(alertLevelDict, item.alertLevel),
              item.remark,
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
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
  },
};
</script>

<style lang="scss" scoped></style>
