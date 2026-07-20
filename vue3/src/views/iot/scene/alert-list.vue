<template>
  <el-dialog :title="$t('scene.alertList.326501-0')" v-model="openList" width="700px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="alertName">
        <el-input
          v-model="queryParams.alertName"
          :placeholder="$t('scene.alertList.326501-1')"
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
    <el-form style="display: flex; justify-content: flex-start; margin-bottom: -10px; margin-top: -10px">
      <el-form-item>
        <el-button type="primary" plain :icon="Plus" @click="handleBind">{{ $t('add') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="alertList" style="width: 100%" :border="false" v-loading="loading">
      <el-table-column :label="$t('scene.alertList.326501-3')" prop="alertName" />
      <el-table-column :label="$t('card.platform.operation')" align="right">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="handleUnbind(scope.row)">
            {{ $t('scene.alertList.326501-2') }}
          </el-button>
        </template>
      </el-table-column>
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
    <alert-config-list ref="alertConfigListRef" @refreshParentList="handleQuery" />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, getCurrentInstance } from 'vue';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { getSceneAlertList, unbindSceneAlert } from '@/api/scene/list';
import alertConfigList from './alert-config-list.vue';

const { proxy } = getCurrentInstance() as any;

const emit = defineEmits(['bindEvent']);

const openList = ref(false);
const total = ref(0);
const loading = ref(false);
const alertList = ref<any[]>([]);
const alertIds = ref<number[]>([]);
const alertConfigListRef = ref();
const queryFormRef = ref();

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  sceneId: null,
  alertName: null,
});

function getList() {
  loading.value = true;
  getSceneAlertList(queryParams)
    .then((res: any) => {
      alertList.value = res.rows;
      total.value = res.total;
      alertIds.value = alertList.value.map((item) => item.alertId);
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleBind() {
  nextTick(() => {
    alertConfigListRef.value.openConfigList = true;
    alertConfigListRef.value.sceneId = queryParams.sceneId;
    alertConfigListRef.value.alertIds = alertIds.value;
    alertConfigListRef.value.getList();
  });
}

function handleUnbind(row: any) {
  proxy.$modal
    .confirm(proxy.$t('scene.alertList.326501-4', { alertName: row.alertName }), proxy.$t('scene.alertList.326501-5'), {
      confirmButtonText: proxy.$t('scene.bridgelist.784127-17'),
      cancelButtonText: proxy.$t('scene.bridgelist.784127-16'),
      type: 'warning',
    })
    .then(() => {
      const sceneId = queryParams.sceneId;
      unbindSceneAlert({ alertIds: [row.alertId], sceneId: sceneId }).then(() => {
        proxy.$modal.msgSuccess(proxy.$t('scene.alertList.326501-6'));
        getList();
      });
    })
    .catch(() => {});
}

function handleConfirm() {
  emit('bindEvent', alertIds.value);
  queryParams.alertName = null;
  openList.value = false;
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
  queryParams.alertName = null;
  openList.value = false;
}

defineExpose({ openList, queryParams, getList });
</script>
