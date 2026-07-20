<template>
  <div>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('device.device-functionlog.399522-0')" label-width="120px" v-if="isSubDev">
        <el-select
          v-model="queryParams.slaveId"
          :placeholder="$t('device.device-functionlog.399522-1')"
          @change="selectSlave"
          @keyup.enter="handleQuery"
          style="width: 200px"
        >
          <el-option
            v-for="slave in slaveList"
            :key="slave.slaveId"
            :label="`${slave.deviceName} (${$t('device.device-functionlog.399522-2')}${slave.slaveId})`"
            :value="slave.slaveId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="funType">
        <el-select
          v-model="queryParams.funType"
          :placeholder="$t('device.device-functionlog.399522-4')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
        >
          <el-option v-for="dict in iot_function_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="identify">
        <el-input
          v-model="queryParams.identify"
          :placeholder="$t('device.device-functionlog.399522-6')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="daterangeTime"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('device.device-functionlog.399522-8')"
          :end-placeholder="$t('device.device-functionlog.399522-9')"
        ></el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.device-functionlog.399522-10') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('device.device-functionlog.399522-11') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange" :border="false">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        :label="$t('device.device-functionlog.399522-27')"
        align="left"
        prop="modelName"
        min-width="120"
      />
      <el-table-column :label="$t('device.device-functionlog.399522-5')" align="left" prop="identify" min-width="150" />
      <el-table-column :label="$t('device.device-functionlog.399522-12')" align="center" prop="funType" min-width="100">
        <template #default="scope">
          <dict-tag :options="iot_function_type" :value="scope.row.funType" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-functionlog.399522-13')" align="left" prop="funValue" min-width="130">
        <template #default="scope">
          <span v-if="scope.row.dataType === 'bool'">
            {{
              scope.row.funValue === '1'
                ? $t('device.device-functionlog.399522-28')
                : $t('device.device-functionlog.399522-29')
            }}
          </span>
          <span v-else>{{ scope.row.funValue }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-edit.148398-7')" align="left" prop="serialNumber" min-width="150" />
      <el-table-column
        :label="$t('device.device-functionlog.399522-15')"
        align="center"
        prop="createTime"
        width="150"
      />
      <el-table-column
        :label="$t('device.device-functionlog.399522-16')"
        align="center"
        prop="resultMsg"
        min-width="160"
      />
      <el-table-column fixed="right" :label="$t('opation')" align="center" width="80">
        <template #default="scope">
          <el-button size="small" link :icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:log:remove']">
            {{ $t('device.device-functionlog.399522-18') }}
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Delete } from '@element-plus/icons-vue';
import { listLog, delLog } from '@/api/iot/functionLog';
import { useDict } from '@/utils/dict/useDict';

const { proxy } = getCurrentInstance() as any;

const { iot_function_type } = useDict('iot_function_type', 'iot_yes_no');

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
const logList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const daterangeTime = ref<any[]>([]);
const isSubDev = ref(false);
const slaveList = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  identify: null as any,
  funType: null as any,
  funValue: null as any,
  messageId: null as any,
  deviceName: null as any,
  serialNumber: null as any,
  deviceId: null as any,
  mode: null as any,
  userId: null as any,
  resultMsg: null as any,
  resultCode: null as any,
  slaveId: null as any,
  beginTime: '' as any,
  endTime: '' as any,
});

watch(
  () => props.device,
  (newVal) => {
    if (newVal && newVal.deviceId != 0) {
      isSubDev.value = newVal.subDeviceList && newVal.subDeviceList.length > 0;
      queryParams.deviceId = newVal.deviceId;
      queryParams.slaveId = newVal.slaveId;
      queryParams.serialNumber = newVal.serialNumber;
      slaveList.value = newVal.subDeviceList;
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

/** 查询设备服务下发日志列表 */
function getList() {
  loading.value = true;
  if (daterangeTime.value != null && daterangeTime.value.length > 0) {
    queryParams.beginTime = daterangeTime.value[0];
    queryParams.endTime = daterangeTime.value[1];
  } else {
    queryParams.beginTime = '';
    queryParams.endTime = '';
  }
  const params = { ...queryParams };
  if (params.slaveId) {
    params.serialNumber = params.serialNumber + '_' + params.slaveId;
  }
  listLog(params).then((response: any) => {
    logList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  daterangeTime.value = [];
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item: any) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleDelete(row: any) {
  const deleteIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('device.device-functionlog.399522-24', [deleteIds]))
    .then(() => {
      return delLog(deleteIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-functionlog.399522-26'));
    })
    .catch(() => {});
}

function selectSlave() {}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/log/export', { ...queryParams }, `log_${new Date().getTime()}.xlsx`);
}
</script>
