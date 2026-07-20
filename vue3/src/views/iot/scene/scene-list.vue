<template>
  <el-dialog :title="$t('alert.scene-list.591934-0')" v-model="openScene" width="800px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="sceneName">
        <el-input
          v-model="queryParams.sceneName"
          :placeholder="$t('alert.scene-list.591934-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="sceneList"
      @row-click="rowClick"
      ref="singleTableRef"
      highlight-current-row
      size="small"
      :border="false"
    >
      <el-table-column :label="$t('select')" width="60" align="center">
        <template #default="scope">
          <input type="radio" :checked="scope.row.isSelect" name="product" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.scene-list.591934-1')" align="left" prop="sceneName" min-width="160" />
      <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="enable" width="80">
        <template #default="scope">
          <el-tag type="success" size="small" v-if="scope.row.enable == 1">{{ $t('alert.index.236501-45') }}</el-tag>
          <el-tag type="danger" size="small" v-if="scope.row.enable == 2">{{ $t('alert.index.236501-46') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.index.236501-19')" align="center" prop="cond" min-width="100">
        <template #default="scope">
          <dict-tag :options="trigger_condition" :value="scope.row.cond" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.index.236501-23')" align="center" prop="executeMode" width="80">
        <template #default="scope">
          <dict-tag :options="execution_method" :value="scope.row.executeMode" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="100">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
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
      <div style="width: 100%">
        <el-button type="primary" @click="confirmSelectScene">{{ $t('confirm') }}</el-button>
        <el-button @click="cancel">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listScene } from '@/api/iot/scene';
import { useDict } from '@/utils/dict';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;
const { trigger_condition, execution_method } = useDict('trigger_condition', 'execution_method');

const emit = defineEmits(['sceneEvent']);

const openScene = ref(false);
const loading = ref(true);
const total = ref(0);
const sceneList = ref<any[]>([]);
const selectSceneId = ref(0);
const scene = ref<any>({});
const sceneId = ref<any>(null);
const queryFormRef = ref();

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  hasAlert: 1,
  sceneName: null,
});

function getList() {
  loading.value = true;
  listScene(queryParams).then((response: any) => {
    sceneList.value = response.rows.filter((item: any) => item.sceneId !== sceneId.value);
    total.value = response.total;
    loading.value = false;
    setRadioSelected(selectSceneId.value);
  });
}

function cancel() {
  openScene.value = false;
  scene.value = {};
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  proxy.resetForm(queryFormRef.value);
  handleQuery();
}

function rowClick(row: any) {
  if (row != null) {
    setRadioSelected(row.sceneId);
    scene.value = row;
  }
}

function setRadioSelected(id: number) {
  for (let i = 0; i < sceneList.value.length; i++) {
    sceneList.value[i].isSelect = sceneList.value[i].sceneId == id;
  }
}

function confirmSelectScene() {
  emit('sceneEvent', scene.value);
  openScene.value = false;
}

defineExpose({ openScene, sceneId, selectSceneId, queryParams, getList });
</script>
