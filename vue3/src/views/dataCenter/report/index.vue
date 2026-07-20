<template>
  <div class="report-container">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('dataCenter.report.451245-2')" name="report">
          <report-list ref="reportListRef"></report-list>
        </el-tab-pane>
        <el-tab-pane :label="$t('dataCenter.report.451245-3')" name="download">
          <report-down-list ref="reportDownListRef" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue';
import reportList from './reportList.vue';
import reportDownList from './reportDownList.vue';

const activeTab = ref('report');
const reportListRef = ref<any>(null);
const reportDownListRef = ref<any>(null);
const { proxy } = getCurrentInstance() as any;

function handleExport() {
  proxy?.download('iot/report/export', {}, `report_${new Date().getTime()}.xlsx`);
}
</script>

<style scoped lang="scss">
.report-container {
  padding: 20px;
}
</style>
