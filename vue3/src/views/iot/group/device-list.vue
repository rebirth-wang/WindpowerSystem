<template>
  <el-dialog :title="$t('iot.group.device-list.849593-0')" v-model="openDeviceList" width="840px" append-to-body>
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
      <el-form-item prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          :placeholder="$t('iot.group.device-list.849593-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('iot.group.device-list.849593-3') }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="deviceList"
      @select="handleSelectionChange"
      @select-all="handleSelectionAll"
      ref="multipleTableRef"
      size="small"
      :border="false"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('iot.group.device-list.849593-4')" align="left" prop="deviceName" min-width="140" />
      <el-table-column :label="$t('iot.group.device-list.849593-5')" align="left" prop="serialNumber" min-width="150" />
      <el-table-column :label="$t('iot.group.device-list.849593-6')" align="left" prop="productName" min-width="140" />
      <el-table-column :label="$t('iot.group.device-list.849593-7')" align="center" min-width="90">
        <template #default="scope">
          <el-tag type="success" size="small" v-if="scope.row.isOwner == 0">
            {{ $t('iot.group.device-list.849593-8') }}
          </el-tag>
          <el-tag type="primary" size="small" v-else>{{ $t('iot.group.device-list.849593-9') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('iot.group.device-list.849593-10')" align="center" prop="status" min-width="90">
        <template #default="scope">
          <dict-tag :options="iot_device_status" size="small" :value="scope.row.status" />
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
        <el-button type="primary" @click="handleDeviceSelected">{{ $t('iot.group.device-list.849593-11') }}</el-button>
        <el-button @click="closeSelectDeviceList">{{ $t('iot.group.device-list.849593-12') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, getCurrentInstance } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { getDeviceIds, updateDeviceGroups } from '@/api/iot/group';
import { listDeviceByGroup } from '@/api/iot/device';
import { useDict } from '@/utils/dict/useDict';

const { iot_device_status } = useDict('iot_device_status');
const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  group: { type: Object, default: () => ({}) },
});

const loading = ref(true);
const ids = ref<any[]>([]);
const openDeviceList = ref(false);
const total = ref(0);
const deviceList = ref<any[]>([]);
const multipleTableRef = ref<any>(null);

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  deviceName: null,
  productId: null,
  productName: null,
  userId: null,
  userName: null,
  tenantId: null,
  tenantName: null,
  serialNumber: null,
  status: null,
  networkAddress: null,
  activeTime: null,
});

watch(
  () => props.group,
  (newVal: any) => {
    if (newVal?.groupId) {
      queryParams.userId = newVal.userId;
      queryParams.pageNum = 1;
      getDeviceIdsByGroupId(newVal.groupId);
    }
  },
  { immediate: true }
);

function getDeviceIdsByGroupId(groupId: any) {
  getDeviceIds(groupId).then((response: any) => {
    ids.value = response.data;
    getList();
  });
}

function getList() {
  loading.value = true;
  queryParams.params = {};
  listDeviceByGroup(queryParams).then((response: any) => {
    deviceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    deviceList.value.forEach((row: any) => {
      nextTick(() => {
        if (ids.value.some((x) => x === row.deviceId)) {
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
  let index = ids.value.indexOf(row.deviceId);
  let value = selection.indexOf(row);
  if (index == -1 && value != -1) ids.value.push(row.deviceId);
  else if (index != -1 && value == -1) ids.value.splice(index, 1);
}

function handleSelectionAll(selection: any[]) {
  deviceList.value.forEach((d) => {
    let index = ids.value.indexOf(d.deviceId);
    let value = selection.indexOf(d);
    if (index == -1 && value != -1) ids.value.push(d.deviceId);
    else if (index != -1 && value == -1) ids.value.splice(index, 1);
  });
}

function closeSelectDeviceList() {
  openDeviceList.value = false;
}

function handleDeviceSelected() {
  const data = { ...props.group, deviceIds: ids.value };
  updateDeviceGroups(data).then(() => {
    proxy.$modal.msgSuccess(proxy.$t('iot.group.device-list.849593-13'));
    openDeviceList.value = false;
  });
}

defineExpose({ openDeviceList });
</script>
