<template>
  <div>
    <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
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
      v-loading="loading"
      ref="tableRef"
      :row-key="rowKey"
      :data="deviceList"
      :border="false"
      @selection-change="handleSelectionChange"
    >
      <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
      <el-table-column :label="$t('device.device-edit.148398-1')" align="left" prop="deviceName" min-width="160" />
      <el-table-column :label="$t('device.device-edit.148398-7')" align="left" prop="serialNumber" min-width="140" />
      <el-table-column :label="$t('device.device-edit.148398-5')" align="left" prop="productName" min-width="160" />
      <el-table-column :label="$t('device.index.105953-4')" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :options="dict.type.iot_device_status" :value="scope.row.status" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { listDeviceShort } from '@/api/iot/device';
import { getDicts as fetchDicts } from '@/api/system/dict/data';
import { Search, Refresh } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';

const { dict } = useDict('iot_device_status');

// 获取字典数据
const loadDicts = async (dictType: string) => {
  try {
    const res: any = await fetchDicts(dictType);
    if (res.code === 200) {
      dict.type[dictType] = res.data;
      return res;
    }
  } catch (error) {
    console.error(`获取字典 ${dictType} 失败:`, error);
  }
};

// 初始化字典数据
onMounted(async () => {
  getList();
});

const loading = ref(true);
const deviceList = ref<any[]>([]);
const total = ref(0);
const selectDevices = ref<any[]>([]);
const tableRef = ref<any>(null);
const queryFormRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceName: '',
  serialNumber: '',
});

// 设备列表
function getList() {
  loading.value = true;
  listDeviceShort(queryParams)
    .then((res: any) => {
      if (res.code == 200) {
        deviceList.value = res.rows.filter((item: any) => item.status !== 1 || item.status !== 2);
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

function handleSelectionChange(selection: any[]) {
  selectDevices.value = selection;
}

function selectRowDataClick() {
  return selectDevices.value.map((item: any) => item.serialNumber);
}

// 取消选择
function clearSelection() {
  tableRef.value?.clearSelection();
}

// 行数据的 Key，用来优化 Table 的渲染
function rowKey(row: any) {
  return row.serialNumber;
}

defineExpose({
  selectRowDataClick,
  clearSelection,
});
</script>
