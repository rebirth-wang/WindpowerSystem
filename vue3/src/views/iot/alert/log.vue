<template>
  <div class="iot-alert-log">
    <el-card v-show="showSearch" class="search-card">
      <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item prop="alertName">
          <el-input
            v-model="queryParams.alertName"
            :placeholder="$t('alert.log.491272-1')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="alertLevel">
          <el-select
            v-model="queryParams.alertLevel"
            :placeholder="$t('alert.index.236501-3')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in iot_alert_level" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('alert.log.491272-5')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in iot_process_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:alert:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:alert:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="alertLogList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('alert.index.236501-0')" align="left" prop="alertName" min-width="200" />
        <el-table-column :label="$t('alert.log.491272-8')" align="left" prop="serialNumber" width="150" />
        <el-table-column :label="$t('alert.log.491272-9')" align="left" prop="deviceName" min-width="200" />
        <el-table-column :label="$t('alert.index.236501-2')" align="center" prop="alertLevel" width="110">
          <template #default="scope">
            <dict-tag :options="iot_alert_level" :value="scope.row.alertLevel" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('alert.log.491272-10')" align="center" prop="createTime" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('alert.log.491272-24')"
          align="left"
          header-align="center"
          prop="detail"
          width="150"
        >
          <template #default="scope">
            <div v-html="formatDetail(scope.row.detail)"></div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('alert.log.491272-4')" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="iot_process_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="190">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:alertLog:edit']"
              v-if="scope.row.status === 2"
            >
              {{ $t('alert.log.491272-25') }}
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

    <!-- 修改设备告警对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item :label="$t('alert.log.491272-27')" prop="statusType">
          <el-radio-group v-model="statusType">
            <el-radio :value="1">{{ $t('alert.log.491272-28') }}</el-radio>
            <el-radio :value="3">{{ $t('alert.log.491272-29') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('alert.log.491272-26')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('notify.log.333543-18')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 350px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
        <el-button @click="cancel">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { Search, Refresh, Delete, Download, Edit } from '@element-plus/icons-vue';
import { listAlertLog, getAlertLog, delAlertLog, updateAlertLog } from '@/api/iot/alertLog';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { iot_alert_level, iot_process_status } = useDict('iot_alert_level', 'iot_process_status');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const alertLogList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const statusType = ref(1);

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  alertName: null as any,
  alertLevel: null as any,
  status: null as any,
  productId: null as any,
  productName: null as any,
  deviceId: null as any,
  deviceName: null as any,
});

const form = ref<any>({});

/** 查询列表 */
const getList = () => {
  loading.value = true;
  listAlertLog(queryParams).then((response: any) => {
    alertLogList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
};

/** 取消 */
const cancel = () => {
  open.value = false;
  reset();
};

/** 重置 */
const reset = () => {
  form.value = {
    alertLogId: null,
    alertName: null,
    alertLevel: null,
    status: null,
    productId: null,
    productName: null,
    deviceId: null,
    deviceName: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
  proxy?.resetForm('formRef');
};

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置搜索 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选 */
const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map((item: any) => item.alertLogId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const getRowKeys = (row: any) => row.alertLogId;

/** 修改 */
const handleUpdate = (row: any) => {
  reset();
  const alertLogId = row.alertLogId || ids.value;
  getAlertLog(alertLogId).then((response: any) => {
    form.value = response.data;
    if (form.value.status === 2) {
      statusType.value = 1;
    }
    open.value = true;
    title.value = proxy?.$t('alert.log.491272-20');
  });
};

/** 提交 */
const submitForm = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.alertLogId != null) {
        const params = {
          ...form.value,
          status: statusType.value,
        };
        updateAlertLog(params).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 删除 */
const handleDelete = (row: any) => {
  const alertLogIds = row.alertLogId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('alert.log.491272-23', [alertLogIds]))
    .then(() => {
      return delAlertLog(alertLogIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
};

/** 导出 */
const handleExport = () => {
  proxy?.download('iot/alertLog/export', { ...queryParams }, `alertLog_${new Date().getTime()}.xlsx`);
};

/** 格式化显示物模型 */
const formatDetail = (json: string) => {
  if (json == null || json == '') return;
  try {
    const item = JSON.parse(json);
    let result = `id：<span style="color:#F56C6C">${item.id ? item.id : '---'}</span><br/>`;
    result += `value：<span style="color:#F56C6C">${item.value ? item.value : '---'}</span><br/>`;
    result += `remark：<span style="color:#F56C6C">${item.remark ? item.remark : '---'}</span>`;
    return result;
  } catch {
    return json;
  }
};

// 初始化
getList();
</script>

<style lang="scss" scoped>
.iot-alert-log {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
}
</style>
