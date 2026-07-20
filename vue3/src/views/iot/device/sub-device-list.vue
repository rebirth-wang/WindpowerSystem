<template>
  <el-dialog :title="$t('device.sub-device-list.323213-0')" v-model="openDeviceList" width="800px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="serialNumber">
        <el-input
          v-model="queryParams.serialNumber"
          :placeholder="$t('device.sub-device-list.323213-1')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          :placeholder="$t('device.sub-device-list.323213-3')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      ref="multipleTableRef"
      :data="gatewayList"
      @selection-change="handleSelectionChange"
      :row-key="getRowKeys"
      :border="false"
    >
      <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="deviceId" width="100" />
      <el-table-column :label="$t('device.device-edit.148398-7')" align="left" prop="serialNumber" min-width="160" />
      <el-table-column :label="$t('device.device-edit.148398-1')" align="left" prop="deviceName" min-width="180" />
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
      <el-button type="primary" @click="handleDeviceSelected">{{ $t('confirm') }}</el-button>
      <el-button @click="closeSelectDeviceList">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listSubGateway, addGatewayBatch } from '@/api/iot/gateway';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  gateway: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['addSuccess']);

const loading = ref(true);
const ids = ref<string[]>([]);
const total = ref(0);
const gatewayList = ref<any[]>([]);
const openDeviceList = ref(false);

const queryFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceName: null as any,
  serialNumber: null as any,
});

/** 监听 gateway 变化 */
watch(
  () => props.gateway,
  () => {
    queryParams.pageNum = 1;
    getList();
  },
  { immediate: true }
);

/** 查询列表 */
const getList = () => {
  loading.value = true;
  listSubGateway(queryParams).then((response: any) => {
    gatewayList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
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
  ids.value = selection.map((item: any) => item.serialNumber);
};

/** 获取行 key */
const getRowKeys = (row: any) => row.deviceId;

/** 关闭对话框 */
const closeSelectDeviceList = () => {
  openDeviceList.value = false;
};

/** 批量新增子设备 */
const handleDeviceSelected = () => {
  const gateway = props.gateway as any;
  gateway.subClientIds = ids.value;
  addGatewayBatch(gateway).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('device.sub-device-list.323213-4'));
    openDeviceList.value = false;
    emit('addSuccess');
  });
};

/** 打开对话框（供父组件调用） */
const openDialog = () => {
  openDeviceList.value = true;
  queryParams.pageNum = 1;
  getList();
  nextTick(() => {
    multipleTableRef.value?.clearSelection();
  });
};

defineExpose({ openDialog });
</script>
