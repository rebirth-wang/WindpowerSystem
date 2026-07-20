<template>
  <el-dialog
    class="property-bar-variate-dialog"
    :title="$t('scada.topo.components.propertyBar.variateDialog.764059-0')"
    v-model="visibleModel"
    width="1000px"
    :before-close="handleClose"
  >
    <div class="variate-table">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px" @submit.prevent>
        <el-form-item v-if="queryParams.type === 3" prop="serialNumber">
          <el-select
            style="width: 168px"
            v-model="queryParams.serialNumber"
            :placeholder="$t('scada.topo.components.propertyBar.variateDialog.764059-1')"
          >
            <el-option
              v-for="(item, index) in deviceBindList"
              :key="index"
              :label="item.deviceName"
              :value="item.serialNumber"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="queryParams.type === 2" prop="sceneModelDeviceId">
          <el-select
            style="width: 168px"
            v-model="queryParams.sceneModelDeviceId"
            :placeholder="$t('scada.topo.components.propertyBar.variateDialog.764059-2')"
            clearable
          >
            <el-option v-for="(item, index) in sceneDeviceList" :key="index" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>

        <el-form-item prop="modelName">
          <el-input
            v-model="queryParams.modelName"
            :placeholder="$t('scada.topo.components.propertyBar.variateDialog.764059-3')"
            clearable
          />
        </el-form-item>
        <el-form-item style="margin-bottom: 18px">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-if="!multiple"
        class="single-table"
        ref="singleTable"
        v-loading="loading"
        :data="variableList"
        :border="false"
        @select="handleRowSelect"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          v-if="queryParams.type === 3"
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-6')"
          align="left"
          prop="productName"
          min-width="150"
        />
        <el-table-column
          v-if="queryParams.type === 3"
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-4')"
          align="left"
          prop="deviceName"
          min-width="150"
        />
        <el-table-column
          v-if="queryParams.type === 2"
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-7')"
          align="left"
          prop="sceneModelDeviceName"
          min-width="120"
        />
        <el-table-column
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-8')"
          align="left"
          prop="modelName"
          min-width="120"
        />
        <el-table-column
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-9')"
          align="left"
          prop="identifier"
          min-width="120"
        />
        <el-table-column :label="$t('template.paramter.038405-7')" align="center" prop="valueType" width="140">
          <template #default="scope">
            <dict-tag :options="iot_data_type" :value="scope.row.valueType" />
          </template>
        </el-table-column>
      </el-table>

      <el-table
        v-else
        ref="multipleTable"
        v-loading="loading"
        :data="variableList"
        :row-key="(row) => row.identifier"
        :border="false"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" :reserve-selection="true" />
        <el-table-column
          v-if="queryParams.type === 3"
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-6')"
          align="left"
          prop="productName"
          min-width="150"
        />
        <el-table-column
          v-if="queryParams.type === 3"
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-4')"
          align="left"
          prop="deviceName"
          min-width="150"
        />
        <el-table-column
          v-if="queryParams.type === 2"
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-7')"
          align="left"
          prop="sceneModelDeviceName"
          min-width="120"
        />
        <el-table-column
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-8')"
          align="left"
          prop="modelName"
          min-width="120"
        />
        <el-table-column
          :label="$t('scada.topo.components.propertyBar.variateDialog.764059-9')"
          align="left"
          prop="identifier"
          min-width="120"
        />
        <el-table-column :label="$t('template.paramter.038405-7')" align="center" prop="valueType" width="140">
          <template #default="scope">
            <dict-tag :options="iot_data_type" :value="scope.row.valueType" />
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        :page="queryParams.page"
        @update:page="queryParams.page = $event"
        :limit="queryParams.size"
        @update:limit="queryParams.size = $event"
        @pagination="getVariableList"
      />
    </div>

    <template #footer>
      <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, getCurrentInstance, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { getSceneModelDetail } from '@/api/scene/list.js';
import { listDeviceBind, getListVariable } from '@/api/scada/topo.js';
import { useDict } from '@/utils/dict/useDict';
import { Search, Refresh } from '@element-plus/icons-vue';
import { getScadaRouteType, getRouteQueryNumber, getRouteQueryString } from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();

const props = defineProps<{
  visible: boolean;
  multiple: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: any): void;
}>();

const visibleModel = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});
const { iot_data_type } = useDict('iot_data_type');
const loading = ref(true);
const queryForm = ref<any>(null);
const singleTable = ref<any>(null);
const multipleTable = ref<any>(null);

const deviceBindList = ref<any[]>([]);
const sceneDeviceList = ref<any[]>([]);
const variableList = ref<any[]>([]);
const total = ref(0);

const queryParams = reactive({
  scadaGuid: '',
  type: null as number | null,
  serialNumber: '',
  sceneModelDeviceId: null as number | null,
  modelName: '',
  page: 1,
  size: 10,
});

const curSinIdentifier = ref<string | null>(null);
const curMultIdentifier = ref<string[]>([]);
const sinSelectData = ref<any>({});
const mulSelectData = ref<any[]>([]);

/** 由 variateDrawer 传入，在设备列表加载完成后写入 queryParams */
const pendingSerialNumber = ref<string | null>(null);
const pendingSceneModelDeviceId = ref<number | null>(null);

async function getDatas() {
  const scadaType = getScadaRouteType(route.query);
  queryParams.scadaGuid = getRouteQueryString(route.query, 'guid');
  queryParams.type = scadaType;

  if (scadaType === 2) await getSceneModelDevices();
  if (scadaType === 3) await getBindDeviceList();

  applyPendingQueryContext();
  getVariableList();
}

