<template>
  <div class="report-list">
    <div v-show="showSearch" class="top">
      <el-form ref="queryFormRef" :model="queryParams">
        <div class="query-form">
          <div class="query-form-left">
            <el-form-item prop="dataType">
              <el-select
                v-model="queryParams.dataType"
                :placeholder="$t('product.product-select-template.318012-12')"
                clearable
                style="width: 192px"
              >
                <el-option v-for="dict in report_data_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item prop="reportName">
              <el-input
                v-model="queryParams.reportName"
                :placeholder="$t('dataCenter.report.451245-1')"
                style="width: 192px"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>
          </div>
          <div class="query-form-right">
            <el-button
              type="primary"
              :icon="Search"
              @click="handleQuery"
              v-hasPermi="['dataCenter:reportRecords:list']"
            >
              {{ $t('search') }}
            </el-button>
            <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
          </div>
        </div>
      </el-form>
    </div>
    <div class="container">
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['dataCenter:records:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
      <el-table
        v-loading="loading"
        :data="reportList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column
          :label="$t('dataCenter.report.451245-1')"
          align="left"
          prop="reportName"
          min-width="140"
        ></el-table-column>
        <el-table-column
          :show-overflow-tooltip="true"
          :label="$t('dataCenter.report.451245-30')"
          align="center"
          prop="reportFilePath"
          min-width="150"
        ></el-table-column>
        <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="state" min-width="100">
          <template #default="scope">
            <dict-tag :options="report_status" :value="scope.row.state"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.product-select-template.318012-12')"
          align="center"
          min-width="100"
          prop="dataType"
        >
          <template #default="scope">
            <dict-tag :options="report_data_type" :value="scope.row.dataType"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column
          :show-overflow-tooltip="true"
          :label="$t('device.device-modbus-task.384302-19')"
          align="center"
          prop="timeCycle"
          min-width="120"
        ></el-table-column>
        <el-table-column :label="$t('updateTime')" align="center" prop="updateTime" min-width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" min-width="150">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Download"
              @click="handleExport(scope.row)"
              v-hasPermi="['dataCenter:records:download']"
            >
              {{ $t('down') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['dataCenter:records:remove']"
            >
              {{ $t('del') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue';
import { listRecords, delRecords } from '@/api/data/records';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;
const { default_dict_type, report_status, report_data_type } = useDict(
  'default_dict_type',
  'report_status',
  'report_data_type'
);

const queryFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);
const queryParams = reactive<any>({ pageSize: 10, pageNum: 1 });
const reportList = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const multiple = ref(true);
const ids = ref<any[]>([]);
const showSearch = ref(true);

/** 获取列表 */
function getList() {
  loading.value = true;
  listRecords(queryParams).then((response: any) => {
    reportList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置 */
function handleResetQuery() {
  queryFormRef.value?.resetFields();
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  handleQuery();
}

/** 多选 */
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

/** 删除 */
function handleDelete(row?: any) {
  const deleteIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('dataCenter.report.451245-10', [deleteIds]))
    .then(() => {
      return delRecords(deleteIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('iot.group.index.637432-27'));
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.id;
}

/** 导出 */
function handleExport(row: any) {
  proxy.$download.resource(row.reportFilePath);
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.report-list {
  .top {
    .query-form {
      display: flex;
      width: 100%;
      justify-content: space-between;
      .query-form-left {
        display: flex;
        justify-content: space-between;
        .el-form-item {
          margin-right: 10px;
        }
      }
      .query-form-right {
        float: right;
      }
    }
  }
}
</style>
