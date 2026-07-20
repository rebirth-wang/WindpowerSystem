<template>
  <el-dialog :title="$t('firmware.index.222541-31')" v-model="openDeviceList" width="1000px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="groupId">
        <el-select
          v-model="groupId"
          filterable
          reserve-keyword
          :placeholder="$t('iot.group.index.637432-1')"
          :loading="grouploading"
          @change="handleChangeGroup"
          style="width: 180px"
        >
          <el-option :label="$t('iot.group.index.637432-31')" value="noSelect" key="noSelect" />
          <el-option v-for="item in filterList" :key="item.groupId" :label="item.groupName" :value="item.groupId" />
        </el-select>
      </el-form-item>
      <el-form-item prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          :placeholder="$t('device.device-edit.148398-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="serialNumber">
        <el-input
          v-model="queryParams.serialNumber"
          :placeholder="$t('device.device-edit.148398-8')"
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
      ref="multipleTableRef"
      v-loading="loading"
      :data="deviceList"
      @select="handleSelectionChange"
      :row-key="getRowKeys"
      size="small"
      :border="false"
    >
      <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
      <el-table-column :label="$t('device.device-edit.148398-1')" align="left" prop="deviceName" min-width="160px" />
      <el-table-column :label="$t('device.device-edit.148398-7')" align="left" prop="serialNumber" min-width="140px" />
      <el-table-column :label="$t('firmware.index.222541-5')" align="left" prop="productName" min-width="160px" />
      <el-table-column :label="$t('firmware.deviceList.index.222542-6')" align="center" width="85">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.isOwner == 0">{{ $t('firmware.deviceList.index.222542-7') }}</el-tag>
          <el-tag type="primary" v-else>{{ $t('firmware.deviceList.index.222542-8') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('home.position')" align="center" prop="locationWay" width="100">
        <template #default="scope">
          <dict-tag :options="iot_location_way" :value="scope.row.locationWay" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('firmware.deviceList.index.222542-9')" align="center" prop="status" width="80">
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
        <el-button type="primary" @click="confirmSelectDevice">{{ $t('confirm') }}</el-button>
        <el-button @click="closeSelectDeviceList">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listDeviceShort } from '@/api/iot/device';
import { listGroup } from '@/api/iot/group';
import { useDict } from '@/utils/dict';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance() as any;
const { iot_device_status, iot_location_way } = useDict('iot_device_status', 'iot_location_way');

const emit = defineEmits(['deviceEvent']);

const openDeviceList = ref(false);
const loading = ref(true);
const grouploading = ref(true);
const total = ref(0);
const deviceList = ref<any[]>([]);
const filterList = ref<any[]>([]);
const groupId = ref<any>('noSelect');
const selectDeviceNums = ref<string[]>([]);
const productId = ref(0);
const productName = ref('');
const multipleTableRef = ref();
const queryFormRef = ref();

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  deviceName: null,
  productId: null,
  productName: null,
  groupId: null,
  groupName: null,
  serialNumber: null,
  status: null,
});

onMounted(() => {
  getGroupList();
});

function getGroupList() {
  grouploading.value = true;
  listGroup({ pageNum: 1, pageSize: 999, groupName: null, userId: null }).then((res: any) => {
    if (res.code === 200) {
      filterList.value = res.rows;
    }
    grouploading.value = false;
  });
}

function handleChangeGroup(n: any) {
  if (n === 'noSelect') {
    queryParams.groupId = null;
    queryParams.productId = null;
    getList();
    return;
  }
  queryParams.groupId = groupId.value;
  selectDeviceNums.value = [];
  queryParams.productId = null;
  getList();
}

function getList() {
  deviceList.value = [];
  loading.value = true;
  queryParams.productId = queryParams.productId == 0 ? null : queryParams.productId;
  listDeviceShort(queryParams).then((response: any) => {
    deviceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    if (selectDeviceNums.value && selectDeviceNums.value.length > 0) {
      deviceList.value.forEach((row: any) => {
        if (selectDeviceNums.value.some((x) => x === row.serialNumber)) {
          multipleTableRef.value?.toggleRowSelection(row, true);
        }
      });
    }
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  groupId.value = 'noSelect';
  proxy.resetForm(queryFormRef.value);
  handleQuery();
}

function handleSelectionChange(selection: any[], row: any) {
  let index = selectDeviceNums.value.indexOf(row.serialNumber);
  let value = selection.indexOf(row);
  if (index == -1 && value != -1) {
    selectDeviceNums.value.push(row.serialNumber);
    productId.value = row.productId;
    productName.value = row.productName;
  } else if (index != -1 && value == -1) {
    selectDeviceNums.value.splice(index, 1);
  }
  if (selectDeviceNums.value.length == 0) {
    queryParams.productId = null;
    getList();
  } else if (selectDeviceNums.value.length == 1) {
    queryParams.productId = row.productId;
    getList();
  }
}

function closeSelectDeviceList() {
  openDeviceList.value = false;
}

function confirmSelectDevice() {
  if (selectDeviceNums.value.length > 0) {
    const data = {
      productId: productId.value,
      productName: productName.value,
      deviceNums: selectDeviceNums.value,
      groupId: queryParams.groupId,
    };
    emit('deviceEvent', data);
  } else {
    ElMessage.warning(proxy.$t('device.device-edit.148398-95'));
    return;
  }
  openDeviceList.value = false;
  multipleTableRef.value?.clearSelection();
}

function getRowKeys(row: any) {
  return row.serialNumber;
}

defineExpose({ openDeviceList, selectDeviceNums, productId, productName, queryParams, getList });
</script>

<style lang="scss" scoped>
/***隐藏全选，避免选中不同产品的设备**/
:deep(.el-table__header-wrapper .el-checkbox) {
  display: none;
}
</style>