function applyPendingQueryContext() {
  if (pendingSerialNumber.value) {
    queryParams.serialNumber = pendingSerialNumber.value;
    pendingSerialNumber.value = null;
  }
  if (pendingSceneModelDeviceId.value != null) {
    queryParams.sceneModelDeviceId = pendingSceneModelDeviceId.value;
    pendingSceneModelDeviceId.value = null;
  }
}

function getBindDeviceList() {
  return new Promise<void>((resolve) => {
    const params = {
      scadaGuid: getRouteQueryString(route.query, 'guid'),
      pageNum: 1,
      pageSize: 9999,
    };
    listDeviceBind(params).then((res: any) => {
      if (res.code === 200) {
        deviceBindList.value = res.rows || [];
        queryParams.serialNumber = deviceBindList.value.length ? deviceBindList.value[0].serialNumber : '';
      }
      resolve();
    });
  });
}

function getSceneModelDevices() {
  return new Promise<void>((resolve) => {
    const id = getRouteQueryNumber(route.query, 'sceneModelId', 0);
    getSceneModelDetail(id).then((res: any) => {
      if (res.code === 200) {
        sceneDeviceList.value = res.data.sceneModelDeviceVOList || [];
      }
      resolve();
    });
  });
}

function getVariableList() {
  loading.value = true;
  getListVariable(queryParams).then((res: any) => {
    if (res.code === 200) {
      variableList.value = res.rows || [];
      total.value = res.total || 0;
      variableList.value.forEach((item: any) => {
        item.valueType = getValueType(item);
      });

      if (!props.multiple && curSinIdentifier.value) {
        variableList.value.forEach((row: any) => {
          if (curSinIdentifier.value === row.identifier) {
            singleTable.value?.toggleRowSelection(row, true);
          }
        });
      }

      if (props.multiple && curMultIdentifier.value.length) {
        variableList.value.forEach((row: any) => {
          if (curMultIdentifier.value.some((x) => x === row.identifier)) {
            multipleTable.value?.toggleRowSelection(row, true);
          }
        });
      }
    }
    loading.value = false;
  });
}

function getValueType(row: any) {
  const rawType = row.valueType ?? row.datatype ?? row.dataType;
  if (rawType) return String(rawType).toLowerCase();

  const specs = parseSpecs(row.specs);
  return specs?.type ? String(specs.type).toLowerCase() : '';
}

function parseSpecs(specs: any) {
  if (!specs) return null;
  if (typeof specs === 'object') return specs;
  try {
    return JSON.parse(specs);
  } catch (_e) {
    return null;
  }
}

function handleQuery() {
  queryParams.page = 1;
  if (props.multiple && queryParams.type !== 2) {
    multipleTable.value?.clearSelection();
  }
  getVariableList();
}

function resetQuery() {
  if (queryParams.type === 3 && deviceBindList.value.length === 0) {
    getBindDeviceList();
  }
  proxy.resetForm('queryForm');
  queryParams.serialNumber = deviceBindList.value.length ? deviceBindList.value[0].serialNumber : '';
  if (props.multiple) {
    multipleTable.value?.clearSelection();
  }
  handleQuery();
}

function handleRowSelect(_: any, row: any) {
  singleTable.value?.clearSelection();
  singleTable.value?.toggleRowSelection(row, true);
  sinSelectData.value = row;
}

function handleSelectionChange(row: any[]) {
  mulSelectData.value = row;
}

function handleConfirm() {
  if (!props.multiple) {
    if (!sinSelectData.value || !sinSelectData.value.identifier) {
      proxy.$message.warning('请先选择数据');
      return;
    }
    emit('confirm', sinSelectData.value);
  } else {
    if (!mulSelectData.value || mulSelectData.value.length === 0) {
      proxy.$message.warning('请至少选择一条数据');
      return;
    }
    emit('confirm', mulSelectData.value);
  }
}

function clearSelection() {
  const tableRef = props.multiple ? multipleTable.value : singleTable.value;
  tableRef?.clearSelection();
}

function open() {
  queryParams.page = 1;
  nextTick(() => {
    // 先清空，再拉取并根据 cur*Identifier 回显
    clearSelection();
    getDatas();
  });
}

function handleClose() {
  emit('update:visible', false);
}

function setCurSinIdentifier(val: string | null) {
  curSinIdentifier.value = val;
}

function setCurMultIdentifier(val: string[] | null | undefined) {
  curMultIdentifier.value = Array.isArray(val) ? val : [];
}

/** 打开前由 variateDrawer 传入，用于预填设备/场景设备筛选 */
function setQueryDeviceContext(serialNumber?: string, sceneModelDeviceId?: string | number | null) {
  pendingSerialNumber.value = serialNumber != null && serialNumber !== '' ? serialNumber : null;
  pendingSceneModelDeviceId.value =
    sceneModelDeviceId != null && sceneModelDeviceId !== '' ? Number(sceneModelDeviceId) : null;
}

defineExpose({ open, setCurSinIdentifier, setCurMultIdentifier, setQueryDeviceContext });
</script>

<style lang="scss" scoped>
.property-bar-variate-dialog {
  width: 100%;

  .variate-table {
    width: 100%;

    .single-table {
      :deep(.el-table__header-wrapper .el-checkbox) {
        display: none;
      }
    }
  }
}
</style>
