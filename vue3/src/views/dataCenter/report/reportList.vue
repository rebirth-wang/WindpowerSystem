<template>
  <div class="report-list">
    <div v-show="showSearch" class="top">
      <el-form ref="queryRef" :model="queryParams">
        <div class="query-form">
          <div class="query-form-left">
            <el-form-item prop="dataType">
              <el-select
                v-model="queryParams.dataType"
                :placeholder="$t('product.product-things-model.142341-17')"
                clearable
                style="width: 192px"
              >
                <el-option v-for="dict in report_data_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item prop="dataDimension">
              <el-select
                v-model="queryParams.dataDimension"
                :placeholder="$t('dataCenter.report.451245-6')"
                clearable
                style="width: 192px"
              >
                <el-option v-for="dict in data_dimension" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item prop="name">
              <el-input
                v-model="queryParams.name"
                :placeholder="$t('dataCenter.report.451245-1')"
                clearable
                @keyup.enter="handleQuery"
                style="width: 192px"
              />
            </el-form-item>
          </div>
          <div class="query-form-right">
            <el-button type="primary" :icon="Search" @click="handleQuery" v-hasPermi="['dataCenter:report:query']">
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['dataCenter:report:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['dataCenter:report:remove']"
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
          min-width="150"
          prop="name"
        ></el-table-column>
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
        <el-table-column :label="$t('dataCenter.report.451245-7')" align="center" min-width="120" prop="cycleType">
          <template #default="scope">
            <dict-tag :options="report_time_period" :value="scope.row.cycleType"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('dataCenter.report.451245-6')" align="center" min-width="100" prop="dataDimension">
          <template #default="scope">
            <dict-tag :options="data_dimension" :value="scope.row.dataDimension"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('updateTime')" align="center" min-width="100" prop="updateTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.notice.670989-8')" align="center" prop="createBy"></el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="150">
          <template #default="scope">
            <el-button
              size="small"
              :icon="Edit"
              link
              @click="handleUpdate(scope.row)"
              v-hasPermi="['dataCenter:report:edit']"
            >
              {{ $t('edit') }}
            </el-button>
            <el-button
              size="small"
              :icon="Delete"
              link
              @click="handleDelete(scope.row)"
              v-hasPermi="['dataCenter:report:remove']"
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
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue';
import { listReport, delReport } from '@/api/data/report';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';

const { report_data_type, report_time_period, data_dimension } = useDict(
  'report_data_type',
  'report_time_period',
  'data_dimension'
);
const { proxy } = getCurrentInstance() as any;
const router = useRouter();

const queryParams = reactive<any>({ pageSize: 10, pageNum: 1 });
const reportList = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const multiple = ref(true);
const ids = ref<any[]>([]);
const showSearch = ref(true);
const multipleTableRef = ref<any>(null);

function getList() {
  loading.value = true;
  listReport(queryParams).then((response: any) => {
    reportList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function handleResetQuery() {
  Object.assign(queryParams, {
    pageSize: 10,
    pageNum: 1,
    dataType: undefined,
    dataDimension: undefined,
    name: undefined,
  });
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

function handleDelete(row?: any) {
  const delIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('dataCenter.report.451245-4', [delIds]))
    .then(() => {
      return delReport(delIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('iot.group.index.637432-27'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleAdd() {
  router.push({ path: '/dataCenter/report/edit' });
}
function handleUpdate(row: any) {
  router.push({ path: '/dataCenter/report/edit', query: { id: row.id } });
}
function getRowKeys(row: any) {
  return row.id;
}

function handleExport() {
  proxy?.download('iot/report/export', { ...queryParams }, `report_${new Date().getTime()}.xlsx`);
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
