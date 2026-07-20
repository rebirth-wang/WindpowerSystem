<template>
  <div
    :key="key"
    v-loading="loading"
    :element-loading-text="$t('sip.splitview.998531-0')"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.4)"
  >
    <dv-scroll-board
      :config="{
        rowNum: this.detail.style.rowNum,
        data: realData,
        header: this.detail.style.header,
        headerBGC: this.detail.style.headerBGC,
        oddRowBGC: this.detail.style.oddRowBGC,
        evenRowBGC: this.detail.style.evenRowBGC,
        waitTime: this.detail.style.waitTime,
        headerHeight: this.detail.style.headerHeight,
        columnWidth: this.detail.style.columnWidth.split(','),
        align: [],
        index: this.detail.style.index,
        indexHeader: this.detail.style.indexHeader,
        carousel: this.detail.style.carousel,
      }"
      :style="{
        width: detail.style.position.w + 'px',
        height: detail.style.position.h + 'px',
        color: detail.style.foreColor,
      }"
      v-if="this.detail.style.header"
    />
    <div
      v-else
      :style="{
        width: this.detail.style.position.w + 'px',
        height: this.detail.style.position.h + 'px',
        'text-align': 'center',
        'line-height': this.detail.style.position.h + 'px',
        'font-size': '30px',
        color: '#368a42',
      }"
    >
      请绑定变量
    </div>
  </div>
</template>

<script lang="ts">
import { useRoute } from 'vue-router';
import request from '@/utils/request';
import BaseView from '../View.vue';
import { getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'view-real-data',
  extends: BaseView,
  setup() {
    const route = useRoute();
    return { route };
  },
  computed: {
    headerChange() {
      return this.detail.style.header;
    },
  },
  watch: {
    headerChange(newVal, oldVal) {
      // console.log("绑定数据变化了",newVal);
      if (oldVal) {
        this.getList();
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
        columnWidth: [60], //列宽度
        align: ['center'], //对齐方式
        index: true, //行号
        indexHeader: '序号', //行号表头
        carousel: 'single', //轮播方式'single'|'page'
        hoverPause: true, //悬停停止轮播
      },
      header: [],
      timer: null,
      key: 0,
      realData: [],
      loading: false,
    };
  },
  mounted() {
    this.detail.style.data = [];
    this.getList();
    this.timer = setInterval(() => {
      this.getList();
    }, 60000);
  },
  methods: {
    //格式化时间
    leaveTime(value) {
      return moment(value).format('HH:mm');
    },
    getList() {
      let query = {
        ztGuid: getRouteQueryString(this.route.query, 'guid'),
        // deviceImei:'863763059649573',
        headListStr: this.headerChange.join(';'),
      };
      //this.loading=true;
      let url = 'prod-api/ghxx/bDeviceHistory/getRealDataList';
      request({
        url: url,
        method: 'get',
        params: query,
      }).then((res) => {
        // console.log("实时数据",res.data);
        let sdata = [];
        res.data.data.tableList.forEach((element) => {
          let data = [];
          element.forEach((ele, index) => {
            if (index == 0) {
              ele = this.leaveTime(ele);
            }
            data.push(ele);
          });
          sdata.push(data);
        });
        this.realData = sdata;
        this.key = new Date().getTime();
        //this.loading=false;
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
