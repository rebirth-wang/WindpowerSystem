<template>
  <div>
    <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="serialNumber">
        <el-input
          v-model="queryParams.serialNumber"
          :placeholder="$t('device.index.105953-3')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button :icon="Plus" plain type="primary" @click="handleAdd">{{ $t('add') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="deviceBindList" :border="false">
      <el-table-column label="id" align="center" prop="id" width="80" />
      <el-table-column :label="$t('device.index.105953-0')" align="left" prop="deviceName" />
      <el-table-column :label="$t('device.index.105953-2')" align="left" prop="serialNumber" />
      <el-table-column :label="$t('opation')" align="center" width="80">
        <template #default="{ row }">
          <el-button style="color: #f56c6c" size="small" type="text" icon="el-icon-delete" @click="handleDelete(row)">
            {{ $t('topo.topoToolbox.250932-9') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      layout="prev, pager, next"
      :total="total"
      :page="queryParams.pageNum"
      @update:page="queryParams.pageNum = $event"
      :limit="queryParams.pageSize"
      @update:limit="queryParams.pageSize = $event"
      @pagination="getList"
    />

    <el-dialog
      :title="$t('topo.topoToolbox.250932-10')"
      v-model="isDeviceDialog"
      width="900px"
      append-to-body
      :close-on-click-modal="false"
    >
      <device ref="deviceRef" />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="isDeviceDialog = false">{{ $t('cancel') }}</el-button>
          <el-button type="primary" @click="handleSelectDevice">{{ $t('confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';
import Device from './device.vue';
import { listDeviceBind, saveDeviceBind, removeDeviceBind } from '@/api/scada/topo';
import { getRouteQueryString } from '@/utils/topo/topoUtil';

const route = useRoute();
const { proxy } = getCurrentInstance() as any;

const loading = ref(true);
const deviceBindList = ref<any[]>([]);
const total = ref(0);
const isDeviceDialog = ref(false);
const serialNumbers = ref('');
const deviceRef = ref<any>(null);
const queryFormRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  serialNumber: '',
  scadaGuid: getRouteQueryString(route.query, 'guid'),
});

// 查询云组态组件关联设备列表
function getList() {
  loading.value = true;
  listDeviceBind(queryParams)
    .then((res: any) => {
      if (res.code == 200) {
        deviceBindList.value = res.rows;
        total.value = res.total;
      }
      loading.value = false;
    })
    .catch((err: any) => {
      console.log(err);
      loading.value = false;
    });
}

// 搜索按钮操作
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

// 重置按钮操作
function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

// 新增按钮操作
function handleAdd() {
  isDeviceDialog.value = true;
  if (deviceRef.value) {
    deviceRef.value.clearSelection();
  }
}

// 选择设备
function handleSelectDevice() {
  isDeviceDialog.value = false;
  const list = deviceRef.value.selectRowDataClick();
  serialNumbers.value = list.join(',');
  saveBind();
}

function saveBind() {
  let params = {
    scadaGuid: getRouteQueryString(route.query, 'guid'),
    serialNumbers: serialNumbers.value,
  };
  saveDeviceBind(params)
    .then((res: any) => {
      getList();
    })
    .catch((err: any) => {
      console.log(err);
    });
}

/** 删除按钮操作 */
function handleDelete(row: any) {
  const ids = row.id;
  proxy.$modal
    .confirm(proxy.$t('topo.topoToolbox.250932-11'))
    .then(() => removeDeviceBind(ids))
    .then((res: any) => {
      if (res.code === 200) {
        proxy.$modal.msgSuccess(proxy.$t('topo.topoToolbox.250932-12'));
        getList();
      }
    })
    .catch((err: any) => {
      if (err === 'cancel' || err === 'close') return;
      proxy.$modal.msgError(err?.msg || err?.message || String(err));
    });
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped></style>
