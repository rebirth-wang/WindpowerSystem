<template>
  <el-dialog :title="$t('scene.configList.326501-0')" v-model="openConfigList" width="700px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="alertName">
        <el-input
          v-model="queryParams.alertName"
          :placeholder="$t('scene.configList.326501-1')"
          clearable
          @keyup.enter="handleQuery"
          @clear="handleResetQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="configList" style="width: 100%" :border="false" v-loading="loading">
      <el-table-column width="50" align="center">
        <template #header>
          <el-checkbox :indeterminate="isIndeterminate" :model-value="isAllSelected" @change="toggleSelectAll" />
        </template>
        <template #default="scope">
          <el-checkbox :model-value="selectedIds.includes(scope.row.alertId)" @change="toggleSelect(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('scene.alertList.326501-3')" prop="alertName" />
    </el-table>

    <pagination
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <template #footer>
      <div style="width: 100%">
        <el-button type="primary" @click="handleConfirm">{{ $t('scene.bridgelist.784127-17') }}</el-button>
        <el-button @click="cancel">{{ $t('scene.bridgelist.784127-16') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listAlert } from '@/api/iot/alert';
import { bindSceneAlert, unbindSceneAlert } from '@/api/scene/list';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/modules/user';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();

const emit = defineEmits(['refreshParentList', 'bindConfigEvent', 'refreshEvent']);

const selectedIds = ref<number[]>([]);
const openConfigList = ref(false);
const total = ref(0);
const loading = ref(false);
const configList = ref<any[]>([]);
const sceneId = ref<any>(null);
const initialSelectedIds = ref<number[]>([]);
const toUnbindIds = ref<number[]>([]);
const alertIds = ref<number[]>([]);
const queryFormRef = ref();

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  alertName: null,
});

const localAlertIds = computed(() => [...(alertIds.value || [])]);

const isAllSelected = computed(
  () => configList.value.length > 0 && selectedIds.value.length === configList.value.length
);
const isIndeterminate = computed(
  () => selectedIds.value.length > 0 && selectedIds.value.length < configList.value.length
);

watch(openConfigList, (val) => {
  if (val) {
    initialSelectedIds.value = [...localAlertIds.value];
    selectedIds.value = [...localAlertIds.value];
    toUnbindIds.value = [];
  }
});

function toggleSelectAll(val: any) {
  if (val) {
    selectedIds.value = configList.value.map((item) => item.alertId);
    toUnbindIds.value = toUnbindIds.value.filter((id) => !initialSelectedIds.value.includes(id));
  } else {
    selectedIds.value = [];
    configList.value.forEach((item) => {
      if (initialSelectedIds.value.includes(item.alertId) && !toUnbindIds.value.includes(item.alertId)) {
        toUnbindIds.value.push(item.alertId);
      }
    });
  }
}

function toggleSelect(row: any) {
  const idx = selectedIds.value.indexOf(row.alertId);
  if (idx === -1) {
    selectedIds.value.push(row.alertId);
    const unbindIdx = toUnbindIds.value.indexOf(row.alertId);
    if (unbindIdx !== -1) toUnbindIds.value.splice(unbindIdx, 1);
  } else {
    selectedIds.value.splice(idx, 1);
    if (initialSelectedIds.value.includes(row.alertId)) {
      toUnbindIds.value.push(row.alertId);
    }
  }
}

function getList() {
  loading.value = true;
  listAlert(queryParams)
    .then((res: any) => {
      configList.value = res.rows;
      total.value = res.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleConfirm() {
  if (!userStore.dept?.deptId) {
    ElMessage.warning(proxy.$t('scene.configList.326501-2'));
    return;
  }
  const newBinds = selectedIds.value.filter((id) => !initialSelectedIds.value.includes(id));
  const isSame =
    selectedIds.value.length === initialSelectedIds.value.length &&
    selectedIds.value.every((id) => initialSelectedIds.value.includes(id)) &&
    toUnbindIds.value.length === 0;
  if (isSame) {
    ElMessage.success(proxy.$t('scene.configList.326501-3'));
    selectedIds.value = [];
    toUnbindIds.value = [];
    queryParams.alertName = null;
    emit('refreshParentList');
    openConfigList.value = false;
    return;
  }
  if (sceneId.value) {
    let bindPromise: any = Promise.resolve();
    if (newBinds.length > 0) {
      bindPromise = bindSceneAlert({ alertIds: newBinds, sceneId: sceneId.value });
    }
    let unbindPromise: any = Promise.resolve();
    if (toUnbindIds.value.length > 0) {
      unbindPromise = unbindSceneAlert({ alertIds: toUnbindIds.value, sceneId: sceneId.value });
    }
    Promise.all([bindPromise, unbindPromise]).then(() => {
      ElMessage.success(proxy.$t('scene.configList.326501-3'));
      selectedIds.value = [];
      toUnbindIds.value = [];
      queryParams.alertName = null;
      emit('refreshParentList');
      openConfigList.value = false;
    });
  } else {
    emit('bindConfigEvent', selectedIds.value);
    openConfigList.value = false;
  }
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  proxy.resetForm(queryFormRef.value);
  handleQuery();
}

function cancel() {
  openConfigList.value = false;
  selectedIds.value = [];
  toUnbindIds.value = [];
  queryParams.alertName = null;
  emit('refreshEvent');
}

defineExpose({ openConfigList, sceneId, alertIds, getList });
</script>
