<template>
  <div>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item prop="alertName">
        <el-input
          v-model="queryParams.alertName"
          :placeholder="$t('device.device-alert.309509-1')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 192px"
        />
      </el-form-item>
      <el-form-item prop="alertLevel">
        <el-select
          v-model="queryParams.alertLevel"
          :placeholder="$t('device.device-alert.309509-3')"
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
          :placeholder="$t('device.device-alert.309509-5')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 192px"
        >
          <el-option v-for="dict in iot_process_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.device-alert.309509-6') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('device.device-alert.309509-7') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="alertLogList" @selection-change="handleSelectionChange" :border="false">
      <el-table-column :label="$t('device.device-alert.309509-0')" align="left" prop="alertName" min-width="160" />
      <el-table-column :label="$t('device.device-alert.309509-2')" align="center" prop="alertLevel" min-width="100">
        <template #default="scope">
          <dict-tag :options="iot_alert_level" :value="scope.row.alertLevel" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-alert.309509-8')" align="left" prop="createBy" min-width="150" />
      <el-table-column :label="$t('device.device-alert.309509-9')" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-alert.309509-10')" align="left" prop="detail" min-width="200">
        <template #default="scope">
          <div v-html="formatDetail(scope.row.detail)"></div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-alert.309509-11')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :options="iot_process_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column fixed="right" :label="$t('device.device-alert.309509-12')" align="center" width="100">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            link
            :icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['iot:alertLog:edit']"
            v-if="scope.row.status === 2"
          >
            {{ $t('device.device-alert.309509-13') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="height: 60px">
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 添加或修改设备告警对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item :label="$t('alert.log.491272-27')" prop="statusType">
          <el-radio-group v-model="statusType">
            <el-radio v-for="dict in iot_process_status" :key="dict.value" :value="Number(dict.value)">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('device.device-alert.309509-14')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('device.device-alert.309509-15')"
            :autosize="{ minRows: 4, maxRows: 6 }"
            style="width: 350px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('device.device-alert.309509-16') }}</el-button>
          <el-button @click="cancel">{{ $t('device.device-alert.309509-17') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Edit } from '@element-plus/icons-vue';
import { getAlertLog, listAlertLog, updateAlertLog } from '@/api/iot/alertLog';
import { useDict } from '@/utils/dict/useDict';

const { proxy } = getCurrentInstance() as any;
const { parseTime } = proxy;

const { iot_alert_level, iot_process_status } = useDict('iot_alert_level', 'iot_process_status');

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const alertLogList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const statusType = ref(1);
const form = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  alertName: null as any,
  alertLevel: null as any,
  status: null as any,
  productId: null as any,
  productName: null as any,
  serialNumber: null as any,
  deviceName: null as any,
});

watch(
  () => props.device,
  (newVal) => {
    if (newVal && newVal.deviceId != 0) {
      queryParams.serialNumber = newVal.serialNumber;
      getList();
    }
  }
);

onMounted(() => {
  if (props.device) {
    queryParams.serialNumber = props.device.serialNumber;
    getList();
  }
});

/** 查询设备告警列表 */
function getList() {
  loading.value = true;
  listAlertLog(queryParams).then((response: any) => {
    alertLogList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    alertLogId: null,
    alertName: null,
    alertLevel: null,
    status: 1,
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
  proxy?.resetForm(formRef.value);
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.alertLogId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleUpdate(row: any) {
  reset();
  const alertLogId = row.alertLogId || ids.value;
  getAlertLog(alertLogId).then((response: any) => {
    form.value = response.data;
    if (form.value.status === 2) {
      statusType.value = 1;
    }
    open.value = true;
    title.value = proxy?.$t('device.device-alert.309509-19');
  });
}

function submitForm() {
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
}

function formatDetail(json: string) {
  if (json == null || json == '') {
    return;
  }
  let item = JSON.parse(json);
  let result = 'id：<span style="color:#F56C6C">' + item.id + '</span><br />';
  result = result + 'value：<span style="color:#F56C6C">' + item.value + '</span><br />';
  result = result + 'remark：<span style="color:#F56C6C">' + item.remark + '</span>';
  return result;
}
</script>
