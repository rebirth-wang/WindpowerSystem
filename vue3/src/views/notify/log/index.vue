<template>
  <div class="notify-log">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="78px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="channelId">
          <el-input
            v-model="queryParams.channelId"
            :placeholder="$t('notify.log.333543-1')"
            clearable
            type="number"
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="notifyTemplateId">
          <el-input
            v-model="queryParams.notifyTemplateId"
            :placeholder="$t('notify.log.333543-3')"
            clearable
            type="number"
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="sendAccount">
          <el-input
            v-model="queryParams.sendAccount"
            :placeholder="$t('notify.log.333543-5')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <template v-if="searchShow">
          <el-form-item prop="serviceCode">
            <el-select
              v-model="queryParams.serviceCode"
              :placeholder="$t('notify.log.333543-7')"
              clearable
              style="width: 192px; display: inline-block"
            >
              <el-option
                v-for="dict in notify_service_code"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </template>
        <template v-if="searchShow">
          <el-form-item>
            <el-date-picker
              v-model="dateRange"
              style="width: 240px"
              value-format="YYYY-MM-DD"
              type="daterange"
              range-separator="-"
              :start-placeholder="$t('notify.log.333543-9')"
              :end-placeholder="$t('notify.log.333543-10')"
              :disabled-date="disabledDate"
            />
          </el-form-item>
        </template>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span class="search-toggle-text" style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon class="search-toggle-icon">
              <ArrowUp v-if="searchShow" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['notify:log:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="logList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('notify.log.333543-0')" align="center" prop="channelId" width="90" />
        <el-table-column :label="$t('notify.log.333543-11')" align="center" prop="channelName" min-width="150" />
        <el-table-column :label="$t('notify.log.333543-2')" align="left" prop="notifyTemplateId" width="160" />
        <el-table-column :label="$t('notify.log.333543-12')" align="left" prop="templateName" min-width="180" />
        <el-table-column :label="$t('notify.log.333543-6')" align="center" prop="serviceCode" width="110">
          <template #default="scope">
            <dict-tag :options="notify_service_code" :value="scope.row.serviceCode" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('notify.log.333543-4')" align="left" prop="sendAccount" min-width="260" />
        <el-table-column :label="$t('notify.log.333543-13')" align="center" prop="sendStatus" width="100">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.sendStatus == 0">{{ $t('notify.log.333543-14') }}</el-tag>
            <el-tag type="success" v-if="scope.row.sendStatus == 1">{{ $t('notify.log.333543-15') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('notify.log.333543-16')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="125"
        >
          <template #default="scope">
            <el-button size="small" link :icon="View" @click="handleView(scope.row)" v-hasPermi="['notify:log:query']">
              {{ $t('detail') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['notify:log:remove']"
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
    </el-card>

    <!-- 通知日志详情对话框 -->
    <el-dialog :title="title" v-model="dialogOpen" width="600px" append-to-body>
      <div class="dialog-wrap">
        <el-form ref="formRef" :model="form" label-width="100px" disabled>
          <el-form-item :label="$t('notify.log.333543-4')" prop="sendAccount">
            <el-input v-model="form.sendAccount" :placeholder="$t('notify.log.333543-5')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('notify.log.333543-17')">
            <el-input
              type="textarea"
              :autosize="{ minRows: 5, maxRows: 6 }"
              :placeholder="$t('notify.log.333543-18')"
              v-model="form.resultContent"
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('notify.log.333543-19')">
            <editor v-model="form.msgContent" :min-height="192" :readOnly="true" style="width: 400px" />
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Delete, ArrowUp, ArrowDown, View } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';
import { listLog, getLog, delLog } from '@/api/notify/log';

const { proxy } = getCurrentInstance() as any;
const { notify_service_code } = useDict('notify_service_code');

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const logList = ref<any[]>([]);
const title = ref('');
const dialogOpen = ref(false);
const dateRange = ref<any[]>([]);
const form = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  notifyTemplateId: null as any,
  channelId: null as any,
  msgContent: null as any,
  sendAccount: null as any,
  sendStatus: null as any,
  resultContent: null as any,
  dateRange: '',
  serviceCode: null as any,
});

function disabledDate(time: Date) {
  return time.getTime() > Date.now();
}

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listLog(proxy?.addDateRange(queryParams, dateRange.value)).then((response: any) => {
    logList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function reset() {
  form.value = {
    id: null,
    notifyTemplateId: null,
    channelId: null,
    msgType: null,
    msgContent: null,
    sendAccount: null,
    sendStatus: null,
    resultContent: null,
    createTime: null,
    createBy: null,
    updateBy: null,
    updateTime: null,
    delFlag: null,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  queryFormRef.value?.resetFields();
  dateRange.value = [];
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleView(row: any) {
  reset();
  const id = row.id || ids.value;
  getLog(id).then((response: any) => {
    if (response.code === 200) {
      form.value = response.data;
      dialogOpen.value = true;
      title.value = proxy?.$t('notify.log.333543-21');
    }
  });
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('notify.log.333543-22', [delIds]))
    .then(() => {
      return delLog(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function searchChange() {
  searchShow.value = !searchShow.value;
}
function getRowKeys(row: any) {
  return row.id;
}

function handleAdd() {
  reset();
  dialogOpen.value = true;
  title.value = proxy?.$t('notify.log.333543-20');
}

function handleExport() {
  proxy?.download('notify/log/export', { ...queryParams }, `log_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.notify-log {
  padding: 20px;
}
.search-toggle-text {
  color: var(--el-color-primary);
  margin-left: 14px;
}
.search-toggle-icon {
  color: var(--el-color-primary);
  margin-left: 10px;
}
.dialog-wrap :deep(.el-input.is-disabled .el-input__inner) {
  background-color: #fff;
  color: #000000 !important;
}
.dialog-wrap :deep(textarea:disabled) {
  color: #000000 !important;
  background-color: #fff !important;
  font-size: 16px;
}
</style>
