<template>
  <el-dialog
    :title="$t('card.sim.select_device')"
    v-model="dialogVisible"
    width="1000px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="deviceQueryParams" ref="deviceQueryFormRef" :inline="true" label-width="68px" size="small">
      <el-form-item :label="$t('card.sim.device_name')">
        <el-input
          v-model="deviceQueryParams.deviceName"
          :placeholder="$t('card.sim.enter_device_name')"
          clearable
          @keyup.enter="handleDeviceQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('card.sim.serial_number')">
        <el-input
          v-model="deviceQueryParams.serialNumber"
          :placeholder="$t('card.sim.enter_serial_number')"
          clearable
          @keyup.enter="handleDeviceQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" size="small" @click="handleDeviceQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" size="small" @click="resetDeviceQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="deviceTableRef" v-loading="deviceLoading" :data="deviceList" :border="false" style="width: 100%">
      <el-table-column width="55" align="center">
        <template #default="scope">
          <el-checkbox v-model="scope.row.checked" @change="handleDeviceCheck(scope.row)">&nbsp;</el-checkbox>
        </template>
      </el-table-column>
      <el-table-column :label="$t('card.sim.device_name')" align="left" prop="deviceName" />
      <el-table-column :label="$t('card.sim.serial_number')" align="left" prop="serialNumber" />
      <el-table-column :label="$t('card.sim.product')" align="left" prop="productName" />
    </el-table>

    <pagination
      v-show="deviceTotal > 0"
      :total="deviceTotal"
      v-model:page="deviceQueryParams.pageNum"
      v-model:limit="deviceQueryParams.pageSize"
      @pagination="getDeviceList"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="confirmDeviceSelect" :disabled="!selectedDevice">
          {{ $t('confirm') }}
        </el-button>
        <el-button @click="closeDialog">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listDeviceShort } from '@/api/iot/device';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits(['update:visible', 'device-selected', 'close']);

const dialogVisible = ref(false);
const deviceList = ref<any[]>([]);
const deviceTotal = ref(0);
const deviceLoading = ref(false);
const deviceQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  deviceName: null as string | null,
  serialNumber: null as string | null,
});
const selectedDevice = ref<any>(null);
const deviceQueryFormRef = ref();
const deviceTableRef = ref();

watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal;
    if (newVal) {
      getDeviceList();
    }
  }
);

watch(dialogVisible, (newVal) => {
  if (!newVal) {
    emit('update:visible', false);
  }
});

async function getDeviceList() {
  deviceLoading.value = true;
  try {
    const response = await listDeviceShort(deviceQueryParams.value);
    deviceList.value = (response.rows || []).map((item: any) => ({
      ...item,
      checked: !!(selectedDevice.value && selectedDevice.value.id === item.id),
    }));
    deviceTotal.value = response.total;
  } finally {
    deviceLoading.value = false;
  }
}

function handleDeviceQuery() {
  deviceQueryParams.value.pageNum = 1;
  getDeviceList();
}

function resetDeviceQuery() {
  deviceQueryParams.value.deviceName = null;
  deviceQueryParams.value.serialNumber = null;
  handleDeviceQuery();
}

function handleDeviceCheck(row: any) {
  deviceList.value.forEach((item) => {
    if (item.id !== row.id) {
      item.checked = false;
    }
  });
  selectedDevice.value = row.checked ? row : null;
}

function confirmDeviceSelect() {
  if (selectedDevice.value) {
    emit('device-selected', selectedDevice.value);
    closeDialog();
  }
}

function closeDialog() {
  dialogVisible.value = false;
}

function handleClose() {
  selectedDevice.value = null;
  deviceList.value.forEach((item) => {
    item.checked = false;
  });
  emit('close');
}
</script>
