<template>
  <el-dialog :title="$t('firmware.deviceList.index.222542-0')" v-model="openDeviceList" width="810px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="deviceName">
        <el-input
          v-model="queryParams.query"
          style="width: 196px"
          :placeholder="$t('firmware.deviceList.index.222542-12')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="version">
        <el-input
          :placeholder="$t('firmware.deviceList.index.222542-13')"
          v-model="queryParams.version"
          clearable
          @keyup.enter="handleQuery"
          class="input-with-select"
          style="vertical-align: middle; width: 318px"
          :disabled="!versionType"
        >
          <template #prepend>
            <el-select
              v-model="versionType"
              :placeholder="$t('firmware.deviceList.index.222542-14')"
              style="width: 126px"
            >
              <el-option :label="$t('firmware.deviceList.index.222542-15')" :value="1"></el-option>
              <el-option :label="$t('firmware.deviceList.index.222542-16')" :value="2"></el-option>
            </el-select>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="deviceListData"
      @select="handleSelectionChange"
      @select-all="handleSelectionAll"
      ref="multipleTableRef"
      size="small"
      :border="false"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        :label="$t('firmware.deviceList.index.222542-1')"
        align="left"
        prop="deviceName"
        min-width="160"
      />
      <el-table-column
        :label="$t('firmware.deviceList.index.222542-5')"
        align="left"
        prop="serialNumber"
        min-width="140"
      />
      <el-table-column
        :label="$t('firmware.deviceList.index.222542-15')"
        align="center"
        prop="firmwareVersion"
        width="100"
      >
        <template #default="scope">
          <span v-if="scope.row.firmwareVersion === 0">-</span>
          <span>Version {{ scope.row.firmwareVersion }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('firmware.deviceList.index.222542-16')"
        align="center"
        prop="wirelessVersion"
        width="110"
      >
        <template #default="scope">
          <span v-if="scope.row.wirelessVersion === 0">-</span>
          <span v-else>Version {{ scope.row.wirelessVersion }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('firmware.deviceList.index.222542-6')" align="center" width="100">
        <template #default="scope">
          <dict-tag :options="iot_device_type" :value="scope.row.deviceType"></dict-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('firmware.deviceList.index.222542-9')" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="iot_device_status" :value="scope.row.status" />
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      layout="prev, pager, next"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleDeviceSelected">{{ $t('confirm') }}</el-button>
        <el-button @click="closeSelectDeviceList">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { listDeviceByGroup } from '@/api/iot/device';
import { deepClone } from '@/utils';
import { useDict } from '@/utils/dict';

const { iot_device_status, iot_device_type } = useDict('iot_device_status', 'iot_device_type');
const { proxy } = getCurrentInstance() as any;

const props = defineProps<{
  upGrade: any;
}>();

const formUpGrade = ref<any>({});
const loading = ref(true);
const ids = ref<any[]>([]);
const openDeviceList = ref(false);
const total = ref(0);
const deviceListData = ref<any[]>([]);
const multipleTableRef = ref<any>(null);
const versionType = ref<number | null>(null);
const queryParams = reactive<any>({
  query: '',
  pageNum: 1,
  pageSize: 10,
  version: null,
});

watch(
  () => props.upGrade,
  (newVal) => {
    if (newVal?.flag) {
      formUpGrade.value = newVal;
      queryParams.productId = formUpGrade.value.productId;
      queryParams.firmwareVersion = formUpGrade.value.firmwareVersion;
      ids.value = formUpGrade.value.deviceList;
    }
  },
  { immediate: true, deep: true }
);

watch(openDeviceList, (val) => {
  if (val) {
    queryParams.pageNum = 1;
    resetQueryParamsHandle();
    getList();
  }
});

function getList() {
  loading.value = true;
  let params = deepClone(queryParams);
  params.firmwareVersion = versionType.value === 1 ? queryParams.version : null;
  params.wirelessVersion = versionType.value === 2 ? queryParams.version : null;
  listDeviceByGroup(params).then((response: any) => {
    deviceListData.value = response.rows;
    total.value = response.total;
    loading.value = false;
    deviceListData.value.forEach((row: any) => {
      nextTick(() => {
        if (ids.value.some((x: any) => x === row.serialNumber)) {
          multipleTableRef.value?.toggleRowSelection(row, true);
        }
      });
    });
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleSelectionChange(selection: any[], row: any) {
  let index = ids.value.indexOf(row.serialNumber);
  let value = selection.indexOf(row);
  if (index == -1 && value != -1) {
    ids.value.push(row.serialNumber);
  } else if (index != -1 && value == -1) {
    ids.value.splice(index, 1);
  }
}

function handleSelectionAll(selection: any[]) {
  for (let i = 0; i < deviceListData.value.length; i++) {
    let index = ids.value.indexOf(deviceListData.value[i].serialNumber);
    let value = selection.indexOf(deviceListData.value[i]);
    if (index == -1 && value != -1) {
      ids.value.push(deviceListData.value[i].serialNumber);
    } else if (index != -1 && value == -1) {
      ids.value.splice(index, 1);
    }
  }
}

function closeSelectDeviceList() {
  openDeviceList.value = false;
  formUpGrade.value.flag = false;
}

function handleDeviceSelected() {
  formUpGrade.value.deviceList = ids.value;
  formUpGrade.value.deviceAmount = ids.value.length;
  formUpGrade.value.flag = false;
  proxy?.$modal.msgSuccess(proxy?.$t('firmware.deviceList.index.222542-10'));
  openDeviceList.value = false;
}

function resetQueryParamsHandle() {
  queryParams.firmwareVersion = null;
  queryParams.query = null;
  versionType.value = null;
}

defineExpose({ openDeviceList });
</script>
