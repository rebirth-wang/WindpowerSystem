<template>
  <el-dialog :title="$t('alert.scene-list.591934-0')" v-model="dialogVisible" width="900px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" @submit.prevent>
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
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table
      :data="sceneList"
      v-loading="loading"
      :border="false"
      @select="handleSelect"
      @select-all="handleSelectAll"
      ref="multipleTableRef"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('alert.scene-list.591934-1')" align="left" prop="sceneName" min-width="200" />
      <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="enable" min-width="80">
        <template #default="scope">
          <dict-tag :options="scene_status" :value="scope.row.enable"></dict-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.index.236501-19')" align="center" prop="cond" min-width="100">
        <template #default="scope">
          <dict-tag :options="trigger_condition" :value="scope.row.cond" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.index.236501-23')" align="center" prop="executeMode" min-width="100">
        <template #default="scope">
          <dict-tag :options="execution_method" :value="scope.row.executeMode" />
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
      <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listScene } from '@/api/iot/scene';
import { useDict } from '@/utils/dict';

const { scene_status, trigger_condition, execution_method } = useDict(
  'scene_status',
  'trigger_condition',
  'execution_method'
);

const emit = defineEmits(['sceneEvent']);

const dialogVisible = ref(false);
const loading = ref(false);
const total = ref(0);
const sceneList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  hasAlert: 1,
  sceneName: null as any,
});

/** 已选场景 */
const selectScenes = ref<any[]>([]);
const ids = ref<any[]>([]);

/** 打开对话框 */
const openDialog = () => {
  dialogVisible.value = true;
  getList();
};

/** 查询场景列表 */
const getList = () => {
  loading.value = true;
  listScene(queryParams).then((response: any) => {
    sceneList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    nextTick(() => {
      // 回显已选中
      if (multipleTableRef.value && ids.value.length > 0) {
        sceneList.value.forEach((row: any) => {
          if (ids.value.includes(row.sceneId)) {
            multipleTableRef.value.toggleRowSelection(row, true);
          }
        });
      }
    });
  });
};

/** 单选处理 */
const handleSelect = (selection: any[], row: any) => {
  const selected = selection.some((item: any) => item.sceneId === row.sceneId);
  if (selected) {
    if (!ids.value.includes(row.sceneId)) {
      ids.value.push(row.sceneId);
      selectScenes.value.push(row);
    }
  } else {
    ids.value = ids.value.filter((id: any) => id !== row.sceneId);
    selectScenes.value = selectScenes.value.filter((item: any) => item.sceneId !== row.sceneId);
  }
};

/** 全选处理 */
const handleSelectAll = (selection: any[]) => {
  if (selection.length > 0) {
    selection.forEach((row: any) => {
      if (!ids.value.includes(row.sceneId)) {
        ids.value.push(row.sceneId);
        selectScenes.value.push(row);
      }
    });
  } else {
    sceneList.value.forEach((row: any) => {
      ids.value = ids.value.filter((id: any) => id !== row.sceneId);
      selectScenes.value = selectScenes.value.filter((item: any) => item.sceneId !== row.sceneId);
    });
  }
};

/** 确认选择 */
const handleConfirm = () => {
  emit('sceneEvent', selectScenes.value);
  dialogVisible.value = false;
};

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

defineExpose({ openDialog, selectScenes, ids });
</script>
