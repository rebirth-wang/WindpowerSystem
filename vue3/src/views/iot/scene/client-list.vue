<template>
  <div class="app-container">
    <el-dialog
      :title="$t('scene.bridgelist.784127-0')"
      v-model="open"
      width="800px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('scene.bridgelist.784127-2')"
            clearable
            size="small"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" size="small" @click="handleQuery">
            {{ $t('scene.bridgelist.784127-3') }}
          </el-button>
          <el-button :icon="Refresh" size="small" @click="handleResetQuery">
            {{ $t('scene.bridgelist.784127-4') }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="bridgeList"
        @row-click="rowClick"
        ref="singleTableRef"
        highlight-current-row
        size="small"
        :border="false"
      >
        <el-table-column :label="$t('scene.bridgelist.784127-5')" width="55" align="center">
          <template #default="scope">
            <input type="radio" :checked="scope.row.isSelect" name="bridge" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.bridgelist.784127-6')" align="center" prop="name" />
        <el-table-column :label="$t('scene.bridgelist.784127-7')" align="center" prop="enable">
          <template #default="scope">
            <el-switch v-model="scope.row.enable" active-value="1" inactive-value="0"></el-switch>
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.bridgelist.784127-8')" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="bridge_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.bridgelist.784127-11')" align="center" prop="type">
          <template #default="scope">
            <dict-tag :options="bridge_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.bridgelist.784127-14')" align="center" prop="direction">
          <template #default="scope">
            <dict-tag :options="bridging_direction" :value="scope.row.direction" />
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
      <template #footer>
        <div style="width: 100%">
          <el-button type="primary" @click="confirmSelectClient">{{ $t('scene.bridgelist.784127-17') }}</el-button>
          <el-button @click="cancel">{{ $t('scene.bridgelist.784127-16') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listBridge } from '@/api/iot/bridge';
import { useDict } from '@/utils/dict';

const { bridge_status, bridge_type, bridging_direction } = useDict(
  'bridge_status',
  'bridge_type',
  'bridging_direction'
);
const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['clientEvent']);

const queryFormRef = ref();
const open = ref(false);
const loading = ref(true);
const total = ref(0);
const bridgeList = ref<any[]>([]);
const selectBridgeId = ref(0);
const bridge = ref<any>({});

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  direction: 2,
  name: null,
  enable: null,
  status: null,
  type: 3,
  configId: null,
});

function getList() {
  loading.value = true;
  if (queryParams.type == 3 || queryParams.type == 4) {
    queryParams.direction = 2;
  } else {
    queryParams.direction = '';
  }
  listBridge(queryParams).then((response: any) => {
    bridgeList.value = response.rows;
    bridgeList.value.forEach((item: any) => {
      item.status = '1';
    });
    total.value = response.total;
    loading.value = false;
    setRadioSelected(selectBridgeId.value);
  });
}

function cancel() {
  open.value = false;
  bridge.value = {};
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  proxy?.resetForm('queryFormRef');
  handleQuery();
}

function rowClick(row: any) {
  if (row) {
    setRadioSelected(row.id);
    bridge.value = row;
  }
}

function setRadioSelected(bridgeId: any) {
  for (let i = 0; i < bridgeList.value.length; i++) {
    bridgeList.value[i].isSelect = bridgeList.value[i].id == bridgeId;
  }
}

function confirmSelectClient() {
  emit('clientEvent', bridge.value);
  open.value = false;
}

defineExpose({ open, queryParams, selectBridgeId, getList });
</script>
