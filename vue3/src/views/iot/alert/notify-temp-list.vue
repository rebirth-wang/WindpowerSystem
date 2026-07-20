<template>
  <el-dialog :title="$t('alert.notify-temp-list.555661-0')" v-model="dialogVisible" width="900px" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" @submit.prevent>
      <el-form-item prop="name">
        <el-input
          v-model="queryParams.name"
          :placeholder="$t('alert.notify-temp-list.555661-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="channelType">
        <el-select
          v-model="queryParams.channelType"
          :placeholder="$t('alert.notify-temp-list.555661-4')"
          clearable
          style="width: 192px"
        >
          <el-option
            v-for="option in notify_channel_type"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table
      :data="notifyTempList"
      v-loading="loading"
      :border="false"
      @select="handleSelect"
      @select-all="handleSelectAll"
      ref="multipleTableRef"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('alert.index.236501-28')" align="left" prop="name" min-width="190" />
      <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="status" min-width="80">
        <template #default="scope">
          <el-tag type="success" size="small" v-if="scope.row.status == '1'">{{ $t('alert.index.236501-45') }}</el-tag>
          <el-tag type="danger" size="small" v-if="scope.row.status == '0'">{{ $t('alert.index.236501-46') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.index.236501-29')" align="center" prop="channelType" min-width="100">
        <template #default="scope">
          <dict-tag :options="notify_channel_type" :value="scope.row.channelType" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('alert.index.236501-30')" align="center" prop="channelName" min-width="100" />
      <el-table-column :label="$t('alert.index.236501-31')" align="left" prop="provider" min-width="120" />
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
import { listTemplate } from '@/api/notify/template';
import { useDict } from '@/utils/dict';

const { notify_channel_type } = useDict('notify_channel_type');

const emit = defineEmits(['notifyEvent']);

const dialogVisible = ref(false);
const loading = ref(false);
const total = ref(0);
const notifyTempList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null as any,
  channelType: null as any,
});

/** 已选通知模板 */
const selectNotifyTemps = ref<any[]>([]);
const ids = ref<any[]>([]);

/** 打开对话框 */
const openDialog = () => {
  dialogVisible.value = true;
  getList();
};

/** 查询通知模板列表 */
const getList = () => {
  loading.value = true;
  listTemplate({ ...queryParams, serviceCode: 'alert' }).then((response: any) => {
    notifyTempList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    nextTick(() => {
      if (multipleTableRef.value && ids.value.length > 0) {
        notifyTempList.value.forEach((row: any) => {
          if (ids.value.includes(row.id)) {
            multipleTableRef.value.toggleRowSelection(row, true);
          }
        });
      }
    });
  });
};

/** 单选处理 */
const handleSelect = (selection: any[], row: any) => {
  const selected = selection.some((item: any) => item.id === row.id);
  if (selected) {
    if (!ids.value.includes(row.id)) {
      ids.value.push(row.id);
      selectNotifyTemps.value.push(row);
    }
  } else {
    ids.value = ids.value.filter((id: any) => id !== row.id);
    selectNotifyTemps.value = selectNotifyTemps.value.filter((item: any) => item.id !== row.id);
  }
};

/** 全选处理 */
const handleSelectAll = (selection: any[]) => {
  if (selection.length > 0) {
    selection.forEach((row: any) => {
      if (!ids.value.includes(row.id)) {
        ids.value.push(row.id);
        selectNotifyTemps.value.push(row);
      }
    });
  } else {
    notifyTempList.value.forEach((row: any) => {
      ids.value = ids.value.filter((id: any) => id !== row.id);
      selectNotifyTemps.value = selectNotifyTemps.value.filter((item: any) => item.id !== row.id);
    });
  }
};

/** 确认选择 */
const handleConfirm = () => {
  emit('notifyEvent', selectNotifyTemps.value);
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

defineExpose({ openDialog, selectNotifyTemps, ids });
</script>
