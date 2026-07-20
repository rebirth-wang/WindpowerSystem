<template>
  <div
    :style="{
      width: detail.style.position.w + 'px',
      height: detail.style.position.h + 'px',
      background: this.detail.style.backColor,
    }"
    class="tableClass"
  >
    <div>
      <el-row :gutter="10" class="mb8">
        <el-col :span="6">
          <el-select
            v-model="queryParams.paramName"
            :placeholder="$t('scada.topo.unit.viewHistory.764059-0')"
            size="small"
            style="width: auto"
            clearable
            filterable
          >
            <el-option
              v-for="realData in realDataOptions"
              :key="realData.id"
              :label="realData.paramName"
              :value="realData.paramName"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="14">
          <el-date-picker
            v-model="dateRange"
            size="small"
            :style="{ width: '340px', background: this.detail.style.backColor, color: this.detail.style.foreColor }"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetimerange"
            range-separator="-"
            :start-placeholder="$t('scada.topo.unit.viewHistory.764059-1')"
            :end-placeholder="$t('scada.topo.unit.viewHistory.764059-2')"
          ></el-date-picker>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">{{ $t('search') }}</el-button>
        </el-col>
      </el-row>

      <el-table
        v-loading="loading"
        :data="historyList"
        :cell-style="{ background: this.detail.style.backColor, color: this.detail.style.foreColor }"
        :row-style="{ background: this.detail.style.backColor, color: this.detail.style.foreColor }"
        :header-cell-style="{ background: this.detail.style.backColor, color: this.detail.style.foreColor }"
      >
        <el-table-column :label="$t('scada.topo.unit.viewHistory.764059-3')" align="center" prop="reportTime" width="180" />
        <el-table-column :label="$t('scada.topo.unit.viewHistory.764059-4')" align="center" prop="paramName" />
        <el-table-column :label="$t('scada.topo.unit.viewHistory.764059-5')" align="center" prop="paramValue" />
      </el-table>
    </div>
    <pagination
      :style="{ background: this.detail.style.backColor, color: this.detail.style.foreColor }"
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
      :page-sizes="[5, 10, 15]"
      :page-size="5"
    />
  </div>
</template>

<script lang="ts">
import { useRoute } from 'vue-router';
import request from '@/utils/request';
import BaseView from '../View.vue';
import { getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'History',
  extends: BaseView,
  setup() {
    const route = useRoute();
    return { route };
  },
  data() {
    return {
      loading: true,
      total: 0,
      historyList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        deviceImei: null,
        orderByColumn: 'id',
        isAsc: 'desc',
        paramName: '',
        beginTime: null,
        endTime: null,
        ztGuid: '',
      },
      dateRange: [],
      realDataOptions: [],
    };
  },
  mounted() {
    this.queryParams.ztGuid = getRouteQueryString(this.route.query, 'guid');
  },
  methods: {
    getRealList() {
      let queryParams = {
        ztGuid: getRouteQueryString(this.route.query, 'guid'),
        pageNum: 1,
        pageSize: 999,
        orderByColumn: 'id',
        isAsc: 'desc',
      };
      let url = 'prod-api/ghxx/bDeviceRealData/list';
      request({
        url: url,
        method: 'get',
        params: queryParams,
      }).then((res) => {
        this.realDataOptions = res.data.rows;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      if (this.dateRange && this.dateRange.length == 2) {
        this.queryParams.beginTime = this.dateRange[0];
        this.queryParams.endTime = this.dateRange[1];
      } else {
        this.queryParams.beginTime = null;
        this.queryParams.endTime = null;
      }
      this.getList();
    },
    getList() {
      this.loading = true;
      let url = 'prod-api/ghxx/bDeviceHistoryData/list';
      request({
        url: url,
        method: 'get',
        params: this.queryParams,
      }).then((res) => {
        this.historyList = res.data.rows;
        this.total = res.data.total;
        this.loading = false;
      });
    },
  },
};
</script>
<style scoped>
.tableClass {
  overflow-y: auto;
  overflow-x: hidden;
  padding: 10px;
}

.el-input {
  background-color: brown !important;
  color: #fff !important;
}
</style>
